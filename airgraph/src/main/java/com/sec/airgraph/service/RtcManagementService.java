package com.sec.airgraph.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.JAXB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sec.rtc.entity.rtc.Actions;
import com.sec.rtc.entity.rtc.CodeDirectory;
import com.sec.rtc.entity.rtc.Configuration;
import com.sec.rtc.entity.rtc.ModelProfile;
import com.sec.rtc.entity.rtc.Rtc;
import com.sec.rtc.entity.rtc.RtcProfile;
import com.sec.rtc.entity.rtc.ServiceInterface;
import com.sec.rtc.entity.rts.Component;
import com.sec.rtc.entity.rts.ConfigurationData;
import com.sec.rtc.entity.rts.ConfigurationSet;
import com.sec.rtc.entity.rts.ConnectionDataPort;
import com.sec.rtc.entity.rts.DataPort;
import com.sec.rtc.entity.rts.DataPortConnector;
import com.sec.rtc.entity.rts.ExecutionContext;
import com.sec.rtc.entity.rts.Location;
import com.sec.rtc.entity.rts.Property;
import com.sec.rtc.entity.rts.Rts;
import com.sec.rtc.entity.rts.RtsProfile;
import com.sec.rtc.entity.rts.ServicePort;
import com.sec.airgraph.util.FileUtil;
import com.sec.airgraph.util.GitUtil;
import com.sec.airgraph.util.RtmEditorUtil;
import com.sec.airgraph.util.PropUtil;
import com.sec.airgraph.util.RtcUtil;
import com.sec.airgraph.util.StringUtil;
import com.sec.airgraph.util.YamlUtil;
import com.sec.airgraph.util.CollectionUtil;
import com.sec.airgraph.util.Const.COMMON.DIR_NAME;
import com.sec.airgraph.util.Const.COMMON.FILE_NAME;
import com.sec.airgraph.util.Const.COMMON.FILE_SUFFIX;
import com.sec.airgraph.util.Const.RT_COMPONENT.COMPONENT_CONNECTOR_TYPE;
import com.sec.airgraph.util.Const.RT_COMPONENT.DATAFLOW_TYPE;
import com.sec.airgraph.util.Const.RT_COMPONENT.INTERFACE_TYPE;
import com.sec.airgraph.util.Const.RT_COMPONENT.LANGUAGE_KIND;
import com.sec.airgraph.util.Const.RT_COMPONENT.MODULE_NAME;
import com.sec.airgraph.util.Const.RT_COMPONENT.PACKAGE_NAME;
import com.sec.airgraph.util.Const.RT_COMPONENT.SUBSCRIPTION_TYPE;

/**
 * RTC管理サービス
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Service
public class RtcManagementService {

	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(RtcManagementService.class);

	/**
	 * インスタンス名の最後につける接尾辞
	 */
	private static final String MODULE_INSTANCE_SUFFIX = "0";

	/************************************************************
	 * RTS(Package)関連
	 ************************************************************/

	/**
	 * 新規RTS生成処理
	 * 
	 * @return
	 */
	public Rts createNewRtsProfile() {
		Rts rts = new Rts();
		rts.getRtsProfile().setId("blank:blank:blank");
		rts.getRtsProfile().setSAbstract("RT System created by AirGraph.");
		rts.getRtsProfile().setUpdateDate(new Date());
		rts.getRtsProfile().setVersion("1.0.0");

		// コンポーネント名設定
		rts.getModelProfile().setModelId(PACKAGE_NAME.NEW_ID);
		rts.getModelProfile().setModelName(PACKAGE_NAME.NEW);

		return rts;
	}

	/**
	 * 指定ディレクトリ配下にあるすべてのRTS読込処理<br>
	 * ソースコードを読み込むかどうかを指定可能
	 * 
	 * @param rtsParentDirPath
	 * @param loadSourceFile
	 * @return
	 */
	public List<Rts> loadRtsProfiles(String rtsParentDirPath, boolean loadSourceFile) {
		List<Rts> rtsList = new ArrayList<Rts>();

		File rtsParentDir = new File(rtsParentDirPath);
		if (FileUtil.exists(rtsParentDir)) {
			File[] rtsDirs = rtsParentDir.listFiles();
			if (CollectionUtil.isNotEmpty(rtsDirs)) {
				for (File file : rtsDirs) {
					Rts rts = loadRtsProfile(file, loadSourceFile);
					if (rts != null) {
						rtsList.add(rts);
					}
				}
			}
		}
		// 名前順でソートする
		rtsList = CollectionUtil.sort(rtsList, rts -> rts.getModelProfile().getModelName());
		return rtsList;
	}

	/**
	 * 指定RTS読込処理<br>
	 * ソースコードを読み込むかどうかを指定可能
	 * 
	 * @param packageDir
	 * @param loadSourceFile
	 * @return
	 */
	public Rts loadRtsProfile(File packageDir, boolean loadSourceFile) {
		if (FileUtil.exists(packageDir)) {
			Rts rts = new Rts();
			// DefaultSystem.xml読み込み
			File rtsXmlFile = FileUtil.concatenateFilePath(packageDir.getPath(), DIR_NAME.PACKAGE_SYSTEM_DIR_NAME,
					FILE_NAME.PACKAGE_XML_FILE_NAME);
			if (FileUtil.exists(rtsXmlFile)) {
				// DefaultSystem.xmlをオブジェクト化
				RtsProfile profile = JAXB.unmarshal(rtsXmlFile, RtsProfile.class);
				if (profile != null && profile.getComponents() != null) {
					logger.debug("Load DefaultSystem.xml id[" + profile.getId() + "]");
					rts.setRtsProfile(profile);
					rts.getModelProfile().setModelId("rts_" + packageDir.getName());
					rts.getModelProfile().setModelName(packageDir.getName());

					// GitのURLを取得する
					rts.getModelProfile().setRemoteUrl(GitUtil.getGitUrl(packageDir.getPath()));

					// System内のComponent情報をMap化する
					profile.setComponentMap(CollectionUtil.toMap(profile.getComponents(), Component::getId));

					// abstractが空の場合は設定しておく
					if (StringUtil.isEmpty(profile.getSAbstract())) {
						profile.setSAbstract("RT System created by AirGraph.");
					}

				} else {
					logger.warn("Failed to load DefaultSystem.xml. path[" + rtsXmlFile.getPath() + "]");
				}

				// パッケージ内のRTC読み込み
				File rtcParentDir = FileUtil.concatenateFilePath(packageDir.getPath(), DIR_NAME.PACKAGE_RTC_DIR_NAME);
				List<Rtc> rtcList = loadRtcProfiles(rtcParentDir, "rts_rtc_" + packageDir.getName(), loadSourceFile);
				if (CollectionUtil.isNotEmpty(rtcList)) {
					rts.setRtcs(rtcList);

					// RtsのComponent情報が不足している場合、追加する
					if (CollectionUtil.isEmpty(profile.getComponents())) {
						List<Component> componentList = new ArrayList<Component>();
						Map<String, List<Component>> componentMap = new HashMap<>();
						profile.setComponents(componentList);
						profile.setComponentMap(componentMap);
					}
					for (Rtc rtc : rtcList) {
						if (!profile.getComponentMap().containsKey(rtc.getRtcProfile().getId())) {
							Component component = createRtsComponentInfo(rtc.getRtcProfile());
							profile.getComponents().add(component);
							List<Component> componentList = new ArrayList<Component>();
							componentList.add(component);
							profile.getComponentMap().put(component.getId(), componentList);
						}
					}
				}

				// System内のComponent情報をMap化する
				profile.setComponentMap(CollectionUtil.toMap(profile.getComponents(), Component::getId));

				if (CollectionUtil.isNotEmpty(profile.getComponents())) {
					for (Component component : profile.getComponents()) {
						// コンフィギュレーション設定をMap化する
						if (CollectionUtil.isNotEmpty(component.getConfigurationSets())) {
							component.setConfigurationSetMap(
									CollectionUtil.toMap(component.getConfigurationSets(), ConfigurationSet::getId));
							component.setEditConfigurationSetMap(
									CollectionUtil.toMap(component.getConfigurationSets(), ConfigurationSet::getId));
						}
						// 有効なコンフィギュレーションも設定しておく
						if (StringUtil.isEmpty(component.getActiveConfigurationSet())) {
							component.setActiveConfigurationSet("default");
						}
						component.setEditActiveConfigurationSet(component.getActiveConfigurationSet());
					}
				}
			}
			return rts;
		}
		return null;
	}

	/**
	 * 指定RTS書込処理
	 * 
	 * @param packageDir
	 * @param rtsProfile
	 * @param createBackup
	 */
	public void saveRtsProfile(File packageDir, RtsProfile rtsProfile, boolean createBackup) {
		if (FileUtil.exists(packageDir)) {
			// DefaultSystem.xml書き込み
			File rtsXmlFile = FileUtil.concatenateFilePath(packageDir.getPath(), DIR_NAME.PACKAGE_SYSTEM_DIR_NAME,
					FILE_NAME.PACKAGE_XML_FILE_NAME);

			if (createBackup) {
				// バックアップファイル生成
				FileUtil.createBackup(rtsXmlFile.getPath());
			}

			JAXB.marshal(rtsProfile, rtsXmlFile);
		}
	}

	/**
	 * RtsProfileをコピーする
	 * 
	 * @param rtsProfileFrom
	 * @param rtsProfileTo
	 * @return
	 */
	public void copyRtsProfile(RtsProfile rtsProfileFrom, RtsProfile rtsProfileTo) {
		if (rtsProfileFrom != null && rtsProfileTo != null) {
			// 入れ替え
			BeanUtils.copyProperties(rtsProfileFrom, rtsProfileTo);
		}
	}

	/**
	 * Packageの変更を保存する
	 * 
	 * @param packageData
	 */
	public void updatePackage(String packageData) {

		// 作業領域パス
		String workspaceDirPath = PropUtil.getValue("workspace.local.directory.path");

		ObjectMapper mapper = new ObjectMapper();
		Rts updated = null;
		try {
			updated = mapper.readValue(packageData, Rts.class);
		} catch (IOException e) {
			logger.error("exception handled. ex:", e);
		}

		if (RtcUtil.rtsPorfileIsNotEmpty(updated)) {
			// 編集した項目を格納する
			updated.getRtsProfile().setComponents(CollectionUtil.toList(updated.getRtsProfile().getComponentMap()));
			for (Component component : updated.getRtsProfile().getComponents()) {
				component.setConfigurationSets(CollectionUtil.toList(component.getEditConfigurationSetMap()));
				component.setActiveConfigurationSet(component.getEditActiveConfigurationSet());
			}

			// 作業領域
			File destPackageDir = new File(workspaceDirPath + File.separator
					+ StringUtil.getPackageNameFromModelName(updated.getModelProfile().getModelId()));

			// ソースコードの保存
			if (CollectionUtil.isNotEmpty(updated.getEditSourceCode())) {
				// 編集したファイルを保存する
				for (Map.Entry<String, String> e : updated.getEditSourceCode().entrySet()) {
					saveCodeFile(e.getKey(), e.getValue());
				}
				// 編集した内容は破棄する
				updated.setEditSourceCode(new HashMap<>());
			}

			// ソースコードの削除
			if (CollectionUtil.isNotEmpty(updated.getDeleteFileList())) {
				// 画面側で削除されたファイルを削除する
				for (String filePath : updated.getDeleteFileList()) {
					deleteCodeFile(filePath);
				}
				// 削除リストは空にする
				updated.getDeleteFileList().clear();
			}

			// コンポーネントの保存
			if (CollectionUtil.isNotEmpty(updated.getRtcs())) {
				for (int i = 0; i < updated.getRtcs().size(); i++) {
					RtcProfile rtcProfile = updated.getRtcs().get(i).getRtcProfile();
					ModelProfile modelProfile = updated.getRtcs().get(i).getModelProfile();

					String rtcDirPath = StringUtil.concatenate(File.separator, destPackageDir.getPath(),
							DIR_NAME.PACKAGE_RTC_DIR_NAME, modelProfile.getModelName());
					if (!modelProfile.getClonedDirectory().contains(modelProfile.getModelName())) {
						rtcDirPath = StringUtil.concatenate(File.separator, destPackageDir.getPath(),
								DIR_NAME.PACKAGE_RTC_DIR_NAME, modelProfile.getGitName());
					}
					File rtcDir = new File(rtcDirPath);

					// 古いRTC構成情報を取得する
					Rtc oldRtc = loadRtcProfile(rtcDir, "", false);

					// RTC構成情報のXMLを出力する
					if (oldRtc == null || !oldRtc.getRtcProfile().equals(rtcProfile)) {
						// NN情報の設定をコンフィギュレーションに反映する
						refreactNNInfoChangedToConfiguration(rtcProfile,
								oldRtc == null ? null : oldRtc.getRtcProfile());
						// RTC.xmlを出力する
						saveRtcProfile(rtcDir, rtcProfile, true);
					} else {
						logger.info("Not Changed RtcProfile. id[" + rtcProfile.getId() + "]");
					}

					if (oldRtc != null) {
						// RTCの変更をソースコードに反映する
						reflectRtcChangeToSourceCode(rtcProfile, oldRtc.getRtcProfile(), rtcDirPath);

						// RTCの変更をコンフィグファイルに反映する
						reflectRtcChangeToConfigFile(rtcProfile, oldRtc.getRtcProfile(), rtcDirPath);

						// RTCの変更をRtsProfileに反映する
						refrectRtcChangeToRtsProfile(updated, rtcProfile, oldRtc.getRtcProfile());
					}
					
					// GitURLが変更されている場合は変更する
					String oldRemoteUrl = GitUtil.getGitUrl(rtcDir.getPath());
					if (!StringUtil.equals(updated.getRtcs().get(i).getModelProfile().getRemoteUrl(), oldRemoteUrl)) {
						GitUtil.changeRemoteUrl(rtcDir.getPath(), updated.getRtcs().get(i).getModelProfile().getRemoteUrl());
					}
				}
			}

			// 古いRTS構成情報を取得する
			Rts oldRts = loadRtsProfile(destPackageDir, false);

			if (!oldRts.getRtsProfile().equals(updated.getRtsProfile())) {
				// RTS構成情報のXMLを出力する
				saveRtsProfile(destPackageDir, updated.getRtsProfile(), true);
			} else {
				logger.info("Not Changed RtsProfile. id[" + updated.getRtsProfile().getId() + "]");
			}
			
			// GitURLが変更されている場合は変更する
			String oldRemoteUrl = GitUtil.getGitUrl(destPackageDir.getPath());
			if (!StringUtil.equals(updated.getModelProfile().getRemoteUrl(), oldRemoteUrl)) {
				GitUtil.changeRemoteUrl(destPackageDir.getPath(), updated.getModelProfile().getRemoteUrl());
			}
		}
	}

	/**
	 * NN情報の設定をコンフィグレーションに反映する
	 * 
	 * @param rtcProfile
	 * @param oldRtcProfile
	 */
	public void refreactNNInfoChangedToConfiguration(RtcProfile rtcProfile, RtcProfile oldRtcProfile) {
		// NN情報が変更されているかどうか
		boolean isNNInfoChanged = oldRtcProfile == null ? true
				: !oldRtcProfile.equalsNeuralNetworkInfo(rtcProfile.getNeuralNetworkInfo());
		if (isNNInfoChanged) {
			RtmEditorUtil.updateNeuralNetworkInfo(rtcProfile.getNeuralNetworkInfo(),
					oldRtcProfile == null ? null : oldRtcProfile.getNeuralNetworkInfo(), rtcProfile);
		}
	}

	/**
	 * RTCの変更をRtsProfileに反映する
	 * 
	 * @param rts
	 * @param rtcProfile
	 * @param oldRtcProfile
	 */
	private void refrectRtcChangeToRtsProfile(Rts rts, RtcProfile rtcProfile, RtcProfile oldRtcProfile) {
		// IDが変更されているかどうか
		boolean idChanged = !oldRtcProfile.getId().equals(rtcProfile.getId());

		// ActivityTypeが変更されているかどうか
		boolean isExecChanged = !oldRtcProfile.getBasicInfo().getActivityType()
				.equals(rtcProfile.getBasicInfo().getActivityType())
				|| !oldRtcProfile.getBasicInfo().getExecutionRate()
						.equals(rtcProfile.getBasicInfo().getExecutionRate());

		// コンフィギュレーションが変更されているかどうか
		boolean isConfigChanged = !oldRtcProfile.getConfigurationSet()
				.equalsConfigurations(rtcProfile.getConfigurationSet());

		// ID、モジュール名、実行コンテキスト、コンフィギュレーション設定が変わっている場合
		if (idChanged || isExecChanged || isConfigChanged) {
			// RtsProfileのコンポーネント部分を変更する
			logger.info("Change RtsProfile Components. id[" + rts.getRtsProfile().getId() + "]");
			reflectToRtsComponentList(rts, rtcProfile, oldRtcProfile, idChanged, isExecChanged, isConfigChanged);
		}

		// IDが変わっている場合
		if (idChanged) {
			// RtsProfileの接続情報部分を変更する
			logger.info("Change RtsProfile Connectors. id[" + rts.getRtsProfile().getId() + "]");
			reflectToRtsConnectorList(rts, rtcProfile, oldRtcProfile);
		}
	}

	/**
	 * RtcProfileの変更をRtsProfileのComponent部分に反映する
	 * 
	 * @param rts
	 * @param rtcProfile
	 * @param oldRtcProfile
	 * @param idChanged
	 * @param isExecChanged
	 * @param isConfigChanged
	 */
	private void reflectToRtsComponentList(Rts rts, RtcProfile rtcProfile, RtcProfile oldRtcProfile, boolean idChanged,
			boolean isExecChanged, boolean isConfigChanged) {
		for (int i = 0; i < rts.getRtsProfile().getComponents().size(); i++) {
			if (rts.getRtsProfile().getComponents().get(i).getId().equals(oldRtcProfile.getId())) {
				if (idChanged) {
					// IDを変更する
					logger.info("Change RtsProfile ID. id[" + rtcProfile.getId() + "]");
					rts.getRtsProfile().getComponents().get(i).setId(rtcProfile.getId());
				}
				if (isExecChanged) {
					// 実行コンテキストを変更する
					logger.info("Change RtsProfile ExecutionContext. " + "kind["
							+ rtcProfile.getBasicInfo().getActivityType() + "]rate["
							+ rtcProfile.getBasicInfo().getExecutionRate() + "]");
					rts.getRtsProfile().getComponents().get(i).getExecutionContexts().get(0)
							.setKind(rtcProfile.getBasicInfo().getActivityType());
					rts.getRtsProfile().getComponents().get(i).getExecutionContexts().get(0)
							.setRate(rtcProfile.getBasicInfo().getExecutionRate());
				}
				if (isConfigChanged) {
					// コンフィギュレーションを変更する
					List<ConfigurationSet> configurationSetList = convertConfigurationSet(rtcProfile);
					rts.getRtsProfile().getComponents().get(i).setConfigurationSets(configurationSetList);
				}
				break;
			}
		}
	}

	/**
	 * RtcProfileの変更をRtsProfileのConnectors部分に反映する
	 * 
	 * @param rts
	 * @param rtcProfile
	 * @param oldRtcProfile
	 */
	private void reflectToRtsConnectorList(Rts rts, RtcProfile rtcProfile, RtcProfile oldRtcProfile) {
		for (int i = 0; i < rts.getRtsProfile().getDataPortConnectors().size(); i++) {
			if (rts.getRtsProfile().getDataPortConnectors().get(i).getSourceDataPort().getComponentId()
					.equals(oldRtcProfile.getId())) {
				// 接続元のIDを変更する
				logger.info("Change RtsProfile Source Data Port. id[" + rtcProfile.getId() + "]");
				rts.getRtsProfile().getDataPortConnectors().get(i).getSourceDataPort()
						.setComponentId(rtcProfile.getId());
				break;
			} else if (rts.getRtsProfile().getDataPortConnectors().get(i).getTargetDataPort().getComponentId()
					.equals(oldRtcProfile.getId())) {
				// 接続先のIDを変更する
				logger.info("Change RtsProfile Target Data Port. id[" + rtcProfile.getId() + "]");
				rts.getRtsProfile().getDataPortConnectors().get(i).getTargetDataPort()
						.setComponentId(rtcProfile.getId());
				break;
			}
		}
	}

	/************************************************************
	 * RTC(Component)関連
	 ************************************************************/

	/**
	 * 新規RTC生成処理
	 * 
	 * @return
	 */
	public Rtc createNewRtcProfile(String languageKind) {
		Rtc rtc = new Rtc();

		String id = "";
		String moduleName = "";

		switch (languageKind) {
		case LANGUAGE_KIND.CPP:
			id = MODULE_NAME.NEW_CPP_ID;
			moduleName = MODULE_NAME.NEW_CPP;
			break;
		case LANGUAGE_KIND.PYTHON:
			id = MODULE_NAME.NEW_PYTHON_ID;
			moduleName = MODULE_NAME.NEW_PYTHON;
			break;
		case LANGUAGE_KIND.JAVA:
			id = MODULE_NAME.NEW_JAVA_ID;
			moduleName = MODULE_NAME.NEW_JAVA;
			break;
		default:
			return null;
		}

		rtc.getRtcProfile().setId(id);

		// コンポーネント名設定
		rtc.getRtcProfile().getBasicInfo().setModuleName(moduleName);
		rtc.getRtcProfile().getLanguage().setKind(languageKind);
		rtc.getModelProfile().setModelId("rtc_" + id);
		rtc.getModelProfile().setModelName(moduleName);

		// アクティビティ初期値
		rtc.getRtcProfile().getActions().getOnInitialize().setImplemented(true);
		rtc.getRtcProfile().getActions().getOnActivated().setImplemented(true);
		rtc.getRtcProfile().getActions().getOnDeactivated().setImplemented(true);
		rtc.getRtcProfile().getActions().getOnExecute().setImplemented(true);
		
		// git初期値
		rtc.getModelProfile().setRemoteUrl(PropUtil.getValue("default.git.url.base") + "new_component.git");

		return rtc;
	}

	/**
	 * すべてのRtcをローカルにCloneする
	 * 
	 * @param bindersLocalDirPath
	 * @param packagesLocalDirPath
	 * @param rtcsLocalDirPath
	 */
	public void cloneRtcsFromBinder(String binderDirPath) {

		// Rtcsローカルリポジトリ格納先
		String rtcsLocalDirPath = PropUtil.getValue("rtcs.local.directory.path");

		// Binderのrtc.yamlの保存先
		String binderRtcDirPath = binderDirPath + "/rtcs/";

		logger.info("Rtc展開処理開始.binderDirPath[" + binderDirPath + "]rtcsLocalDirPath[" + rtcsLocalDirPath
				+ "]binderRtcDirPath[" + binderRtcDirPath + "]");

		// Yamlの一覧を取得する
		File binderRtcDir = new File(binderRtcDirPath);
		File[] yamls = binderRtcDir.listFiles();

		if (CollectionUtil.isNotEmpty(yamls)) {
			for (File rtcYaml : yamls) {
				// Rtc設定ファイルを読み込む
				@SuppressWarnings("unchecked")
				// Map形式で読み込む
				Map<String, Map<String, String>> packageRtcSettings = (Map<String, Map<String, String>>) YamlUtil
						.loadYamlFromFile(rtcYaml.getPath());
				// Rtcが定義されている
				if (CollectionUtil.isNotEmpty(packageRtcSettings)) {
					for (Map<String, String> setting : packageRtcSettings.values()) {
						// 対象はGitのみ
						if ("git".equals(setting.get("type"))) {
							// GitURLからディレクトリパスを生成する
							String dirPath = GitUtil.createGitDirPath(rtcsLocalDirPath, setting.get("url"));
							File dirFile = new File(dirPath);
							if (FileUtil.notExists(dirFile)) {
								logger.info("リモートリポジトリをローカルにCloneする url[" + setting.get("url") + "]local path["
										+ dirPath + "]");
								GitUtil.gitClone(setting.get("url"), dirPath, rtcsLocalDirPath);
							}
						}
					}
				}
			}
		}
		logger.info("Rtc(From Binder)展開処理終了.");
	}

	/**
	 * 指定されたディレクトリ配下にあるすべてのRTC読込処理<br>
	 * ソースコードを読み込むかどうかを指定可能
	 * 
	 * @param rtcParentDirPath
	 * @param modelIdBase
	 * @param loadSourceFile
	 * @return
	 */
	public List<Rtc> loadRtcProfiles(File rtcParentDir, String modelIdBase, boolean loadSourceFile) {
		List<Rtc> rtcList = new ArrayList<Rtc>();
		int index = 0;

		if (FileUtil.exists(rtcParentDir)) {
			File[] rtcDirs = rtcParentDir.listFiles();
			if (CollectionUtil.isNotEmpty(rtcDirs)) {
				for (File file : rtcDirs) {
					if (file.isDirectory() && !file.getName().matches("^[\\.].+")) {
						Rtc rtc = loadRtcProfile(file, modelIdBase + String.valueOf(index++), loadSourceFile);
						if (rtc != null) {
							rtcList.add(rtc);
						}
					}
				}
			}
		}
		// ソートする
		rtcList = CollectionUtil.sort(rtcList, rts -> rts.getModelProfile().getModelName());
		return rtcList;
	}

	/**
	 * 指定RTC読込処理<br>
	 * ソースコードを読み込むかどうかを指定可能
	 * 
	 * @param rtcDir
	 * @param modelId
	 * @param loadSourceFile
	 * @return
	 */
	public Rtc loadRtcProfile(File rtcDir, String modelId, boolean loadSourceFile) {
		if (FileUtil.exists(rtcDir)) {
			Rtc rtc = new Rtc();
			// RTC.xml読み込み
			File rtcXmlFile = new File(rtcDir.getPath() + File.separator + FILE_NAME.RTC_XML_FILE_NAME);
			if (rtcXmlFile != null && rtcXmlFile.exists()) {
				// RTC.xmlをオブジェクト化
				RtcProfile profile = JAXB.unmarshal(rtcXmlFile, RtcProfile.class);
				if (profile != null && profile.getBasicInfo() != null) {
					logger.debug("Load RTC.xml. id[" + profile.getId() + "]");
					rtc.setRtcProfile(profile);
					// 描画用にID,Nameを生成する
					rtc.getModelProfile().setModelId(modelId);
					rtc.getModelProfile().setModelName(profile.getBasicInfo().getModuleName());
					// GitのURLを取得し、Gitの名称を取得する
					String gitUrl = GitUtil.getGitUrl(rtcDir.getPath());
					if (StringUtil.isNotEmpty(gitUrl)) {
						rtc.getModelProfile().setGitName(GitUtil.createGitName(gitUrl));
					} else {
						rtc.getModelProfile().setGitName(rtcDir.getName());
					}
					rtc.getModelProfile().setRemoteUrl(gitUrl);
					rtc.getModelProfile().setClonedDirectory(rtcDir.getPath());

					// IDLファイルパスはファイルパスからファイル名に変更する
					if (CollectionUtil.isNotEmpty(profile.getServicePorts())) {
						for (int i = 0; i < profile.getServicePorts().size(); i++) {
							com.sec.rtc.entity.rtc.ServicePort servicePort = profile.getServicePorts().get(i);
							if (CollectionUtil.isNotEmpty(servicePort.getServiceInterfaces())) {
								for (int j = 0; j < servicePort.getServiceInterfaces().size(); j++) {
									ServiceInterface serviceIf = servicePort.getServiceInterfaces().get(j);
									if (StringUtil.isNotEmpty(serviceIf.getIdlFile())) {
										if (serviceIf.getIdlFile().contains("\\")) {
											String fileName = serviceIf.getIdlFile()
													.substring(serviceIf.getIdlFile().lastIndexOf("\\") + 1);
											rtc.getRtcProfile().getServicePorts().get(i).getServiceInterfaces().get(j)
													.setIdlFile(fileName);
										} else {
											File idlFile = new File(serviceIf.getIdlFile());
											if (idlFile != null) {
												rtc.getRtcProfile().getServicePorts().get(i).getServiceInterfaces()
														.get(j).setIdlFile(idlFile.getName());
											}
										}
									}
									rtc.getRtcProfile().getServicePorts().get(i).getServiceInterfaces().get(j)
											.setPath(null);
								}
							}
						}
					}

					if (loadSourceFile) {
						// ソースコード読み込み
						String[] targetCodeDir = new String[] { DIR_NAME.COMP_INCLUDE_DIR_NAME,
								DIR_NAME.COMP_SRC_DIR_NAME, DIR_NAME.COMP_IDL_DIR_NAME,
								rtc.getRtcProfile().getBasicInfo().getModuleName() };
						String[] targetCodeSuffix = { "c", "cpp", "h", "hpp", "py", "java", "txt", "yml", "xml",
								"idl" };
						String[] ignoreFile = new String[] { FILE_NAME.RTC_XML_FILE_NAME };
						if (LANGUAGE_KIND.CPP.equals(rtc.getRtcProfile().getLanguage().getKind())) {
							// C++
							targetCodeDir = new String[] { DIR_NAME.COMP_INCLUDE_DIR_NAME, DIR_NAME.COMP_SRC_DIR_NAME,
									DIR_NAME.COMP_IDL_DIR_NAME, rtc.getRtcProfile().getBasicInfo().getModuleName().toLowerCase() };
						} else if (LANGUAGE_KIND.PYTHON.equals(rtc.getRtcProfile().getLanguage().getKind())) {
							// Python
							targetCodeDir = new String[] { DIR_NAME.COMP_IDL_DIR_NAME };
						} else if (LANGUAGE_KIND.JAVA.equals(rtc.getRtcProfile().getLanguage().getKind())) {
							// Java
							targetCodeDir = new String[] { DIR_NAME.COMP_SRC_DIR_NAME, DIR_NAME.COMP_IDL_DIR_NAME };
						}

						rtc.setCodeDirectory(getCodeFile(rtcDir.getPath(), targetCodeDir, targetCodeSuffix, ignoreFile,
								rtc.getPathContentMap()));

						// RTC.conf読み込み
						rtc.getCodeDirectory().getCodePathMap().put(
								profile.getBasicInfo().getModuleName() + FILE_SUFFIX.SUFFIX_CONFIG,
								rtcDir.getPath() + File.separator + profile.getBasicInfo().getModuleName()
										+ FILE_SUFFIX.SUFFIX_CONFIG);
						rtc.getPathContentMap()
								.put(rtcDir.getPath() + File.separator + profile.getBasicInfo().getModuleName()
										+ FILE_SUFFIX.SUFFIX_CONFIG,
										loadConfigFile(rtcDir.getPath(), profile.getBasicInfo().getModuleName()));
					}

				} else {
					logger.warn("Failed to load RTC.xml path[" + rtcXmlFile.getPath() + "]");
				}
			}
			return rtc;
		}
		return null;
	}

	/**
	 * 指定RTC書込処理
	 * 
	 * @param rtcDir
	 * @param rtcProfile
	 * @param createBackup
	 */
	public void saveRtcProfile(File rtcDir, RtcProfile rtcProfile, boolean createBackup) {
		if (FileUtil.exists(rtcDir)) {
			// DefaultSystem.xml書き込み
			File rtcXmlFile = new File(rtcDir.getPath() + File.separator + FILE_NAME.RTC_XML_FILE_NAME);

			if (createBackup) {
				// バックアップファイル生成
				FileUtil.createBackup(rtcXmlFile.getPath());
			}

			// 出力
			rtcProfile.setVersion("0.2");
			rtcProfile.getBasicInfo().setSaveProject(rtcProfile.getBasicInfo().getModuleName());
			JAXB.marshal(rtcProfile, rtcXmlFile);
		}
	}

	/**
	 * RtcProfileからPackageのComponent情報を生成する
	 * 
	 * @param rtcProfile
	 * @return
	 */
	public Component createRtsComponentInfo(RtcProfile rtcProfile) {
		// コンポーネント情報
		Component component = new Component();
		component.setId(rtcProfile.getId());
		component.setInstanceName(rtcProfile.getBasicInfo().getModuleName() + MODULE_INSTANCE_SUFFIX);
		component.setPathUri("localhost/" + component.getInstanceName() + FILE_SUFFIX.SUFFIX_RTC);
		component.setCompositeType("None");
		component.setIsRequired(true);
		component.setActiveConfigurationSet("default");
		component.setVisible(true);

		// データポート
		if (CollectionUtil.isNotEmpty(rtcProfile.getDataPorts())) {
			List<DataPort> dataPortList = new ArrayList<DataPort>();
			for (com.sec.rtc.entity.rtc.DataPort rtcPort : rtcProfile.getDataPorts()) {
				DataPort dataPort = new DataPort();
				dataPort.setName(rtcPort.getName());
				dataPort.setVisible(true);
				dataPortList.add(dataPort);
			}
			component.setDataPorts(dataPortList);
		}

		// サービスポート
		if (CollectionUtil.isNotEmpty(rtcProfile.getServicePorts())) {
			List<ServicePort> servicePortList = new ArrayList<ServicePort>();
			for (com.sec.rtc.entity.rtc.ServicePort rtcPort : rtcProfile.getServicePorts()) {
				ServicePort sericePort = new ServicePort();
				sericePort.setName(rtcPort.getName());
				sericePort.setVisible(true);
				servicePortList.add(sericePort);
			}
			component.setServicePorts(servicePortList);
		}

		// コンフィギュレーション設定
		List<ConfigurationSet> configurationSetList = convertConfigurationSet(rtcProfile);
		component.setConfigurationSets(configurationSetList);

		// 実行コンテキスト
		List<ExecutionContext> executionContextList = new ArrayList<ExecutionContext>();
		ExecutionContext executionContext = new ExecutionContext();
		executionContext.setId("0");
		executionContext.setKind(rtcProfile.getBasicInfo().getActivityType());
		executionContext.setRate(rtcProfile.getBasicInfo().getExecutionRate());
		executionContextList.add(executionContext);
		component.setExecutionContexts(executionContextList);

		// 位置
		Location location = new Location();
		location.setDirection("DOWN");
		location.setHeight(0);
		location.setWidth(0);
		location.setX(0);
		location.setY(0);

		return component;
	}

	/**
	 * 接続情報を生成する
	 * 
	 * @param srcId
	 * @param srcInstanceName
	 * @param srcPortName
	 * @param srcPathUri
	 * @param destId
	 * @param destInstanceName
	 * @param destPortName
	 * @param destPathUri
	 * @param dataType
	 * @param connectorType
	 * @return
	 */
	public DataPortConnector createDataPortConnector(String srcId, String srcInstanceName, String srcPortName,
			String srcPathUri, String destId, String destInstanceName, String destPortName, String destPathUri,
			String dataType, String connectorType) {

		// 接続情報
		DataPortConnector dataPortConnector = new DataPortConnector();
		dataPortConnector.setConnectorId(UUID.randomUUID().toString());
		dataPortConnector.setName(srcInstanceName + "." + srcPortName + "_" + destInstanceName + "." + destPortName);
		dataPortConnector.setDataType(RtcUtil.getConnectionPortDataType(dataType));
		dataPortConnector.setDataflowType(DATAFLOW_TYPE.PUSH);
		dataPortConnector.setInterfaceType(INTERFACE_TYPE.CORBA_CDR);
		dataPortConnector.setPushInterval(0.0);
		dataPortConnector.setSubscriptionType(SUBSCRIPTION_TYPE.NEW);
		if (!COMPONENT_CONNECTOR_TYPE.LOGGER.equals(connectorType)) {
			dataPortConnector.setVisible(true);
		} else {
			dataPortConnector.setVisible(false);
		}
		dataPortConnector.setConnectorType(connectorType);

		// 接続元
		ConnectionDataPort sourceDataPort = new ConnectionDataPort();
		sourceDataPort.setComponentId(srcId);
		sourceDataPort.setPortName(srcInstanceName + "." + srcPortName);
		sourceDataPort.setInstanceName(srcInstanceName);
		sourceDataPort.getProperties().add(new Property("COMPONENT_PATH_ID", srcPathUri));
		dataPortConnector.setSourceDataPort(sourceDataPort);

		// 接続先
		ConnectionDataPort targetDataPort = new ConnectionDataPort();
		targetDataPort.setComponentId(destId);
		targetDataPort.setPortName(destInstanceName + "." + destPortName);
		targetDataPort.setInstanceName(destInstanceName);
		targetDataPort.getProperties().add(new Property("COMPONENT_PATH_ID", destPathUri));
		dataPortConnector.setTargetDataPort(targetDataPort);

		// その他の情報
		Property prop1 = new Property("dataport.dataflow_type", DATAFLOW_TYPE.PUSH);
		Property prop2 = new Property("dataport.serializer.cdr.endian", "little,big");
		Property prop3 = new Property("dataport.data_type", RtcUtil.getConnectionPortDataType(dataType));
		Property prop4 = new Property("dataport.interface_type", INTERFACE_TYPE.CORBA_CDR);
		Property prop5 = new Property("dataport.subscription_type", SUBSCRIPTION_TYPE.NEW);
		dataPortConnector.getProperties().add(prop1);
		dataPortConnector.getProperties().add(prop2);
		dataPortConnector.getProperties().add(prop3);
		dataPortConnector.getProperties().add(prop4);
		dataPortConnector.getProperties().add(prop5);

		return dataPortConnector;
	}

	/**
	 * RTCのコンフィギュレーション設定をRTSのコンフィギュレーション設定に変換する
	 * 
	 * @param rtcProfile
	 * @return
	 */
	private List<ConfigurationSet> convertConfigurationSet(RtcProfile rtcProfile) {
		List<ConfigurationSet> configurationSetList = new ArrayList<ConfigurationSet>();
		ConfigurationSet configurationSet = new ConfigurationSet();
		configurationSet.setId("default");
		if (rtcProfile.getConfigurationSet() != null
				&& CollectionUtil.isNotEmpty(rtcProfile.getConfigurationSet().getConfigurations())) {
			List<ConfigurationData> configurationDataList = new ArrayList<ConfigurationData>();
			for (Configuration rtcConfig : rtcProfile.getConfigurationSet().getConfigurations()) {
				ConfigurationData configurationData = new ConfigurationData();
				configurationData.setData(rtcConfig.getDefaultValue());
				configurationData.setName(rtcConfig.getName());
				configurationDataList.add(configurationData);
			}
			configurationSet.setConfigurationDatas(configurationDataList);
		}
		configurationSetList.add(configurationSet);
		return configurationSetList;
	}

	/************************************************************
	 * ソースコード関連
	 ************************************************************/
	/**
	 * コンフィグファイルを読み込む
	 * 
	 * @param rtcDirPath
	 * @param moduleName
	 * @return
	 */
	private String loadConfigFile(String rtcDirPath, String moduleName) {
		String ret = "";
		File configFile = FileUtil.concatenateFilePath(rtcDirPath, moduleName + FILE_SUFFIX.SUFFIX_CONFIG);
		if (FileUtil.exists(configFile)) {
			ret = FileUtil.readAll(configFile.getPath());
		}
		return ret;
	}

	/**
	 * ソースコードを階層的に読み込む
	 * 
	 * @param dirPath
	 * @return
	 */
	private CodeDirectory getCodeFile(String dirPath, String[] targetDirName, String[] targetCodeSuffix,
			String[] ignoreFileName, Map<String, String> pathContentMap) {
		File dir = new File(dirPath);
		if (FileUtil.exists(dir)) {
			CodeDirectory parent = new CodeDirectory();
			parent.setCurDirName(dir.getName());
			parent.setDirPath(dirPath);
			File[] codes = dir.listFiles();
			if (CollectionUtil.isNotEmpty(codes)) {
				for (File code : codes) {
					if (code.isDirectory() && (targetDirName == null
							|| Arrays.asList(targetDirName).contains(code.getName().toLowerCase()))) {
						CodeDirectory directory = getCodeFile(code.getPath(), targetDirName, targetCodeSuffix,
								ignoreFileName, pathContentMap);
						if (directory != null) {
							parent.getDirectoryMap().put(code.getName(), directory);
						}
					} else if (Arrays.asList(targetCodeSuffix)
							.contains(FileUtil.getFileSuffix(code.getName()).toLowerCase())
							&& !Arrays.asList(ignoreFileName).contains(code.getName())) {
						// ソースコードの場合
						String codeStr = FileUtil.readAll(code.getPath());
						// ファイル名とファイルの絶対パスをMAP化する
						parent.getCodePathMap().put(code.getName(), code.getPath());
						// ファイルの絶対パスとコードをMAP化する
						pathContentMap.put(code.getPath(), codeStr);
					}
				}
			}
			return parent;
		}
		return null;
	}

	/**
	 * 編集したファイルを保存する
	 * 
	 * @param filePath
	 * @param codeStr
	 */
	private void saveCodeFile(String filePath, String codeStr) {
		FileUtil.writeAll(filePath, codeStr);
	}

	/**
	 * ファイルを削除する
	 * 
	 * @param filePath
	 */
	private void deleteCodeFile(String filePath) {
		FileUtil.deleteFile(filePath);
	}

	/**
	 * RtcProfileの変更をソースコードに反映する
	 * 
	 * @param rtcProfile
	 * @param rtcDirPath
	 * @param oldRtcProfile
	 */
	private void reflectRtcChangeToSourceCode(RtcProfile rtcProfile, RtcProfile oldRtcProfile, String rtcDirPath) {
		// スペック情報が変更されているかどうか
		boolean isSpecChanged = !oldRtcProfile.getBasicInfo().equalModuleSpec(rtcProfile.getBasicInfo());

		// コンポーネント種類が変更されているかどうか
		boolean isComponentKindChanged = !oldRtcProfile.getBasicInfo().getComponentKind()
				.equals(rtcProfile.getBasicInfo().getComponentKind());

		// データインポートが変更されているかどうか
		boolean isDataInPortChanged = !oldRtcProfile.equalsInportDeclares(rtcProfile);

		// データアウトポートが変更されているかどうか
		boolean isDataOutPortChanged = !oldRtcProfile.equalsOutportDeclares(rtcProfile);

		// サービスポートが変更されているかどうか
		boolean isServicePortChanged = !oldRtcProfile.equalsServiceportDeclares(rtcProfile);

		// サービスインタフェースが変更されているかどうか
		boolean isProvidedInterfaceChanged = !oldRtcProfile.equalsProvidedServiceInterfaceDeclares(rtcProfile);
		boolean isRequiredInterfaceChaned = !oldRtcProfile.equalsRequiredServiceInterfaceDeclares(rtcProfile);

		// コンフィギュレーションが変更されているかどうか
		boolean isConfigurationChanged = !oldRtcProfile.getConfigurationSet()
				.equalsConfigurations(rtcProfile.getConfigurationSet());

		// アクティビティが変更されているかどうか
		boolean isActivityChanged = !oldRtcProfile.getActions().equalsActions(rtcProfile.getActions());

		// 一つでも変更されているかどうか
		boolean isChanged = isSpecChanged | isComponentKindChanged | isDataInPortChanged | isDataOutPortChanged
				| isServicePortChanged | isProvidedInterfaceChanged | isRequiredInterfaceChaned | isConfigurationChanged
				| isActivityChanged;

		if (isChanged && LANGUAGE_KIND.CPP.equals(rtcProfile.getLanguage().getKind())) {
			// C++
			logger.info("Change Sorce Code(C++). target directory[" + rtcDirPath + "]");

			File sourceFile = FileUtil.concatenateFilePath(rtcDirPath, DIR_NAME.COMP_SRC_DIR_NAME,
					rtcProfile.getBasicInfo().getModuleName() + FILE_SUFFIX.SUFFIX_CPP);
			File headerFile = FileUtil.concatenateFilePath(rtcDirPath, DIR_NAME.COMP_INCLUDE_DIR_NAME,
					rtcProfile.getBasicInfo().getModuleName(),
					rtcProfile.getBasicInfo().getModuleName() + FILE_SUFFIX.SUFFIX_CPP_HEADER);

			if (FileUtil.exists(sourceFile) && FileUtil.exists(headerFile)) {
				// バックアップファイル生成
				FileUtil.createBackup(sourceFile.getPath());
				FileUtil.createBackup(headerFile.getPath());

				if (isSpecChanged || isConfigurationChanged) {
					// ソース側のmodule_specを更新する
					updateModuleSpecCpp(rtcProfile, sourceFile);
				}

				if (isComponentKindChanged || isDataInPortChanged || isDataOutPortChanged || isServicePortChanged
						|| isProvidedInterfaceChanged || isRequiredInterfaceChaned) {
					// ソース側のinitializerを更新する
					updateInitializerCpp(rtcProfile, sourceFile);

					// ソース側のregistrationを更新する
					updateRegistrationCpp(rtcProfile, sourceFile);
				}

				if (isConfigurationChanged) {
					// ソース側のbind_configを更新する
					updateBindConfigCpp(rtcProfile, sourceFile);
				}

				if (isDataInPortChanged || isDataOutPortChanged) {
					// ヘッダ側のport_stub_hを更新する
					updatePortStubHeaderCppHeader(rtcProfile, headerFile);
				}

				if (isComponentKindChanged) {
					// ヘッダ側のコンポーネント種別関連を更新する
					updateComponentKindCppHeader(rtcProfile, headerFile);
				}

				if (isDataInPortChanged) {
					// ヘッダ側のinport_declareを更新する
					updateInportDeclareCppHeader(rtcProfile, headerFile);
				}

				if (isDataOutPortChanged) {
					// ヘッダ側のoutport_declareを更新する
					updateOutportDeclareCppHeader(rtcProfile, headerFile);
				}

				if (isServicePortChanged) {
					// ヘッダ側のcorbaport_declareを更新する
					updateCorbaportDeclareCppHeader(rtcProfile, headerFile);
				}

				if (isProvidedInterfaceChanged) {
					// ヘッダ側のservice_impl_hを更新する
					updateServiceImplementHeaderCppHeader(rtcProfile, headerFile);

					// ヘッダ側のservice_declareを更新する
					updateServiceDeclareCppHeader(rtcProfile, headerFile);

					// IDLファイルの設定を行う
					updateServiceProviderImplFileCpp(rtcProfile, rtcDirPath);
				}

				if (isRequiredInterfaceChaned) {
					// ヘッダ側のconsumer_stub_hを更新する
					updateConsumerStubHeaderCppHeader(rtcProfile, headerFile);

					// ヘッダ側のconsumer_declareを更新する
					updateConsumerDeclareCppHeader(rtcProfile, headerFile);
				}

				if (isConfigurationChanged) {
					// ヘッダ側のconfig_declareを変更する
					updateConfigDeclareCppHeader(rtcProfile, headerFile);
				}

				if (isActivityChanged) {
					// ヘッダ・ソースのメソッドをコメントを変更する
					updateCommentMethodCpp(rtcProfile.getActions(), oldRtcProfile.getActions(), sourceFile, headerFile);
				}
			}
		} else if (isChanged && LANGUAGE_KIND.PYTHON.equals(rtcProfile.getLanguage().getKind())) {
			// Python
			logger.info("Change Sorce Code(Python). target directory[" + rtcDirPath + "]");

			File sourceFile = FileUtil.concatenateFilePath(rtcDirPath,
					rtcProfile.getBasicInfo().getModuleName() + FILE_SUFFIX.SUFFIX_PYTHON);

			if (FileUtil.exists(sourceFile)) {
				// バックアップファイル生成
				FileUtil.createBackup(sourceFile.getPath());

				if (isSpecChanged || isConfigurationChanged) {
					// module_specを更新する
					updateModuleSpecPython(rtcProfile, sourceFile);
				}

				if (isComponentKindChanged || isDataInPortChanged || isDataOutPortChanged || isServicePortChanged
						|| isProvidedInterfaceChanged || isRequiredInterfaceChaned) {
					// コンストラクタを更新する
					updateConstructorPython(rtcProfile, sourceFile);
				}

				if (isComponentKindChanged) {
					// コンポーネント種類（クラス宣言）を更新する
					updateComponentKindPython(rtcProfile, sourceFile);
				}

				if (isConfigurationChanged || isDataInPortChanged || isDataOutPortChanged || isServicePortChanged
						|| isProvidedInterfaceChanged || isRequiredInterfaceChaned) {
					// onInitializeを更新する
					updateOnInitializePython(rtcProfile, sourceFile);
				}

				if (isProvidedInterfaceChanged || isRequiredInterfaceChaned) {
					// IDLのインポート文を更新する
					updateIdlImportPython(rtcProfile, sourceFile);
				}

				if (isProvidedInterfaceChanged) {
					// service_implを更新する
					updateServiceImplementPython(rtcProfile, sourceFile);
				}

				if (isRequiredInterfaceChaned) {
					// consumer_importを更新する
					updateConsumerImportPython(rtcProfile, sourceFile);
				}

				if (isConfigurationChanged) {
					// init_conf_paramを更新する
					updateInitConfParamPython(rtcProfile, sourceFile);
				}

				if (isProvidedInterfaceChanged || isRequiredInterfaceChaned) {
					// IDLファイルの設定を行う
					updateServiceProviderConsumerIdlFilePython(rtcProfile, rtcDirPath);
				}

				if (isActivityChanged) {
					// メソッドのコメントを変更する
					updateCommentMethodPython(rtcProfile.getActions(), oldRtcProfile.getActions(), sourceFile);
				}
			}
		}
	}

	/**
	 * コンフィグファイルに変更を反映する
	 * 
	 * @param rtcProfile
	 * @param oldRtcProfile
	 * @param rtcDirPath
	 */
	private void reflectRtcChangeToConfigFile(RtcProfile rtcProfile, RtcProfile oldRtcProfile, String rtcDirPath) {
		// ExecutionRate
		boolean isExecutionRateChanged = !oldRtcProfile.getBasicInfo().getExecutionRate()
				.equals(rtcProfile.getBasicInfo().getExecutionRate());

		File configFile = FileUtil.concatenateFilePath(rtcDirPath,
				rtcProfile.getBasicInfo().getModuleName() + FILE_SUFFIX.SUFFIX_CONFIG);

		if (isExecutionRateChanged) {
			if (FileUtil.exists(configFile)) {
				// バックアップファイル生成
				FileUtil.createBackup(configFile.getPath());

				// ExecutionRateの自動反映
				logger.info("Change Config File Exec Rate. target directory[" + rtcDirPath + "]");
				RtcUtil.updateExecutionRateRtcConfig(configFile.getPath(), rtcProfile.getBasicInfo().getExecutionRate());
			}
		}
	}

	/************************************************************
	 * C++用のソースコード自動反映処理
	 ************************************************************/
	/**
	 * C++のソースファイルにmodule_specの変更を反映する
	 * 
	 * @param rtcProfile
	 * @param sourceFile
	 */
	public void updateModuleSpecCpp(RtcProfile rtcProfile, File sourceFile) {
		List<String> newModuleSpecList = RtcUtil.createModuleSpecForCpp(rtcProfile);
		RtcUtil.updateModuleSpecCpp(sourceFile.getPath(), newModuleSpecList);
	}

	/**
	 * C++のソースファイルにinitializerの変更を反映する
	 * 
	 * @param rtcProfile
	 * @param sourceFile
	 */
	private void updateInitializerCpp(RtcProfile rtcProfile, File sourceFile) {
		List<String> newInitializerList = RtcUtil.createInitializerCppSource(rtcProfile);
		RtcUtil.updateInitializerCpp(sourceFile.getPath(), newInitializerList);
	}

	/**
	 * C++のソースファイルにregistrationの変更を反映する
	 * 
	 * @param rtcProfile
	 * @param sourceFile
	 */
	private void updateRegistrationCpp(RtcProfile rtcProfile, File sourceFile) {
		List<String> newRegistrationList = RtcUtil.createRegistrationCppSource(rtcProfile);
		RtcUtil.updateRegistrationCpp(sourceFile.getPath(), newRegistrationList);
	}

	/**
	 * C++のソースファイルにbind_configの変更を反映する
	 * 
	 * @param rtcProfile
	 * @param sourceFile
	 */
	public void updateBindConfigCpp(RtcProfile rtcProfile, File sourceFile) {
		List<String> newBindConfigList = RtcUtil.createBindConfigCppSource(rtcProfile);
		RtcUtil.updateBindConfigCpp(sourceFile.getPath(), newBindConfigList);
	}

	/**
	 * C++のヘッダファイルにservice_impl_hの変更を反映する
	 * 
	 * @param rtcProfile
	 * @param headerFile
	 */
	private void updateServiceImplementHeaderCppHeader(RtcProfile rtcProfile, File headerFile) {
		List<String> newServiceImplementHeaderList = RtcUtil.createServiceImplementHeaderCppHeader(rtcProfile);
		RtcUtil.updateServiceImplheaderCppHeader(headerFile.getPath(), newServiceImplementHeaderList);
	}

	/**
	 * C++のヘッダファイルにconsumer_stub_hの変更を反映する
	 * 
	 * @param rtcProfile
	 * @param headerFile
	 */
	private void updateConsumerStubHeaderCppHeader(RtcProfile rtcProfile, File headerFile) {
		List<String> newConsumerStubHeaderList = RtcUtil.createConsumerStubHeaderCppHeader(rtcProfile);
		RtcUtil.updateConsumerStubheaderCppHeader(headerFile.getPath(), newConsumerStubHeaderList);
	}

	/**
	 * C++のヘッダファイルにport_stub_hの変更を反映する
	 * 
	 * @param rtcProfile
	 * @param headerFile
	 */
	private void updatePortStubHeaderCppHeader(RtcProfile rtcProfile, File headerFile) {
		List<String> newPortStubHeaderList = RtcUtil.createPortStubHeaderCppHeader(rtcProfile);
		RtcUtil.updatePortStubheaderCppHeader(headerFile.getPath(), newPortStubHeaderList);
	}

	/**
	 * C++のヘッダファイルにコンポーネント種類の変更を反映する
	 * 
	 * @param rtcProfile
	 * @param headerFile
	 */
	private void updateComponentKindCppHeader(RtcProfile rtcProfile, File headerFile) {
		RtcUtil.updateComponentKindCppHeader(headerFile.getPath(), rtcProfile.getBasicInfo().getModuleName(),
				rtcProfile.getBasicInfo().getComponentKind());
	}

	/**
	 * C++のヘッダファイルにconfig_declareの変更を反映する
	 * 
	 * @param rtcProfile
	 * @param headerFile
	 */
	public void updateConfigDeclareCppHeader(RtcProfile rtcProfile, File headerFile) {
		List<String> newConfigDeclareHeaderList = RtcUtil.craeteConfigDeclareListCppHeader(rtcProfile);
		RtcUtil.updateConfigDeclareCppHeader(headerFile.getPath(), newConfigDeclareHeaderList);
	}

	/**
	 * C++のヘッダファイルにinport_declareの変更を反映する
	 * 
	 * @param rtcProfile
	 * @param headerFile
	 */
	private void updateInportDeclareCppHeader(RtcProfile rtcProfile, File headerFile) {
		List<String> newInportDeclareHeaderList = RtcUtil.createInportDeclareListCppHeader(rtcProfile);
		RtcUtil.updateInportDeclareCppHeader(headerFile.getPath(), newInportDeclareHeaderList);
	}

	/**
	 * C++のヘッダファイルにoutport_declareの変更を反映する
	 * 
	 * @param rtcProfile
	 * @param headerFile
	 */
	private void updateOutportDeclareCppHeader(RtcProfile rtcProfile, File headerFile) {
		List<String> newOutportDeclareHeaderList = RtcUtil.createOutportDeclareListCppHeader(rtcProfile);
		RtcUtil.updateOutportDeclareCppHeader(headerFile.getPath(), newOutportDeclareHeaderList);
	}

	/**
	 * C++のヘッダファイルにcorbaport_declareの変更を反映する
	 * 
	 * @param rtcProfile
	 * @param rtcDirPath
	 * @param headerFile
	 * @param rtcTemplate
	 */
	private void updateCorbaportDeclareCppHeader(RtcProfile rtcProfile, File headerFile) {
		List<String> newCorbaportDeclareHeaderList = RtcUtil.createCorbaportDeclareListCppHeader(rtcProfile);
		RtcUtil.updateCorbaportDeclareCppHeader(headerFile.getPath(), newCorbaportDeclareHeaderList);
	}

	/**
	 * C++のヘッダファイルにservice_declareの変更を反映する
	 * 
	 * @param rtcProfile
	 * @param headerFile
	 */
	private void updateServiceDeclareCppHeader(RtcProfile rtcProfile, File headerFile) {
		List<String> newServiceDeclareHeaderList = RtcUtil.createServiceDeclareListCppHeader(rtcProfile);
		RtcUtil.updateServiceDeclareCppHeader(headerFile.getPath(), newServiceDeclareHeaderList);
	}

	/**
	 * C++のヘッダファイルにconsumer_declareの変更を反映する
	 * 
	 * @param rtcProfile
	 * @param headerFile
	 */
	private void updateConsumerDeclareCppHeader(RtcProfile rtcProfile, File headerFile) {
		List<String> newConsumerDeclareHeaderList = RtcUtil.createConsumerDeclareListCppHeader(rtcProfile);
		RtcUtil.updateConsumerDeclareCppHeader(headerFile.getPath(), newConsumerDeclareHeaderList);
	}

	/**
	 * IDLファイルの設定およびImplファイルの生成を行う
	 * 
	 * @param rtcProfile
	 * @param rtcDirPath
	 */
	private void updateServiceProviderImplFileCpp(RtcProfile rtcProfile, String rtcDirPath) {
		RtcUtil.createServiceProviderImplFileCpp(rtcProfile, rtcDirPath);
	}

	/**
	 * Cppのソース・ヘッダファイルの各アクティビティメソッドの有効無効を切り替える
	 * 
	 * @param rtcProfile
	 * @param oldRtcProfile
	 * @param sourceFile
	 * @param headerFile
	 */
	public void updateCommentMethodCpp(Actions actions, Actions oldActions, File sourceFile, File headerFile) {
		// onInitialize
		if (!actions.getOnInitialize().getImplemented().equals(oldActions.getOnInitialize().getImplemented())) {
			RtcUtil.changeCommentMethodCppHeader(headerFile.getPath(), "onInitialize",
					!actions.getOnInitialize().getImplemented());
			RtcUtil.changeCommentMethodCppSource(sourceFile.getPath(), "onInitialize",
					!actions.getOnInitialize().getImplemented());
		}
		// onFinalize
		if (!actions.getOnFinalize().getImplemented().equals(oldActions.getOnFinalize().getImplemented())) {
			RtcUtil.changeCommentMethodCppHeader(headerFile.getPath(), "onFinalize",
					!actions.getOnFinalize().getImplemented());
			RtcUtil.changeCommentMethodCppSource(sourceFile.getPath(), "onFinalize",
					!actions.getOnFinalize().getImplemented());
		}
		// onStartup
		if (!actions.getOnStartup().getImplemented().equals(oldActions.getOnStartup().getImplemented())) {
			RtcUtil.changeCommentMethodCppHeader(headerFile.getPath(), "onStartup",
					!actions.getOnStartup().getImplemented());
			RtcUtil.changeCommentMethodCppSource(sourceFile.getPath(), "onStartup",
					!actions.getOnStartup().getImplemented());
		}
		// onShutdown
		if (!actions.getOnShutdown().getImplemented().equals(oldActions.getOnShutdown().getImplemented())) {
			RtcUtil.changeCommentMethodCppHeader(headerFile.getPath(), "onShutdown",
					!actions.getOnShutdown().getImplemented());
			RtcUtil.changeCommentMethodCppSource(sourceFile.getPath(), "onShutdown",
					!actions.getOnShutdown().getImplemented());
		}
		// onActivated
		if (!actions.getOnActivated().getImplemented().equals(oldActions.getOnActivated().getImplemented())) {
			RtcUtil.changeCommentMethodCppHeader(headerFile.getPath(), "onActivated",
					!actions.getOnActivated().getImplemented());
			RtcUtil.changeCommentMethodCppSource(sourceFile.getPath(), "onActivated",
					!actions.getOnActivated().getImplemented());
		}
		// onDeactivated
		if (!actions.getOnDeactivated().getImplemented().equals(oldActions.getOnDeactivated().getImplemented())) {
			RtcUtil.changeCommentMethodCppHeader(headerFile.getPath(), "onDeactivated",
					!actions.getOnDeactivated().getImplemented());
			RtcUtil.changeCommentMethodCppSource(sourceFile.getPath(), "onDeactivated",
					!actions.getOnDeactivated().getImplemented());
		}
		// onAborting
		if (!actions.getOnAborting().getImplemented().equals(oldActions.getOnAborting().getImplemented())) {
			RtcUtil.changeCommentMethodCppHeader(headerFile.getPath(), "onAborting",
					!actions.getOnAborting().getImplemented());
			RtcUtil.changeCommentMethodCppSource(sourceFile.getPath(), "onAborting",
					!actions.getOnAborting().getImplemented());
		}
		// onError
		if (!actions.getOnError().getImplemented().equals(oldActions.getOnError().getImplemented())) {
			RtcUtil.changeCommentMethodCppHeader(headerFile.getPath(), "onError",
					!actions.getOnError().getImplemented());
			RtcUtil.changeCommentMethodCppSource(sourceFile.getPath(), "onError",
					!actions.getOnError().getImplemented());
		}
		// onReset
		if (!actions.getOnReset().getImplemented().equals(oldActions.getOnReset().getImplemented())) {
			RtcUtil.changeCommentMethodCppHeader(headerFile.getPath(), "onReset",
					!actions.getOnReset().getImplemented());
			RtcUtil.changeCommentMethodCppSource(sourceFile.getPath(), "onReset",
					!actions.getOnReset().getImplemented());
		}
		// onExecute
		if (!actions.getOnExecute().getImplemented().equals(oldActions.getOnExecute().getImplemented())) {
			RtcUtil.changeCommentMethodCppHeader(headerFile.getPath(), "onExecute",
					!actions.getOnExecute().getImplemented());
			RtcUtil.changeCommentMethodCppSource(sourceFile.getPath(), "onExecute",
					!actions.getOnExecute().getImplemented());
		}
		// onStateUpdate
		if (!actions.getOnStateUpdate().getImplemented().equals(oldActions.getOnStateUpdate().getImplemented())) {
			RtcUtil.changeCommentMethodCppHeader(headerFile.getPath(), "onStateUpdate",
					!actions.getOnStateUpdate().getImplemented());
			RtcUtil.changeCommentMethodCppSource(sourceFile.getPath(), "onStateUpdate",
					!actions.getOnStateUpdate().getImplemented());
		}
		// onRateChanged
		if (!actions.getOnRateChanged().getImplemented().equals(oldActions.getOnRateChanged().getImplemented())) {
			RtcUtil.changeCommentMethodCppHeader(headerFile.getPath(), "onRateChanged",
					!actions.getOnRateChanged().getImplemented());
			RtcUtil.changeCommentMethodCppSource(sourceFile.getPath(), "onRateChanged",
					!actions.getOnRateChanged().getImplemented());
		}
		// // onAction
		// if
		// (!actions.getOnAction().getImplemented().equals(oldActions.getOnAction().getImplemented()))
		// {
		// RtcUtil.changeCommentMethodCppHeader(headerFile.getPath(),
		// "onAction",
		// !actions.getOnAction().getImplemented());
		// RtcUtil.changeCommentMethodCppSource(sourceFile.getPath(),
		// "onAction",
		// !actions.getOnAction().getImplemented());
		// }
		// // onModeChanged
		// if
		// (!actions.getOnModeChanged().getImplemented().equals(oldActions.getOnModeChanged().getImplemented()))
		// {
		// RtcUtil.changeCommentMethodCppHeader(headerFile.getPath(),
		// "onModeChanged",
		// !actions.getOnModeChanged().getImplemented());
		// RtcUtil.changeCommentMethodCppSource(sourceFile.getPath(),
		// "onModeChanged",
		// !actions.getOnModeChanged().getImplemented());
		// }
	}

	/************************************************************
	 * Python用のソースコード自動反映処理
	 ************************************************************/
	/**
	 * PythonのソースファイルにModuleSpecの変更を反映する
	 * 
	 * @param rtcProfile
	 * @param sourceFile
	 */
	public void updateModuleSpecPython(RtcProfile rtcProfile, File sourceFile) {
		List<String> moduleSpec = RtcUtil.createModuleSpecForPyhon(rtcProfile);
		RtcUtil.updateModuleSpecPython(sourceFile.getPath(), moduleSpec);
	}

	/**
	 * PythonのソースファイルのIDLインポート文の変更を反映する
	 * 
	 * @param rtcDirPath
	 * @param sourceFile
	 */
	private void updateIdlImportPython(RtcProfile rtcProfile, File sourceFile) {
		List<String> newIdlImportList = RtcUtil.createIdlImportForPython(rtcProfile);
		RtcUtil.updateIdlImportPython(sourceFile.getPath(), newIdlImportList);
	}

	/**
	 * Pythonのソースファイルにservice_implの変更を反映する
	 * 
	 * @param rtcProfile
	 * @param sourceFile
	 */
	private void updateServiceImplementPython(RtcProfile rtcProfile, File sourceFile) {
		List<String> newServiceImplementList = RtcUtil.createServiceImplementForPython(rtcProfile);
		RtcUtil.updateServiceImplementPython(sourceFile.getPath(), newServiceImplementList);
	}

	/**
	 * Pythonのソースファイルにconsumer_declareの変更を反映する
	 * 
	 * @param rtcProfile
	 * @param sourceFile
	 */
	private void updateConsumerImportPython(RtcProfile rtcProfile, File sourceFile) {
		List<String> newConsumerImportList = RtcUtil.createConsumerImportForPython(rtcProfile);
		RtcUtil.updateConsumerImportPython(sourceFile.getPath(), newConsumerImportList);
	}

	/**
	 * Pythonのソースファイルにinit_conf_paramの変更を反映する
	 * 
	 * @param rtcProfile
	 * @param sourceFile
	 */
	public void updateInitConfParamPython(RtcProfile rtcProfile, File sourceFile) {
		List<String> newInitConfParamList = RtcUtil.createInitConfParamForPython(rtcProfile);
		RtcUtil.updateInitConfParamPython(sourceFile.getPath(), newInitConfParamList);
	}

	/**
	 * Pythonのソースファイルにコンポーネント種類の変更を反映する
	 * 
	 * @param rtcProfile
	 * @param headerFile
	 */
	private void updateComponentKindPython(RtcProfile rtcProfile, File sourceFile) {
		RtcUtil.udpateClassNamePython(sourceFile.getPath(), rtcProfile.getBasicInfo().getModuleName(),
				rtcProfile.getBasicInfo().getComponentKind());
	}

	/**
	 * PythonのソースファイルにConstroctorの変更を反映する
	 * 
	 * @param rtcProfile
	 * @param sourceFile
	 */
	private void updateConstructorPython(RtcProfile rtcProfile, File sourceFile) {
		List<String> newConstructorList = RtcUtil.createConstructorForPython(rtcProfile);
		RtcUtil.updateConstructorPython(sourceFile.getPath(), newConstructorList);
	}

	/**
	 * PythonのソースファイルにOnInitializeの変更を反映する
	 * 
	 * @param rtcProfile
	 * @param sourceFile
	 */
	public void updateOnInitializePython(RtcProfile rtcProfile, File sourceFile) {
		List<String> newOnInitializeList = RtcUtil.createOnIntializeForPython(rtcProfile);
		RtcUtil.updateOnInitializePython(sourceFile.getPath(), newOnInitializeList);
	}

	/**
	 * IDLファイルの設定およびidl.pyファイルの生成を行う
	 * 
	 * @param rtcProfile
	 * @param rtcDirPath
	 */
	private void updateServiceProviderConsumerIdlFilePython(RtcProfile rtcProfile, String rtcDirPath) {
		RtcUtil.createServiceProviderConsumerIdlFilePython(rtcProfile, rtcDirPath);
	}

	/**
	 * Pythonのソースファイルの各アクティビティメソッドの有効無効を切り替える
	 * 
	 * @param actions
	 * @param oldActions
	 * @param sourceFile
	 */
	public void updateCommentMethodPython(Actions actions, Actions oldActions, File sourceFile) {
		// onInitialize
		if (!actions.getOnInitialize().getImplemented().equals(oldActions.getOnInitialize().getImplemented())) {
			RtcUtil.changeCommentMethodPython(sourceFile.getPath(), "onInitialize",
					!actions.getOnInitialize().getImplemented());
		}
		// onFinalize
		if (!actions.getOnFinalize().getImplemented().equals(oldActions.getOnFinalize().getImplemented())) {
			RtcUtil.changeCommentMethodPython(sourceFile.getPath(), "onFinalize",
					!actions.getOnFinalize().getImplemented());
		}
		// onStartup
		if (!actions.getOnStartup().getImplemented().equals(oldActions.getOnStartup().getImplemented())) {
			RtcUtil.changeCommentMethodPython(sourceFile.getPath(), "onStartup",
					!actions.getOnStartup().getImplemented());
		}
		// onShutdown
		if (!actions.getOnShutdown().getImplemented().equals(oldActions.getOnShutdown().getImplemented())) {
			RtcUtil.changeCommentMethodPython(sourceFile.getPath(), "onShutdown",
					!actions.getOnShutdown().getImplemented());
		}
		// onActivated
		if (!actions.getOnActivated().getImplemented().equals(oldActions.getOnActivated().getImplemented())) {
			RtcUtil.changeCommentMethodPython(sourceFile.getPath(), "onActivated",
					!actions.getOnActivated().getImplemented());
		}
		// onDeactivated
		if (!actions.getOnDeactivated().getImplemented().equals(oldActions.getOnDeactivated().getImplemented())) {
			RtcUtil.changeCommentMethodPython(sourceFile.getPath(), "onDeactivated",
					!actions.getOnDeactivated().getImplemented());
		}
		// onAborting
		if (!actions.getOnAborting().getImplemented().equals(oldActions.getOnAborting().getImplemented())) {
			RtcUtil.changeCommentMethodPython(sourceFile.getPath(), "onAborting",
					!actions.getOnAborting().getImplemented());
		}
		// onError
		if (!actions.getOnError().getImplemented().equals(oldActions.getOnError().getImplemented())) {
			RtcUtil.changeCommentMethodPython(sourceFile.getPath(), "onError", !actions.getOnError().getImplemented());
		}
		// onReset
		if (!actions.getOnReset().getImplemented().equals(oldActions.getOnReset().getImplemented())) {
			RtcUtil.changeCommentMethodPython(sourceFile.getPath(), "onReset", !actions.getOnReset().getImplemented());
		}
		// onExecute
		if (!actions.getOnExecute().getImplemented().equals(oldActions.getOnExecute().getImplemented())) {
			RtcUtil.changeCommentMethodPython(sourceFile.getPath(), "onExecute",
					!actions.getOnExecute().getImplemented());
		}
		// onStateUpdate
		if (!actions.getOnStateUpdate().getImplemented().equals(oldActions.getOnStateUpdate().getImplemented())) {
			RtcUtil.changeCommentMethodPython(sourceFile.getPath(), "onStateUpdate",
					!actions.getOnStateUpdate().getImplemented());
		}
		// onRateChanged
		if (!actions.getOnRateChanged().getImplemented().equals(oldActions.getOnRateChanged().getImplemented())) {
			RtcUtil.changeCommentMethodPython(sourceFile.getPath(), "onRateChanged",
					!actions.getOnRateChanged().getImplemented());
		}
		// // onAction
		// if
		// (!actions.getOnAction().getImplemented().equals(oldActions.getOnAction().getImplemented()))
		// {
		// RtcUtil.changeCommentMethodPython(sourceFile.getPath(), "onAction",
		// !actions.getOnAction().getImplemented());
		// }
		// // onModeChanged
		// if
		// (!actions.getOnModeChanged().getImplemented().equals(oldActions.getOnModeChanged().getImplemented()))
		// {
		// RtcUtil.changeCommentMethodPython(sourceFile.getPath(),
		// "onModeChanged",
		// !actions.getOnModeChanged().getImplemented());
		// }
	}
}
