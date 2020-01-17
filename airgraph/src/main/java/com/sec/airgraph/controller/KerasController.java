package com.sec.airgraph.controller;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;

import com.sec.rtc.entity.rtc.CodeDirectory;
import com.sec.keras.entity.field.KerasFieldInfo;
import com.sec.keras.entity.model.KerasModel;
import com.sec.airgraph.form.KerasForm;
import com.sec.airgraph.service.KerasService;
import com.sec.airgraph.util.KerasEditorUtil;
import com.sec.airgraph.util.PropUtil;
import com.sec.airgraph.util.StringUtil;

/**
 * AirGraph Keras-Editorコントトーラ
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Controller
@RequestMapping(value = "/keras")
@SessionAttributes({ "kerasForm" })
public class KerasController {

	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(KerasController.class);

	/**
	 * Kersサービス
	 */
	@Autowired
	private KerasService kerasService;

	/**
	 * モデルオブジェクト初期化
	 * 
	 * @param session
	 * @return
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
	 * 初期表示処理
	 * 
	 * @param form
	 * @param bindingResult
	 * @param model
	 * @param session
	 * @param sessionStatus
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String init(@ModelAttribute("kerasForm") KerasForm form, BindingResult bindingResult, Model model,
			HttpSession session, SessionStatus sessionStatus) {
		logger.info("Intialized keras.");

		// 初期化
		sessionStatus.setComplete();
		model.addAttribute("kerasForm", newRequest(session));

		return "keras/keras";
	}

	/**
	 * ネットワーク領域情報を取得する
	 * 
	 * @return
	 */
	@RequestMapping(value = "loadNetworkArea", method = RequestMethod.GET)
	@ResponseBody
	public KerasFieldInfo loadNetworkArea() {
		logger.info("Load all Network data.");
		return kerasService.loadAllNetworkArea();
	}

	/**
	 * 作業領域のすべてのPackageの内容を読み込む
	 * 
	 * @param rtsName
	 * @return
	 */
	@RequestMapping(value = "loadAllWorkspace", method = RequestMethod.GET)
	@ResponseBody
	public List<KerasModel> loadAllWorkspace() {
		logger.info("Load all workspace data.");
		return kerasService.loadAllPackagesWorkspace();
	}

	/**
	 * 全てのレイヤープロパティ設定用テンプレートを読み込む
	 * 
	 * @return
	 */
	@RequestMapping(value = "loadAllLayerPropertyTemplates", method = RequestMethod.GET)
	@ResponseBody
	public List<String> loadAllLayerPropertyTemplates() {
		return kerasService.getAllLayerPropertyTemplates();
	}

	/**
	 * 指定されたモデルを作業領域フォルダに保存する
	 * 
	 * @param json
	 * @return
	 */
	@RequestMapping(value = "saveModel", method = RequestMethod.POST)
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
	 * 指定されたモデルを削除する
	 * 
	 * @param json
	 * @return
	 */
	@RequestMapping(value = "deleteModel", method = RequestMethod.POST)
	@ResponseBody
	public String deleteModel(@RequestParam(value = "modelName") String modelName) {
		logger.info("Delete model in workspace. modelName[" + modelName + "]");
		if (StringUtil.isNotEmpty(modelName)) {
			kerasService.deleteModel(modelName);
		}
		return "{\"response\" : \"OK\"}";
	}

	/**
	 * 指定されたモデルの学習を実行
	 * 
	 * @param json
	 */
	@RequestMapping(value = "fit", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String fit(@RequestBody String json) {
		logger.info("Run Keras Fit in workspace.");
		if (StringUtil.isNotEmpty(json)) {
			kerasService.fit(json);
		}
		return "{\"response\" : \"OK\"}";
	}

	/**
	 * ログファイルを読み込む
	 * 
	 * @param workPackageName
	 * @return
	 */
	@RequestMapping(value = "tailLog", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, String> tailLog(@RequestParam(value = "workPackageName") String workPackageName) {
		logger.info("Tailing Log. workspace[" + workPackageName + "]");
		return kerasService.tailAllLog(StringUtil.getPackageNameFromModelName(workPackageName));
	}

	/**
	 * KerasのDataMakerのテンプレートファイルをダウンロードする
	 * 
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "getDataMakerTemplate", method = RequestMethod.POST)
	public String downloadDataMakerTemplate(HttpServletResponse response) throws IOException {
		logger.info("Download data_maker template file. ");
		// 結果格納ファイル
		String templateFilePath = PropUtil.getValue("keras_ide_util.template.data_maker.file.path");
		File file = new File(templateFilePath);
		response.addHeader("Content-Type", "application/octet-stream");
		response.addHeader("Content-Disposition",
				"attachment; filename*=UTF-8''" + URLEncoder.encode(file.getName(), StandardCharsets.UTF_8.name()));

		Files.copy(file.toPath(), response.getOutputStream());
		return null;
	}

	/**
	 * DataMakerファイルをアップロードする
	 * 
	 * @param response
	 * @param workspaceModelName
	 * @param componentName
	 * @param file
	 */
	@RequestMapping(value = "dataMakerUpload", method = RequestMethod.POST)
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
	 * Datasetをダウンロードする
	 * 
	 * @param response
	 * @param workspaceModelName
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "datasetDownload", method = RequestMethod.POST)
	public String datasetDownload(HttpServletResponse response,
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
		return null;
	}

	/**
	 * Datasetをアップロードする
	 * 
	 * @param response
	 * @param workspaceModelName
	 * @param file
	 */
	@RequestMapping(value = "datasetUpload", method = RequestMethod.POST)
	public void datasetUpload(HttpServletResponse response,
			@RequestParam(value = "workspace-model-name-dataset") String workspaceModelName,
			@RequestParam(value = "dataset-upload") MultipartFile file) {
		logger.info("Upload dataset. modelName[" + workspaceModelName + "]");
		kerasService.uploadDataset(file);
	}

	/**
	 * データセットの選択肢取得
	 * 
	 * @return
	 */
	@RequestMapping(value = "getDatasetChoices", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> getDatasetChoices() {
		return kerasService.getDatasetChoices();
	}

	/**
	 * 指定されたデータセットのデータ一覧取得
	 * 
	 * @return
	 */
	@RequestMapping(value = "getDatasetDataList", method = RequestMethod.POST)
	@ResponseBody
	public CodeDirectory getDatasetDataList(@RequestParam(value = "datasetName") String datasetName) {
		logger.info("Get Dataset Data. datasetName[" + datasetName + "]");
		return kerasService.getDatasetDataList(datasetName);
	}
}
