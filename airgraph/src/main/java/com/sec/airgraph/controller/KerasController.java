package com.sec.airgraph.controller;

import com.sec.airgraph.form.KerasForm;
import com.sec.airgraph.service.KerasService;
import com.sec.airgraph.util.KerasEditorUtil;
import com.sec.airgraph.util.PropUtil;
import com.sec.airgraph.util.StringUtil;
import com.sec.keras.entity.field.KerasFieldInfo;
import com.sec.keras.entity.model.KerasModel;
import com.sec.rtc.entity.rtc.CodeDirectory;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;


/**
 * AirGraph Keras-Editorコントローラ.
 *
 * @author Tsuyoshi Hirose
 *
 */
@Controller
@RequestMapping(value = "/keras")
@SessionAttributes({ "kerasForm" })
public class KerasController {

	/**
	 * logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(KerasController.class);

	/**
	 * Kersサービス.
	 */
	@Autowired
	private KerasService kerasService;

	/**
	 * モデルオブジェクト初期化.
	 *
	 * @param session セッション
	 * @return フォーム
	 */
	@ModelAttribute("kerasForm")
	public KerasForm newRequest(HttpSession session) {
		KerasForm f = new KerasForm();
		return f;
	}

	/***********************************************
	 * リクエスト処理
	 ***********************************************/

	/**
	 * 初期表示処理.
	 *
	 * @param form フォーム
	 * @param bindingResult 結果
	 * @param model モデル
	 * @param session セッション
	 * @param sessionStatus セッションステータス
	 * @return URI
	 */
	@GetMapping("")
	public String init(@ModelAttribute("kerasForm") KerasForm form, BindingResult bindingResult, Model model,
			HttpSession session, SessionStatus sessionStatus) {
		logger.info("Intialized keras.");

		// 初期化
		sessionStatus.setComplete();
		model.addAttribute("kerasForm", newRequest(session));

		return "keras/keras";
	}

	/**
	 * ネットワーク領域情報を取得する.
	 *
	 * @return ネットワーク領域情報
	 */
	@GetMapping("loadNetworkArea")
	@ResponseBody
	public KerasFieldInfo loadNetworkArea() {
		logger.info("Load all Network data.");
		return kerasService.loadAllNetworkArea();
	}

	/**
	 * 作業領域のすべてのPackageの内容を読み込む.
	 *
	 * @return 作業領域のすべてのPackageの内容
	 */
	@GetMapping("loadAllWorkspace")
	@ResponseBody
	public List<KerasModel> loadAllWorkspace() {
		logger.info("Load all workspace data.");
		return kerasService.loadAllPackagesWorkspace();
	}

	/**
	 * 全てのレイヤープロパティ設定用テンプレートを読み込む.
	 *
	 * @return プロパティ設定用テンプレート
	 */
	@GetMapping("loadAllLayerPropertyTemplates")
	@ResponseBody
	public List<String> loadAllLayerPropertyTemplates() {
		return kerasService.getAllLayerPropertyTemplates();
	}

	/**
	 * 指定されたモデルを作業領域フォルダに保存する.
	 *
	 * @param dirName 作業領域フォルダ
	 * @param json 指定されたモデル
	 * @return レスポンス
	 */
	@PostMapping("saveModel")
	@ResponseBody
	public String saveModel(@RequestParam(value = "dirName") String dirName,
			@RequestParam(value = "json") String json) {
		logger.info("Save model in workspace.");
		if (StringUtil.isNotEmpty(json)) {
			kerasService.saveModel(dirName, json);
		}
		return "{\"response\" : \"OK\"}";
	}

	/**
	 * 指定されたモデルを削除する.
	 *
	 * @param modelName モデル
	 * @return レスポンス
	 */
	@PostMapping("deleteModel")
	@ResponseBody
	public String deleteModel(@RequestParam(value = "modelName") String modelName) {
		logger.info("Delete model in workspace. modelName[" + modelName + "]");
		if (StringUtil.isNotEmpty(modelName)) {
			kerasService.deleteModel(modelName);
		}
		return "{\"response\" : \"OK\"}";
	}

	/**
	 * 指定されたモデルの学習を実行.
	 *
	 * @param json モデル
	 * @return レスポンス
	 */
	@PostMapping(value = "fit", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String fit(@RequestBody String json) {
		logger.info("Run Keras Fit in workspace.");
		if (StringUtil.isNotEmpty(json)) {
			kerasService.fit(json);
		}
		return "{\"response\" : \"OK\"}";
	}

	/**
	 * ログファイルを読み込む.
	 *
	 * @param workPackageName パッケージ名
	 * @return ログ
	 */
	@GetMapping("tailLog")
	@ResponseBody
	public Map<String, String> tailLog(@RequestParam(value = "workPackageName") String workPackageName) {
		logger.info("Tailing Log. workspace[" + workPackageName + "]");
		return kerasService.tailAllLog(StringUtil.getPackageNameFromModelName(workPackageName));
	}

	/**
	 * KerasのDataMakerのテンプレートファイルをダウンロードする.
	 *
	 * @param response レスポンス
	 * @throws IOException
	 */
	@PostMapping("getDataMakerTemplate")
	public void downloadDataMakerTemplate(HttpServletResponse response) throws IOException {
		logger.info("Download data_maker template file. ");
		// 結果格納ファイル
		String templateFilePath = PropUtil.getValue("keras_ide_util.template.data_maker.file.path");
		File file = new File(templateFilePath);
		response.addHeader("Content-Type", "application/octet-stream");
		response.addHeader("Content-Disposition",
				"attachment; filename*=UTF-8''" + URLEncoder.encode(file.getName(), StandardCharsets.UTF_8.name()));

		Files.copy(file.toPath(), response.getOutputStream());
	}

	/**
	 * DataMakerファイルをアップロードする.
	 *
	 * @param response レスポンス
	 * @param workspaceModelName ワークスペースモデル名
	 * @param file DataMakerファイル
	 */
	@PostMapping("dataMakerUpload")
	public void dataMakerUpload(HttpServletResponse response,
			@RequestParam(value = "workspace-model-name") String workspaceModelName,
			@RequestParam(value = "datamaker-upload") MultipartFile file) {

		// ファイルが空の場合は HTTP 400 を返す。
		if (file.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		logger.info("Upload data_maker file");
		KerasEditorUtil.saveDataMakerFile(workspaceModelName, file);
	}

	/**
	 * Datasetをダウンロードする.
	 *
	 * @param response レスポンス
	 * @param workspaceModelName ワークスペースモデル名
	 * @throws IOException 
	 */
	@PostMapping("datasetDownload")
	public void datasetDownload(HttpServletResponse response,
			@RequestParam(value = "workspace-model-name-dataset") String workspaceModelName) throws IOException {
		logger.info("Download dataset. modelName[" + workspaceModelName + "]");
		// データセットを圧縮したファイル名
		String filePath = kerasService.downloadDataset(workspaceModelName);
		if (StringUtil.isNotEmpty(filePath)) {
			File file = new File(filePath);
			response.addHeader("Content-Type", "application/octet-stream");
			response.addHeader("Content-Disposition",
					"attachment; filename*=UTF-8''" + URLEncoder.encode(file.getName(), StandardCharsets.UTF_8.name()));

			Files.copy(file.toPath(), response.getOutputStream());
		}
	}

	/**
	 * Datasetをアップロードする.
	 *
	 * @param response レスポンス
	 * @param workspaceModelName ワークスペースモデル名
	 * @param file Dataset
	 */
	@PostMapping("datasetUpload")
	public void datasetUpload(HttpServletResponse response,
			@RequestParam(value = "workspace-model-name-dataset") String workspaceModelName,
			@RequestParam(value = "dataset-upload") MultipartFile file) {
		logger.info("Upload dataset. modelName[" + workspaceModelName + "]");
		kerasService.uploadDataset(file);
	}

	/**
	 * データセットの選択肢取得.
	 *
	 * @return データセットの選択肢
	 */
	@PostMapping("getDatasetChoices")
	@ResponseBody
	public List<String> getDatasetChoices() {
		return kerasService.getDatasetChoices();
	}

	/**
	 * 指定されたデータセットのデータ一覧取得.
	 *
	 * @param datasetName データセット
	 * @return データセットのデータ一覧
	 */
	@PostMapping("getDatasetDataList")
	@ResponseBody
	public CodeDirectory getDatasetDataList(@RequestParam(value = "datasetName") String datasetName) {
		logger.info("Get Dataset Data. datasetName[" + datasetName + "]");
		return kerasService.getDatasetDataList(datasetName);
	}

	/**
	 *  AirGraphホストの一覧を取得する.
	 *
	 * @return AirGraphホストの一覧
	 */
	@PostMapping("getAirGraphHostChoices")
	@ResponseBody
	public List<String> getAirGraphHostChoices() {
		logger.info("Get AirGraph Host Choices.");
		return kerasService.getAirGraphHostChoices();
	}

	/**
	 * 指定されたロボットのAirGraphのデータセット選択肢取得.
	 *
	 * @param robotHostName ホスト名
	 * @return ロボットのAirGraphのデータセット選択肢
	 */
	@PostMapping("getRobotDatasetChoices")
	@ResponseBody
	public List<String> getRobotDatasetChoices(@RequestParam(value = "robotHostName") String robotHostName) {
		logger.info("Get Dataset Choices. target host[" + robotHostName + "]");
		return kerasService.getRobotDatasetChoices(robotHostName);
	}

	/**
	 * 対象のロボットから指定されたデータセットを取得する.
	 *
	 * @param response レスポンス
	 * @param robotHostName ホスト名
	 * @param datasetName データセット名
	 * @return 対象のロボットから指定されたデータセット
	 * @throws IOException
	 */
	@PostMapping("getRobotDatasets")
	@ResponseBody
	public String getRobotDatasets(HttpServletResponse response,
	    @RequestParam(value = "robotHostName") String robotHostName, 
		@RequestParam(value = "datasetName") String datasetName) throws IOException {
		logger.info("Get Robot Dataset. target robot[" + robotHostName +  "]dataset name[" + datasetName + "]");

		kerasService.downloadDatasets(robotHostName, datasetName);
		return "{\"response\" : \"OK\"}";
	}

	/**
	 * 指定されたデータセットをダウンロードする.
	 *
	 * @param response レスポンス
	 * @param datasetName データセット名
	 * @param targetDate 対象日
	 * @return 指定されたデータセット
	 * @throws IOException
	 */
	@PostMapping("downloadDatasets")
	public String downloadDatasets(HttpServletResponse response,
		@RequestParam(value = "datasetName") String datasetName, 
		@RequestParam(value = "targetDate") String targetDate) throws IOException {
		logger.info("Get Dataset. dataset name[" + datasetName + "]target date[" + targetDate + "]");

		// データセットを圧縮する
		File tmpFile = new File("/tmp/dataset.zip");
		kerasService.compressDatasets(datasetName, targetDate);

		// 結果格納ファイル
		response.addHeader("Content-Type", "application/octet-stream");
		response.addHeader("Content-Disposition",
				"attachment; filename*=UTF-8''" + URLEncoder.encode(tmpFile.getName(), StandardCharsets.UTF_8.name()));

		Files.copy(tmpFile.toPath(), response.getOutputStream());
		return null;
	}
}
