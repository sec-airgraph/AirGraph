package com.sec.airgraph.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sec.airgraph.util.CollectionUtil;
import com.sec.airgraph.util.Const.COMMON.DIR_NAME;
import com.sec.airgraph.util.Const.COMMON.FILE_SUFFIX;
import com.sec.airgraph.util.Const.RT_COMPONENT.COMPONENT_CONNECTOR_TYPE;
import com.sec.airgraph.util.Const.RT_COMPONENT.LANGUAGE_KIND;
import com.sec.airgraph.util.Const.RT_COMPONENT.MODULE_NAME;
import com.sec.airgraph.util.FileUtil;
import com.sec.airgraph.util.GitUtil;
import com.sec.airgraph.util.PropUtil;
import com.sec.airgraph.util.RtcUtil;
import com.sec.airgraph.util.StringUtil;
import com.sec.airgraph.util.WasanbonUtil;
import com.sec.rtc.entity.BuildRunDTO;
import com.sec.rtc.entity.rtc.Actions;
import com.sec.rtc.entity.rtc.Rtc;
import com.sec.rtc.entity.rts.Component;
import com.sec.rtc.entity.rts.DataPortConnector;
import com.sec.rtc.entity.rts.Rts;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Wasanbon管理サービス.
 *
 * @author Tsuyoshi Hirose
 * 
 */
@Service
public class WasanbonManagementService {

	/**
	 * logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(WasanbonManagementService.class);

	/**
	 * RTC管理サービス.
	 */
	@Autowired
	private RtcManagementService rtcManagementService;

	/************************************************************
	 * RTS(Package)関連
	 ************************************************************/
	/**
	 * すべてのPackageをCloneする.
	 *
	 * @param hostId ホストID
	 */
	public void cloneAllPackages(String hostId) {

		// Packagesローカルリポジトリ格納先
		String packagesLocalDirPath = PropUtil.getValue("packages.local.directory.path");

		logger.info("Start clone package. packagesLocalDirPath[" + packagesLocalDirPath + "]");

		// Packageの一覧を取得
		List<String> packagesList = WasanbonUtil.getPackagesListFromBinder(hostId);

		if (CollectionUtil.isNotEmpty(packagesList)) {
			for (String packageRepositoryName : packagesList) {

				File packageDir = new File(packagesLocalDirPath + packageRepositoryName);
				if (FileUtil.notExists(packageDir)) {
					// 存在しない場合にのみ
					WasanbonUtil.clonePackageFromRepositoryToDirectory(packagesLocalDirPath, packageRepositoryName);
				}
			}
		}

		logger.info("Finish clone package.");
	}

	/**
	 * 新規パッケージを作業領域に生成する.
	 *
	 * @param workPackageName パッケージ名
	 * @param packageRepositoryName パッケージリポジトリ名
	 * @param newId ID
	 * @param newSAbstract newSAbstract
	 * @param newVersion バージョン
	 * @param newRemoteUrl リモートリポジトリURL
	 * @param newPackageName パッケージ名
	 * @param hostId ホストID
	 */
	public void createNewPackageToWorkspace(String workPackageName, String packageRepositoryName, String newId,
			String newSAbstract, String newVersion, String newRemoteUrl, String newPackageName, String hostId) {

		// 作業領域パス
		String workspaceDirPath = PropUtil.getValue("workspace.local.directory.path");

		logger.info("Package作業領域展開処理開始. workspaceDirPath[" + workspaceDirPath + "]packageRepositoryName["
				+ packageRepositoryName + "]" + workPackageName);

		// 空のパッケージを作成する
		WasanbonUtil.createPackage(workspaceDirPath, newPackageName, hostId);

		// 空のRtsProfileを生成する
		File packageDir = new File(workspaceDirPath + File.separator + newPackageName);
		Rts rts = rtcManagementService.createNewRtsProfile();

		// 入力内容を設定
		rts.getRtsProfile().setId(newId);
		rts.getRtsProfile().setSAbstract(newSAbstract);
		rts.getRtsProfile().setVersion(newVersion);
		rts.getModelProfile().setRemoteUrl(newRemoteUrl);

		// RTS構成情報のXMLを出力する
		rtcManagementService.saveRtsProfile(packageDir, rts.getRtsProfile(), false);

		// Git初期化
		GitUtil.gitPackageInit(newPackageName);

		// GitIgnore
		GitUtil.createGitIgnoreForPackage(packageDir.getPath());

		// リモートリポジトリを設定する
		GitUtil.gitAddPackageRemote(newPackageName, newRemoteUrl);
	}

	/**
	 * 指定されたパッケージを作業領域にCloneする.
	 *
	 * @param workPackageName パッケージ名
	 * @param packageRepositoryName パッケージリポジトリ名
	 * @param newId ID
	 * @param newSAbstract newSAbstract
	 * @param newVersion バージョン
	 * @param newRemoteUrl リモートリポジトリURL
	 * @param newPackageName パッケージ名
	 * @param hostId ホストID
	 */
	public void clonePackageToWorkspace(String workPackageName, String packageRepositoryName, String newId,
			String newSAbstract, String newVersion, String newRemoteUrl, String newPackageName, String hostId) {

		// Packagesローカルリポジトリ格納先
		String packagesLocalDirPath = PropUtil.getValue("packages.local.directory.path");

		// 作業領域パス
		String workspaceDirPath = PropUtil.getValue("workspace.local.directory.path");

		logger.info("Package作業領域展開処理開始. workspaceDirPath[" + workspaceDirPath + "]packageRepositoryName["
				+ packageRepositoryName + "]");

		// 空のパッケージを作成する
		WasanbonUtil.createPackage(workspaceDirPath, workPackageName, hostId);

		// コピー元からRTCをコピーする
		File srcRtcDir = new File(
				packagesLocalDirPath + packageRepositoryName + File.separator + DIR_NAME.PACKAGE_RTC_DIR_NAME);
		File destRtcDir = new File(workspaceDirPath + workPackageName);
		FileUtil.directoryCopy(srcRtcDir, destRtcDir);

		// コピー元のRtsProfileを読み込む
		File srcPackageDir = new File(packagesLocalDirPath + packageRepositoryName);
		Rts srcRts = rtcManagementService.loadRtsProfile(srcPackageDir, false);

		// 作業領域のRtsProfileを読み込む
		File destPackageDir = new File(workspaceDirPath + File.separator + workPackageName);
		Rts destRts = rtcManagementService.loadRtsProfile(destPackageDir, false);

		// RtsProfileをコピーする
		rtcManagementService.copyRtsProfile(srcRts.getRtsProfile(), destRts.getRtsProfile());

		// 入力内容を設定
		destRts.getRtsProfile().setId(newId);
		destRts.getRtsProfile().setSAbstract(newSAbstract);
		destRts.getRtsProfile().setVersion(newVersion);
		destRts.getModelProfile().setRemoteUrl(newRemoteUrl);

		// RTS構成情報のXMLを出力する
		rtcManagementService.saveRtsProfile(destPackageDir, destRts.getRtsProfile(), false);

		// Git初期化
		GitUtil.gitPackageInit(newPackageName);

		// コピー元のリモートリポジトリを設定する
		GitUtil.gitAddPackageRemote(destPackageDir.getName(), srcRts.getModelProfile().getRemoteUrl().trim());

		// リモートリポジトリの内容をマージする
		GitUtil.gitFetchOrigin(destPackageDir.getPath(), true);

		// GitIgnore
		GitUtil.createGitIgnoreForPackage(destPackageDir.getPath());

		// リモートリポジトリのURLを置き換える
		if (!newRemoteUrl.equals(srcRts.getModelProfile().getRemoteUrl())) {
			GitUtil.changeRemoteUrl(destPackageDir.getPath(), newRemoteUrl);
		}
		// RTS構成情報のXMLを出力する
		rtcManagementService.saveRtsProfile(destPackageDir, destRts.getRtsProfile(), false);
	}

	/**
	 * 作業領域にCloneされているすべてのパッケージを読み込む.
	 * 
	 * @return パッケージリスト
	 */
	public List<Rts> loadAllPackagesWorkspace() {
		List<Rts> list = new ArrayList<Rts>();
		// 作業領域パス
		String workspaceDirPath = PropUtil.getValue("workspace.local.directory.path");

		File workspaceDir = new File(workspaceDirPath);
		if (FileUtil.exists(workspaceDir)) {
			File[] workPackages = workspaceDir.listFiles();
			if (CollectionUtil.isNotEmpty(workPackages)) {
				for (File workPackageDir : workPackages) {
					Rts work = null;
					if (FileUtil.exists(workPackageDir) && workPackageDir.isDirectory()) {
						work = rtcManagementService.loadRtsProfile(workPackageDir, true);
					}
					if (work != null && work.getRtsProfile() != null) {
						list.add(work);
					}

				}
			}
		}
		// 名前順でソートする
		list = CollectionUtil.sort(list, rts -> rts.getModelProfile().getModelName());

		return list;
	}

	/**
	 * 作業領域にCloneされている指定されたパッケージを読み込む.
	 *
	 * @param workPackageName パッケージ名
	 * @return 指定されたパッケージの情報
	 */
	public Rts loadPackageWorkspace(String workPackageName) {
		Rts work = null;
		// 作業領域パス
		String workspaceDirPath = PropUtil.getValue("workspace.local.directory.path");

		String workPackageDirPath = workspaceDirPath + File.separator + workPackageName;

		File workPackageDir = new File(workPackageDirPath);
		if (FileUtil.exists(workPackageDir)) {
			work = rtcManagementService.loadRtsProfile(workPackageDir, true);
		}
		return work;
	}

	/**
	 * 指定されたPackageを削除する.
	 *
	 * @param packageRepositoryName パッケージリポジトリ名
	 */
	public void deletePackage(String packageRepositoryName) {
		WasanbonUtil.deletePackage(packageRepositoryName);
	}

	/************************************************************
	 * RTC(Component)関連
	 ************************************************************/
	/**
	 * 指定されたPackageに新規コンポーネントを組み込む.
	 *
	 * @param componentData コンポーネントの情報
	 */
	public void createNewComponent(String componentData) {

		ObjectMapper mapper = new ObjectMapper();
		Rtc newRtc = null;
		try {
			newRtc = mapper.readValue(componentData, Rtc.class);
		} catch (IOException e) {
			logger.error("exception handled. ex:", e);
		}

		String workSpaceName = StringUtil.getPackageNameFromModelName(newRtc.getWorkspaceName());
		String componentName = newRtc.getRtcProfile().getBasicInfo().getModuleName().replaceAll(" ", "").replaceAll("　",
				"");
		newRtc.getRtcProfile().getBasicInfo().setModuleName(componentName);
		
		// 作業領域パス
		String workspaceDirPath = PropUtil.getValue("workspace.local.directory.path");
		// 作業領域Rtcディレクトリパス
		String workRtcDirPath = workspaceDirPath + workSpaceName + File.separator + DIR_NAME.PACKAGE_RTC_DIR_NAME;
		// テンプレートディレクトリパス
		String templateRtcDirPath = PropUtil.getValue("rtcs.template.directory.path");
		// テンプレートパス
		String templateName = "";
		switch (newRtc.getRtcProfile().getLanguage().getKind()) {
		case LANGUAGE_KIND.CPP:
			templateName = MODULE_NAME.NEW_CPP_TEMPLATE_NAME;
			break;
		case LANGUAGE_KIND.JAVA:
			templateName = MODULE_NAME.NEW_JAVA_TEMPLATE_NAME;
			break;
		case LANGUAGE_KIND.PYTHON:
			templateName = MODULE_NAME.NEW_PYTHON_TEMPLATE_NAME;
			break;
		default:
			break;
		}
		String templateRtcPath = templateRtcDirPath + templateName;
		String rtcDirPath = workRtcDirPath + File.separator + templateName;

		// テンプレートをコピーする
		FileUtil.directoryCopy(new File(templateRtcPath), new File(workRtcDirPath));

		// ファイル名・ファイル内容をすべて変更
		FileUtil.renameAllFiles(rtcDirPath, templateName, newRtc.getRtcProfile().getBasicInfo().getModuleName());
		FileUtil.renameFileName(workRtcDirPath, templateName, newRtc.getRtcProfile().getBasicInfo().getModuleName());

		// cmakeのファイルは小文字化する
		File newRtcDir = FileUtil.concatenateFilePath(workRtcDirPath,
				newRtc.getRtcProfile().getBasicInfo().getModuleName());
		String cmakeDirPath = newRtcDir + File.separator + DIR_NAME.COMP_CMAKE_DIR_NAME;
		FileUtil.renameFileName(cmakeDirPath, newRtc.getRtcProfile().getBasicInfo().getModuleName(),
				newRtc.getRtcProfile().getBasicInfo().getModuleName().toLowerCase());

		// ヘッダファイルのInclude文を変更する
		String headerDirPath = newRtcDir + File.separator + DIR_NAME.COMP_INCLUDE_DIR_NAME;
		FileUtil.renameAllFiles(headerDirPath, templateName.toUpperCase(),
				newRtc.getRtcProfile().getBasicInfo().getModuleName().toUpperCase());

		boolean isNNUpdate = false;
		if (StringUtil.isNotEmpty(newRtc.getRtcProfile().getNeuralNetworkInfo().getDatasetName())
				|| StringUtil.isNotEmpty(newRtc.getRtcProfile().getNeuralNetworkInfo().getModelName())) {
			// NN情報を更新する
			rtcManagementService.refreactNNInfoChangedToConfiguration(newRtc.getRtcProfile(), null);
			isNNUpdate = true;
		}

		// アクティビティ変更用
		Actions defaultActions = new Actions();
		defaultActions.getOnInitialize().setImplemented(true);
		defaultActions.getOnActivated().setImplemented(true);
		defaultActions.getOnDeactivated().setImplemented(true);
		defaultActions.getOnExecute().setImplemented(true);

		// RTC.XMLを保存する
		rtcManagementService.saveRtcProfile(newRtcDir, newRtc.getRtcProfile(), false);

		// スペック情報を更新する
		if (LANGUAGE_KIND.CPP.equals(newRtc.getRtcProfile().getLanguage().getKind())) {
			File sourceFile = FileUtil.concatenateFilePath(newRtcDir.getPath(), DIR_NAME.COMP_SRC_DIR_NAME,
					newRtc.getRtcProfile().getBasicInfo().getModuleName() + FILE_SUFFIX.SUFFIX_CPP);
			File headerFile = FileUtil.concatenateFilePath(newRtcDir.getPath(), DIR_NAME.COMP_INCLUDE_DIR_NAME,
					newRtc.getRtcProfile().getBasicInfo().getModuleName(),
					newRtc.getRtcProfile().getBasicInfo().getModuleName() + FILE_SUFFIX.SUFFIX_CPP_HEADER);

			// module_specを更新する
			rtcManagementService.updateModuleSpecCpp(newRtc.getRtcProfile(), sourceFile);

			if (isNNUpdate) {
				// bind_configを更新する
				rtcManagementService.updateBindConfigCpp(newRtc.getRtcProfile(), sourceFile);
				// config_declareを変更する
				rtcManagementService.updateConfigDeclareCppHeader(newRtc.getRtcProfile(), headerFile);
			}

			// アクティビティを変更する
			rtcManagementService.updateCommentMethodCpp(newRtc.getRtcProfile().getActions(), defaultActions, sourceFile,
					headerFile);

		} else if (LANGUAGE_KIND.PYTHON.equals(newRtc.getRtcProfile().getLanguage().getKind())) {
			File sourceFile = FileUtil.concatenateFilePath(newRtcDir.getPath(),
					newRtc.getRtcProfile().getBasicInfo().getModuleName() + FILE_SUFFIX.SUFFIX_PYTHON);

			// module_specを更新する
			rtcManagementService.updateModuleSpecPython(newRtc.getRtcProfile(), sourceFile);

			if (isNNUpdate) {
				// onInitializeを更新する
				rtcManagementService.updateOnInitializePython(newRtc.getRtcProfile(), sourceFile);
				// init_conf_paramを更新する
				rtcManagementService.updateInitConfParamPython(newRtc.getRtcProfile(), sourceFile);
			}

			// アクティビティを変更する
			rtcManagementService.updateCommentMethodPython(newRtc.getRtcProfile().getActions(), defaultActions,
					sourceFile);
		}

		// Configファイルを変更する
		File configFile = FileUtil.concatenateFilePath(newRtcDir.getPath(),
				newRtc.getRtcProfile().getBasicInfo().getModuleName() + FILE_SUFFIX.SUFFIX_CONFIG);
		RtcUtil.updateExecutionRateRtcConfig(configFile.getPath(),
				newRtc.getRtcProfile().getBasicInfo().getExecutionRate());

		// Git初期化 
		GitUtil.gitComponentInit(workSpaceName, componentName);

		// GitIgnore
		GitUtil.createGitIgnoreForComponent(newRtcDir.getPath());

		// リモートリポジトリを設定する
		GitUtil.gitAddComponentRemote(workSpaceName, componentName, newRtc.getModelProfile().getRemoteUrl());

	}

	/**
	 * 指定されたPackageに指定されたComponentを組み込む.
	 *
	 * @param workPackageName パッケージ名
	 * @param componentName コンポーネント名
	 * @param gitName git名
	 * @param clonedDirectory クローンされたディレクトリ
	 */
	public void addComponent(String workPackageName, String componentName, String gitName, String clonedDirectory) {
		// 作業領域パス
		String workspaceDirPath = PropUtil.getValue("workspace.local.directory.path");

		String packageName = workPackageName.replace("rts_", "");
		// 作業領域Packageディレクトリパス
		String workPackageDirPath = workspaceDirPath + workPackageName;
		
		// すでに組み込まれていないかの確認をする
		List<String> curRtcList = WasanbonUtil.getRtcsListFromPackage(packageName);
		if (CollectionUtil.isEmpty(curRtcList) || !curRtcList.contains(componentName)) {
			if (StringUtil.isNotEmpty(clonedDirectory)) {
				// すでにCloneされているディレクトリからコピーする
				if (StringUtil.isNotEmpty(clonedDirectory) && FileUtil.exists(new File(clonedDirectory))) {
					FileUtil.directoryCopy(new File(clonedDirectory),
							new File(workPackageDirPath + File.separator + DIR_NAME.PACKAGE_RTC_DIR_NAME),
							componentName);
					logger.info("Copy local RTC to Package.");
				}

			} else {
				// 組み込まれていない場合は組み込む
				WasanbonUtil.cloneRtcToPackage(packageName, componentName);
			}
		}
	}

	/**
	 * 指定されたデータ型からロガー用Componentを追加する.
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
		// 作業領域パス
		String workspaceDirPath = PropUtil.getValue("workspace.local.directory.path");
		// 作業領域Packageディレクトリパス
		File workPackageDir = new File(workspaceDirPath + workPackageName);

		// データ型からロガー用のコンポーネント名を取得する
		String componentName = RtcUtil.getComponentNameByDataType(dataType);
		String gitName = RtcUtil.getGitNameByDataType(dataType);
		String dirName = RtcUtil.getDirectoryNameByDataType(dataType);
		String rtcDirPath = PropUtil.getValue("rtcs.local.directory.path");
		if (StringUtil.isNotEmpty(componentName) && StringUtil.isNotEmpty(gitName)) {
			// PackageにRtcを組み込む
			addComponent(workPackageName, componentName, gitName, rtcDirPath + dirName);
		}

		// コンポーネントの名称を取得する
		File loggerComponentDir = FileUtil.concatenateFilePath(workPackageDir.getPath(), DIR_NAME.PACKAGE_RTC_DIR_NAME,
				gitName);
		Rtc loggerRtc = rtcManagementService.loadRtcProfile(loggerComponentDir, "", false);
		if (loggerRtc != null) {
			String loggerComponentId = loggerRtc.getRtcProfile().getId();

			// 作業領域の再読み込みを行う
			Rts rts = rtcManagementService.loadRtsProfile(workPackageDir, true);

			// ロガー種別を追加する
			Component targetComponent = null;
			if (CollectionUtil.isNotEmpty(rts.getRtsProfile().getComponents())) {
				for (int i = 0; i < rts.getRtsProfile().getComponents().size(); i++) {
					if (rts.getRtsProfile().getComponents().get(i).getId().equals(loggerComponentId)) {
						rts.getRtsProfile().getComponents().get(i).setComponentType(COMPONENT_CONNECTOR_TYPE.LOGGER);
						rts.getRtsProfile().getComponents().get(i).setVisible(false);
						targetComponent = rts.getRtsProfile().getComponents().get(i);
					}
					if (rts.getRtsProfile().getComponents().get(i).getId().equals(id)) {
						for (int j = 0; j < rts.getRtsProfile().getComponents().get(i).getDataPorts().size(); j++) {
							if (rts.getRtsProfile().getComponents().get(i).getDataPorts().get(j).getName()
									.equals(portName)) {
								rts.getRtsProfile().getComponents().get(i).getDataPorts().get(j).setLogging(true);
								rts.getRtsProfile().getComponents().get(i).getDataPorts().get(j)
										.setLoggerVisible(false);
							}
						}
					}
				}
				rts.getRtsProfile()
						.setComponentMap(CollectionUtil.toMap(rts.getRtsProfile().getComponents(), Component::getId));
			}

			// 接続情報を追加する
			DataPortConnector loggerConenctor = rtcManagementService.createDataPortConnector(id, instanceName, portName,
					pathUri, targetComponent.getId(), targetComponent.getInstanceName(),
					targetComponent.getDataPorts().get(0).getName(), targetComponent.getPathUri(), dataType,
					COMPONENT_CONNECTOR_TYPE.LOGGER);
			rts.getRtsProfile().getDataPortConnectors().add(loggerConenctor);

			// 作業領域を更新する
			rtcManagementService.saveRtsProfile(workPackageDir, rts.getRtsProfile(), true);
		}
	}

	/**
	 * 指定されたPackageから指定されたComponentを削除する.
	 *
	 * @param workPackageName パッケージ名
	 * @param id ID
	 * @param componentName コンポーネント名
	 */
	public void deleteComponent(String workPackageName, String id, String componentName) {
		// 作業領域パス
		String workspaceDirPath = PropUtil.getValue("workspace.local.directory.path");

		String packageName = workPackageName.replace("rts_", "");
		// 作業領域Packageディレクトリパス
		File workPackageDir = new File(workspaceDirPath + packageName);
		
		// 組み込まれている場合は削除する
		WasanbonUtil.deleteRtcFromPackage(packageName, componentName);

		// 作業領域の再読み込みを行う
		Rts rts = rtcManagementService.loadRtsProfile(workPackageDir, false);
		if (rts != null) {
			// 削除したコンポーネントを削除する
			if (CollectionUtil.isNotEmpty(rts.getRtsProfile().getComponents())) {
				for (int i = rts.getRtsProfile().getComponents().size() - 1; i >= 0; i--) {
					if (rts.getRtsProfile().getComponents().get(i).getId().equals(id)) {
						rts.getRtsProfile().getComponents().remove(i);
					}
				}
			}
			// 接続情報も削除する
			if (CollectionUtil.isNotEmpty(rts.getRtsProfile().getDataPortConnectors())) {
				for (int i = rts.getRtsProfile().getDataPortConnectors().size() - 1; i >= 0; i--) {
					if (rts.getRtsProfile().getDataPortConnectors().get(i).getTargetDataPort().getComponentId()
							.equals(id)
							|| rts.getRtsProfile().getDataPortConnectors().get(i).getSourceDataPort().getComponentId()
									.equals(id)) {
						rts.getRtsProfile().getDataPortConnectors().remove(i);
					}
				}
			}
			// 作業領域を更新する
			rtcManagementService.saveRtsProfile(workPackageDir, rts.getRtsProfile(), true);
		}
	}

	/************************************************************
	 * ビルド・実行関連
	 ************************************************************/
	/**
	 * 指定されたPackageのすべてのRtcをビルドする.
	 *
	 * @param buildRunDto ビルドに必要な情報
	 */
	public void buildPackageAll(BuildRunDTO buildRunDto) {

		WasanbonUtil.buildPackageAll(buildRunDto);

		// Configファイルをコピーする
		String rtcDirPath = buildRunDto.getPackageDirPath() + DIR_NAME.PACKAGE_RTC_DIR_NAME + File.separator;
		String confDirPath = buildRunDto.getPackageDirPath() + DIR_NAME.PACKAGE_CONF_DIR_NAME + File.separator;
		File[] rtcs = new File(rtcDirPath).listFiles();
		if (CollectionUtil.isNotEmpty(rtcs)) {
			for (File rtc : rtcs) {
				if (rtc.isDirectory() && !rtc.getName().startsWith(".")) {
					File sourceConfig = new File(
							rtc.getPath() + File.separator + rtc.getName() + FILE_SUFFIX.SUFFIX_CONFIG);
					File destConfig = new File(confDirPath + rtc.getName() + "0" + FILE_SUFFIX.SUFFIX_CONFIG);
					if (sourceConfig.exists()) {
						FileUtil.fileCopy(sourceConfig, destConfig);
					}
				}
			}
		}
	}

	/**
	 * 指定されたPackageのすべてのRtcをCleanする.
	 *
	 * @param buildRunDto クリーンに必要な情報
	 */
	public void cleanPackageAll(BuildRunDTO buildRunDto) {
		
		WasanbonUtil.cleanPackageAll(buildRunDto);
	}

	/**
	 * 指定されたPackageのSystemを実行する.
	 *
	 * @param buildRunDto 実行に必要な情報
	 */
	public void runSystem(BuildRunDTO buildRunDto) {
		
		WasanbonUtil.runSystem(buildRunDto);
	}
	
	/**
	 * RTCをスタートする.
	 *
	 * @param buildRunDto 実行に必要な情報
	 */
	public void startRtcs(BuildRunDTO buildRunDto) {
	
		WasanbonUtil.startRtcs(buildRunDto);
	}
	
	/**
	 * コネクトする.
	 *
	 * @param buildRunDto 実行に必要な情報
	 */
	public void connectPorts(BuildRunDTO buildRunDto) {

		WasanbonUtil.connectPorts(buildRunDto);
	}
	
	/**
	 * アクティベイトかディアクティベイトをする.
	 *
	 * @param buildRunDto 実行に必要な情報
	 * @param isActivate アクティベイトかどうか
	 */
	public void activateOrDeactivateRtcs(BuildRunDTO buildRunDto, boolean isActivate) {

		WasanbonUtil.activateOrDeactivateRtcs(buildRunDto, isActivate);
	}

	/**
	 * 指定されたPackageのSystemを停止する.
	 *
	 * @param buildRunDto 実行に必要な情報
	 */
	public void terminateSystem(BuildRunDTO buildRunDto) {

		WasanbonUtil.terminateSystem(buildRunDto);
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
		
		String packageName = rtsName.replace("rts_", "");
		if (ws.equals("exec")) { 
			packageName = "exec_" + packageName;
		}
		return WasanbonUtil.isRunningPackage(ws, packageName, hostId);
	}
	
	/************************************************************
	 * Binder関連
	 ************************************************************/
	/**
	 * すべてのBinderを取得する.
	 *
	 * @param hostId ホストID
	 * @return binderの一覧
	 */
	public Map<String, Map<String, String>> getBinderList(String hostId) {
		return WasanbonUtil.getBinderList(hostId);
	}
	
	/**
	 * Binderにパッケージを追加する.
	 *
	 * @param packageName パッケージ名
	 * @param binderName バインダー名
	 * @return 実行結果
	 */
	public String addPackageToBinder(String packageName, String binderName) {

		return WasanbonUtil.addPackageToBinder(packageName, binderName);
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

		return WasanbonUtil.updatePackageToBinder(ws, packageName, binderName);
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

		return WasanbonUtil.addRtcToBinder(ws, packageName, rtcName, binderName);
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
		
		return WasanbonUtil.updateRtcToBinder(ws, packageName, rtcName, binderName);
	}
	
	/**
	 * Binderをcommitする.
	 *
	 * @param binderName バインダー名
	 * @param comment コミットメッセージ
	 * @return コミット結果
	 */
	public String commitBinder(String binderName, String comment) {

		return WasanbonUtil.commitBinder(binderName, comment);
	}

	/**
	 * Binderをpushする.
	 *
	 * @param binderName バインダー名
	 * @param comment コミットメッセージ
	 * @return push結果
	 */
	public String pushBinder(String binderName, String comment) {

		return WasanbonUtil.pushBinder(binderName, comment);
	}
	
	/**
	 * nameserverを起動する.
	 *
	 * @param hostId ホストID
	 */
	public void startNameserver(String hostId) {

		WasanbonUtil.startNameserver(hostId);
	}
	
	/**
	 * nameserverのstatusを確認する.
	 *
	 * @param hostId ホストID
	 * @return nameserverのstatus
	 */
	public boolean checkNameserverStatus(String hostId)  {

		return WasanbonUtil.checkNameserverStatus(hostId);
	}
}
