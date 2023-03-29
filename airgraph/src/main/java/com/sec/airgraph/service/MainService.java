package com.sec.airgraph.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sec.airgraph.util.CollectionUtil;
import com.sec.airgraph.util.Const;
import com.sec.airgraph.util.FileUtil;
import com.sec.airgraph.util.HttpUtil;
import com.sec.airgraph.util.PropUtil;
import com.sec.airgraph.util.RtcUtil;
import com.sec.airgraph.util.StringUtil;
import com.sec.airgraph.util.WasanbonUtil;
import com.sec.airgraph.util.YamlUtil;
import com.sec.keras.entity.model.KerasModel;
import com.sec.rtc.entity.BuildRunDTO;
import com.sec.rtc.entity.field.ComponentFieldInfo;
import com.sec.rtc.entity.field.ComponentTabInfo;
import com.sec.rtc.entity.rts.Rts;
import com.sec.rtc.entity.yaml.AirGraphHostYaml;
import com.sec.rtc.entity.yaml.GitHubSetting;
import com.sec.rtc.entity.yaml.HostSettingYaml;
import com.sec.version.entity.version.WasanbonVersion;


/**
 * AirGraphメインサービス RTM-Editor.
 *
 * @author Tsuyoshi Hirose
 *
 */
@Service
public class MainService {

	/**
	 * logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(MainService.class);

	/**
	 * Wasanbon管理サービス.
	 */
	@Autowired
	private WasanbonManagementService wasanbonManagementService;

	/**
	 * コンポーネント領域管理サービス.
	 */
	@Autowired
	private FieldManagementService fieldManagementService;

	/**
	 * RTC管理サービス.
	 */
	@Autowired
	private RtcManagementService rtcManagementService;

	/**
	 * Keras管理サービス.
	 */
	@Autowired
	private KerasManagementService kerasManagementService;

	/**
	 * IDE管理サービス.
	 */
	@Autowired
	private IdeManagementService ideManagementService;
	
	/**
	 * Component情報を全て取得する.
	 *
	 * @param hostId ホストID
	 * @return Component情報
	 */
	public ComponentFieldInfo loadAllComponentArea(String hostId) {
		
		// Wasanbonにてすべてのリポジトリを展開する
		cloneWasanbonRepositry(hostId);

		// コンポーネント領域情報を取得する
		return getComponentFieldInfo();
	}

	/**
	 * Binder定義を元にすべてのリポジトリを展開する.
	 *
	 * @param hostId ホストID
	 */
	private void cloneWasanbonRepositry(String hostId) {

		// Binderの一覧を取得する
		Map<String, Map<String, String>> binderMap = wasanbonManagementService.getBinderList(hostId);

		if (CollectionUtil.isNotEmpty(binderMap)) {
			// すべてのPackageをCloneする
			wasanbonManagementService.cloneAllPackages(hostId);

			// すべてのRTCをCloneする
			for (Map<String, String> binders : binderMap.values()) {
				rtcManagementService.cloneRtcsFromBinder(binders.get("url"));
			}
		}
	}

	/**
	 * コンポーネント領域情報を取得する.
	 *
	 * @return コンポーネント領域情報
	 */
	private ComponentFieldInfo getComponentFieldInfo() {
		logger.info("Start create component area.");
		// コンポーネント領域情報取得
		ComponentFieldInfo componentFieldInfo = new ComponentFieldInfo();
		List<ComponentTabInfo> componentTabs = new ArrayList<ComponentTabInfo>();

		// 新規生成タブ
		ComponentTabInfo newTab = fieldManagementService.createNewRtcComponentTab();
		if (newTab != null) {
			componentTabs.add(newTab);
		}

		// Packageタブ
		ComponentTabInfo packageTab = fieldManagementService.createRtsPackageComponentTab();
		if (packageTab != null) {
			componentTabs.add(packageTab);
		}

		// Rtcタブ
		ComponentTabInfo rtcTab = fieldManagementService.createRtcComponentTab();
		if (rtcTab != null) {
			componentTabs.add(rtcTab);
	    }

		componentFieldInfo.setComponentTabs(componentTabs);
		logger.info("Finish create component area.");
		return componentFieldInfo;
	}

	/**
	 * 作業領域のすべてのPackageを読み込む.
	 *
	 * @return 作業領域のすべてのPackage情報
	 */
	public List<Rts> loadAllPackagesWorkspace() {
		return wasanbonManagementService.loadAllPackagesWorkspace();
	}

	/**
	 * 指定された作業領域のPackageを読み込む.
	 *
	 * @param workPackageName パッケージ名
	 * @return 作業領域のPackage情報
	 */
	public Rts loadPackageWorkspace(String workPackageName) {
		// 作業領域のPackageを読み込む
		return wasanbonManagementService.loadPackageWorkspace(workPackageName);
	}

	/**
	 * 指定されたPackageを指定された作業領域にCloneし、読み込む.
	 *
	 * @param workPackageName パッケージ名
	 * @param rtsName パッケージ名 
	 * @param newId ID
	 * @param newSAbstract newSAbstract
	 * @param newVersion バージョン
	 * @param newRemoteUrl リモートリポジトリURL
	 * @param newPackageName パッケージ名
	 * @param hostId ホストID
	 * @return 展開されたPackage情報
	 */
	public Rts clonePackageToWorkspace(String workPackageName, String rtsName, String newId, String newSAbstract,
			String newVersion, String newRemoteUrl, String newPackageName, String hostId) {
		if (StringUtil.equals("blank", rtsName)) {
			// 新規パッケージを生成する
			wasanbonManagementService.createNewPackageToWorkspace(workPackageName, rtsName, newId, newSAbstract,
					newVersion, newRemoteUrl, newPackageName, hostId);
		} else {
			// 作業領域に展開する
			wasanbonManagementService.clonePackageToWorkspace(workPackageName, rtsName, newId, newSAbstract, newVersion,
					newRemoteUrl, newPackageName, hostId);
		}

		// 展開されたPackageを読み込む
		return loadPackageWorkspace(newPackageName);
	}

	/**
	 * 編集したPackageを作業領域に保存する.
	 *
	 * @param workspaceData パッケージ情報
	 */
	public void updatePackage(String workspaceData) {
		rtcManagementService.updatePackage(workspaceData);
	}

	/**
	 * 指定されたPackageを削除する.
	 *
	 * @param packageRepositoryName パッケージ名
	 */
	public void deletePackage(String packageRepositoryName) {
		// 指定されたPackageを削除する
		wasanbonManagementService.deletePackage(packageRepositoryName);
	}

	/**
	 * 作業領域に指定されたRtcを追加する.
	 *
	 * @param workPackageName パッケージ名
	 * @param componentName コンポーネント名 
	 * @param gitName git名
	 * @param clonedDirectory クローンされたディレクトリ
	 */
	public void addComponent(String workPackageName, String componentName, String gitName, String clonedDirectory) {
		// 既存コンポーネント
		wasanbonManagementService.addComponent(workPackageName, componentName, gitName, clonedDirectory);
	}

	/**
	 * 作業領域に新規Rtcを追加する.
	 *
	 * @param componentData コンポーネント情報
	 */
	public void createNewComponent(String componentData) {
		// 新規コンポーネント
		wasanbonManagementService.createNewComponent(componentData);
	}

	/**
	 * 指定されたデータ型からロガー用コンポーネントを作業領域に追加する.
	 *
	 * @param workPackageName パッケージ名
	 * @param id ID
	 * @param instanceName インスタンス名
	 * @param portName ポート名
	 * @param pathUri パスURI
	 * @param dataType データタイプ
	 */
	public void addLoggerComponent(String workPackageName, String id, String instanceName, String portName,
			String pathUri, String dataType) {
		wasanbonManagementService.addLoggerComponent(workPackageName, id, instanceName, portName, pathUri, dataType);
	}

	/**
	 * 指定されたRtcを作業領域から削除する.
	 *
	 * @param workPackageName パッケージ名
	 * @param id ID
	 * @param componentName コンポーネント名
	 */
	public void deleteComponent(String workPackageName, String id, String componentName) {
		wasanbonManagementService.deleteComponent(workPackageName, id, componentName);
	}

	/**
	 * 指定されたデータ型がロギング可能な型かを調べる.
	 *
	 * @param dataType データタイプ 
	 */
	public Boolean canLogging(String dataType) {
		if (StringUtil.isNotEmpty(RtcUtil.getComponentNameByDataType(dataType))) {
			return true;
		}
		return false;
	}

	/**
	 * パッケージ名が競合していないかを調べる.
	 *
	 * @param packageName  パッケージ名
	 * @param hostId ホストID
	 * @return パッケージ名が競合していないか
	 */
	public Boolean checkAvailablePackageName(String packageName, String hostId) {
		// Packageの一覧を取得
		List<String> packagesList = WasanbonUtil.getPackagesListFromBinder(hostId);
		if (CollectionUtil.isNotEmpty(packagesList) && packagesList.contains(packageName)) {
			return false;
		}
		return true;
	}

	/**
	 * RTC名が競合していないかを調べる.
	 *
	 * @param workPackageName パッケージ名
	 * @param componentName コンポーネント名
	 * @param hostId ホストID
	 * @return 利用可能かどうか
	 */
	public Boolean checkAvailableComponentName(String workPackageName, String componentName, String hostId) {

		String packageName = workPackageName.replaceAll("rts_", "");
		
		// すでに組み込まれていないかの確認をする
		List<String> curRtcList = WasanbonUtil.getRtcsListFromPackage(packageName);
		List<String> componentList = WasanbonUtil.getRtcsListFromBinder(hostId);
		if ((CollectionUtil.isNotEmpty(componentList) && componentList.contains(componentName))
				|| (CollectionUtil.isNotEmpty(curRtcList) && curRtcList.contains(componentName))) {
			return false;
		}
		return true;
	}

	/**
	 * デプロイする.
	 *
	 * @param hostId ホスト名
	 * @param ws ワークスペース名
	 * @param remoteRepositoryUrl リモートリポジトリのURL
	 * @param commitHash コミットハッシュ
	 * @return HTTP Status Code
	 */
	public int deploy(String hostId, String ws, String remoteRepositoryUrl, String commitHash) {
		String url = WasanbonUtil.getUrl(hostId, "deploy", ws, "packages", "deploy");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		Map<String, String> body = new HashMap<String, String>();
		body.put("rts_git_url", remoteRepositoryUrl);
		body.put("commit_hash", commitHash);
		
		HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
		
		ResponseEntity<String> response = HttpUtil.sendHttpRequestAndGetResponse(url, HttpMethod.POST, entity, String.class);
		return response.getStatusCodeValue();

	}
	
	/**
	 * 指定されたPackageのすべてのRtcをビルドする.
	 *
	 * @param buildRunDto ビルドに必要な情報
	 */
	public void buildPackageAll(BuildRunDTO buildRunDto) {
		wasanbonManagementService.buildPackageAll(buildRunDto);
	}

	/**
	 * 指定されたPackageのすべてのRtcをcleanする.
	 *
	 * @param buildRunDto クリーンに必要な情報
	 */
	public void cleanPackageAll(BuildRunDTO buildRunDto) {
		wasanbonManagementService.cleanPackageAll(buildRunDto);
	}

	/**
	 * 指定されたPackageのSystemを実行する.
	 *
	 * @param buildRunDto 実行に必要な情報
	 */
	public void runSystem(BuildRunDTO buildRunDto) {
		wasanbonManagementService.runSystem(buildRunDto);
	}
	
	/**
	 * スタートする.
	 *
	 * @param buildRunDto 実行に必要な情報
	 */
	public void startRtcs(BuildRunDTO buildRunDto) {
		wasanbonManagementService.startRtcs(buildRunDto);
	}
	
	/**
	 * コネクトする.
	 *
	 * @param buildRunDto 実行に必要な情報
	 */
	public void connectPorts(BuildRunDTO buildRunDto) {
		wasanbonManagementService.connectPorts(buildRunDto);
	}
	
	/**
	 * アクティベイトかディアクティベイトをする.
	 *
	 * @param buildRunDto 実行に必要な情報
	 * @param isActivate アクティベイトかどうか
	 */
	public void activateOrDeactivateRtcs(BuildRunDTO buildRunDto, boolean isActivate) {
		wasanbonManagementService.activateOrDeactivateRtcs(buildRunDto, isActivate);
	}

	/**
	 * 指定されたPackageのSystemを停止する.
	 *
	 * @param buildRunDto 実行に必要な情報
	 */
	public void terminateSystem(BuildRunDTO buildRunDto) {
		wasanbonManagementService.terminateSystem(buildRunDto);
	}

	/**
	 * Packageの実行状況を確認する.
	 *
	 * @param ws ワークスペース名
	 * @param rtsName パッケージ名
	 * @param hostId ホストID
	 * @return Packageの実行状況
	 */
	public Map<String, Object> isRunningPackage(String ws, String rtsName, String hostId) {
		return wasanbonManagementService.isRunningPackage(ws, rtsName, hostId);
	}

	/**
	 * ログを読み込む.
	 *
	 * @param ws ワークスペース名
	 * @param workPackageName パッケージ名
	 * @param hostId ホストID
	 * @return ログファイル
	 */
	public byte[] tailLog(String ws, String workPackageName, String hostId) {
		
		// packageNameを取得
		workPackageName = workPackageName.replace("rts_", "");
		if (ws.equals("exec")) {
			workPackageName = "exec_" + workPackageName;
		}

		String url = WasanbonUtil.getUrl(hostId, ws, "packages", workPackageName, "rts", "logs");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<byte[]> responseEntity = HttpUtil.sendHttpRequestAndGetResponse(url, HttpMethod.GET, entity, byte[].class);
		return responseEntity.getBody();
	}
	
	/**
	 * 実行時のwasanbon.log取得.
	 *
	 * @return ログファイル
	 */
	public String getExecuteWasanbonLog() {
		
		String filePath = PropUtil.getValue("workspace.execute.wasanbon.logFile.path");

		return FileUtil.readAll(filePath);
	}

	/**
	 * 実行時のwasanbon.logをクリアする.
	 *
	 */
	public void clearExecuteWasanbonLog() {
		String filePath = PropUtil.getValue("workspace.execute.wasanbon.logFile.path");

		FileUtil.createBackup(filePath);
		FileUtil.deleteFile(filePath);
	}

	/**
	 * 対象のディレクトリにある一番新しい画像ファイルを取得する.
	 *
	 * @param imageDirectoryPath 画像ファイルのパス
	 * @param targetExtension 対象とする拡張子
	 * @return 画像ファイル
	 */
	public byte[] tailImage(String imageDirectoryPath, List<String> targetExtension) {
		// 対象とする拡張子
		List<String> extensions = new ArrayList<>();
		extensions.add("png");
		extensions.add("jpg");
		extensions.add("jpeg");

		File imageFile = FileUtil.getLatestUpdateFile(imageDirectoryPath, extensions);
		if (FileUtil.exists(imageFile)) {
			return getImage(imageFile, targetExtension);
		} else {
			logger.warn("対象の画像ファイルが存在しない.imageFilePath[" + imageDirectoryPath + "]");
		}
		return null;
	}

	/**
	 * 対象のディレクトリにある一番新しい画像ファイルを取得する.
	 *
	 * @param imageFile 画像ファイル
	 * @param targetExtension 対象とする拡張子
	 * @return 画像ファイル
	 */
	public byte[] getImage(File imageFile, List<String> targetExtension) {
		byte[] result = null;

		// 対象とする拡張子
		List<String> extensions = new ArrayList<>();
		extensions.add("png");
		extensions.add("jpg");
		extensions.add("jpeg");

		if (!extensions.contains(FileUtil.getFileExtension(imageFile.getPath()).toLowerCase())) {
			return null;
		}

		if (FileUtil.exists(imageFile)) {
			// BufferedImageへ設定
			BufferedImage bufferedImage = null;
			try {
				String extension = FileUtil.getFileExtension(imageFile.getPath().toLowerCase());
				targetExtension.add(extension);
				bufferedImage = ImageIO.read(imageFile);
				// byteに変換
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				ImageIO.write(bufferedImage, extension, bout);
				result = bout.toByteArray();
			} catch (Exception ex) {
				logger.error("例外発生. :", ex);
			}
		}
		return result;
	}

	/**
	 * 実行結果ファイルを取得する.
	 *
	 * @return 実行結果ファイル
	 */
	public Boolean createResultFile() {
		// 結果格納ディレクリ
		String resultDirPath = PropUtil.getValue("result.local.directory.path");

		// 結果格納ファイル
		String resultFilePath = PropUtil.getValue("result.local.file.path");

		return FileUtil.compressDirectory(resultFilePath, resultDirPath);
	}

	/**
	 * RTSをローカルリポジトリにCommitする.
	 *
	 * @param workPackageName パッケージ名
	 * @param packageName パッケージ名
	 * @param commitMessage コミットメッセージ
	 * @return コミット結果
	 */
	public String commitPackage(String workPackageName, String packageName, String commitMessage) {
		return ideManagementService.commitPackage(workPackageName, packageName, commitMessage);
	}

	/**
	 * RTSをリモートリポジトリにPushする.
	 *
	 * @param workPackageName パッケージ名
	 * @param packageName パッケージ名
	 * @param commitMessage コミットメッセージ
	 * @param userName GitHubユーザー名
	 * @param password パスワード
	 * @return push結果
	 */
	public String pushPackage(String workPackageName, String packageName, String commitMessage, String userName, String password) {
		StringBuilder sb = new StringBuilder();

		String rsltCommit = ideManagementService.commitPackage(workPackageName, packageName, commitMessage);
		String rsltPush = ideManagementService.pushPackage(workPackageName, packageName, userName, password);

		if (StringUtil.isNotEmpty(rsltCommit)) {
			sb.append(rsltCommit);
		}
		if (StringUtil.isNotEmpty(rsltPush)) {
			if (sb.length() > 0) {
				sb.append(System.lineSeparator());
			}
			sb.append(rsltPush);
		}
		return sb.toString();
	}

	/**
	 * RTSをリモートリポジトリからPullする.
	 *
	 * @param workPackageName パッケージ名
	 * @param userName ユーザー名
	 * @param password パスワード
	 * @return pull結果
	 */
	public String pullPackage(String workPackageName, String userName, String password) {
		return ideManagementService.pullPackage(workPackageName, userName, password);
	}

	/**
	 * RTCをローカルリポジトリにCommitする.
	 *
	 * @param workPackageName パッケージ名
	 * @param componentName コンポーネント名
	 * @param commitMessage コミットメッセージ
	 * @return コミット結果
	 */
	public String commitComponent(String workPackageName, String componentName, String commitMessage) {
		return ideManagementService.commitComponent(workPackageName, componentName, commitMessage);
	}

	/**
	 * RTCをリモートリポジトリにPushする.
	 *
	 * @param workPackageName パッケージ名
	 * @param componentName コンポーネント名
	 * @param commitMessage コミットメッセージ
	 * @param userName GitHubユーザー名
	 * @param password パスワード
	 * @return push結果
	 */
	public String pushComponent(String workPackageName, String componentName, String commitMessage, String userName,
			String password) {
		StringBuilder sb = new StringBuilder();

		String rsltCommit = ideManagementService.commitComponent(workPackageName, componentName, commitMessage);
		String rsltPush = ideManagementService.pushComponent(workPackageName, componentName, userName, password);

		if (StringUtil.isNotEmpty(rsltCommit)) {
			sb.append(rsltCommit);
		}
		if (StringUtil.isNotEmpty(rsltPush)) {
			if (sb.length() > 0) {
				sb.append(System.lineSeparator());
			}
			sb.append(rsltPush);
		}
		return sb.toString();
	}

	/**
	 * RTCをリモートリポジトリからPullする.
	 *
	 * @param workPackageName パッケージ名
	 * @param componentName コンポーネント名
	 * @param userName GitHubユーザー名
	 * @param password パスワード
	 * @return pull結果
	 */
	public String pullComponent(String workPackageName, String componentName, String userName, String password) {
		return ideManagementService.pullComponent(workPackageName, componentName, userName, password);
	}
	
	/**
	 * コミットハッシュを取得する.
	 *
	 * @param packageName パッケージ名
	 * @return コミットハッシュ
	 */
	public String getCommitHash(String packageName) {
		return ideManagementService.getCommitHash(packageName);
	}
	
	/**
	 * packageのstatusを確認する.
	 *
	 * @param ws ワークスペース名
	 * @param rtsName パッケージ名
	 * @return packageのstatus
	 */
	public String checkPackageStatus(String ws, String rtsName) {
		return ideManagementService.checkPackageStatus(ws, rtsName);
	}
	
	/**
	 * RTCのstatusを確認する.
	 *
	 * @param ws ワークスペース名
	 * @param rtsName パッケージ名
	 * @return RTCのstatus
	 */
	public String checkRtcsStatus(String ws, String rtsName) {
		return ideManagementService.checkRtcsStatus(ws, rtsName);
	}
	
	/**
	 * nameserverのstatusを確認する.
	 *
	 * @param hostId ホストID
	 * @return nameserverのstatus
	 */
	public boolean checkNameserverStatus(String hostId) {
		return wasanbonManagementService.checkNameserverStatus(hostId);
	}

	/**
	 * Kerasの選択肢を取得する.
	 *
	 * @return Kerasの選択肢
	 */
	public Map<String, String> getKerasModelChoices() {
		Map<String, String> ret = new HashMap<>();
		// モデル情報保存先パス
		String modelDirPath = PropUtil.getValue("workspace.local.keras.directory.path");

		// Kerasのモデル一覧を取得する
		List<KerasModel> models = kerasManagementService.loadAllKerasModels(modelDirPath, false);
		if (CollectionUtil.isNotEmpty(models)) {
			for (KerasModel model : models) {
				ret.put(model.getModelName(), model.getModelName());
			}
		}

		return ret;
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
	 * 指定されたDNNモデル名に関連するファイルをダウンロードする.
	 *
	 * @param dnnModelName DNNモデル名
	 * @param pathUri コンポーネントのpathUri
	 * @return DNNモデル名に関連するファイル
	 */
	public boolean downloadDnnFiles(String dnnModelName, String pathUri) {
		boolean result = kerasManagementService.downloadDnnFiles(dnnModelName, pathUri, "json");
		if (result) {
			result = kerasManagementService.downloadDnnDataMakerFiles(dnnModelName, pathUri);
		}
		if (result) {
			result = kerasManagementService.downloadDnnFiles(dnnModelName, pathUri, "hdf5");
		}
		return result;
	}
	
	/**
	 * ホスト定義ファイルを参照し、ホスト定義ファイルに新規登録する.
	 *
	 * @param id ホストID
	 * @param password パスワード
	 * @param hostName ホスト名
	 * @param ip IPアドレス
	 * @param nsport ネームサーバーのポート番号
	 * @param wwport wasanbon-webframeworkのポート番号
	 * @return ファイルに書き込みできたかどうか
	 */
	public boolean registerHostToConfigFile(String id, String password, String hostName, String ip, String nsport, String wwport) {
		// ファイルパスを指定
		String filepath = PropUtil.getValue("hosts.setting.dir") + Const.COMMON.FILE_NAME.WASANBON_HOST_CONFIG;
		// Hostオブジェクトを作成する
		List<HostSettingYaml> hosts = new ArrayList<HostSettingYaml>();
		hosts = YamlUtil.loadYamlFromFile(hosts.getClass(), filepath);
		if (hosts == null) {
			hosts = new ArrayList<HostSettingYaml>();
		}

		HostSettingYaml newHost = new HostSettingYaml();
		
		newHost.setId(id);
		newHost.setHostName(hostName);
		newHost.setIp(ip);
		newHost.setNsport(nsport);
		newHost.setWwport(wwport);
		newHost.setPassword(password);
		
		hosts.add(newHost);

		// ホスト定義ファイルに新規登録する
		return YamlUtil.dampDataToYamlFile(hosts, filepath);
	}

	/**
	 * AirGraph用ホストを追加する.
	 *
	 * @param hostname ホスト名
	 * @param ip IPアドレス
	 * @param port ネームサーバーのポート番号
	 * @return ファイルに書き込みできたかどうか
	 */
	public boolean addAirGraphHost(String hostName, String ip, String port) {
		// ファイルパスを指定
		String filepath = PropUtil.getValue("hosts.setting.dir") + Const.COMMON.FILE_NAME.AIRGRAPH_HOST_CONFIG;
		// Hostオブジェクトを作成する
		List<AirGraphHostYaml> hosts = new ArrayList<AirGraphHostYaml>();
		hosts = YamlUtil.loadYamlFromFile(hosts.getClass(), filepath);
		if (hosts == null) {
			hosts = new ArrayList<AirGraphHostYaml>();
		}

		// IDを生成する
		int id = hosts.size();
		AirGraphHostYaml newHost = new AirGraphHostYaml();
		
		newHost.setId(Integer.valueOf(id).toString(id));
		newHost.setHostName(hostName);
		newHost.setIp(ip);
		newHost.setPort(port);
		
		hosts.add(newHost);

		// ホスト定義ファイルに新規登録する
		return YamlUtil.dampDataToYamlFile(hosts, filepath);
	}
	
	/**
	 * ホスト名、または(ip, Port)が重複しないか判定する.
	 *
	 * @param hostName ホスト名
	 * @param ip IPアドレス
	 * @param port ネームサーバーのポート番号
	 * @return 重複しないかどうか
	 */
	public boolean isHostNameUnique(boolean isWasanbon, String hostName, String ip, String port) {
		// ファイルパスを指定
		if (isWasanbon) {
			String filepath = PropUtil.getValue("hosts.setting.dir") + Const.COMMON.FILE_NAME.WASANBON_HOST_CONFIG;
			// Hostオブジェクトを作成する
			List<HostSettingYaml> hosts = new ArrayList<HostSettingYaml>();
			hosts = YamlUtil.loadYamlFromFile(hosts.getClass(), filepath);
			if (hosts == null) {
				return true;
			}
			for (HostSettingYaml host : hosts) {
				if (host.getHostName().equals(hostName)) {
					return false;
				} else if (host.getIp().equals(ip) && host.getNsport().equals(port)) {
					return false;
				}
			}
			return true;
			
		} else {

			String filepath = PropUtil.getValue("hosts.setting.dir") + Const.COMMON.FILE_NAME.AIRGRAPH_HOST_CONFIG;
			// Hostオブジェクトを作成する
			List<AirGraphHostYaml> hosts = new ArrayList<AirGraphHostYaml>();
			hosts = YamlUtil.loadYamlFromFile(hosts.getClass(), filepath);
			if (hosts == null) {
				return true;
			}
			for (AirGraphHostYaml host : hosts) {
				if (host.getHostName().equals(hostName)) {
					return false;
				} else if (host.getIp().equals(ip) && host.getPort().equals(port)) {
					return false;
				}
			}
			return true;
		}
	}
	
	/**
	 * ホスト名、または(ip, Port)が重複しないか判定する.
	 *
	 * @param id ホストID
	 * @return 重複しないかどうか
	 */
	public boolean isHostIdUnique(String id) {
		// ファイルパスを指定
		String filepath = PropUtil.getValue("hosts.setting.dir") + Const.COMMON.FILE_NAME.WASANBON_HOST_CONFIG;
		// Hostオブジェクトを作成する
		List<HostSettingYaml> hosts = new ArrayList<HostSettingYaml>();
		if (YamlUtil.loadYamlFromFile(hosts.getClass(), filepath) == null) {
			return true;
		}
		hosts = YamlUtil.loadYamlFromFile(hosts.getClass(), filepath);
		for (HostSettingYaml host : hosts) {
			if (host.getId().equals(id)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * ホスト定義ファイルを参照し、ホスト定義ファイルを更新する.
	 *
	 * @param json ホスト情報
	 * @return ホストファイルに書き込みできたかどうか
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	public boolean updateHostConfigFile(ArrayList<ArrayList<Object>> hosts) throws JsonMappingException, JsonProcessingException {
		
		// ファイルパスを指定
		String w_filepath = PropUtil.getValue("hosts.setting.dir") + Const.COMMON.FILE_NAME.WASANBON_HOST_CONFIG;
		String a_filepath = PropUtil.getValue("hosts.setting.dir") + Const.COMMON.FILE_NAME.AIRGRAPH_HOST_CONFIG;
		// Hostオブジェクトを作成する
		List<HostSettingYaml> w_oldHosts = new ArrayList<HostSettingYaml>();
		List<HostSettingYaml> w_newHosts = new ArrayList<HostSettingYaml>();
		List<AirGraphHostYaml> a_oldHosts = new ArrayList<AirGraphHostYaml>();
		List<AirGraphHostYaml> a_newHosts = new ArrayList<AirGraphHostYaml>();
		w_oldHosts = (List<HostSettingYaml>) YamlUtil.loadYamlFromFile(w_oldHosts.getClass(), w_filepath);
		a_oldHosts = (List<AirGraphHostYaml>) YamlUtil.loadYamlFromFile(a_oldHosts.getClass(), a_filepath);
		
		ObjectMapper mapper = new ObjectMapper();
		List<HostSettingYaml> w_requestDatas = mapper.convertValue(hosts.get(0), new TypeReference<List<HostSettingYaml>>() {});
		List<AirGraphHostYaml> a_requestDatas = mapper.convertValue(hosts.get(1), new TypeReference<List<AirGraphHostYaml>>() {});

		// ホストの数だけ実施
		for (HostSettingYaml requestHost : w_requestDatas) {

			if (!requestHost.getHostName().isEmpty()) {
				// oldHostsから同じIDのホストをさがす
				for (int i = 0; i < w_oldHosts.size(); i++) {

					if (requestHost.getId().equals(w_oldHosts.get(i).getId())) {
						//見つかったホストのデータを上書きする
						w_oldHosts.get(i).setHostName(requestHost.getHostName());
						w_oldHosts.get(i).setIp(requestHost.getIp());
						w_oldHosts.get(i).setNsport(requestHost.getNsport());
						w_oldHosts.get(i).setWwport(requestHost.getWwport());
						// 見つかったホストをnewHostsに追加する
						w_newHosts.add(w_oldHosts.get(i));
						break;
					}
				}
				
			}
		}
		// ホストの数だけ実施
		for (AirGraphHostYaml requestHost : a_requestDatas) {

			if (!requestHost.getHostName().isEmpty()) {
				// oldHostsから同じIDのホストをさがす
				for (int i = 0; i < a_oldHosts.size(); i++) {

					if (requestHost.getId().equals(a_oldHosts.get(i).getId())) {
						//見つかったホストのデータを上書きする
						a_oldHosts.get(i).setHostName(requestHost.getHostName());
						a_oldHosts.get(i).setIp(requestHost.getIp());
						a_oldHosts.get(i).setPort(requestHost.getPort());
						// 見つかったホストをnewHostsに追加する
						a_newHosts.add(a_oldHosts.get(i));
						break;
					}
				}
				
			}
		}
		// ホスト定義ファイルを更新する
		boolean res1 = YamlUtil.dampDataToYamlFile(w_newHosts, w_filepath);
		boolean res2 = YamlUtil.dampDataToYamlFile(a_newHosts, a_filepath);

		return res1 && res2;
	}
	
	/**
	 *GitHub設定ファイルを読み込む.
	 *
	 * @return GitHub設定ファイル
	 */
	public GitHubSetting getGitHubConfigFile() {
	    
	    logger.info("Get GitHub Config File.");
	    // ファイルパスを指定
	    String filepath = PropUtil.getValue("hosts.setting.dir") + Const.COMMON.FILE_NAME.GITHUB_CONFIG;
	    
	    // githubsettingオブジェクトを生成する
	    GitHubSetting githubUser = new GitHubSetting();
	    
	    githubUser = YamlUtil.loadYamlFromFile(githubUser.getClass(), filepath);
	    
	    return githubUser;
	}
	
	/**
	 *ホスト定義ファイルを読み込む.
	 *
	 * @return ホスト定義ファイル
	 */
	@SuppressWarnings({"unchecked"})
	public List<Object>  loadHostList() {
		// ファイルパスを指定
		String w_filepath = PropUtil.getValue("hosts.setting.dir") + Const.COMMON.FILE_NAME.WASANBON_HOST_CONFIG;
		String a_filepath = PropUtil.getValue("hosts.setting.dir") + Const.COMMON.FILE_NAME.AIRGRAPH_HOST_CONFIG;
		// Hostオブジェクトを作成する
		List<HostSettingYaml> w_hosts = new ArrayList<HostSettingYaml>();
		List<AirGraphHostYaml> a_hosts = new ArrayList<AirGraphHostYaml>();
		ArrayList<Object> res = new ArrayList<Object>();
		w_hosts = YamlUtil.loadYamlFromFile(w_hosts.getClass(), w_filepath);
		a_hosts = YamlUtil.loadYamlFromFile(a_hosts.getClass(), a_filepath);
		// もしローカルのhostが一つも設定されていなければ、localhostを追加する
		if (w_hosts.stream().noneMatch(e -> Const.HOST_CONFIG.LOCALHOST_ID.equals(e.getId()))) {
			HostSettingYaml localhost = new HostSettingYaml();
			localhost.setHostName("localhost");
			localhost.setIp("127.0.0.1");
			localhost.setNsport("2809");
			localhost.setWwport("8000");
			localhost.setId(Const.HOST_CONFIG.LOCALHOST_ID);
			localhost.setPassword("pass");
			w_hosts.add(0, localhost);
			YamlUtil.dampDataToYamlFile(w_hosts, w_filepath);
		}
		if (a_hosts.stream().noneMatch(e -> Const.HOST_CONFIG.LOCALHOST_ID.equals(e.getId()))) {
			AirGraphHostYaml localhost = new AirGraphHostYaml();
			localhost.setHostName("localhost");
			localhost.setIp("127.0.0.1");
			localhost.setPort("8000");
			localhost.setId(Const.HOST_CONFIG.LOCALHOST_ID);
			a_hosts.add(0, localhost);
			YamlUtil.dampDataToYamlFile(a_hosts, a_filepath);
		}
		res.add(w_hosts);
		res.add(a_hosts);
		logger.info(res.toString());
		return res;
	}
	
	/**
	 * Airgraphのバージョンを確認する.
	 *
	 * @return Airgraphのバージョン
	 */
	public String getAirgraphVersion() {
		logger.info("Airgraph Version");
		String version = Const.AIRGRAPH_VERSION.AIRGRAPH_VERSION;
		return version;
	}
	
	/**
	 *binderを作成する.
	 *
	 * @param username GitHubユーザー名
	 * @param token GitHubトークン
	 */
	public void createBinder(String username, String token) {
		String hostId = Const.HOST_CONFIG.LOCALHOST_ID;
		logger.info("Create Binder.");

		String url = WasanbonUtil.getUrl(hostId, "binders");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		Map<String, String> body = new HashMap<String, String>();

		body.put("username", username);
		body.put("token", token);

		HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
		
		HttpUtil.sendHttpRequestAndGetResponse(url, HttpMethod.POST, entity, String.class);
	}
	
	/**
	 * binderを更新する.
	 */
	public void updateBinder() {
		String hostId = Const.HOST_CONFIG.LOCALHOST_ID;
		String url = WasanbonUtil.getUrl(hostId, "binders", "update");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		HttpUtil.sendHttpRequestAndGetResponse(url, HttpMethod.POST, entity, String.class);

	}
	
	/**
	 * Wasanbonのバージョンを確認する.
	 *
	 * @param hostId ホストID
	 * @return バージョン情報
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	public String getWasanbonVersion(String hostId) throws JsonMappingException, JsonProcessingException {
		String url = WasanbonUtil.getUrl(hostId, "version");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<String> response = HttpUtil.sendHttpRequestAndGetResponse(url, HttpMethod.GET, entity, String.class);

		//結果の取得
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<WasanbonVersion> type = new TypeReference<WasanbonVersion>(){};
		WasanbonVersion body = mapper.readValue(response.getBody(), type);
		return body.getWasanbonVersion();
	}
	
	/**
	 * Binderにパッケージを追加する.
	 *
	 * @param packageName パッケージ名
	 * @param binderName バインダー名
	 * @return 実行結果
	 */
	public String addPackageToBinder(String packageName, String binderName) {

		logger.info("Add Package to Binder.");

		return wasanbonManagementService.addPackageToBinder(packageName, binderName);
	}

	/**
	 * Binderのパッケージを更新する.
	 *
	 * @param ws ワークスペース名
	 * @param packageName パッケージ名
	 * @param binderName バインダー名
	 * @return 実行結果
	 */
	public String updatePackageToBinder(String ws, String packageName, String binderName) {

		logger.info("Update Package to Binder.");

		return wasanbonManagementService.updatePackageToBinder(ws, packageName, binderName);
	}

	/**
	 * BinderにRTCを追加する.
	 *
	 * @param ws ワークスペース名
	 * @param packageName パッケージ名
	 * @param rtcName コンポーネント名
	 * @param binderName バインダー名
	 * @return 実行結果
	 */
	public String addRtcToBinder(String ws, String packageName, String rtcName, String binderName) {

		return wasanbonManagementService.addRtcToBinder(ws, packageName, rtcName, binderName);
	}

	/**
	 * BinderのRTCを更新する.
	 *
	 * @param ws ワークスペース名
	 * @param packageName パッケージ名
	 * @param rtcName コンポーネント名
	 * @param binderName バインダー名
	 * @return 実行結果
	 */
	public String updateRtcToBinder(String ws, String packageName, String rtcName, String binderName) {

		return wasanbonManagementService.updateRtcToBinder(ws, packageName, rtcName, binderName);
	}

	/**
	 * Binderをcommitする.
	 *
	 * @param binderName バインダー名
	 * @param comment コミットメッセージ
	 * @return コミット結果
	 */
	public String commitBinder(String binderName, String comment) {

		return wasanbonManagementService.commitBinder(binderName, comment);
	}
	
	/**
	 * Binderをpushする.
	 *
	 * @param binderName バインダー名
	 * @param comment コミットメッセージ
	 * @return コミット結果
	 */
	public String pushBinder(String binderName, String comment) {

		logger.info("Push Binder.");

		return wasanbonManagementService.pushBinder(binderName, comment);
	}

	/**
	 * nameserverを起動する.
	 *
	 * @param hostId ホストID
	 */
	public void startNameserver(String hostId) {

		wasanbonManagementService.startNameserver(hostId);
	}

}
