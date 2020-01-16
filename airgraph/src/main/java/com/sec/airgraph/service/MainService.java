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
import org.springframework.stereotype.Service;

import com.sec.keras.entity.model.KerasModel;
import com.sec.rtc.entity.field.ComponentFieldInfo;
import com.sec.rtc.entity.field.ComponentTabInfo;
import com.sec.rtc.entity.rts.Rts;
import com.sec.airgraph.util.FileUtil;
import com.sec.airgraph.util.PropUtil;
import com.sec.airgraph.util.RtcUtil;
import com.sec.airgraph.util.StringUtil;
import com.sec.airgraph.util.WasanbonUtil;
import com.sec.airgraph.util.CollectionUtil;
import com.sec.airgraph.util.Const.COMMON.DIR_NAME;
import com.sec.airgraph.util.Const.COMMON.FILE_NAME;

/**
 * AirGraphメインサービス
 * RTM-Editor
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Service
public class MainService {

	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(MainService.class);

	/**
	 * Wasanbon管理サービス
	 */
	@Autowired
	private WasanbonManagementService wasanbonManagementService;

	/**
	 * コンポーネント領域管理サービス
	 */
	@Autowired
	private FieldManagementService fieldManagementService;

	/**
	 * RTC管理サービス
	 */
	@Autowired
	private RtcManagementService rtcManagementService;
	
	/**
	 * Keras管理サービス
	 */
	@Autowired
	private KerasManagementService kerasManagementService;
	
	/**
	 * IDE管理サービス
	 */
	@Autowired
	private IdeManagementService ideManagementService;

	/**
	 * Component情報を全て取得する
	 */
	public ComponentFieldInfo loadAllComponentArea() {

		// Wasanbonにてすべてのリポジトリを展開する
		cloneWasanbonRepositry();

		// コンポーネント領域情報を取得する
		return getComponentFieldInfo();
	}

	/**
	 * Binder定義を元にすべてのリポジトリを展開する
	 */
	private void cloneWasanbonRepositry() {
		
		// Binderの一覧を取得する
		Map<String, Map<String, String>> binderMap = wasanbonManagementService.getBinderList();

		if (CollectionUtil.isNotEmpty(binderMap)) {
			// すべてのPackageをCloneする
			wasanbonManagementService.cloneAllPackages();

			// すべてのRTCをCloneする
			for (Map<String, String> binders : binderMap.values()) {
				rtcManagementService.cloneRtcsFromBinder(binders.get("url"));
			}
		}
	}

	/**
	 * コンポーネント領域情報を取得する
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

		// 履歴タブ
		// ComponentTabInfo recentTab = fieldManagementService.createRecentRtcComponentTab();
		// if (recentTab != null) {
		// 	componentTabs.add(recentTab);
		// }

		componentFieldInfo.setComponentTabs(componentTabs);
		logger.info("Finish create component area.");
		return componentFieldInfo;
	}

	/**
	 * 作業領域のすべてのPackageを読み込む
	 * 
	 * @return
	 */
	public List<Rts> loadAllPackagesWorkspace() {
		return wasanbonManagementService.loadAllPackagesWorkspace();
	}

	/**
	 * 指定された作業領域のPackageを読み込む
	 * 
	 * @param workPackageName
	 * @return
	 */
	public Rts loadPackageWorkspace(String workPackageName) {
		// 作業領域のPackageを読み込む
		return wasanbonManagementService.loadPackageWorkspace(workPackageName);
	}

	/**
	 * 指定されたPackageを指定された作業領域にCloneし、読み込む
	 * 
	 * @param workPackageName
	 * @param rtsName
	 * @return
	 */
	public Rts clonePackageToWorkspace(String workPackageName, String rtsName, String newId, String newSAbstract,
			String newVersion, String newRemoteUrl) {
		if (StringUtil.equals("blank", rtsName)) {
			// 新規パッケージを生成する
			wasanbonManagementService.createNewPackageToWorkspace(workPackageName, rtsName, newId, newSAbstract,
					newVersion, newRemoteUrl);
		} else {
			// 作業領域に展開する
			wasanbonManagementService.clonePackageToWorkspace(workPackageName, rtsName, newId, newSAbstract, newVersion,
					newRemoteUrl);
		}

		// 展開されたPackageを読み込む
		return loadPackageWorkspace(workPackageName);
	}

	/**
	 * 編集したPackageを作業領域に保存する
	 * 
	 * @param packageData
	 */
	public void updatePackage(String workspaceData) {
		rtcManagementService.updatePackage(workspaceData);
	}

	/**
	 * 指定されたPackageを削除する
	 * 
	 * @param packageRepositoryName
	 */
	public void deletePackage(String packageRepositoryName) {
		// 指定されたPackageを削除する
		wasanbonManagementService.deletePackage(packageRepositoryName);
	}

	/**
	 * 作業領域に指定されたRtcを追加する
	 * 
	 * @param workPackageName
	 * @param componentName
	 * @param gitName
	 */
	public void addComponent(String workPackageName, String componentName, String gitName, String clonedDirectory) {
		// 既存コンポーネント
		wasanbonManagementService.addComponent(workPackageName, componentName, gitName, clonedDirectory);
	}

	/**
	 * 作業領域に新規Rtcを追加する
	 * 
	 * @param componentData
	 */
	public void createNewComponent(String componentData) {
		// 新規コンポーネント
		wasanbonManagementService.createNewComponent(componentData);
	}

	/**
	 * 指定されたデータ型からロガー用コンポーネントを作業領域に追加する
	 * 
	 * @param workPackageName
	 * @param instanceName
	 * @param portName
	 * @param dataType
	 */
	public void addLoggerComponent(String workPackageName, String id, String instanceName, String portName,
			String pathUri, String dataType) {
		wasanbonManagementService.addLoggerComponent(workPackageName, id, instanceName, portName, pathUri, dataType);
	}

	/**
	 * 指定されたRtcを作業領域から削除する
	 * 
	 * @param workPackageName
	 * @param id
	 * @param componentName
	 */
	public void deleteComponent(String workPackageName, String id, String componentName) {
		wasanbonManagementService.deleteComponent(workPackageName, id, componentName);
	}

	/**
	 * 指定されたデータ型がロギング可能な型かを調べる
	 * 
	 * @param dataType
	 */
	public Boolean canLogging(String dataType) {
		if (StringUtil.isNotEmpty(RtcUtil.getComponentNameByDataType(dataType))) {
			return true;
		}
		return false;
	}
	
	/**
	 * パッケージ名が競合していないかを調べる
	 * 
	 * @param packageName
	 * @return
	 */
	public Boolean checkAvailablePackageName(String packageName) {
		// Packageの一覧を取得
		List<String> packagesList = WasanbonUtil.getPackagesListFromBinder();
		if (CollectionUtil.isNotEmpty(packagesList) && packagesList.contains(packageName)) {
			return false;
		}
		return true;
	}

	/**
	 * RTC名が競合していないかを調べる
	 * 
	 * @param componentName
	 * @return
	 */
	public Boolean checkAvailableComponentName(String workPackageName, String componentName) {
		// 作業領域パス
		String workspaceDirPath = PropUtil.getValue("workspace.local.directory.path");

		// すでに組み込まれていないかの確認をする
		List<String> curRtcList = WasanbonUtil.getRtcsListFromPackage(workspaceDirPath, workPackageName);
		List<String> componentList = WasanbonUtil.getRtcsListFromBinder();
		if ((CollectionUtil.isNotEmpty(componentList) && componentList.contains(componentName))
				|| (CollectionUtil.isNotEmpty(curRtcList) && curRtcList.contains(componentName))) {
			return false;
		}
		return true;
	}

	/**
	 * 指定されたPackageのすべてのRtcをビルドする
	 * 
	 * @param packageRepositoryName
	 */
	public void buildPackageAll(String packageRepositoryName) {
		// 指定されたPackageのすべてのRtcをビルドする
		wasanbonManagementService.buildPackageAll(packageRepositoryName);
	}

	/**
	 * 指定されたPackageのすべてのRtcをcleanする
	 * 
	 * @param packageRepositoryName
	 */
	public void cleanPackageAll(String packageRepositoryName) {
		// 指定されたPackageのすべてのRtcをビルドする
		wasanbonManagementService.cleanPackageAll(packageRepositoryName);
	}

	/**
	 * 指定されたPackageを実行する
	 * 
	 * @param packageRepositoryName
	 */
	public void runPackage(String packageRepositoryName) {
		// 指定されたPackageを実行する
		wasanbonManagementService.runPackage(packageRepositoryName);
	}

	/**
	 * 指定されたPackageを停止する
	 * 
	 * @param packageRepositoryName
	 */
	public void terminatePackage(String packageRepositoryName) {
		// 指定されたPackageを停止する
		wasanbonManagementService.terminatePackage(packageRepositoryName);
	}
	/**
	 * Packageの実行状況を確認する
	 * 
	 * @param packageRepositoryName
	 * @return
	 */
	public boolean isRunningPackage(String packageRepositoryName) {
		// Packageの実行状況を確認する
		return wasanbonManagementService.isRunningPackage(packageRepositoryName);
	}

	/**
	 * ログを読み込む
	 * 
	 * @param workPackageName
	 * @return
	 */
	public Map<String, String> tailAllLog(String workPackageName) {
		Map<String, String> logMap = new HashMap<>();
		// 作業領域パス
		String workspaceDirPath = PropUtil.getValue("workspace.local.directory.path");

		// Wasanbonログ
		String wasanbonLogFilePath = FileUtil.concatenateFilePathStr(workspaceDirPath + workPackageName,
				DIR_NAME.PACKAGE_LOG_DIR_NAME, FILE_NAME.WASANBON_LOG);
		// CPPログディレクトリパス
		String cppLogFilePath = FileUtil.concatenateFilePathStr(workspaceDirPath + workPackageName,
				DIR_NAME.PACKAGE_LOG_DIR_NAME, FILE_NAME.APP_CPP_LOG);
		// PYTHONログディレクトリパス
		String pyLogFilePath = FileUtil.concatenateFilePathStr(workspaceDirPath + workPackageName,
				DIR_NAME.PACKAGE_LOG_DIR_NAME, FILE_NAME.APP_PYTHON_LOG);
		// JAVAログディレクトリパス
		String javaLogFilePath = FileUtil.concatenateFilePathStr(workspaceDirPath + workPackageName,
				DIR_NAME.PACKAGE_LOG_DIR_NAME, FILE_NAME.APP_JAVA_LOG);

		// 全てのログを取得マップに詰めておく
		logMap.put("wasanbon", FileUtil.readAll(wasanbonLogFilePath));
		logMap.put("cpp", FileUtil.readAll(cppLogFilePath));
		logMap.put("python", FileUtil.readAll(pyLogFilePath));
		logMap.put("java", FileUtil.readAll(javaLogFilePath));
		return logMap;
	}

	/**
	 * 対象のディレクトリにある一番新しいjpgファイルを取得する
	 * 
	 * @param imageDirectoryPath
	 * @return
	 */
	public byte[] tailImage(String imageDirectoryPath) {
		byte[] result = null;
		File imageFile = FileUtil.getLatestUpdateFile(imageDirectoryPath, "jpg");
		if (FileUtil.exists(imageFile)) {
			// BufferedImageへ設定
			BufferedImage bufferedImage = null;
			try {
				bufferedImage = ImageIO.read(imageFile);
				// byteに変換
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				ImageIO.write(bufferedImage, "jpg", bout);
				result = bout.toByteArray();
			} catch (Exception ex) {
				logger.error("例外発生. :", ex);
			}
		} else {
			logger.warn("対象の画像ファイルが存在しない.imageDirectoryPath[" + imageDirectoryPath + "]");
		}
		return result;
	}

	/**
	 * 実行結果ファイルを取得する
	 * 
	 * @return
	 */
	public Boolean createResultFile() {
		// 結果格納ディレクリ
		String resultDirPath = PropUtil.getValue("result.local.directory.path");

		// 結果格納ファイル
		String resultFilePath = PropUtil.getValue("result.local.file.path");

		return FileUtil.compressDirectory(resultFilePath, resultDirPath);
	}
	
	/**
	 * RTSをローカルリポジトリにCommitする
	 * 
	 * @param workPackageName
	 * @param commitMessage
	 */
	public String commitPackage(String workPackageName, String commitMessage) {
		return ideManagementService.commitPackage(workPackageName, commitMessage);
	}
	
	/**
	 * RTSをリモートリポジトリにPushする
	 * 
	 * @param workPackageName
	 * @param commitMessage
	 * @param userName
	 * @param password
	 * @return
	 */
	public String pushPackage(String workPackageName, String commitMessage, String userName, String password) {
		StringBuilder sb = new StringBuilder();
		
		String rsltCommit = ideManagementService.commitPackage(workPackageName, commitMessage);
		String rsltPush = ideManagementService.pushPackage(workPackageName, userName, password);
		
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
	 * RTSをリモートリポジトリからPullする
	 * 
	 * @param workPackageName
	 * @param userName
	 * @param password
	 * @return
	 */
	public String pullPackage(String workPackageName, String userName, String password) {
		return ideManagementService.pullPackage(workPackageName, userName, password);
	}
	
	/**
	 * RTCをローカルリポジトリにCommitする
	 * 
	 * @param workPackageName
	 * @param componentName
	 * @param commitMessage
	 */
	public String commitComponent(String workPackageName, String componentName, String commitMessage) {
		return ideManagementService.commitComponent(workPackageName, componentName, commitMessage);
	}
	
	/**
	 * RTCをリモートリポジトリにPushする
	 * 
	 * @param workPackageName
	 * @param componentName
	 * @param commitMessage
	 * @param userName
	 * @param password
	 * @return
	 */
	public String pushComponent(String workPackageName, String componentName, String commitMessage, String userName, String password) {
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
	 * RTCをリモートリポジトリからPullする
	 * 
	 * @param workPackageName
	 * @param componentName
	 * @param userName
	 * @param password
	 * @return
	 */
	public String pullComponent(String workPackageName, String componentName, String userName, String password) {
		return ideManagementService.pullComponent(workPackageName, componentName, userName, password);
	}
	
	/**
	 * Kerasの選択肢を取得する
	 * @return
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
	 * データセットの選択肢を取得する
	 * 
	 * @return
	 */
	public Map<String, String> getDatasetChoices() {
		return kerasManagementService.loadDatasetList();
	}

	/**
	 * 指定されたDNNモデル名に関連するファイルをダウンロードする
	 * 
	 * @param dnnModelName DNNモデル名
	 */
	public boolean downloadDnnFiles(String dnnModelName) {
		boolean result = kerasManagementService.downloadDnnFiles(dnnModelName, "json");
		if (result) {
			result = kerasManagementService.downloadDnnFiles(dnnModelName, "hdf5");
		}
		return result;
	}
}
