package com.sec.airgraph.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sec.airgraph.util.FileUtil;
import com.sec.airgraph.util.KerasEditorUtil;
import com.sec.airgraph.util.PropUtil;
import com.sec.airgraph.util.StringUtil;
import com.sec.keras.entity.field.KerasFieldInfo;
import com.sec.keras.entity.field.KerasTabInfo;
import com.sec.keras.entity.model.KerasModel;
import com.sec.rtc.entity.rtc.CodeDirectory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Kerasサービス.
 *
 * @author Tsuyoshi Hirose
 *
 */
@Service
public class KerasService {

	/**
	 * logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(KerasService.class);

	/**
	 * Keras管理サービス.
	 */
	@Autowired
	private KerasManagementService kerasManagementService;

	/**
	 * RTC管理サービス.
	 */
	@Autowired
	private RtcManagementService rtcManagementService;

	/**
	 * Kerasネットワーク情報を全て取得する.
	 */
	public KerasFieldInfo loadAllNetworkArea() {

		// 全てのNN,Layerを展開する
		cloneKerasRepository();

		// Kerasネットワーク領域情報を取得する
		return getKerasFieldInfo();
	}

	/**
	 * すべてのリポジトリを展開する.
	 */
	private void cloneKerasRepository() {

		// TODO: KerasEditorのGit連携
		// すべてのNetworkを展開する
		// すべてのLayerを展開する
	}

	/**
	 * Kerasネットワーク領域情報を取得する.
	 *
	 * @return Kerasネットワーク領域情報
	 */
	private KerasFieldInfo getKerasFieldInfo() {
		logger.info("Start create Keras network area.");
		// レイヤー情報保存先パス
		String layerTemplateDirPath = PropUtil.getValue("layer.keras.template.directory.path");
		// モデルテンプレート情報保存先パス
		String modelTemplateDirPath = PropUtil.getValue("model.keras.template.directory.path");
		// モデル情報保存先パス
		String modelDirPath = PropUtil.getValue("models.keras.directory.path");

		KerasFieldInfo kerasFieldInfo = new KerasFieldInfo();
		List<KerasTabInfo> kerasTabs = new ArrayList<>();

		// 新規作成タブ
		KerasTabInfo newModelTab = new KerasTabInfo();
		newModelTab.setTabName("New");

		// Modelタブ
		KerasTabInfo modelTab = new KerasTabInfo();
		modelTab.setTabName("Model");

		// レイヤータブ
		KerasTabInfo layerTab = new KerasTabInfo();
		layerTab.setTabName("Layer");

		// テンプレートから新規作成タブの情報を全て読み込みレイヤーに設定
		List<KerasModel> newModels = kerasManagementService.loadAllKerasModels(modelTemplateDirPath, true);
		newModelTab.setModels(newModels);

		// 保存されたモデル情報を全て読み込みレイヤーに設定
		List<KerasModel> models = kerasManagementService.loadAllKerasModels(modelDirPath, true);
		modelTab.setModels(models);

		// レイヤーのテンプレートフォルダ内にあるファイルをすべて読込レイヤーに設定
		List<KerasTabInfo> layerTabs = kerasManagementService.loadAllKerasLayers(layerTemplateDirPath);
		layerTab.setChildTabs(layerTabs);

		// 親レイヤーのリストに追加
		// workingTab.setModels(modelTab.getModels().stream()
		// .filter(model -> model.getModelName().equals("model_blank"))
		// .collect(Collectors.toList()));
		kerasTabs.add(newModelTab);
		kerasTabs.add(modelTab);
		kerasTabs.add(layerTab);

		kerasFieldInfo.setKerasTabs(kerasTabs);
		logger.info("Finish create Keras network area.");
		return kerasFieldInfo;
	}

	/**
	 * レイヤーのプロパティ設定用JSONを読み込む.
	 *
	 * @return プロパティ設定用JSON
	 */
	public List<String> getAllLayerPropertyTemplates() {
		logger.info("Start load Keras layer propery templates.");
		// レイヤー情報保存先パス
		String propertyTemplateDirPath = PropUtil.getValue("property.layer.keras.template.directory.path");

		List<String> list = new ArrayList<>();

		// レイヤーのテンプレートフォルダ内にあるファイルをすべて読込レイヤーに設定
		File propertyTemplateDir = new File(propertyTemplateDirPath);
		File[] propertyDirs = propertyTemplateDir.listFiles();
		for (File propertyDir : propertyDirs) {
			if (FileUtil.exists(propertyDir) && propertyDir.isDirectory()) {
				File[] files = propertyDir.listFiles();
				for (File file : files) {
					list.add(FileUtil.readAll(file.getPath()));
				}
			}
		}
		logger.info("Finish load Keras layer propery templates.");
		return list;
	}

	/**
	 * 作業領域のすべてのモデルを読み込む.
	 *
	 * @return 作業領域のすべてのモデル
	 */
	public List<KerasModel> loadAllPackagesWorkspace() {
		// 作業領域パス
		String workspaceDirPath = PropUtil.getValue("workspace.local.keras.directory.path");

		return kerasManagementService.loadAllKerasModels(workspaceDirPath, true);
	}

	/**
	 * モデルを作業領域フォルダに保存する.
	 *
	 * @param dirName ディレクトリ名
	 * @param modelString モデル
	 */
	public void saveModel(String dirName, String modelString) {
		// 作業領域パス
		String workspaceDirPath = PropUtil.getValue("workspace.local.keras.directory.path");

		ObjectMapper mapper = new ObjectMapper();
		KerasModel savingModel = null;
		try {
			// デシリアライズ
			savingModel = mapper.readValue(modelString, KerasModel.class);

			// 保存先ディレクトリが存在しなければ作成
			String targetDirPath = workspaceDirPath + savingModel.getModelName();
			File targetDir = new File(targetDirPath);
			FileUtil.createDirectory(targetDir);

			// 作業ディレクトリに作業内容を保存
			// JSONファイルを保存
			String modelFilePath = targetDirPath + "/" + savingModel.getModelName() + ".json";
			FileUtil.writeAll(modelFilePath, savingModel.getJsonString(), true);

			// data_maker.pyを保存
			String dataMakerFilePath = targetDirPath + "/" + "data_maker.py";
			FileUtil.writeAll(dataMakerFilePath, savingModel.getDataMakerStr(), true);

			// データ・セット連携
			String datasetName = savingModel.getDataset();
			FileUtil.createDatasetLink(targetDirPath, datasetName);

			// 保存先のディレクトリ名と現在のディレクトリが異なる場合は古い方を削除する
			if (!StringUtil.equals(savingModel.getModelName(), dirName)) {
				File sourceDir = new File(workspaceDirPath + dirName);
				FileUtil.deleteDirectory(sourceDir);
			}

		} catch (IOException e) {
			logger.error("exception handled. ex:", e);
		}
	}

	/**
	 * モデルを削除する.
	 *
	 * @param modelName モデル名
	 */
	public void deleteModel(String modelName) {
		// 作業領域パス
		String workspaceDirPath = PropUtil.getValue("workspace.local.keras.directory.path");
		String modelDirPath = workspaceDirPath + modelName;
		FileUtil.deleteDirectory(new File(modelDirPath));
	}

	/**
	 * Kerasの学習実行.
	 *
	 * @param modelString modelの情報が乗ったJSON文字列
	 */
	public void fit(String modelString) {
		// 作業領域パス
		String workspaceDirPath = PropUtil.getValue("workspace.local.keras.directory.path");
		try {
			// デシリアライズ
			ObjectMapper mapper = new ObjectMapper();
			KerasModel savingModel = mapper.readValue(modelString, KerasModel.class);
			// モデルディレクトリパスの生成
			String dirPath = workspaceDirPath + savingModel.getModelName();
			// モデルティレクトリの作成
			File newDir = new File(dirPath);
			newDir.mkdirs();
			// ファイルパス生成・JSONファイル保存
			String filePath = dirPath + "/" + savingModel.getModelName() + ".json";
			FileWriter fileWriter = new FileWriter(new File(filePath));
			fileWriter.write(savingModel.getJsonString());
			fileWriter.close();

			// 学習実行
			KerasEditorUtil.learn(savingModel.getModelName(), dirPath);
		} catch (IOException e) {
			logger.error("exception handled. ex:", e);
		}
	}

	/**
	 * ログを読み込む.
	 *
	 * @param workPackageName パッケージ名
	 * @return ログ
	 */
	public Map<String, String> tailAllLog(String workPackageName) {
		Map<String, String> logMap = new HashMap<>();
		// keras_ide_utilログ
		String kerasLogFilePath = PropUtil.getValue("workspace.local.keras_ide_util.logfile.path");

		// 全てのログを取得マップに詰めておく
		logMap.put("keras", FileUtil.readAll(kerasLogFilePath));
		return logMap;
	}

	/**
	 * データセットの選択肢を取得する.
	 *
	 * @return データセットの選択肢
	 */
	public List<String> getDatasetChoices() {
		return kerasManagementService.loadDatasetList();
	}

	/**
	 * データセットディレクトリを圧縮して保存先を返す.
	 *
	 * @param workspaceModelName ワークスペースモデル名
	 * @return 保存先
	 */
	public String downloadDataset(String workspaceModelName) {
		return kerasManagementService.downloadDataset(workspaceModelName);
	}

	/**
	 * データセットファイルをアップロードする.
	 *
	 * @param datasetFile データセットファイル
	 */
	public void uploadDataset(MultipartFile datasetFile) {
		kerasManagementService.uploadDataset(datasetFile);
	}

	/**
	 * 指定されたデータセットのデータの一覧を取得する.
	 *
	 * @param datasetName データセット名
	 * @return データセットのデータの一覧
	 */
	public CodeDirectory getDatasetDataList(String datasetName) {
		// データセット領域のパス
		String dataSetDirPath = PropUtil.getValue("dataset.directory.path") + datasetName;
		// データセット以下のファイルの一覧を取得する
		return rtcManagementService.getCodeFile(dataSetDirPath, null, null, null, null);
	}

	/**
	 * AirGraphホストの一覧を取得する.
	 *
	 * @return AirGraphホストの一覧
	 */
	public List<String> getAirGraphHostChoices() {
		return kerasManagementService.getAirGraphHostChoices();
	}

	/**
	 * 指定されたロボットのデータセットの一覧を取得する.
	 *
	 * @param robotHostName ホスト名
	 * @return ロボットのデータセットの一覧
	 */
	public List<String> getRobotDatasetChoices(String robotHostName) {
		return kerasManagementService.getRobotDatasetChoices(robotHostName);
	}

	/**
	 * 指定されたデータセットディレクトリのデータを取得する.
	 *
	 * @param datasetName データセット名
	 * @param targetDate 対象日
	 * @return データセットディレクトリのデータ
	 */
	public boolean compressDatasets(String datasetName, String targetDate) {
		return kerasManagementService.compressDatasets(datasetName, targetDate);
	}

	/**
	 * 指定されたデータセットをダウンロードする.
	 *
	 * @param robotHostName ロボットホスト名 
	 * @param datasetName データセット名
	 */
	public boolean downloadDatasets(String robotHostName, String datasetName) {
		return kerasManagementService.downloadDatasets(robotHostName, datasetName);
	}
}
