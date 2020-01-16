package com.sec.airgraph.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

import com.sec.rtc.entity.field.ComponentFieldInfo;
import com.sec.rtc.entity.rts.Rts;
import com.sec.airgraph.form.MainForm;
import com.sec.airgraph.service.MainService;
import com.sec.airgraph.util.Const.RT_COMPONENT.CONFIGURATION_TYPE;
import com.sec.airgraph.util.Const.RT_COMPONENT.DATAFLOW_TYPE;
import com.sec.airgraph.util.Const.RT_COMPONENT.INTERFACE_TYPE;
import com.sec.airgraph.util.Const.RT_COMPONENT.SUBSCRIPTION_TYPE;
import com.sec.airgraph.util.Const.RT_COMPONENT.WIDGET_TYPE;
import com.sec.airgraph.util.RtmEditorUtil;
import com.sec.airgraph.util.PropUtil;
import com.sec.airgraph.util.StringUtil;

/**
 * AirGraph RTM-Editorコントローラ
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Controller
@RequestMapping(value = "/main")
@SessionAttributes({ "mainForm" })
public class MainController {

	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(MainController.class);

	/**
	 * メインサービス
	 */
	@Autowired
	private MainService mainService;

	/**
	 * モデルオブジェクト初期化
	 * 
	 * @param session
	 * @return
	 */
	@ModelAttribute("mainForm")
	public MainForm newRequest(HttpSession session) {
		MainForm f = new MainForm();
		return f;
	}

	/***********************************************
	 * 選択肢等
	 ***********************************************/

	/**
	 * コンポーネント型の選択肢取得
	 * 
	 * @return
	 */
	@ModelAttribute("componentTypeChoices")
	public Map<String, String> getComponentTypeChoices() {
		return RtmEditorUtil.createComponentTypeMap();
	}

	/**
	 * アクティビティ型の選択肢取得
	 * 
	 * @return
	 */
	@ModelAttribute("activityTypeChoices")
	public Map<String, String> getActivityTypeChoices() {
		return RtmEditorUtil.createActivityTypeMap();
	}

	/**
	 * コンポーネント種類の選択肢取得
	 * 
	 * @return
	 */
	@ModelAttribute("componentKindChoices")
	public Map<String, String> getComponentKindChoices() {
		return RtmEditorUtil.createComponentKindMap();
	}

	/**
	 * 実行型の選択肢取得
	 * 
	 * @return
	 */
	@ModelAttribute("executionTypeChoices")
	public Map<String, String> getExecutionTypeChoices() {
		return RtmEditorUtil.createExecutionTypeMap();
	}

	/**
	 * ポート表示位置の選択肢取得
	 * 
	 * @return
	 */
	@ModelAttribute("portPositionChoices")
	public Map<String, String> getPortPositionChoices() {
		return RtmEditorUtil.createPortPositionMap();
	}

	/**
	 * 接続時インタフェース種別の選択肢取得
	 * 
	 * @return
	 */
	@ModelAttribute("connectInterfaceTypeChoices")
	public Map<String, String> getConnectInterfaceTypeChoices() {
		Map<String, String> map = new LinkedHashMap<>();
		map.put(INTERFACE_TYPE.CORBA_CDR, INTERFACE_TYPE.CORBA_CDR);
		return map;
	}

	/**
	 * 接続時データフロー種別の選択肢取得
	 * 
	 * @return
	 */
	@ModelAttribute("connectDataflowTypeChoices")
	public Map<String, String> getConnectDataflowTypeChoices() {
		Map<String, String> map = new LinkedHashMap<>();
		map.put(DATAFLOW_TYPE.PUSH, DATAFLOW_TYPE.PUSH);
		map.put(DATAFLOW_TYPE.PULL, DATAFLOW_TYPE.PULL);
		return map;
	}

	/**
	 * 接続時サブスクリプション種別の選択肢取得
	 * 
	 * @return
	 */
	@ModelAttribute("connectSubscriptionTypeChoices")
	public Map<String, String> getConnectSubscriptionTypeChoices() {
		Map<String, String> map = new LinkedHashMap<>();
		map.put(SUBSCRIPTION_TYPE.FLUSH, SUBSCRIPTION_TYPE.FLUSH);
		map.put(SUBSCRIPTION_TYPE.NEW, SUBSCRIPTION_TYPE.NEW);
		map.put(SUBSCRIPTION_TYPE.PERIODIC, SUBSCRIPTION_TYPE.PERIODIC);
		return map;
	}

	/**
	 * インタフェース向きの選択肢取得
	 * 
	 * @return
	 */
	@ModelAttribute("ifDirectionChoices")
	public Map<String, String> getIfDirectionChoices() {
		return RtmEditorUtil.createIfDirectionMap();
	}

	/**
	 * コンフィギュレーション設定の選択肢取得
	 * 
	 * @return
	 */
	@ModelAttribute("configurationTypeChoices")
	public Map<String, String> getConfigurationTypeChoices() {
		Map<String, String> map = new LinkedHashMap<>();
		map.put(CONFIGURATION_TYPE.SHORT, CONFIGURATION_TYPE.SHORT);
		map.put(CONFIGURATION_TYPE.INT, CONFIGURATION_TYPE.INT);
		map.put(CONFIGURATION_TYPE.LONG, CONFIGURATION_TYPE.LONG);
		map.put(CONFIGURATION_TYPE.FLOAT, CONFIGURATION_TYPE.FLOAT);
		map.put(CONFIGURATION_TYPE.DOUBLE, CONFIGURATION_TYPE.DOUBLE);
		map.put(CONFIGURATION_TYPE.STRING, CONFIGURATION_TYPE.STRING);
		return map;
	}

	/**
	 * コンフィギュレーション設定のウィジェット選択肢取得
	 * 
	 * @return
	 */
	@ModelAttribute("configurationWidgetChoices")
	public Map<String, String> getConfigurationWidgetChoices() {
		Map<String, String> map = new LinkedHashMap<>();
		map.put(WIDGET_TYPE.TEXT, WIDGET_TYPE.TEXT);
		return map;
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
	public String init(@ModelAttribute("mainForm") MainForm form, BindingResult bindingResult, Model model,
			HttpSession session, SessionStatus sessionStatus) {
		logger.info("Intialized main.");

		// 初期化
		sessionStatus.setComplete();
		model.addAttribute("mainForm", newRequest(session));

		return "rtsystem/main";
	}
	
	/**
	 * コンポーネント領域情報を取得する
	 * 
	 * @return
	 */
	@RequestMapping(value = "loadComponentArea", method = RequestMethod.GET)
	@ResponseBody
	public ComponentFieldInfo loadComponentArea() {
		logger.info("Load all Component data.");
		return mainService.loadAllComponentArea();
	}

	/**
	 * 作業領域のすべてのPackageの内容を読み込む
	 * 
	 * @param rtsName
	 * @return
	 */
	@RequestMapping(value = "loadAllWorkspace", method = RequestMethod.GET)
	@ResponseBody
	public List<Rts> loadAllWorkspace() {
		logger.info("Load all workspace data.");
		return mainService.loadAllPackagesWorkspace();
	}

	/**
	 * 作業領域内の指定Pakcageの内容を読み込む
	 * 
	 * @param rtsName
	 * @return
	 */
	@RequestMapping(value = "loadWorkspace", method = RequestMethod.GET)
	@ResponseBody
	public Rts loadWorkspace(@RequestParam(value = "rtsName") String rtsName) {
		logger.info("Load workspace data. workspace[" + rtsName + "]");
		return mainService.loadPackageWorkspace(rtsName);
	}

	/**
	 * 指定Packageを指定作業領域に追加する
	 * 
	 * @param workPackageName
	 * @param dropedRtsName
	 * @param newId
	 * @param newSAbstruct
	 * @param newVersion
	 * @param newRemoteUrl
	 * @return
	 */
	@RequestMapping(value = "addPackage", method = RequestMethod.POST)
	@ResponseBody
	public Rts addPackage(@RequestParam(value = "workPackageName") String workPackageName,
			@RequestParam(value = "dropedRtsName") String dropedRtsName, @RequestParam(value = "newId") String newId,
			@RequestParam(value = "newSAbstruct") String newSAbstruct,
			@RequestParam(value = "newVersion") String newVersion,
			@RequestParam(value = "newRemoteUrl") String newRemoteUrl) {
		logger.info("Add package to workspace. package[" + dropedRtsName + "]workspace[" + workPackageName + "]");
		return mainService.clonePackageToWorkspace(StringUtil.getPackageNameFromModelName(workPackageName),
				StringUtil.getPackageNameFromModelName(dropedRtsName), newId, newSAbstruct, newVersion, newRemoteUrl);
	}

	/**
	 * 指定されたPackageを更新する
	 * 
	 * @param json
	 * @return
	 */
	@RequestMapping(value = "updatePackage", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String updatePackage(@RequestBody String json) {
		logger.info("Save workspace.");
		if (StringUtil.isNotEmpty(json)) {
			mainService.updatePackage(json);
		}
		return "{\"response\" : \"OK\"}";
	}

	/**
	 * 指定されたPackageを削除する
	 * 
	 * @param rtsName
	 * @return
	 */
	@RequestMapping(value = "deletePackage", method = RequestMethod.POST)
	@ResponseBody
	public Rts deletePackage(@RequestParam(value = "rtsName") String rtsName) {
		logger.info("Delete workspace. workspace[" + rtsName + "]");
		if (StringUtil.isNotEmpty(rtsName)) {
			mainService.deletePackage(StringUtil.getPackageNameFromModelName(rtsName));
		}
		return null;
	}

	/**
	 * 指定されたComponentを作業領域に追加する
	 * 
	 * @param workPackageName
	 * @param componentName
	 * @param gitName
	 * @param clonedDirectory
	 * @return
	 */
	@RequestMapping(value = "addComponent", method = RequestMethod.POST)
	@ResponseBody
	public Rts addComponent(@RequestParam(value = "workPackageName") String workPackageName,
			@RequestParam(value = "componentName") String componentName,
			@RequestParam(value = "gitName") String gitName,
			@RequestParam(value = "clonedDirectory") String clonedDirectory) {
		logger.info("Add component to workspace. component[" + componentName + "]git[" + gitName + "]clonedDirectory[" + clonedDirectory
				+ "]workspace[" + workPackageName + "]");
		mainService.addComponent(StringUtil.getPackageNameFromModelName(workPackageName), componentName, gitName, clonedDirectory);
		return null;
	}

	/**
	 * 作業領域に新規Componentを追加する
	 * 
	 * @param componentData
	 * @return
	 */
	@RequestMapping(value = "createNewComponent", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String createNewComponent(@RequestBody String json) {
		logger.info("crete New component to workspace.");
		mainService.createNewComponent(json);
		return "{\"response\" : \"OK\"}";
	}

	/**
	 * 指定されたデータ型に対応するロガー用コンポーネントを追加する
	 * 
	 * @param workPackageName
	 * @param id
	 * @param instanceName
	 * @param portName
	 * @param pathUri
	 * @param dataType
	 * @return
	 */
	@RequestMapping(value = "addLogger", method = RequestMethod.POST)
	@ResponseBody
	public Rts addLogger(@RequestParam(value = "workPackageName") String workPackageName,
			@RequestParam(value = "id") String id, @RequestParam(value = "instanceName") String instanceName,
			@RequestParam(value = "portName") String portName, @RequestParam(value = "pathUri") String pathUri,
			@RequestParam(value = "dataType") String dataType) {
		logger.info("Add logger to component. id[" + id + "]instance[" + instanceName + "]port[" + portName
				+ "]pathUri[" + pathUri + "]dataType[" + dataType + "]workspace[" + workPackageName + "]");
		mainService.addLoggerComponent(StringUtil.getPackageNameFromModelName(workPackageName), id, instanceName,
				portName, pathUri, dataType);
		return null;
	}

	/**
	 * 指定されたComponentを作業領域から削除する
	 * 
	 * @param workPackageName
	 * @param id
	 * @param componentName
	 * @return
	 */
	@RequestMapping(value = "deleteComponent", method = RequestMethod.POST)
	@ResponseBody
	public Rts deleteComponent(@RequestParam(value = "workPackageName") String workPackageName,
			@RequestParam(value = "id") String id, @RequestParam(value = "componentName") String componentName) {
		logger.info("Delete component from package. id[" + id + "]component[" + componentName + "]workspace["
				+ workPackageName + "]");
		mainService.deleteComponent(StringUtil.getPackageNameFromModelName(workPackageName), id, componentName);
		return null;
	}

	/**
	 * 指定されたデータ型がロギング可能な型かを調べる
	 * 
	 * @param dataType
	 * @return
	 */
	@RequestMapping(value = "canLogging", method = RequestMethod.POST)
	@ResponseBody
	public String canLogging(@RequestParam(value = "dataType") String dataType) {
		logger.info("Check logging enabled. dataType[" + dataType + "]");
		return mainService.canLogging(dataType) ? "OK" : "NG";
	}

	/**
	 * パッケージ名の競合を調べる
	 * 
	 * @param name
	 * @return
	 */
	@RequestMapping(value = "isAvailablePackageName", method = RequestMethod.POST)
	@ResponseBody
	public String isAvailablePackageName(@RequestParam(value = "name") String name) {
		logger.info("Check dupliacted package name. name[" + name + "]");
		return mainService.checkAvailablePackageName(name) ? "OK" : "NG";
	}

	/**
	 * コンポーネント名の競合を調べる
	 * 
	 * @param workPackageName
	 * @param componentName
	 * @return
	 */
	@RequestMapping(value = "isAvailableComponentName", method = RequestMethod.POST)
	@ResponseBody
	public String isAvailableComponentName(@RequestParam(value = "workPackageName") String workPackageName,
			@RequestParam(value = "componentName") String componentName) {
		logger.info("Check dupliacted component name. workspace[" + workPackageName + "] componentName[" + componentName
				+ "]");
		return mainService.checkAvailableComponentName(StringUtil.getPackageNameFromModelName(workPackageName),
				componentName) ? "OK" : "NG";
	}
	
	/**
	 * 指定されたComponentに設定しているIDLファイルを含めて、IDLファイルの一覧を取得する
	 * 
	 * @param workPackageName
	 * @param componentName
	 * @return
	 */
	@RequestMapping(value = "idlFileChoices", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> idlFileChoices(@RequestParam(value = "workPackageName") String workPackageName,
			@RequestParam(value = "componentName") String componentName) {
		logger.info("Get IDL File Choices. component[" + componentName + "]workspace[" + workPackageName + "]");
		return RtmEditorUtil.createIdlFileMap(StringUtil.getPackageNameFromModelName(workPackageName), componentName);
	}

	/**
	 * 指定されたRTCに設定しているIDLファイルを含めて、指定されたIDLファイルのDataType型を取得する
	 * 
	 * @param workPackageName
	 * @param componentName
	 * @param idlFileName
	 * @return
	 */
	@RequestMapping(value = "dataTypeChoices", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> dataTypeChoices(@RequestParam(value = "workPackageName") String workPackageName,
			@RequestParam(value = "componentName") String componentName) {
		logger.info("Get Interface Type Choices. component[" + componentName + "]workspace["
				+ workPackageName + "]");
		return RtmEditorUtil.createDataTypeMap(StringUtil.getPackageNameFromModelName(workPackageName), componentName, true);
	}

	/**
	 * 指定されたRTCに設定しているIDLファイルを含めて、指定されたIDLファイルのDataType型を取得する
	 * 
	 * @param workPackageName
	 * @param componentName
	 * @param idlFileName
	 * @return
	 */
	@RequestMapping(value = "connectorDataTypeChoices", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> connectorDataTypeChoices(@RequestParam(value = "workPackageName") String workPackageName,
			@RequestParam(value = "componentName") String componentName) {
		logger.info("Get Interface Type Choices. component[" + componentName + "]workspace["
				+ workPackageName + "]");
		return RtmEditorUtil.createDataTypeMap(StringUtil.getPackageNameFromModelName(workPackageName), componentName, false);
	}

	/**
	 * 指定されたRTCに設定しているIDLファイルを含めて、指定されたIDLファイルのinterface型を取得する
	 * 
	 * @param workPackageName
	 * @param componentName
	 * @param idlFileName
	 * @return
	 */
	@RequestMapping(value = "interfaceTypeChoices", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> interfaceTypeChoices(@RequestParam(value = "workPackageName") String workPackageName,
			@RequestParam(value = "componentName") String componentName,
			@RequestParam(value = "idlFileName") String idlFileName) {
		logger.info("Get Interface Type Choices. component[" + componentName + "]idlFile[" + idlFileName + "]workspace["
				+ workPackageName + "]");
		return RtmEditorUtil.createInterfaceTypeMap(StringUtil.getPackageNameFromModelName(workPackageName), componentName,
				idlFileName);
	}

	/**
	 * 指定されたpackageをローカルリポジトリにCommitする
	 * 
	 * @param workPackageName
	 * @param commitMessage
	 * @return
	 */
	@RequestMapping(value = "commitPackage", method = RequestMethod.POST)
	@ResponseBody
	public String commitPackage(@RequestParam(value = "workPackageName") String workPackageName,
			@RequestParam(value = "commitMessage") String commitMessage) {
		logger.info("Commit Package to Local Repository. workspace[" + workPackageName + "]commitMessage[" + commitMessage + "]");
		return mainService.commitPackage(StringUtil.getPackageNameFromModelName(workPackageName), commitMessage);
	}

	/**
	 * 指定されたpackageをリモートリポジトリにPushする
	 * 
	 * @param workPackageName
	 * @param commitMessage
	 * @param userName
	 * @param password
	 * @return
	 */
	@RequestMapping(value = "pushPackage", method = RequestMethod.POST)
	@ResponseBody
	public String pushPackage(@RequestParam(value = "workPackageName") String workPackageName,
			@RequestParam(value = "commitMessage") String commitMessage,
			@RequestParam(value = "userName") String userName,
			@RequestParam(value = "password") String password) {
		logger.info("Push Package to Local Repository. workspace[" + workPackageName + "]commitMessage[" + commitMessage + "]userName[" + userName + "]");
		return mainService.pushPackage(StringUtil.getPackageNameFromModelName(workPackageName), commitMessage, userName, password);
	}

	/**
	 * 指定されたpackageをリモートリポジトリからPullする
	 * 
	 * @param workPackageName
	 * @param commitMessage
	 * @return
	 */
	@RequestMapping(value = "pullPackage", method = RequestMethod.POST)
	@ResponseBody
	public String pullPackage(@RequestParam(value = "workPackageName") String workPackageName,
			@RequestParam(value = "userName") String userName,
			@RequestParam(value = "password") String password) {
		logger.info("Pull Package from Local Repository. workspace[" + workPackageName + "]userName[" + userName + "]");
		return mainService.pullPackage(StringUtil.getPackageNameFromModelName(workPackageName), userName, password);
	}

	/**
	 * 指定されたComponentをローカルリポジトリにCommitする
	 * 
	 * @param workPackageName
	 * @param componentName
	 * @param gitName
	 * @param commitMessage
	 * @return
	 */
	@RequestMapping(value = "commitComponent", method = RequestMethod.POST)
	@ResponseBody
	public String commitComponent(@RequestParam(value = "workPackageName") String workPackageName,
			@RequestParam(value = "componentName") String componentName,
			@RequestParam(value = "gitName") String gitName,
			@RequestParam(value = "commitMessage") String commitMessage) {
		logger.info("Commit Component to Local Repository. component[" + componentName + "]git[" + gitName
				+ "]workspace[" + workPackageName + "]commitMessage[" + commitMessage + "]");
		return mainService.commitComponent(StringUtil.getPackageNameFromModelName(workPackageName), componentName, commitMessage);
	}

	/**
	 * 指定されたComponentをリモートリポジトリにPushする
	 * 
	 * @param workPackageName
	 * @param componentName
	 * @param gitName
	 * @param commitMessage
	 * @param userName
	 * @param password
	 * @return
	 */
	@RequestMapping(value = "pushComponent", method = RequestMethod.POST)
	@ResponseBody
	public String pushComponent(@RequestParam(value = "workPackageName") String workPackageName,
			@RequestParam(value = "componentName") String componentName,
			@RequestParam(value = "gitName") String gitName,
			@RequestParam(value = "commitMessage") String commitMessage,
			@RequestParam(value = "userName") String userName,
			@RequestParam(value = "password") String password) {
		logger.info("Push Component to Local Repository. component[" + componentName + "]git[" + gitName
				+ "]workspace[" + workPackageName + "]commitMessage[" + commitMessage + "]userName[" + userName + "]");
		return mainService.pushComponent(StringUtil.getPackageNameFromModelName(workPackageName), componentName, commitMessage, userName, password);
	}

	/**
	 * 指定されたComponentをリモートリポジトリからPullする
	 * 
	 * @param workPackageName
	 * @param componentName
	 * @param gitName
	 * @param userName
	 * @param password
	 * @return
	 */
	@RequestMapping(value = "pullComponent", method = RequestMethod.POST)
	@ResponseBody
	public String pullComponent(@RequestParam(value = "workPackageName") String workPackageName,
			@RequestParam(value = "componentName") String componentName,
			@RequestParam(value = "gitName") String gitName,
			@RequestParam(value = "userName") String userName,
			@RequestParam(value = "password") String password) {
		logger.info("Pull Component from Local Repository. component[" + componentName + "]git[" + gitName
				+ "]workspace[" + workPackageName + "]userName[" + userName + "]");
		return mainService.pullComponent(StringUtil.getPackageNameFromModelName(workPackageName), componentName, userName, password);
	}
	
	/**
	 * Kerasのモデル一覧の選択肢取得
	 * 
	 * @return
	 */
	@RequestMapping(value = "getKerasModelChoices", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> getKerasModelChoices() {
		logger.info("Get Keras Model Choices.");
		return mainService.getKerasModelChoices();
	}

	/**
	 * データセットの選択肢取得
	 * 
	 * @return
	 */
	@RequestMapping(value = "getDatasetChoices", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> getDatasetChoices() {
		logger.info("Get Dataset Choices.");
		return mainService.getDatasetChoices();
	}

	/********************************************************************
	 * ビルド・実行関連
	 ********************************************************************/
	/**
	 * 指定されたPackageのすべてのRtcをビルドする
	 * 
	 * @param rtsName
	 * @return
	 */
	@RequestMapping(value = "buildPackageAll", method = RequestMethod.POST)
	@ResponseBody
	public Rts buildPackageAll(@RequestParam(value = "rtsName") String rtsName) {
		logger.info("Build package all. package[" + rtsName + "]");
		if (StringUtil.isNotEmpty(rtsName)) {
			mainService.buildPackageAll(StringUtil.getPackageNameFromModelName(rtsName));
		}
		return null;
	}

	/**
	 * 指定されたPackageのすべてのRtcをcleanする
	 * 
	 * @param rtsName
	 * @return
	 */
	@RequestMapping(value = "cleanPackageAll", method = RequestMethod.POST)
	@ResponseBody
	public Rts cleanPackageAll(@RequestParam(value = "rtsName") String rtsName) {
		logger.info("Clean package all. package[" + rtsName + "]");
		if (StringUtil.isNotEmpty(rtsName)) {
			mainService.cleanPackageAll(StringUtil.getPackageNameFromModelName(rtsName));
		}
		return null;
	}

	/**
	 * 指定されたPackageを実行する
	 * 
	 * @param rtsName
	 * @return
	 */
	@RequestMapping(value = "runPackage", method = RequestMethod.POST)
	@ResponseBody
	public Boolean runPackage(@RequestParam(value = "rtsName") String rtsName) {
		logger.info("Run System. package[" + rtsName + "]");
		if (StringUtil.isNotEmpty(rtsName)) {
			mainService.runPackage(StringUtil.getPackageNameFromModelName(rtsName));
		}
		return true;
	}

	/**
	 * 指定されたPackageを停止する
	 * 
	 * @param rtsName
	 * @return
	 */
	@RequestMapping(value = "terminatePackage", method = RequestMethod.POST)
	@ResponseBody
	public Boolean terminatePackage(@RequestParam(value = "rtsName") String rtsName) {
		logger.info("Terminate System. package[" + rtsName + "]");
		if (StringUtil.isNotEmpty(rtsName)) {
			mainService.terminatePackage(StringUtil.getPackageNameFromModelName(rtsName));
		}

		return mainService.createResultFile();
	}
	
	/**
	 * 指定されたPackageの実行状況を確認する
	 * 
	 * @param rtsName
	 * @return
	 */
	@RequestMapping(value = "isRunningPackage", method = RequestMethod.POST)
	@ResponseBody
	public Boolean isRunningPackage(@RequestParam(value = "rtsName") String rtsName) {
		logger.info("Check Running System. package[" + rtsName + "]");
		boolean result = false;
		if (StringUtil.isNotEmpty(rtsName)) {
			result = mainService.isRunningPackage(StringUtil.getPackageNameFromModelName(rtsName));
		}

		return result;
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
		return mainService.tailAllLog(StringUtil.getPackageNameFromModelName(workPackageName));
	}

	/**
	 * 画像ファイルを読み込む
	 * 
	 * @param imageDirectoryPath
	 * @param date
	 * @param workPackageName
	 * @return
	 */
	@RequestMapping(value = "tailImage", method = RequestMethod.GET)
	@ResponseBody
	public HttpEntity<byte[]> tailImage(
			@RequestParam(value = "imageDirectoryPath") String imageDirectoryPath,
			@RequestParam(value = "date") Long date,
			@RequestParam(value = "workPackageName") String workPackageName) {
		logger.info("Tailing Image. image directiry path[" + imageDirectoryPath + "]");

		byte[] image = mainService.tailImage(imageDirectoryPath);

		if (image != null) {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_JPEG);
			headers.setContentLength(image.length);
			return new HttpEntity<byte[]>(image, headers);
		}
		return null;
	}

	/**
	 * 実行結果ファイルをダウンロードする
	 * 
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "getResult", method = RequestMethod.POST)
	public String download(HttpServletResponse response) throws IOException {
		logger.info("Download result file. ");
		// 結果格納ファイル
		String resultFilePath = PropUtil.getValue("result.local.file.path");
		File file = new File(resultFilePath);
		response.addHeader("Content-Type", "application/octet-stream");
		response.addHeader("Content-Disposition",
				"attachment; filename*=UTF-8''" + URLEncoder.encode(file.getName(), StandardCharsets.UTF_8.name()));

		Files.copy(file.toPath(), response.getOutputStream());
		return null;
	}

	/**
	 * IDLファイルをアップロードする
	 * 
	 * @param response
	 * @param workPackageName
	 * @param componentName
	 * @param file
	 */
	@RequestMapping(value = "idlUpload", method = RequestMethod.POST)
	public void idlUpload(HttpServletResponse response, @RequestParam(value = "idl-package-name") String workPackageName,
			@RequestParam(value = "idl-component-name") String componentName, @RequestParam(value = "idl-upload") MultipartFile file) {

		// ファイルが空の場合は HTTP 400 を返す。
		if (file.isEmpty()) {
			logger.warn("IDL File is Empty.");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		RtmEditorUtil.saveIdlFile(StringUtil.getPackageNameFromModelName(workPackageName), componentName, file);
	}

	/**
	 * 指定されたDNNモデルを更新する
	 * 
	 * @param dnnModelName
	 * @return
	 */
	@RequestMapping(value = "updateDnnModels", method = RequestMethod.GET)
	@ResponseBody
	public String updateDnnModels(@RequestParam(value = "dnnModelName") String dnnModelName) throws IOException {
		logger.info("updateDnnModels. DNN Model Name[" + dnnModelName + "]");
		if (mainService.downloadDnnFiles(dnnModelName)) {
			return "{\"response\" : \"OK\"}";
		} else {
			return "{\"response\" : \"ERROR\"}";
		}
	}

	/**
	 * 指定されたDNNファイルをダウンロードする
	 * 
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "getDnnFiles", method = RequestMethod.POST)
	public String getDnnFiles(HttpServletResponse response, @RequestParam(value = "dnnModelName") String dnnModelName,
		@RequestParam(value = "fileExtentions") String fileExtentions) throws IOException {
		logger.info("Get DNN Files. file Name[" + dnnModelName + "][" + fileExtentions + "]");

		// Keras-Editorの作業領域パス
		String workspaceDirPath = PropUtil.getValue("workspace.local.keras.directory.path");
		// ダウンロードするDNNファイルパス（JSON or hdf5)
		String dnnFilePath = workspaceDirPath + dnnModelName + "/" + dnnModelName + "." + fileExtentions;

		File file = new File(dnnFilePath);
		response.addHeader("Content-Type", "application/octet-stream");
		response.addHeader("Content-Disposition",
				"attachment; filename*=UTF-8''" + URLEncoder.encode(file.getName(), StandardCharsets.UTF_8.name()));

		// ファイルが存在する場合は格納
		if (com.sec.airgraph.util.FileUtil.exists(file)) {
			Files.copy(file.toPath(), response.getOutputStream());
		}
		return null;
	}
}
