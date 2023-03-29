package com.sec.airgraph.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.sec.airgraph.form.MainForm;
import com.sec.airgraph.service.MainService;
import com.sec.airgraph.util.Const.RT_COMPONENT.CONFIGURATION_TYPE;
import com.sec.airgraph.util.Const.RT_COMPONENT.DATAFLOW_TYPE;
import com.sec.airgraph.util.Const.RT_COMPONENT.INTERFACE_TYPE;
import com.sec.airgraph.util.Const.RT_COMPONENT.SUBSCRIPTION_TYPE;
import com.sec.airgraph.util.Const.RT_COMPONENT.WIDGET_TYPE;
import com.sec.airgraph.util.PropUtil;
import com.sec.airgraph.util.RtmEditorUtil;
import com.sec.airgraph.util.StringUtil;
import com.sec.rtc.entity.BuildRunDTO;
import com.sec.rtc.entity.field.ComponentFieldInfo;
import com.sec.rtc.entity.rts.Rts;
import com.sec.rtc.entity.yaml.GitHubSetting;

/**
 * AirGraph RTM-Editorコントローラ.
 *
 * @author Tsuyoshi Hirose
 *
 */
@Controller
@RequestMapping(value = "/main")
@SessionAttributes({ "mainForm" })
public class MainController {

	/**
	 * logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(MainController.class);

	/**
	 * メインサービス.
	 */
	@Autowired
	private MainService mainService;

	/**
	 * モデルオブジェクト初期化.
	 *
	 * @param session セッション
	 * @return フォーム
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
	 * コンポーネント型の選択肢取得.
	 *
	 * @return コンポーネント型の選択肢
	 */
	@ModelAttribute("componentTypeChoices")
	public Map<String, String> getComponentTypeChoices() {
		return RtmEditorUtil.createComponentTypeMap();
	}

	/**
	 * アクティビティ型の選択肢取得.
	 *
	 * @return アクティビティ型の選択肢
	 */
	@ModelAttribute("activityTypeChoices")
	public Map<String, String> getActivityTypeChoices() {
		return RtmEditorUtil.createActivityTypeMap();
	}

	/**
	 * コンポーネント種類の選択肢取得.
	 *
	 * @return コンポーネント種類の選択肢
	 */
	@ModelAttribute("componentKindChoices")
	public Map<String, String> getComponentKindChoices() {
		return RtmEditorUtil.createComponentKindMap();
	}

	/**
	 * 実行型の選択肢取得.
	 *
	 * @return 実行型の選択肢
	 */
	@ModelAttribute("executionTypeChoices")
	public Map<String, String> getExecutionTypeChoices() {
		return RtmEditorUtil.createExecutionTypeMap();
	}

	/**
	 * ポート表示位置の選択肢取得.
	 *
	 * @return ポート表示位置の選択肢
	 */
	@ModelAttribute("portPositionChoices")
	public Map<String, String> getPortPositionChoices() {
		return RtmEditorUtil.createPortPositionMap();
	}

	/**
	 * 接続時インタフェース種別の選択肢取得.
	 *
	 * @return 接続時インタフェース種別の選択肢
	 */
	@ModelAttribute("connectInterfaceTypeChoices")
	public Map<String, String> getConnectInterfaceTypeChoices() {
		Map<String, String> map = new LinkedHashMap<>();
		map.put(INTERFACE_TYPE.CORBA_CDR, INTERFACE_TYPE.CORBA_CDR);
		return map;
	}

	/**
	 * 接続時データフロー種別の選択肢取得.
	 *
	 * @return 接続時データフロー種別の選択肢
	 */
	@ModelAttribute("connectDataflowTypeChoices")
	public Map<String, String> getConnectDataflowTypeChoices() {
		Map<String, String> map = new LinkedHashMap<>();
		map.put(DATAFLOW_TYPE.PUSH, DATAFLOW_TYPE.PUSH);
		map.put(DATAFLOW_TYPE.PULL, DATAFLOW_TYPE.PULL);
		return map;
	}

	/**
	 * 接続時サブスクリプション種別の選択肢取得.
	 *
	 * @return 接続時サブスクリプション種別の選択肢
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
	 * インタフェース向きの選択肢取得.
	 *
	 * @return インタフェース向きの選択肢
	 */
	@ModelAttribute("ifDirectionChoices")
	public Map<String, String> getIfDirectionChoices() {
		return RtmEditorUtil.createIfDirectionMap();
	}

	/**
	 * コンフィギュレーション設定の選択肢取得.
	 *
	 * @returns map コンフィギュレーション設定の選択肢.
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
	 * コンフィギュレーション設定のウィジェット選択肢取得.
	 *
	 * @return map コンフィギュレーション設定のウィジェット選択肢.
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
	 * @param form メインフォーム
	 * @param bindingResult bindingResult
	 * @param model model
	 * @param session session
	 * @param sessionStatus sessionStatus
	 * @return htmlファイルのパス
	 */
	@GetMapping("")
	public String init(@ModelAttribute("mainForm") MainForm form, BindingResult bindingResult, Model model,
			HttpSession session, SessionStatus sessionStatus) {
		logger.info("Intialized main.");

		// 初期化
		sessionStatus.setComplete();
		model.addAttribute("mainForm", newRequest(session));

		return "rtsystem/main";
	}

	/**
	 * コンポーネント領域情報を取得する.
	 *
	 * @param hostId ホストID
	 * @return コンポーネントエリアの情報
	 */
	@GetMapping("loadComponentArea")
	@ResponseBody
	public ComponentFieldInfo loadComponentArea(@RequestParam(value = "hostId") String hostId) {
		logger.info("Load all Component data.");
		return mainService.loadAllComponentArea(hostId);
	}

	/**
	 * 作業領域のすべてのPackageの内容を読み込む.
	 *
	 * @return すべてのPackageの情報
	 */
	@GetMapping("loadAllWorkspace")
	@ResponseBody
	public List<Rts> loadAllWorkspace() {
		logger.info("Load all workspace data.");
		return mainService.loadAllPackagesWorkspace();
	}

	/**
	 * 作業領域内の指定Packageの内容を読み込む.
	 *
	 * @param rtsName パッケージ名
	 * @return 作業領域のパッケージ情報
	 */
	@GetMapping("loadWorkspace")
	@ResponseBody
	public Rts loadWorkspace(@RequestParam(value = "rtsName") String rtsName) {
		logger.info("Load workspace data. workspace[" + rtsName + "]");
		return mainService.loadPackageWorkspace(rtsName);
	}

	/**
	 * 指定Packageを指定作業領域に追加する.
	 *
	 * @param workPackageName パッケージ名
	 * @param dropedRtsName ドロップされたパッケージ名
	 * @param newId 新しいパッケージID
	 * @param newSAbstruct newSAbstruct
	 * @param newVersion バージョン
	 * @param newRemoteUrl リモートリポジトリのURL
	 * @param newPackageName 新しいパッケージ名
	 * @param hostId ホストID
	 * @return RTSの情報
	 */
	@PostMapping("addPackage")
	@ResponseBody
	public Rts addPackage(
			@RequestParam(value = "workPackageName") String workPackageName,
			@RequestParam(value = "dropedRtsName") String dropedRtsName, 
			@RequestParam(value = "newId") String newId,
			@RequestParam(value = "newSAbstruct") String newSAbstruct,
			@RequestParam(value = "newVersion") String newVersion,
			@RequestParam(value = "newRemoteUrl") String newRemoteUrl,
			@RequestParam(value = "newPackageName") String newPackageName,
			@RequestParam(value = "hostId") String hostId) {
		logger.info("Add package to workspace. package[" + dropedRtsName + "]workspace[" + workPackageName + "]");
		return mainService.clonePackageToWorkspace(StringUtil.getPackageNameFromModelName(workPackageName),
				StringUtil.getPackageNameFromModelName(dropedRtsName), newId, newSAbstruct, newVersion, newRemoteUrl, newPackageName, hostId);
	}

	/**
	 * 指定されたPackageを更新する.
	 *
	 * @param json パッケージの情報
	 */
	@PostMapping(value = "updatePackage", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public void updatePackage(@RequestBody String json) {
		logger.info("Save workspace.");
		if (StringUtil.isNotEmpty(json)) {
			mainService.updatePackage(json);
		}
	}

	/**
	 * 指定されたPackageを削除する.
	 *
	 * @param rtsName パッケージ名
	 */
	@DeleteMapping("deletePackage")
	@ResponseBody
	public void deletePackage(@RequestParam(value = "rtsName") String rtsName) {
		logger.info("Delete workspace. workspace[" + rtsName + "]");
		if (StringUtil.isNotEmpty(rtsName)) {
			mainService.deletePackage(rtsName);
		}
	}

	/**
	 * 指定されたComponentを作業領域に追加する.
	 *
	 * @param workPackageName パッケージ名
	 * @param componentName コンポーネント名
	 * @param gitName gitURL
	 * @param clonedDirectory クローンされたディレクトリ
	 */
	@PostMapping("addComponent")
	@ResponseBody
	public void addComponent(
			@RequestParam(value = "workPackageName") String workPackageName,
			@RequestParam(value = "componentName") String componentName,
			@RequestParam(value = "gitName") String gitName,
			@RequestParam(value = "clonedDirectory") String clonedDirectory) {
		logger.info("Add component to workspace. component[" + componentName + "]git[" + gitName + "]clonedDirectory["
				+ clonedDirectory + "]workspace[" + workPackageName + "]");
		mainService.addComponent(StringUtil.getPackageNameFromModelName(workPackageName), componentName, gitName,
				clonedDirectory);
	}

	/**
	 * 作業領域に新規Componentを追加する.
	 *
	 * @param json コンポーネント情報
	 */
	@PostMapping(value = "createNewComponent", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public void createNewComponent(@RequestBody String json) {
		logger.info("crete New component to workspace.");
		mainService.createNewComponent(json);
	}

	/**
	 * 指定されたデータ型に対応するロガー用コンポーネントを追加する.
	 *
	 * @param workPackageName パッケージ名
	 * @param id パッケージID
	 * @param instanceName インスタンス名
	 * @param portName ポート名
	 * @param pathUri パスURI
	 * @param dataType データタイプ
	 */
	@PostMapping("addLogger")
	@ResponseBody
	public void addLogger(
			@RequestParam(value = "workPackageName") String workPackageName,
			@RequestParam(value = "id") String id, 
			@RequestParam(value = "instanceName") String instanceName,
			@RequestParam(value = "portName") String portName, 
			@RequestParam(value = "pathUri") String pathUri,
			@RequestParam(value = "dataType") String dataType) {
		logger.info("Add logger to component. id[" + id + "]instance[" + instanceName + "]port[" + portName
				+ "]pathUri[" + pathUri + "]dataType[" + dataType + "]workspace[" + workPackageName + "]");
		mainService.addLoggerComponent(StringUtil.getPackageNameFromModelName(workPackageName), id, instanceName,
				portName, pathUri, dataType);
	}

	/**
	 * 指定されたComponentを作業領域から削除する.
	 *
	 * @param workPackageName パッケージ名
	 * @param id パッケージID
	 * @param componentName コンポーネント名
	 */
	@PostMapping("deleteComponent")
	@ResponseBody
	public void deleteComponent(
			@RequestParam(value = "workPackageName") String workPackageName,
			@RequestParam(value = "id") String id, 
			@RequestParam(value = "componentName") String componentName) {
		logger.info("Delete component from package. id[" + id + "]component[" + componentName + "]workspace["
				+ workPackageName + "]");
		mainService.deleteComponent(workPackageName, id, componentName);
	}

	/**
	 * 指定されたデータ型がロギング可能な型かを調べる.
	 *
	 * @param dataType データタイプ
	 * @return ロギング可能かどうか
	 */
	@PostMapping("canLogging")
	@ResponseBody
	public boolean canLogging(@RequestParam(value = "dataType") String dataType) {
		logger.info("Check logging enabled. dataType[" + dataType + "]");
		return mainService.canLogging(dataType) ? true : false;
	}

	/**
	 * パッケージ名の競合を調べる.
	 *
	 * @param name パッケージ名
	 * @param hostId ホストID
	 * @return 利用可能かどうか
	 */
	@PostMapping("isAvailablePackageName")
	@ResponseBody
	public boolean isAvailablePackageName(
			@RequestParam(value = "name") String name,
			@RequestParam(value = "hostId") String hostId) {
		logger.info("Check dupliacted package name. name[" + name + "]");
		return mainService.checkAvailablePackageName(name, hostId) ? true : false;
	}

	/**
	 * コンポーネント名の競合を調べる.
	 *
	 * @param workPackageName パッケージ名
	 * @param componentName コンポーネント名
	 * @param hostId ホストID
	 * @return 利用可能かどうか
	 */
	@PostMapping("isAvailableComponentName")
	@ResponseBody
	public boolean isAvailableComponentName(
			@RequestParam(value = "workPackageName") String workPackageName,
			@RequestParam(value = "componentName") String componentName,
			@RequestParam(value = "hostId") String hostId) {
		logger.info("Check dupliacted component name. workspace[" + workPackageName + "] componentName[" + componentName
				+ "]");
		return mainService.checkAvailableComponentName(StringUtil.getPackageNameFromModelName(workPackageName),
				componentName, hostId) ? true : false;
	}

	/**
	 * 指定されたComponentに設定しているIDLファイルを含めて、IDLファイルの一覧を取得する.
	 *
	 * @param workPackageName パッケージ名
	 * @param componentName コンポーネント名
	 * @return IDLファイルの一覧
	 */
	@PostMapping("idlFileChoices")
	@ResponseBody
	public Map<String, String> idlFileChoices(
			@RequestParam(value = "workPackageName") String workPackageName,
			@RequestParam(value = "componentName") String componentName) {
		logger.info("Get IDL File Choices. component[" + componentName + "]workspace[" + workPackageName + "]");
		return RtmEditorUtil.createIdlFileMap(StringUtil.getPackageNameFromModelName(workPackageName), componentName);
	}

	/**
	 * 指定されたRTCに設定しているIDLファイルを含めて、指定されたIDLファイルのDataType型を取得する.
	 *
	 * @param workPackageName パッケージ名
	 * @param componentName コンポーネント名
	 * @return 指定されたIDLファイルのDataType型
	 */
	@PostMapping("dataTypeChoices")
	@ResponseBody
	public Map<String, String> dataTypeChoices(
			@RequestParam(value = "workPackageName") String workPackageName,
			@RequestParam(value = "componentName") String componentName) {
		logger.info("Get Interface Type Choices. component[" + componentName + "]workspace[" + workPackageName + "]");
		return RtmEditorUtil.createDataTypeMap(StringUtil.getPackageNameFromModelName(workPackageName), componentName,
				true);
	}

	/**
	 * 指定されたRTCに設定しているIDLファイルを含めて、指定されたIDLファイルのDataType型を取得する.
	 *
	 * @param workPackageName パッケージ名
	 * @param componentName コンポーネント名
	 * @return 指定されたIDLファイルのDataType型
	 */
	@PostMapping("connectorDataTypeChoices")
	@ResponseBody
	public Map<String, String> connectorDataTypeChoices(
			@RequestParam(value = "workPackageName") String workPackageName,
			@RequestParam(value = "componentName") String componentName) {
		logger.info("Get Interface Type Choices. component[" + componentName + "]workspace[" + workPackageName + "]");
		return RtmEditorUtil.createDataTypeMap(StringUtil.getPackageNameFromModelName(workPackageName), componentName,
				false);
	}

	/**
	 * 指定されたRTCに設定しているIDLファイルを含めて、指定されたIDLファイルのinterface型を取得する..
	 *
	 * @param workPackageName パッケージ名
	 * @param componentName コンポーネント名
	 * @param idlFileName IDLファイル名
	 * @return 指定されたIDLファイルのinterface型
	 */
	@PostMapping("interfaceTypeChoices")
	@ResponseBody
	public Map<String, String> interfaceTypeChoices(
			@RequestParam(value = "workPackageName") String workPackageName,
			@RequestParam(value = "componentName") String componentName,
			@RequestParam(value = "idlFileName") String idlFileName) {
		logger.info("Get Interface Type Choices. component[" + componentName + "]idlFile[" + idlFileName + "]workspace["
				+ workPackageName + "]");
		return RtmEditorUtil.createInterfaceTypeMap(StringUtil.getPackageNameFromModelName(workPackageName),
				componentName, idlFileName);
	}

	/**
	 * 指定されたpackageをローカルリポジトリにCommitする.
	 *
	 * @param workPackageName パッケージ名
	 * @param commitMessage コミットメッセージ
	 * @return コミットの結果　
	 */
	@PostMapping("commitPackage")
	@ResponseBody
	public String commitPackage(
			@RequestParam(value = "workPackageName") String workPackageName,
			@RequestParam(value = "commitMessage") String commitMessage) {
		logger.info("Commit Package to Local Repository. workspace[" + workPackageName + "]commitMessage["
				+ commitMessage + "]");
		return mainService.commitPackage(StringUtil.getPackageNameFromModelName(workPackageName), workPackageName, commitMessage);
	}

	/**
	 * 指定されたpackageをリモートリポジトリにPushする.
	 *
	 * @param workPackageName パッケージ名
	 * @param commitMessage コミットメッセージ
	 * @param userName ユーザー名
	 * @param password パスワード
	 * @return pushの結果
	 */
	@PostMapping("pushPackage")
	@ResponseBody
	public String pushPackage(
			@RequestParam(value = "workPackageName") String workPackageName,
			@RequestParam(value = "commitMessage") String commitMessage,
			@RequestParam(value = "userName") String userName, 
			@RequestParam(value = "password") String password) {
		logger.info("Push Package to Local Repository. workspace[" + workPackageName + "]commitMessage[" + commitMessage
				+ "]userName[" + userName + "]");
		return mainService.pushPackage(StringUtil.getPackageNameFromModelName(workPackageName), workPackageName, commitMessage, userName,
				password);
	}

	/**
	 * 指定されたpackageをリモートリポジトリからPullする.
	 *
	 * @param workPackageName パッケージ名
	 * @param userName ユーザー名
	 * @param password パスワード
	 * @return pull結果
	 */
	@PostMapping("pullPackage")
	@ResponseBody
	public String pullPackage(
			@RequestParam(value = "workPackageName") String workPackageName,
			@RequestParam(value = "userName") String userName, 
			@RequestParam(value = "password") String password) {
		logger.info("Pull Package from Local Repository. workspace[" + workPackageName + "]userName[" + userName + "]");
		return mainService.pullPackage(StringUtil.getPackageNameFromModelName(workPackageName), userName, password);
	}

	/**
	 * 指定されたComponentをローカルリポジトリにCommitする.
	 *
	 * @param workPackageName パッケージ名
	 * @param componentName コンポーネント名
	 * @param gitName git名
	 * @param commitMessage コミットメッセージ
	 * @return コミットの結果　
	 */
	@PostMapping("commitComponent")
	@ResponseBody
	public String commitComponent(
			@RequestParam(value = "workPackageName") String workPackageName,
			@RequestParam(value = "componentName") String componentName,
			@RequestParam(value = "gitName") String gitName,
			@RequestParam(value = "commitMessage") String commitMessage) {
		logger.info("Commit Component to Local Repository. component[" + componentName + "]git[" + gitName
				+ "]workspace[" + workPackageName + "]commitMessage[" + commitMessage + "]");
		return mainService.commitComponent(StringUtil.getPackageNameFromModelName(workPackageName), componentName,
				commitMessage);
	}

	/**
	 * 指定されたComponentをリモートリポジトリにPushする.
	 *
	 * @param workPackageName パッケージ名
	 * @param componentName コンポーネント名
	 * @param gitName git名
	 * @param commitMessage コミットメッセージ
	 * @param userName ユーザー名
	 * @param password パスワード
	 * @return pushの結果
	 */
	@PostMapping("pushComponent")
	@ResponseBody
	public String pushComponent(
			@RequestParam(value = "workPackageName") String workPackageName,
			@RequestParam(value = "componentName") String componentName,
			@RequestParam(value = "gitName") String gitName,
			@RequestParam(value = "commitMessage") String commitMessage,
			@RequestParam(value = "userName") String userName, 
			@RequestParam(value = "password") String password) {
		logger.info("Push Component to Local Repository. component[" + componentName + "]git[" + gitName + "]workspace["
				+ workPackageName + "]commitMessage[" + commitMessage + "]userName[" + userName + "]");
		return mainService.pushComponent(StringUtil.getPackageNameFromModelName(workPackageName), componentName,
				commitMessage, userName, password);
	}

	/**
	 * 指定されたComponentをリモートリポジトリからPullする.
	 *
	 * @param workPackageName パッケージ名
	 * @param componentName コンポーネント名
	 * @param gitName git名
	 * @param userName ユーザー名
	 * @param password パスワード
	 * @return 実行結果
	 */
	@PostMapping("pullComponent")
	@ResponseBody
	public String pullComponent(
			@RequestParam(value = "workPackageName") String workPackageName,
			@RequestParam(value = "componentName") String componentName,
			@RequestParam(value = "gitName") String gitName, 
			@RequestParam(value = "userName") String userName,
			@RequestParam(value = "password") String password) {
		logger.info("Pull Component from Local Repository. component[" + componentName + "]git[" + gitName
				+ "]workspace[" + workPackageName + "]userName[" + userName + "]");
		return mainService.pullComponent(StringUtil.getPackageNameFromModelName(workPackageName), componentName,
				userName, password);
	}
	
	/**
	 * コミットハッシュを取得する.
	 *
	 * @param packageName パッケージ名
	 * @return ハッシュ
	 */
	@GetMapping("getCommitHash")
	@ResponseBody
	public String getCommitHash(
			@RequestParam(value = "packageName") String packageName
			) {
		logger.info("Get Commit Hash.");
		return mainService.getCommitHash(packageName);
	}
	
	/**
	 * packageのstatusを確認する.
	 *
	 * @param ws ワークスペース名
	 * @param rtsName パッケージ名
	 * @return packageのstatus
	 */
	@GetMapping("checkPackageStatus")
	@ResponseBody
	public String checkPackageStatus(
			@RequestParam(value = "ws") String ws,
			@RequestParam(value = "rtsName") String rtsName
			) {
		logger.info("Check Package Status.");
		return mainService.checkPackageStatus(ws, rtsName);
	}
	
	/**
	 * RTCの状態を確認する.
	 *
	 * @param ws ワークスペース名
	 * @param rtsName パッケージ名
	 * @return RTCのstatus
	 */
	@GetMapping("checkRtcsStatus")
	@ResponseBody
	public String checkRtcsStatus(
			@RequestParam(value = "ws") String ws,
			@RequestParam(value = "rtsName") String rtsName
			) {
		logger.info("Check RTSs Status.");
		return mainService.checkRtcsStatus(ws, rtsName);
	}
	
	/**
	 * nameserverの状態を確認する.
	 *
	 * @param hostId ホストID
	 * @return nameserverのstatus
	 */
	@GetMapping("checkNameserverStatus")
	@ResponseBody
	public boolean checkNameserverStatus(@RequestParam(value = "hostId") String hostId) {
		logger.info("Check Nameserver Status.");
		return mainService.checkNameserverStatus(hostId);
	}

	/**
	 * Kerasのモデル一覧の選択肢取得.
	 *
	 * @return Kerasのモデル一覧
	 */
	@PostMapping("getKerasModelChoices")
	@ResponseBody
	public Map<String, String> getKerasModelChoices() {
		logger.info("Get Keras Model Choices.");
		return mainService.getKerasModelChoices();
	}

	/**
	 * データセットの選択肢取得.
	 *
	 * @return データセットの選択肢
	 */
	@PostMapping("getDatasetChoices")
	@ResponseBody
	public List<String> getDatasetChoices() {
		logger.info("Get Dataset Choices.");
		return mainService.getDatasetChoices();
	}

	/********************************************************************
	 * ビルド・実行関連
	 ********************************************************************/
	/**
	 * すべてのRTCをデプロイする.
	 *
	 * @param hostId ホスト名
	 * @param ws ワークスペース名
	 * @param remoteRepositoryUrl リモートリポジトリのURL
	 * @param commitHash コミットハッシュ
	 * @return HTTP Status Code
	 */
	@PostMapping("deploy")
	@ResponseBody
	public int deploy(
			@RequestParam(value = "hostId") String hostId,
			@RequestParam(value = "ws") String ws,
			@RequestParam(value = "remoteRepositoryUrl") String remoteRepositoryUrl,
			@RequestParam(value = "commitHash") String commitHash) {
		logger.info("Deploy All RTCs.");
		return mainService.deploy(hostId, ws, remoteRepositoryUrl, commitHash);

	}

	/**
	 * 指定されたPackageのすべてのRtcをビルドする.
	 *
	 * @param ws ワークスペース名
	 * @param rtsName パッケージ名
	 * @param hostId ホストID
	 */
	@PostMapping("buildPackageAll")
	@ResponseBody
	public void buildPackageAll(
			@RequestParam(value = "ws") String ws,
			@RequestParam(value = "rtsName") String rtsName,
			@RequestParam(value = "hostId") String hostId) {
		logger.info("Build package all. package[" + rtsName + "]");
		if (StringUtil.isNotEmpty(rtsName)) {
			mainService.buildPackageAll(new BuildRunDTO(ws, rtsName, hostId));
		}
	}

	/**
	 * 指定されたPackageのすべてのRtcをcleanする.
	 *
	 * @param ws ワークスペース名
	 * @param rtsName パッケージ名
	 * @param hostId ホストID
	 */
	@PostMapping("cleanPackageAll")
	@ResponseBody
	public void cleanPackageAll(
			@RequestParam(value = "ws") String ws,
			@RequestParam(value = "rtsName") String rtsName,
			@RequestParam(value = "hostId") String hostId) {
		logger.info("Clean package all. package[" + rtsName + "]");
		if (StringUtil.isNotEmpty(rtsName)) {
			mainService.cleanPackageAll(new BuildRunDTO(ws, rtsName, hostId));
		}
	}

	/**
	 * 指定されたPackageのSystemを実行する.
	 *
	 * @param ws ワークスペース名
	 * @param rtsName パッケージ名
	 * @param hostId ホストID
	 */
	@PostMapping("runSystem")
	@ResponseBody
	public void runSystem(
			@RequestParam(value = "ws") String ws, 
			@RequestParam(value = "rtsName") String rtsName,
			@RequestParam(value = "hostId") String hostId) {
		logger.info("Run System. package[" + rtsName + "]");
		if (StringUtil.isNotEmpty(rtsName)) {
			mainService.runSystem(new BuildRunDTO(ws, rtsName, hostId));
		}
	}

	/**
	 * RTCをスタート状態にする.
	 *
	 * @param ws ワークスペース名
	 * @param rtsName パッケージ名
	 * @param hostId ホストID
	 */
	@PostMapping("StartRTCs")
	@ResponseBody
	public void startRtcs(
			@RequestParam(value = "ws") String ws, 
			@RequestParam(value = "rtsName") String rtsName,
			@RequestParam(value = "hostId") String hostId
			) {
		logger.info("Start System. package[" + rtsName + "] Remotely");
		if (StringUtil.isNotEmpty(rtsName)) {
			mainService.startRtcs(new BuildRunDTO(ws, rtsName, hostId));
		}
	}

	/**
	 * コネクトする.
	 *
	 * @param ws ワークスペース名
	 * @param rtsName パッケージ名
	 * @param hostId ホストID
	 */
	@PostMapping("connectPorts")
	@ResponseBody
	public void connectPorts(
			@RequestParam(value = "ws") String ws, 
			@RequestParam(value = "rtsName") String rtsName,
			@RequestParam(value = "hostId") String hostId) {
		logger.info("Connect Ports");
		if (StringUtil.isNotEmpty(rtsName)) {
			mainService.connectPorts(new BuildRunDTO(ws, rtsName, hostId));
		}
	}
	
	/**
	 * アクティベイトかディアクティベイトをする.
	 *
	 * @param isActivate アクティベイトかどうか
	 * @param ws ワークスペース名
	 * @param rtsName パッケージ名
	 * @param hostId ホストID
	 */
	@PostMapping("activateOrDeactivateRtcs")
	@ResponseBody
	public void activateOrDeactivateRtcs(
			@RequestParam(value = "isActivate") boolean isActivate,
			@RequestParam(value = "ws") String ws,
			@RequestParam(value = "rtsName") String rtsName,
			@RequestParam(value = "hostId") String hostId) {
		logger.info("Activate RTCs.");
		if (StringUtil.isNotEmpty(rtsName)) {
			mainService.activateOrDeactivateRtcs(new BuildRunDTO(ws, rtsName, hostId), isActivate);
		}
	}

	/**
	 * 指定されたPackageののSystemを停止する.
	 *
	 * @param ws ワークスペース名
	 * @param rtsName パッケージ名
	 * @param hostId ホストID
	 */
	@PostMapping("terminateSystem")
	@ResponseBody
	public void terminateSystem(
			@RequestParam(value = "ws") String ws,
			@RequestParam(value = "rtsName") String rtsName,
			@RequestParam(value = "hostId") String hostId) {
		logger.info("Terminate System. package[" + rtsName + "]");
		if (StringUtil.isNotEmpty(rtsName)) {
			mainService.terminateSystem(new BuildRunDTO(ws, rtsName, hostId));
		}

		mainService.createResultFile();
	}

	/**
	 * 指定されたPackageの実行状況を確認する.
	 *
	 * @param ws ワークスペース名
	 * @param rtsName パッケージ名
	 * @param hostId ホストID
	 * @return Packageの実行状況
	 */
	@PostMapping("isRunningPackage")
	@ResponseBody
	public Map<String, Object> isRunningPackage(
			@RequestParam(value = "ws") String ws,
			@RequestParam(value = "rtsName") String rtsName,
			@RequestParam(value = "hostId") String hostId) {
		logger.info("Check Running System. package[" + rtsName + "]");
		Map<String, Object> result = null;
		if (StringUtil.isNotEmpty(rtsName)) {
			result = mainService.isRunningPackage(ws, rtsName, hostId);
		}

		return result;
	}

	/**
	 * ログファイルを読み込む.
	 *
	 * @param ws ワークスペース名
	 * @param workPackageName パッケージ名
	 * @param hostId ホストID
	 * @return ログファイル
	 */
	@PostMapping("tailLog")
	@ResponseBody
	public HttpEntity<byte[]> tailLog(
			@RequestParam(value = "ws") String ws,
			@RequestParam(value = "workPackageName") String workPackageName,
			@RequestParam(value = "hostId") String hostId) {
		logger.info("Tail Log. workspace[" + workPackageName + "]");
 		byte[] log = mainService.tailLog(ws, workPackageName, hostId);
		if (log != null) {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			headers.setContentLength(log.length);
			return new HttpEntity<byte[]>(log, headers);
		}
		return null;
	}
	
	/**
	 * 実行時のwasanbon.log取得.
	 *
	 * @return wasanbon.log
	 */
	@GetMapping("getExecuteWasanbonLog")
	@ResponseBody
	public String getExecuteWasanbonLog() {
		logger.info("実行時のwasanbon.log取得");
		return mainService.getExecuteWasanbonLog();
	}

	/**
	 * 実行時のwasanbon.logをクリアする.
	 *
	 */
	@PostMapping("clearExecuteWasanbonLog")
	@ResponseBody
	public void clearExecuteWasanbonLog() {
		logger.info("実行時のwasanbon.logをクリア");
		mainService.clearExecuteWasanbonLog();
	}

	/**
	 * 画像ファイルを読み込む.
	 *
	 * @param imageDirectoryPath 画像ファイルのパス
	 * @param date 日付
	 * @return 画像ファイル
	 */
	@GetMapping("tailImage")
	@ResponseBody
	public HttpEntity<byte[]> tailImage(
			@RequestParam(value = "imageDirectoryPath") String imageDirectoryPath,
			@RequestParam(value = "date") Long date) {
		logger.info("Tailing Image. image directiry path[" + imageDirectoryPath + "]");

		List<String> targetExtension = new ArrayList<>();
		byte[] image = mainService.tailImage(imageDirectoryPath, targetExtension);

		if (image != null) {
			HttpHeaders headers = new HttpHeaders();
			if (targetExtension.contains("png")) {
				headers.setContentType(MediaType.IMAGE_PNG);
			} else if (targetExtension.contains("jpg") || targetExtension.contains("jpeg")) {
				headers.setContentType(MediaType.IMAGE_JPEG);
			}
			headers.setContentLength(image.length);
			return new HttpEntity<byte[]>(image, headers);
		}
		return null;
	}

	/**
	 * 画像ファイルを読み込む.
	 *
	 * @param imageFilePath 画像ファイルのパス
	 * @return 画像ファイル
	 */
	@GetMapping("getImage")
	@ResponseBody
	public HttpEntity<byte[]> getImage(@RequestParam(value = "imageFilePath") String imageFilePath) {
		logger.info("Get Image. image directiry path[" + imageFilePath + "]");

		List<String> targetExtension = new ArrayList<>();
		File imageFile = new File(imageFilePath);
		byte[] image = mainService.getImage(imageFile, targetExtension);

		if (image != null) {
			HttpHeaders headers = new HttpHeaders();
			if (targetExtension.contains("png")) {
				headers.setContentType(MediaType.IMAGE_PNG);
			} else if (targetExtension.contains("jpg") || targetExtension.contains("jpeg")) {
				headers.setContentType(MediaType.IMAGE_JPEG);
			}
			headers.setContentLength(image.length);
			return new HttpEntity<byte[]>(image, headers);
		}
		return null;
	}

	/**
	 * 実行結果ファイルをダウンロードする.
	 *
	 * @param response レスポンス
	 * @return 実行結果ファイル
	 * @throws IOException
	 */
	@PostMapping("getResult")
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
	 * IDLファイルをアップロードする.
	 *
	 * @param response レスポンス
	 * @param workPackageName パッケージ名
	 * @param componentName コンポーネント名
	 * @param file ファイル
	 */
	@PostMapping("idlUpload")
	public void idlUpload(HttpServletResponse response,
			@RequestParam(value = "idl-package-name") String workPackageName,
			@RequestParam(value = "idl-component-name") String componentName,
			@RequestParam(value = "idl-upload") MultipartFile file) {

		// ファイルが空の場合は HTTP 400 を返す。
		if (file.isEmpty()) {
			logger.warn("IDL File is Empty.");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		RtmEditorUtil.saveIdlFile(StringUtil.getPackageNameFromModelName(workPackageName), componentName, file);
	}

	/**
	 * 指定されたDNNモデルを更新する.
	 *
	 * @param dnnModelName DNNモデル
	 * @param pathUri コンポーネントのpathUri
	 * @return 正常終了したかどうか
	 */
	@GetMapping("updateDnnModels")
	@ResponseBody
	public boolean updateDnnModels(
		@RequestParam(value = "dnnModelName") String dnnModelName,
		@RequestParam(value = "pathUri") String pathUri) throws IOException {
		logger.info("updateDnnModels. DNN Model Name[" + dnnModelName + "]");
		if (mainService.downloadDnnFiles(dnnModelName, pathUri)) {
				return true;
		} else {
			return false;
		}
	}

	/**
	 * 指定されたDNNファイルをダウンロードする.
	 *
	 * @param response レスポンス
	 * @param dnnModelName DNNモデル
	 * @param fileExtentions ファイル拡張子
	 * @throws IOException
	 */
	@PostMapping("getDnnFiles")
	public void getDnnFiles(
			HttpServletResponse response, 
			@RequestParam(value = "dnnModelName") String dnnModelName,
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
	}

	/**
	 * 指定されたdata_maker.pyをダウンロードする.
	 *
	 * @param response レスポンス
	 * @param dnnModelName DNNモデル
	 * @throws IOException
	 */
	@PostMapping("getDnnDataMakerFiles")
	public void getDnnDataMakerFiles(
			HttpServletResponse response, 
			@RequestParam(value = "dnnModelName") String dnnModelName) throws IOException {
		logger.info("Get DNN Files. file Name[" + dnnModelName);

		// Keras-Editorの作業領域パス
		String workspaceDirPath = PropUtil.getValue("workspace.local.keras.directory.path");
		// ダウンロードするDNNファイルパス
		String dnnFilePath = workspaceDirPath + dnnModelName + "/data_maker.py";

		File file = new File(dnnFilePath);
		response.addHeader("Content-Type", "application/octet-stream");
		response.addHeader("Content-Disposition",
				"attachment; filename*=UTF-8''" + URLEncoder.encode(file.getName(), StandardCharsets.UTF_8.name()));

		// ファイルが存在する場合は格納
		if (com.sec.airgraph.util.FileUtil.exists(file)) {
			Files.copy(file.toPath(), response.getOutputStream());
		}
	}

	/**
	 * ホスト定義ファイルに追加する.
	 *
	 * @param hostname ホスト名
	 * @param ip IPアドレス
	 * @param nsport ネームサーバーのポート番号
	 * @param wwport wasanbon-webframeworkのポート番号
	 * @param id ホストID
	 * @param password パスワード
	 * @return ファイルに書き込みできたかどうか
	 */
	@PostMapping("registerHostToConfigFile")
	@ResponseBody
	public boolean authHost(@RequestParam(value = "hostname") String hostname,
			@RequestParam(value = "IP") String ip,
			@RequestParam(value = "nsport") String nsport,
			@RequestParam(value = "wwport") String wwport,
			@RequestParam(value = "ID") String id,
			@RequestParam(value = "password") String password
	) {
		logger.info("Authenticate host.");
		return mainService.registerHostToConfigFile(id, password, hostname, ip, nsport, wwport);
	}

	/**
	 * AirGraph用ホストを追加する.
	 *
	 * @param hostname ホスト名
	 * @param ip IPアドレス
	 * @param port ネームサーバーのポート番号
	 * @return ファイルに書き込みできたかどうか
	 */
	@PostMapping("addAirGraphHost")
	@ResponseBody
	public boolean addAirGraphHost(
			@RequestParam(value = "hostname") String hostname,
			@RequestParam(value = "ip") String ip,
			@RequestParam(value = "port") String port
	) {
		logger.info("Add AirGraph host.");
		return mainService.addAirGraphHost(hostname, ip, port);
	}

	/**
	 * ホスト名、または(IP, Port)が重複しないか判定する.
	 *
	 * @param hostname ホスト名
	 * @param ip IPアドレス
	 * @param nsport ネームサーバーのポート番号
	 * @return 重複しないかどうか
	 */
	@PostMapping("isHostNameUnique")
	@ResponseBody
	public boolean isHostNameUnique(
			@RequestParam(value = "isWasanbon") boolean isWasanbon,
			@RequestParam(value = "hostname") String hostname,
			@RequestParam(value = "ip") String ip,
			@RequestParam(value = "nsport") String nsport
	) {
		logger.info("check whether hostName, (IP, Nameserver Port) are Unique or not.");
		return mainService.isHostNameUnique(isWasanbon, hostname, ip, nsport);
	}
	
	/**
	 * ホストIDが重複しないか判定する.
	 *
	 * @param id ホストID
	 * @return 重複しないかどうか
	 */
	@PostMapping("isHostIdUnique")
	@ResponseBody
	public boolean isHostIdUnique(
			@RequestParam(value = "id") String id
	) {
		logger.info("check whether hostID is Unique or not.");
		return mainService.isHostIdUnique(id);
	}
	
	/**
	 * Airgraphのバージョンを確認する.
	 *
	 * @return バージョン
	 */
	@GetMapping("getAirgraphVersion")
	@ResponseBody
	public String getAirgraphVersion() {

		logger.info("Airgraph Version");
		return mainService.getAirgraphVersion();
	}

	/**
	 * 問題がなければホスト定義ファイルを更新する.
	 *
	 * @param json ホストの情報
	 * @return 正常終了できたかどうか
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@PostMapping(value = "updateHostConfigFile", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public boolean updateHostConfigFile(@RequestBody ArrayList<ArrayList<Object>> json) throws JsonMappingException, JsonProcessingException {
		logger.info("Update Host Config File.");

		return mainService.updateHostConfigFile(json);
	}

	/**
	 * ホスト定義ファイルを読み込む.
	 *
	 * @return ホストの情報
	 */
	@GetMapping("loadHostList")
	@ResponseBody
	public List<Object> loadHostList() {
		logger.info("Load Host List.");

		return mainService.loadHostList();
	}
		
	/**
	 * GitHub設定ファイルを読み込む.
	 *
	 * @return GitHub設定ファイル
	 */
	@GetMapping("getGitHubConfigFile")
	@ResponseBody
	public GitHubSetting getGitHubConfigFile() {
	    logger.info("Get GitHub Config File.");

	    return mainService.getGitHubConfigFile();
	}
	
	/**
	 * nameserverを起動する.
	 *
	 * @param hostId ホストID
	 */
	@PostMapping("startNameserver")
	@ResponseBody
	public void startNameserver(
			@RequestParam(value = "hostId") String hostId) {
	    logger.info("Start Nameserver.");

	    mainService.startNameserver(hostId);
	}
	
	/********************************************************************
	 * binder関連
	 ********************************************************************/
	/**
	 * binderを作成する.
	 *
	 * @param username GitHubユーザー名
	 * @param token GitHubトークン
	 */
	@PostMapping("createBinder")
	@ResponseBody
	public void createBinder(
			@RequestParam(value = "username") String username,
			@RequestParam(value = "token") String token) {
		logger.info("Create Binder.");

		mainService.createBinder(username, token);
	}

	/**
	 * binderを更新する.
	 */
	@PostMapping("updateBinder")
	@ResponseBody
	public void updateBinder() {
		logger.info("Update Binder.");
		mainService.updateBinder();
	}

	/**
	 * Binderにパッケージを追加する.
	 *
	 * @param packageName パッケージ名
	 * @param binderName バインダー名
	 * @return 実行結果
	 */
	@PostMapping("addPackageToBinder")
	@ResponseBody
	public String addPackageToBinder(
			@RequestParam(value = "packageName") String packageName,
			@RequestParam(value = "binderName") String binderName) {

		logger.info("Add Package to Binder.");

		return mainService.addPackageToBinder(packageName, binderName);
	}

	/**
	 * Binderのパッケージを更新する.
	 *
	 * @param ws ワークスペース名
	 * @param packageName パッケージ名
	 * @param binderName バインダー名
	 * @return 実行結果
	 */
	@PutMapping("updatePackageToBinder")
	@ResponseBody
	public String updatePackageToBinder(
			@RequestParam(value = "ws") String ws, 
			@RequestParam(value = "packageName") String packageName, 
			@RequestParam(value = "binderName") String binderName) {

		logger.info("Update Package to Binder.");

		return mainService.updatePackageToBinder(ws, packageName, binderName);
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
	@PostMapping("addRtcToBinder")
	@ResponseBody
	public String addRtcToBinder(
			@RequestParam(value = "ws") String ws,
			@RequestParam(value = "packageName") String packageName,
			@RequestParam(value = "rtcName") String rtcName,
			@RequestParam(value = "binderName") String binderName) {

		logger.info("Add RTC to Binder.");

		return mainService.addRtcToBinder(ws, packageName, rtcName, binderName);
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
	@PutMapping("updateRtcToBinder")
	@ResponseBody
	public String updateRtcToBinder(
			@RequestParam(value = "ws") String ws, 
			@RequestParam(value = "packageName") String packageName, 
			@RequestParam(value = "rtcName") String rtcName, 
			@RequestParam(value = "binderName") String binderName) {

		logger.info("Update RTC to Binder.");

		return mainService.updateRtcToBinder(ws, packageName, rtcName, binderName);
	}

	/**
	 * Binderをcommitする.
	 *
	 * @param binderName バインダー名
	 * @param comment コミットメッセージ
	 * @return コミットの結果
	 */
	@PostMapping("commitBinder")
	@ResponseBody
	public String commitBinder(@RequestParam(value = "binderName") String binderName, 
								  @RequestParam(value = "comment") String comment) {

		logger.info("Commit Binder.");

		return mainService.commitBinder(binderName, comment);
	}

	/**
	 * Binderをpushする.
	 *
	 * @param binderName バインダー名
	 * @param comment コミットメッセージ
	 * @return pushの結果
	 */
	@PostMapping("pushBinder")
	@ResponseBody
	public String pushBinder(@RequestParam(value = "binderName") String binderName, 
								  @RequestParam(value = "comment") String comment) {

		logger.info("Push Binder.");

		return mainService.pushBinder(binderName, comment);
	}

	/**
	 * Wasanbonのバージョンを確認する.
	 *
	 * @param hostId ホストID
	 * @return バージョン
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@GetMapping("getWasanbonVersion")
	@ResponseBody
	public String getWasanbonVersion(@RequestParam(value = "hostId") String hostId) throws JsonMappingException, JsonProcessingException {

		logger.info("Wasanbon Version");
		return mainService.getWasanbonVersion(hostId);
	}
}


