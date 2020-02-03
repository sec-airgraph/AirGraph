package com.sec.airgraph.service;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.sec.keras.entity.field.KerasTabInfo;
import com.sec.keras.entity.model.KerasModel;
import com.sec.airgraph.util.CollectionUtil;
import com.sec.airgraph.util.FileUtil;
import com.sec.airgraph.util.PropUtil;
import com.sec.airgraph.util.StringUtil;

/**
 * Keras管理サービス
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Service
public class KerasManagementService {

	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(KerasManagementService.class);

	/**
	 * Kerasのモデル情報を読み込む
	 * 
	 * @param modelDirPath
	 * @param loadJson
	 * @return
	 */
	public List<KerasModel> loadAllKerasModels(String modelDirPath, boolean loadJson) {
		List<KerasModel> models = new ArrayList<>();
		// モデルのフォルダ内にあるファイルをすべて読込レイヤーに設定
		File userModelDir = new File(modelDirPath);
		File userModelDirs[] = userModelDir.listFiles();
		for (File modelDir : userModelDirs) {
			if (FileUtil.exists(modelDir) && modelDir.isDirectory()) {
				File[] modelFiles = modelDir.listFiles();

				File modelJson = null;
				File dataMaker = null;

				if (CollectionUtil.isNotEmpty(modelFiles)) {
					for (File file : modelFiles) {
						if (file.isFile() && file.getPath().endsWith(".json")) {
							// モデルJSONファイル
							modelJson = file;
						} else if (file.isFile() && file.getPath().endsWith("data_maker.py")) {
							// データ読み込みコード
							dataMaker = file;
						}
					}
				}

				if (modelJson != null && dataMaker != null) {
					logger.debug("load model json file. path[" + modelJson.getPath() + "]");
					logger.debug("load data maker file. path[" + dataMaker.getPath() + "]");

					// モデル情報の読み込み
					KerasModel model = new KerasModel();
					// モデル名は拡張子を覗いたファイル名とする
					String modelName = modelJson.getName();
					int index = modelName.lastIndexOf('.');
					if (index != -1) {
						modelName = modelName.substring(0, index);
					}
					model.setModelName(modelName);
					// ディレクトリ名称も保存しておく
					String dirName = modelDir.getName();
					model.setDirName(dirName);

					if (loadJson) {
						// JSONファイルの読み込み
						model.setJsonString(FileUtil.readAll(modelJson.getPath()));

						// dataMaker.pyファイルの読み込み
						model.setDataMakerStr(FileUtil.readAll(dataMaker.getPath()));

						// データ・セットリンクの取得
						model.setDataset(FileUtil.getDatasetLink(modelDir.getPath()));
					}
					models.add(model);
				}
			}
		}
		// ソートする
		models = CollectionUtil.sort(models, keras -> keras.getModelName());
		return models;
	}

	/**
	 * Kerasのレイヤー情報を読み込む
	 * 
	 * @param layerTemplateDirPath
	 * @return
	 */
	public List<KerasTabInfo> loadAllKerasLayers(String layerTemplateDirPath) {
		List<KerasTabInfo> layerTabs = new ArrayList<>();
		File layerTemplateDir = new File(layerTemplateDirPath);
		File layerDirs[] = layerTemplateDir.listFiles();
		for (File layerDir : layerDirs) {
			if (FileUtil.exists(layerDir) && layerDir.isDirectory()) {
				KerasTabInfo layerInfo = new KerasTabInfo();
				File files[] = layerDir.listFiles();
				List<String> layers = new ArrayList<>();
				for (File file : files) {
					// JSONファイルの読み込み
					layers.add(FileUtil.readAll(file.getPath()));
				}
				if (layers != null && !layers.isEmpty()) {
					layerInfo.setTabName(layerDir.getName());
					layerInfo.setLayers(layers);
					layerTabs.add(layerInfo);
				}
			}
		}
		return layerTabs;
	}

	/**
	 * datasetのリストを取得する
	 * 
	 * @return
	 */
	public Map<String, String> loadDatasetList() {
		Map<String, String> map = new HashMap<>();

		// 作業領域パス
		String datasetDirPath = PropUtil.getValue("dataset.directory.path");
		File datasetRootDir = new File(datasetDirPath);

		File datasetDirs[] = datasetRootDir.listFiles();
		for (File datasetDir : datasetDirs) {
			if (datasetDir.isDirectory()) {
				map.put(datasetDir.getName(), datasetDir.getName());
			}
		}
		return map;
	}

	/**
	 * データセットディレクトリを圧縮して保存先を返す
	 * 
	 * @param workspaceModelName
	 * @return
	 */
	public String downloadDataset(String workspaceModelName) {
		String result = "";
		// Datasetのディレクトリパスを取得する
		String modelDirPath = PropUtil.getValue("workspace.local.keras.directory.path") + workspaceModelName;
		String datasetDirPath = PropUtil.getValue("dataset.directory.path") + FileUtil.getDatasetLink(modelDirPath);
		if (StringUtil.isNotEmpty(datasetDirPath) && new File(datasetDirPath).exists()) {
			// データセットディレクトリが存在する
			result = "/tmp/dataset.zip";
			boolean compressResult = FileUtil.compressDirectory(result, datasetDirPath);
			if (!compressResult) {
				logger.error("Failed to compress dataset Directory.");
				result = "";
			}
		}
		return result;
	}

	/**
	 * データセットファイルをアップロードする
	 * 
	 * @param datasetFile
	 */
	public void uploadDataset(MultipartFile datasetFile) {
		// Uploadされたファイルを保存する
		File savedFile = new File("/tmp/datasetUpload.zip");
		if (savedFile.exists()) {
			savedFile.delete();
		}
		FileUtil.saveUploadFile(datasetFile, savedFile);

		// ZIPファイルの上位ディレクトリを取得する
		String datasetName = FileUtil.getRootDirNameZipFile(savedFile.getPath());
		if (StringUtil.isEmpty(datasetName)) {
			return;
		}

		// Datasetディレクトリ
		String datasetDirPath = PropUtil.getValue("dataset.directory.path") + datasetName;
		File datasetDir = new File(datasetDirPath);
		if (FileUtil.exists(datasetDir)) {
			FileUtil.deleteDirectory(datasetDir);
		}

		// ZIPファイルを解凍する
		logger.info("unzip dataset file file[" + savedFile.getPath() + "]datasetDir[" + datasetDirPath + "]");
		boolean result = FileUtil.unzipFile(savedFile.getPath(), PropUtil.getValue("dataset.directory.path"));
		if (!result) {
			// 解凍失敗
			logger.error("Failed to unzip dataset file.");
		}
	}

	/**
	 * 指定されたDNNファイルをダウンロードする
	 * 
	 * @param dnnModelName  DNNモデル名
	 * @param fileExtention 拡張子
	 */
	public boolean downloadDnnFiles(String dnnModelName, String fileExtention) {
		boolean downloadResult = false;
		// KerasEditorのURL
		String url = PropUtil.getValue("airgraph.keras_editor.server.uri");
		if (StringUtil.equals(url, "localhost:8080")) {
			// 単一マシンで実行している場合は除外
			return true;
		}
		String downloadDnnUrl = "http://" + url + "main/getDnnFiles";

		// モデルファイルの取得
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("dnnModelName", dnnModelName);
		map.add("fileExtentions", fileExtention);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		// ここでPOSTリクエスト実行
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Resource> result = restTemplate.postForEntity(downloadDnnUrl, request, Resource.class);
		HttpStatus responseHttpStatus = result.getStatusCode();
		try {
			if (responseHttpStatus.equals(HttpStatus.OK) && result.getBody() != null
					&& result.getBody().getInputStream() != null) { // 200
				InputStream inputStream = result.getBody().getInputStream();
				InputStreamReader reader = new InputStreamReader(inputStream);

				if (reader.ready()) {
					// 保存する
					String kerasDirPath = PropUtil.getValue("workspace.local.keras.directory.path") + dnnModelName;
					// ディレクトリの作成
					FileUtil.createDirectory(kerasDirPath);
					// ファイルの保存
					String filePath = kerasDirPath + "/" + dnnModelName + "." + fileExtention;
					FileUtil.deleteFile(filePath);
					File file = new File(filePath);
					FileUtil.saveInputStream(inputStream, file);
					downloadResult = true;
				}
			}
		} catch (Exception e) {
			logger.error("例外発生:", e);
		}
		return downloadResult;
	}

	/**
	 * ロボットの選択肢を取得する
	 * 
	 * @return
	 */
	public Map<String, String> getRobotChoices() {
		Map<String, String> result = new HashMap<>();

		String robotHostStr = PropUtil.getValue("airgraph.rtm_editor.server.uri");
		String[] robotHosts = robotHostStr.split(",");
		for (String host : robotHosts) {
			result.put(host, host);
		}
		return result;
	}

	/**
	 * 指定されたロボットのデータセットの一覧を取得する
	 * @param robotHostName
	 * @return
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public Map<String, String> getRobotDatasetChoices(String robotHostName) {
		// KerasEditorのURL
		String url = robotHostName;
		if (StringUtil.equals(url, "localhost:8080")) {
			// 単一マシンで実行している場合は除外
			return loadDatasetList();
		}
		String getDatasetChoicesUrl = "http://" + url + "main/getDatasetChoices";

		// モデルファイルの取得
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("robotHostName", robotHostName);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		// ここでPOSTリクエスト実行
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Map> result = restTemplate.postForEntity(getDatasetChoicesUrl, request, Map.class);
		return result.getBody();
	}

	/**
	 * 指定されたデータセットディレクトリのデータを取得する
	 * 
	 * @param datasetName
	 * @param targetDate
	 * @return
	 */
	public boolean compressDatasets(String datasetName, String targetDate) {
		// データセットのディレクトリ
		String datasetDir = PropUtil.getValue("dataset.directory.path") + datasetName;
		FileUtil.deleteFile("/tmp/dataset.zip");
		return FileUtil.compressDirectory("/tmp/dataset.zip", datasetDir);
	}

	/**
	 * 指定されたデータセットをダウンロードする
	 * 
	 * @param robotHostName
	 * @param datasetName
	 * @param targetDate
	 */
	public boolean downloadDatasets(String robotHostName, String datasetName, String targetDate) {
		boolean downloadResult = false;
		// RtmEditorのURL
		// if (StringUtil.equals(robotHostName, "localhost:8080")) {
		// 	// 単一マシンで実行している場合は除外
		// 	return true;
		// }
		String downloadDnnUrl = "http://" + robotHostName + "/keras/downloadDatasets";

		// モデルファイルの取得
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("datasetName", datasetName);
		map.add("targetDate", targetDate);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		// ここでPOSTリクエスト実行
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Resource> result = restTemplate.postForEntity(downloadDnnUrl, request, Resource.class);
		HttpStatus responseHttpStatus = result.getStatusCode();
		try {
			if (responseHttpStatus.equals(HttpStatus.OK) && result.getBody() != null
					&& result.getBody().getInputStream() != null) { // 200
				InputStream inputStream = result.getBody().getInputStream();
				InputStreamReader reader = new InputStreamReader(inputStream);

				if (reader.ready()) {
					// 保存する
					String datasetDirPath = PropUtil.getValue("dataset.directory.path");
					String filePath = "/tmp/tmp.zip";
					FileUtil.deleteFile(filePath);
					File file = new File(filePath);
					FileUtil.saveInputStream(inputStream, file);
					// 解答する
					FileUtil.unzip(filePath, datasetDirPath);
					downloadResult = true;
				}
			}
		} catch (Exception e) {
			logger.error("例外発生:", e);
		}
		return downloadResult;
	}
}
