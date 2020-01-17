package com.sec.airgraph.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sec.rtc.entity.rtc.Configuration;
import com.sec.rtc.entity.rtc.DataPort;
import com.sec.rtc.entity.rtc.Rtc;
import com.sec.rtc.entity.rtc.RtcProfile;
import com.sec.rtc.entity.rtc.ServiceInterface;
import com.sec.rtc.entity.rtc.ServicePort;
import com.sec.rtc.entity.rts.Rts;
import com.sec.airgraph.util.Const.RT_COMPONENT.CONFIGURATION_TYPE;
import com.sec.airgraph.util.Const.RT_COMPONENT.INTERFACE_DIRECTION;
import com.sec.airgraph.util.Const.RT_COMPONENT.PORT_DATA_TYPE;
import com.sec.airgraph.util.Const.RT_COMPONENT.PORT_TYPE;

/**
 * RTC関連Utility
 * 
 * @author Tsuyoshi Hirose
 *
 */
public class RtcUtil {
	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(RtcUtil.class);

	/************************************************************
	 * オブジェクト操作関連
	 ************************************************************/
	/**
	 * RtsProfileがNullかどうか判定する
	 * 
	 * @param rts
	 * @return
	 */
	public static boolean rtsPorfileIsNotEmpty(Rts rts) {
		if (rts != null && rts.getRtsProfile() != null) {
			return true;
		}
		return false;
	}

	/**
	 * RtcProfileがNullかどうか判定する
	 * 
	 * @param rtc
	 * @return
	 */
	public static boolean rtcPorfileIsNotEmpty(Rtc rtc) {
		if (rtc != null && rtc.getRtcProfile() != null) {
			return true;
		}
		return false;
	}

	/**
	 * データタイプから必要となるロガーのコンポーネント名を取得する
	 * 
	 * @param dataType
	 * @return
	 */
	public static String getComponentNameByDataType(String dataType) {
		String componentName = null;
		switch (dataType) {
		case "Img::" + PORT_DATA_TYPE.IMG.TIMED_CAMERA_IMAGE:
			componentName = "LoggerForTimedCameraImage";
			break;
		default:
			break;
		}
		return componentName;
	}

	/**
	 * データタイプから必要となるロガーのGit名を取得する
	 * 
	 * @param dataType
	 * @return
	 */
	public static String getGitNameByDataType(String dataType) {
		String componentName = null;
		switch (dataType) {
		case "Img::" + PORT_DATA_TYPE.IMG.TIMED_CAMERA_IMAGE:
			componentName = "LoggerForImage";
			break;
		default:
			break;
		}
		return componentName;
	}

	/**
	 * データタイプから必要となるロガーのGit名を取得する
	 * 
	 * @param dataType
	 * @return
	 */
	public static String getDirectoryNameByDataType(String dataType) {
		String dirName = null;
		switch (dataType) {
		case "Img::" + PORT_DATA_TYPE.IMG.TIMED_CAMERA_IMAGE:
			dirName = "PX039_LoggerForImage";
			break;
		default:
			break;
		}
		return dirName;
	}

	/**
	 * データ型から接続時のデータ型に変換する
	 * 
	 * @param portDataType
	 * @return
	 */
	public static String getConnectionPortDataType(String portDataType) {
		if (portDataType.contains("::")) {
			String[] arr = portDataType.split("::");
			return "IDL:" + arr[0] + "/" + arr[1] + ":1.0";
		} else {
			return "IDL:" + portDataType + ":1.0";
		}
	}

	/**
	 * データポートに設定されたデータ型からPythonのコンストラクタを取得する
	 * 
	 * @param dataType
	 * @return
	 */
	private static String getPythonCostructorFromDataPort(String dataType) {
		String result = null;
		if (StringUtil.isNotEmpty(dataType)) {
			if (dataType.contains("Img::")) {
				result = RtmEditorUtil.getPythonConstructorForImg(dataType.replace("Img::", ""));
			} else if (dataType.contains("RTC::")) {
				result = RtmEditorUtil.getPythonConstructorForRtc(dataType.replace("RTC::", ""));
			} else {
				result = (dataType.contains("::") ? dataType.replace("::", ".") : "") + dataType + "(0)";
			}
		}
		return result;
	}

	/************************************************************
	 * ソースコード自動反映処理
	 ************************************************************/
	/**
	 * ModuleSpec用文字列を生成する
	 * 
	 * @param rtcProfile
	 * @return
	 */
	private static List<String> createModuleSpec(RtcProfile rtcProfile) {
		List<String> stringList = new ArrayList<String>();

		// implementation_id
		stringList.add("\"implementation_id\", \"" + rtcProfile.getBasicInfo().getModuleName() + "\",");
		// type_name
		stringList.add("\"type_name\", \"" + rtcProfile.getBasicInfo().getModuleName() + "\",");
		// description
		stringList.add("\"description\", \"" + rtcProfile.getBasicInfo().getModuleDescription() + "\",");
		// version
		stringList.add("\"version\", \"" + rtcProfile.getBasicInfo().getVersion() + "\",");
		// vendor
		stringList.add("\"vendor\", \"" + rtcProfile.getBasicInfo().getVendor() + "\",");
		// category
		stringList.add("\"category\", \"" + rtcProfile.getBasicInfo().getModuleCategory() + "\",");
		// activity_type
		stringList.add("\"activity_type\", \"" + rtcProfile.getBasicInfo().getActivityType() + "\",");
		// kind
		stringList.add("\"kind\", \"" + (StringUtil.isNotEmpty(rtcProfile.getBasicInfo().getComponentKind())
				? rtcProfile.getBasicInfo().getComponentKind()
				: "") + "\",");
		// max_instance
		stringList.add("\"max_instance\", \""
				+ (rtcProfile.getBasicInfo().getMaxInstances() != null ? rtcProfile.getBasicInfo().getMaxInstances()
						: "")
				+ "\",");

		return stringList;
	}

	/**
	 * ModuleSpecのコンフィギュレーション用文字列を生成する
	 * 
	 * @param configurationList
	 * @param appendComment
	 * @return
	 */
	private static List<String> createModuleSpecConfiguration(List<Configuration> configurationList,
			boolean appendComment) {
		List<String> stringList = new ArrayList<>();

		List<String> variableList = new ArrayList<>();
		List<String> widgetList = new ArrayList<>();
		List<String> constraintList = new ArrayList<>();
		List<String> typeList = new ArrayList<>();

		if (appendComment) {
			variableList.add("// Configuration variables");
			widgetList.add("// Widget");
			constraintList.add("// Constraints");
		}
		for (Configuration configuration : configurationList) {
			variableList.add(
					"\"conf.default." + configuration.getName() + "\", \"" + configuration.getVariableName() + "\",");
			widgetList.add("\"conf.__widget__." + configuration.getName() + "\", \""
					+ configuration.getProperties().getValue() + "\",");
			if (configuration.getConstraint() != null && configuration.getConstraint().getConstraintUnitType() != null
					&& configuration.getConstraint().getConstraintUnitType().getPropertyIsEqualTo() != null
					&& StringUtil.isNotEmpty(configuration.getConstraint().getConstraintUnitType()
							.getPropertyIsEqualTo().getLiteral())) {
				constraintList.add("\"conf.__constraints__." + configuration.getName() + "\", \""
						+ configuration.getConstraint().getConstraintUnitType().getPropertyIsEqualTo().getLiteral()
						+ "\",");
			}
			typeList.add("\"conf.__type__." + configuration.getName() + "\", \"" + configuration.getDataType() + "\",");
		}

		stringList.addAll(variableList);
		stringList.addAll(widgetList);
		if (constraintList.size() > 1) {
			stringList.addAll(constraintList);
		}
		stringList.addAll(typeList);

		return stringList;
	}

	/************************************************************
	 * C++用のソースコード自動反映処理（自動反映文字列生成）
	 ************************************************************/
	/**
	 * C++用のModuleSpeを生成する
	 * 
	 * @param rtcProfile
	 * @return
	 */
	public static List<String> createModuleSpecForCpp(RtcProfile rtcProfile) {
		List<String> stringList = new ArrayList<String>();

		// prefix
		stringList.add("static const char* " + rtcProfile.getBasicInfo().getModuleName() + "_spec[] =");
		stringList.add("{");

		// 共通
		stringList.addAll(createModuleSpec(rtcProfile));

		// suffix
		stringList.add("\"language\", \"C++\",");
		stringList.add("\"lang_type\", \"compile\",");

		// コンフィギュレーション設定
		if (CollectionUtil.isNotEmpty(rtcProfile.getConfigurationSet().getConfigurations())) {
			stringList
					.addAll(createModuleSpecConfiguration(rtcProfile.getConfigurationSet().getConfigurations(), true));
		}

		stringList.add("\"\"");
		stringList.add("};");

		return stringList;
	}

	/**
	 * C++ヘッダ用のservice_impl_hを生成する
	 * 
	 * @param rtcProfile
	 * @return
	 */
	public static List<String> createServiceImplementHeaderCppHeader(RtcProfile rtcProfile) {
		List<String> stringList = new ArrayList<String>();
		if (CollectionUtil.isNotEmpty(rtcProfile.getServicePorts())) {
			for (ServicePort servicePort : rtcProfile.getServicePorts()) {
				if (CollectionUtil.isNotEmpty(servicePort.getServiceInterfaces())) {
					for (ServiceInterface serviceInterface : servicePort.getServiceInterfaces()) {
						if (INTERFACE_DIRECTION.PROVIDED.equals(serviceInterface.getDirection())) {
							String str = "#include \""
									+ createServiceInterfaceImplementCpp(serviceInterface.getIdlFile()) + ".h\"";
							if (!stringList.contains(str)) {
								stringList.add(str);
							}
						}
					}
				}
			}
		}
		return stringList;
	}

	/**
	 * C++ヘッダ用のconsumer_stub_hを生成する
	 * 
	 * @param rtcProfile
	 * @return
	 */
	public static List<String> createConsumerStubHeaderCppHeader(RtcProfile rtcProfile) {
		List<String> stringList = new ArrayList<String>();
		if (CollectionUtil.isNotEmpty(rtcProfile.getServicePorts())) {
			for (ServicePort servicePort : rtcProfile.getServicePorts()) {
				if (CollectionUtil.isNotEmpty(servicePort.getServiceInterfaces())) {
					for (ServiceInterface serviceInterface : servicePort.getServiceInterfaces()) {
						if (INTERFACE_DIRECTION.REQUIRED.equals(serviceInterface.getDirection())) {
							String str = "#include \"" + createServiceInterfaceStubCpp(serviceInterface.getIdlFile())
									+ ".h\"";
							if (!stringList.contains(str)) {
								stringList.add(str);
							}
						}
					}
				}
			}
		}
		return stringList;
	}

	/**
	 * C++ヘッダ用のport_stub_hを生成する
	 * 
	 * @param rtcProfile
	 * @return
	 */
	public static List<String> createPortStubHeaderCppHeader(RtcProfile rtcProfile) {
		List<String> stringList = new ArrayList<String>();
		Map<String, String> map = new HashMap<>();
		if (CollectionUtil.isNotEmpty(rtcProfile.getDataPorts())) {
			for (DataPort dataPort : rtcProfile.getDataPorts()) {
				map.put(dataPort.getDataType().split("::")[0], dataPort.getDataType().split("::")[0]);
			}
			for (String str : map.values()) {
				stringList.add("using namespace " + str + ";");
			}
		}
		return stringList;
	}

	/**
	 * C++ヘッダ用のインポートの宣言文を生成する
	 * 
	 * @param rtcProfile
	 * @return
	 */
	public static List<String> createInportDeclareListCppHeader(RtcProfile rtcProfile) {
		List<String> stringList = new ArrayList<String>();
		if (CollectionUtil.isNotEmpty(rtcProfile.getDataPorts())) {
			for (DataPort dataPort : rtcProfile.getDataPorts()) {
				if (PORT_TYPE.IN.equals(dataPort.getPortType())) {
					stringList.addAll(createDataPortDeclareCppHeader(dataPort.getName(), dataPort.getVariableName(),
							dataPort.getDataType(), "In"));
				}
			}
		}
		return stringList;
	}

	/**
	 * C++ヘッダ用のアウトポートの宣言文を生成する
	 * 
	 * @param rtcProfile
	 * @return
	 */
	public static List<String> createOutportDeclareListCppHeader(RtcProfile rtcProfile) {
		List<String> stringList = new ArrayList<String>();
		if (CollectionUtil.isNotEmpty(rtcProfile.getDataPorts())) {
			for (DataPort dataPort : rtcProfile.getDataPorts()) {
				if (PORT_TYPE.OUT.equals(dataPort.getPortType())) {
					stringList.addAll(createDataPortDeclareCppHeader(dataPort.getName(), dataPort.getVariableName(),
							dataPort.getDataType(), "Out"));
				}
			}
		}
		return stringList;
	}

	/**
	 * C++ヘッダ用のサービスポートの宣言文を生成する
	 * 
	 * @param rtcProfile
	 * @return
	 */
	public static List<String> createCorbaportDeclareListCppHeader(RtcProfile rtcProfile) {
		List<String> stringList = new ArrayList<String>();
		if (CollectionUtil.isNotEmpty(rtcProfile.getServicePorts())) {
			for (ServicePort servicePort : rtcProfile.getServicePorts()) {
				stringList.add("RTC::CorbaPort " + createServicePortInstanceCpp(servicePort.getName()) + ";");
			}
		}
		return stringList;
	}

	/**
	 * C++ヘッダ用のProvidedのサービスインタフェースの宣言文を生成する
	 * 
	 * @param rtcProfile
	 * @return
	 */
	public static List<String> createServiceDeclareListCppHeader(RtcProfile rtcProfile) {
		List<String> stringList = new ArrayList<String>();
		if (CollectionUtil.isNotEmpty(rtcProfile.getServicePorts())) {
			for (ServicePort servicePort : rtcProfile.getServicePorts()) {
				if (CollectionUtil.isNotEmpty(servicePort.getServiceInterfaces())) {
					for (ServiceInterface serviceInterface : servicePort.getServiceInterfaces()) {
						if (INTERFACE_DIRECTION.PROVIDED.equals(serviceInterface.getDirection())) {
							stringList.add(createServiceInterfaceClassCpp(serviceInterface.getInterfaceType()) + " "
									+ createServiceInterfaceInstanceCpp(serviceInterface.getName(),
											serviceInterface.getInstanceName(), serviceInterface.getVariableName())
									+ ";");
						}
					}
				}
			}
		}
		return stringList;
	}

	/**
	 * C++ヘッダ用のRequiredのサービスインタフェースの宣言文を生成する
	 * 
	 * @param rtcProfile
	 * @return
	 */
	public static List<String> createConsumerDeclareListCppHeader(RtcProfile rtcProfile) {
		List<String> stringList = new ArrayList<String>();
		if (CollectionUtil.isNotEmpty(rtcProfile.getServicePorts())) {
			for (ServicePort servicePort : rtcProfile.getServicePorts()) {
				if (CollectionUtil.isNotEmpty(servicePort.getServiceInterfaces())) {
					for (ServiceInterface serviceInterface : servicePort.getServiceInterfaces()) {
						if (INTERFACE_DIRECTION.REQUIRED.equals(serviceInterface.getDirection())) {
							stringList.add("RTC::CorbaConsumer<" + serviceInterface.getInterfaceType() + "> "
									+ createServiceInterfaceInstanceCpp(serviceInterface.getName(),
											serviceInterface.getInstanceName(), serviceInterface.getVariableName())
									+ ";");
						}
					}
				}
			}
		}
		return stringList;
	}

	/**
	 * C++ヘッダ用のコンフィギュレーションの宣言文を生成する
	 * 
	 * @param rtcProfile
	 * @return
	 */
	public static List<String> craeteConfigDeclareListCppHeader(RtcProfile rtcProfile) {
		List<String> stringList = new ArrayList<String>();
		if (CollectionUtil.isNotEmpty(rtcProfile.getConfigurationSet().getConfigurations())) {
			for (Configuration configuration : rtcProfile.getConfigurationSet().getConfigurations()) {
				stringList.add("/*!");
				stringList.add(" *");
				if (StringUtil.isNotEmpty(configuration.getVariableName())) {
					stringList.add(" * - Name:  " + configuration.getVariableName());
				} else {
					stringList.add(" * - Name:  " + configuration.getName());
				}
				stringList.add(" * - DefaultValue: " + configuration.getDefaultValue());
				stringList.add(" */");

				switch (configuration.getDataType()) {
				case CONFIGURATION_TYPE.SHORT:
					stringList.add("short int "
							+ createConfigurationInsatanceCpp(configuration.getName(), configuration.getVariableName())
							+ ";");
					break;
				case CONFIGURATION_TYPE.INT:
					stringList.add("int "
							+ createConfigurationInsatanceCpp(configuration.getName(), configuration.getVariableName())
							+ ";");
					break;
				case CONFIGURATION_TYPE.LONG:
					stringList.add("long int "
							+ createConfigurationInsatanceCpp(configuration.getName(), configuration.getVariableName())
							+ ";");
					break;
				case CONFIGURATION_TYPE.FLOAT:
					stringList.add("float "
							+ createConfigurationInsatanceCpp(configuration.getName(), configuration.getVariableName())
							+ ";");
					break;
				case CONFIGURATION_TYPE.DOUBLE:
					stringList.add("double "
							+ createConfigurationInsatanceCpp(configuration.getName(), configuration.getVariableName())
							+ ";");
					break;
				case CONFIGURATION_TYPE.STRING:
					stringList.add("std::string "
							+ createConfigurationInsatanceCpp(configuration.getName(), configuration.getVariableName())
							+ ";");
					break;
				}
			}
		}
		return stringList;
	}

	/**
	 * C++ソースコード用のInitializerを生成する
	 * 
	 * @param rtcProfile
	 * @return
	 */
	public static List<String> createInitializerCppSource(RtcProfile rtcProfile) {
		List<String> stringList = new ArrayList<String>();

		// ComponentKind
		stringList.add(": RTC::" + rtcProfile.getBasicInfo().getComponentKind() + "Base(manager),");

		// DataInPort
		if (CollectionUtil.isNotEmpty(rtcProfile.getDataInPorts())) {
			for (DataPort dataPort : rtcProfile.getDataInPorts()) {
				stringList
						.add(createDataPortInitializerCppSource(dataPort.getName(), dataPort.getVariableName(), "In"));
			}
		}

		// DataOutPort
		if (CollectionUtil.isNotEmpty(rtcProfile.getDataOutPorts())) {
			for (DataPort dataPort : rtcProfile.getDataOutPorts()) {
				stringList
						.add(createDataPortInitializerCppSource(dataPort.getName(), dataPort.getVariableName(), "Out"));
			}
		}

		// ServicePort
		if (CollectionUtil.isNotEmpty(rtcProfile.getServicePorts())) {
			for (ServicePort servicePort : rtcProfile.getServicePorts()) {
				stringList.add(createServicePortInitializerCppSource(servicePort.getName()));
			}
		}

		// 最後の,を削除する
		String last = stringList.get(stringList.size() - 1);
		last = last.substring(0, last.length() - 1);
		stringList.remove(stringList.size() - 1);
		stringList.add(last);

		return stringList;
	}

	/**
	 * C++ソースコード用のregistrationを生成する
	 * 
	 * @param rtcProfile
	 * @return
	 */
	public static List<String> createRegistrationCppSource(RtcProfile rtcProfile) {
		List<String> stringList = new ArrayList<String>();

		// DataInPort
		stringList.add("// Set InPort Buffers");
		if (CollectionUtil.isNotEmpty(rtcProfile.getDataInPorts())) {
			for (DataPort dataPort : rtcProfile.getDataInPorts()) {
				stringList
						.add(createDataPortRegistrationCppSource(dataPort.getName(), dataPort.getVariableName(), "In"));
			}
		}

		// DataOutPort
		stringList.add("// Set OutPort Buffers");
		if (CollectionUtil.isNotEmpty(rtcProfile.getDataOutPorts())) {
			for (DataPort dataPort : rtcProfile.getDataOutPorts()) {
				stringList.add(
						createDataPortRegistrationCppSource(dataPort.getName(), dataPort.getVariableName(), "Out"));
			}
		}

		// ServicePort
		List<String> providerList = new ArrayList<String>();
		List<String> consumerList = new ArrayList<String>();
		List<String> serviceList = new ArrayList<String>();
		if (CollectionUtil.isNotEmpty(rtcProfile.getServicePorts())) {
			for (ServicePort servicePort : rtcProfile.getServicePorts()) {
				if (CollectionUtil.isNotEmpty(servicePort.getServiceInterfaces())) {
					for (ServiceInterface serviceInterface : servicePort.getServiceInterfaces()) {
						if (INTERFACE_DIRECTION.PROVIDED.equals(serviceInterface.getDirection())) {
							providerList.add(createInterfaceRegistrationCppSource(servicePort.getName(), "Provider",
									serviceInterface.getInterfaceType(), serviceInterface.getName(),
									serviceInterface.getInstanceName(), serviceInterface.getVariableName()));
						} else if (INTERFACE_DIRECTION.REQUIRED.equals(serviceInterface.getDirection())) {
							consumerList.add(createInterfaceRegistrationCppSource(servicePort.getName(), "Consumer",
									serviceInterface.getInterfaceType(), serviceInterface.getName(),
									serviceInterface.getInstanceName(), serviceInterface.getVariableName()));
						}
					}
				}
				serviceList.add(createServicePortRegistrationCppSource(servicePort.getName()));
			}
		}
		stringList.add("// Set service provider to Ports");
		if (CollectionUtil.isNotEmpty(providerList)) {
			stringList.addAll(providerList);
		}
		stringList.add("// Set service consumers to Ports");
		if (CollectionUtil.isNotEmpty(consumerList)) {
			stringList.addAll(consumerList);
		}
		stringList.add("// Set CORBA Service Ports");
		if (CollectionUtil.isNotEmpty(serviceList)) {
			stringList.addAll(serviceList);
		}

		return stringList;
	}

	/**
	 * C++ソースコード用のbind_configを生成する
	 * 
	 * @param rtcProfile
	 * @return
	 */
	public static List<String> createBindConfigCppSource(RtcProfile rtcProfile) {
		List<String> stringList = new ArrayList<String>();
		stringList.add("// Bind variables and configuration variable");
		if (CollectionUtil.isNotEmpty(rtcProfile.getConfigurationSet().getConfigurations())) {
			for (Configuration configuration : rtcProfile.getConfigurationSet().getConfigurations()) {
				StringBuilder sbBindConfig = new StringBuilder();
				sbBindConfig.append("bindParameter(\"");
				sbBindConfig.append(configuration.getName());
				sbBindConfig.append("\", ");
				sbBindConfig.append(
						createConfigurationInsatanceCpp(configuration.getName(), configuration.getVariableName()));
				sbBindConfig.append(", \"");
				sbBindConfig.append(configuration.getDefaultValue());
				sbBindConfig.append("\");");
				stringList.add(sbBindConfig.toString());
			}
		}

		return stringList;
	}

	/**
	 * C++ヘッダ用のDataPortの宣言文を生成する
	 * 
	 * @param portName
	 * @param valName
	 * @param dataType
	 * @param portType
	 * @return
	 */
	private static List<String> createDataPortDeclareCppHeader(String portName, String valName, String dataType,
			String portType) {
		List<String> stringList = new ArrayList<String>();

		// データ型の宣言
		StringBuilder sbDataType = new StringBuilder();
		sbDataType.append(dataType);
		sbDataType.append(" ");
		sbDataType.append(createPortDataTypeInstanceCpp(portName, valName));
		sbDataType.append(";");
		stringList.add(sbDataType.toString());

		// ポートの宣言
		StringBuilder sbPort = new StringBuilder();
		sbPort.append(portType);
		sbPort.append("Port<");
		sbPort.append(dataType);
		sbPort.append("> ");
		sbPort.append(createDataPortInstanceCpp(portName, valName, portType));
		sbPort.append(";");
		stringList.add(sbPort.toString());

		return stringList;
	}

	/**
	 * C++ソースコード用のデータポート用のInitializerを生成する
	 * 
	 * @param portName
	 * @param valName
	 * @param portType
	 * @return
	 */
	private static String createDataPortInitializerCppSource(String portName, String valName, String portType) {
		StringBuilder sb = new StringBuilder();
		sb.append(createDataPortInstanceCpp(portName, valName, portType));
		sb.append("(\"");
		sb.append(portName);
		sb.append("\", ");
		sb.append(createPortDataTypeInstanceCpp(portName, valName));
		sb.append("),");
		return sb.toString();
	}

	/**
	 * C++ソースコード用のデータポート用のInitializerを生成する
	 * 
	 * @param portName
	 * @return
	 */
	private static String createServicePortInitializerCppSource(String portName) {
		StringBuilder sb = new StringBuilder();
		sb.append(createServicePortInstanceCpp(portName));
		sb.append("(\"");
		sb.append(portName);
		sb.append("\"),");
		return sb.toString();
	}

	/**
	 * C++ソースコード用のデータポート用のRegistrationを生成する
	 * 
	 * @param portName
	 * @param valName
	 * @param portType
	 * @return
	 */
	private static String createDataPortRegistrationCppSource(String portName, String valName, String portType) {
		StringBuilder sb = new StringBuilder();
		sb.append("add");
		sb.append(portType);
		sb.append("Port(\"");
		sb.append(portName);
		sb.append("\", ");
		sb.append(createDataPortInstanceCpp(portName, valName, portType));
		sb.append(");");
		return sb.toString();
	}

	/**
	 * C++ソースコード用のサービスインタフェース用のRegistrationを生成する
	 * 
	 * @param portName
	 * @param direction
	 * @param ifType
	 * @param ifName
	 * @param valName
	 * @return
	 */
	private static String createInterfaceRegistrationCppSource(String portName, String directon, String ifType,
			String ifName, String insName, String valName) {
		StringBuilder sb = new StringBuilder();
		sb.append(createServicePortInstanceCpp(portName));
		sb.append(".register");
		sb.append(directon);
		sb.append("(\"");
		sb.append(ifName);
		sb.append("\", \"");
		sb.append(ifType);
		sb.append("\", ");
		sb.append(createServiceInterfaceInstanceCpp(ifName, insName, valName));
		sb.append(");");
		return sb.toString();
	}

	/**
	 * C++ソースコード用のサービスポート用のRegistrationを生成する
	 * 
	 * @param portName
	 * @return
	 */
	private static String createServicePortRegistrationCppSource(String portName) {
		StringBuilder sb = new StringBuilder();
		sb.append("addPort(");
		sb.append(createServicePortInstanceCpp(portName));
		sb.append(");");
		return sb.toString();
	}

	/**
	 * C++用のデータポートのデータ型のインスタンス名を生成する
	 * 
	 * @param portName
	 * @param valName
	 * @return
	 */
	private static String createPortDataTypeInstanceCpp(String portName, String valName) {
		if (StringUtil.isNotEmpty(valName)) {
			return "m_" + valName;
		} else {
			return "m_" + portName;
		}
	}

	/**
	 * C++用のデータポートのインスタンス名を生成する
	 * 
	 * @param portName
	 * @param valName
	 * @param portType
	 * @return
	 */
	private static String createDataPortInstanceCpp(String portName, String valName, String portType) {
		if (StringUtil.isNotEmpty(valName)) {
			return "m_" + valName + portType;
		} else {
			return "m_" + portName + portType;
		}
	}

	/**
	 * C++用のサービスポートのインスタンス名を生成する
	 * 
	 * @param portName
	 * @return
	 */
	private static String createServicePortInstanceCpp(String portName) {
		return "m_" + portName + "Port";
	}

	/**
	 * C++用のサービスインタフェースのimplement名を生成する
	 * 
	 * @param ifType
	 * @return
	 */
	private static String createServiceInterfaceImplementCpp(String idlFile) {
		return idlFile.replace(".idl", "") + "SVC_impl";
	}

	/**
	 * C++用のサービスインタフェースのstub名を生成する
	 * 
	 * @param idlFile
	 * @return
	 */
	private static String createServiceInterfaceStubCpp(String idlFile) {
		return idlFile.replace(".idl", "") + "Stub";
	}

	/**
	 * C++用のサービスインタフェースのクラス宣言を生成する
	 * 
	 * @param ifName
	 * @return
	 */
	private static String createServiceInterfaceClassCpp(String ifType) {
		return ifType.contains("::") ? (ifType.split("::")[0] + "_" + ifType.split("::")[1] + "SVC_impl") : ifType;
	}

	/**
	 * C++用のサービスインタフェースのインスタンス名を生成する
	 * 
	 * @param ifName
	 * @param insName
	 * @param valName
	 * @return
	 */
	private static String createServiceInterfaceInstanceCpp(String ifName, String insName, String valName) {
		if (StringUtil.isNotEmpty(valName)) {
			return "m_" + valName;
		} else if (StringUtil.isNotEmpty(insName)) {
			return "m_" + insName;
		} else {
			return "m_" + ifName;
		}
	}

	/**
	 * C++用のコンフィギュレーションのインスタンス名を生成する
	 * 
	 * @param configName
	 * @param valName
	 * @return
	 */
	private static String createConfigurationInsatanceCpp(String configName, String valName) {
		StringBuilder sb = new StringBuilder();
		sb.append("m_");
		if (StringUtil.isNotEmpty(valName)) {
			sb.append(valName);
		} else {
			sb.append(configName);
		}
		return sb.toString();
	}

	/************************************************************
	 * C++用のソースコード自動反映処理（自動反映実行）
	 ************************************************************/
	/**
	 * C++ソースファイルのmodule_specを更新する
	 * 
	 * @param codeFilePath
	 * @param strList
	 */
	public static void updateModuleSpecCpp(String codeFilePath, List<String> strList) {
		String rtcTemplateTag = "block=\"module_spec\"";

		if (CollectionUtil.isNotEmpty(strList)) {
			for (int i = 0; i < strList.size(); i++) {
				String line = strList.get(i);
				if (i == 0) {
					// NOP
				} else if ("{".equals(line) || "};".equals(line)) {
					// C++の{}はインデントは半角スペース２文字
					strList.set(i, "  " + line);
				} else {
					// それ以外の行は半角スペース４文字
					strList.set(i, "    " + line);
				}
			}
			autoGenerateRtcTemplate(codeFilePath, strList, rtcTemplateTag);
		}
	}

	/**
	 * C++ソースファイルのinitializerを更新する
	 * 
	 * @param codeFilePath
	 * @param strList
	 */
	public static void updateInitializerCpp(String codeFilePath, List<String> strList) {
		String rtcTemplateTag = "block=\"initializer\"";

		if (CollectionUtil.isNotEmpty(strList)) {
			for (int i = 0; i < strList.size(); i++) {
				String line = strList.get(i);
				if (i == 0) {
					// １行目は半角スペース２文字
					strList.set(i, "  " + line);
				} else {
					// それ以外の行は半角スペース４文字
					strList.set(i, "    " + line);
				}
			}
			autoGenerateRtcTemplate(codeFilePath, strList, rtcTemplateTag);
		}
	}

	/**
	 * C++ソースファイルのregistrationを更新する
	 * 
	 * @param codeFilePath
	 * @param strList
	 */
	public static void updateRegistrationCpp(String codeFilePath, List<String> strList) {
		String rtcTemplateTag = "block=\"registration\"";

		if (CollectionUtil.isNotEmpty(strList)) {
			for (int i = 0; i < strList.size(); i++) {
				String line = strList.get(i);
				strList.set(i, "    " + line);
			}
			autoGenerateRtcTemplate(codeFilePath, strList, rtcTemplateTag);
		}
	}

	/**
	 * C++ソースファイルのbind_configを更新する
	 * 
	 * @param codeFilePath
	 * @param strList
	 */
	public static void updateBindConfigCpp(String codeFilePath, List<String> strList) {
		String rtcTemplateTag = "block=\"bind_config\"";

		if (CollectionUtil.isNotEmpty(strList)) {
			for (int i = 0; i < strList.size(); i++) {
				String line = strList.get(i);
				strList.set(i, "    " + line);
			}
			autoGenerateRtcTemplate(codeFilePath, strList, rtcTemplateTag);
		}
	}

	/**
	 * C++ソースファイルのcomponent_kindを更新する
	 * 
	 * @param headerFilePath
	 * @param moduleName
	 * @param newComponentKind
	 */
	public static void updateComponentKindCppHeader(String headerFilePath, String moduleName, String newComponentKind) {
		try {
			File file = new File(headerFilePath);
			ArrayList<String> strArr = new ArrayList<String>();

			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(file));
				String s;
				boolean includeFlg = false;
				boolean constructFlg = false;

				while ((s = br.readLine()) != null) {
					if (!includeFlg && !constructFlg) {
						strArr.add(s);
					}

					if (includeFlg) {
						strArr.add("#include <rtm/" + newComponentKind + "Base.h>");
						includeFlg = false;
					}

					if (constructFlg) {
						strArr.add("  : public RTC::" + newComponentKind + "Base");
						constructFlg = false;
					}

					if (s.contains("#include <rtm/Manager.h>")) {
						includeFlg = true;
					}
					if (s.startsWith("class " + moduleName)) {
						constructFlg = true;
					}
				}
			} catch (Exception e) {
				logger.error("例外発生:", e);
			} finally {
				if (br != null) {
					br.close();
				}
			}

			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(new FileWriter(file));
				for (int i = 0; i < strArr.size(); i++) {
					bw.write(strArr.get(i) + "\n");
				}
			} catch (Exception e) {
				logger.error("例外発生:", e);
			} finally {
				if (bw != null) {
					bw.close();
				}
			}
		} catch (Exception e) {
			logger.error("例外発生:", e);
		}
	}

	/**
	 * C++ヘッダのservice_impl_hを更新する
	 * 
	 * @param headerFilePath
	 * @param strList
	 */
	public static void updateServiceImplheaderCppHeader(String headerFilePath, List<String> strList) {
		String rtcTemplateTag = "block=\"service_impl_h\"";

		if (CollectionUtil.isNotEmpty(strList)) {
			autoGenerateRtcTemplate(headerFilePath, strList, rtcTemplateTag);
		}
	}

	/**
	 * C++ヘッダのconsumer_stub_hを更新する
	 * 
	 * @param headerFilePath
	 * @param strList
	 */
	public static void updateConsumerStubheaderCppHeader(String headerFilePath, List<String> strList) {
		String rtcTemplateTag = "block=\"consumer_stub_h\"";

		if (CollectionUtil.isNotEmpty(strList)) {
			autoGenerateRtcTemplate(headerFilePath, strList, rtcTemplateTag);
		}
	}

	/**
	 * C++ヘッダのport_stub_hを更新する
	 * 
	 * @param headerFilePath
	 * @param strList
	 */
	public static void updatePortStubheaderCppHeader(String headerFilePath, List<String> strList) {
		String rtcTemplateTag = "block=\"port_stub_h\"";

		if (CollectionUtil.isNotEmpty(strList)) {
			autoGenerateRtcTemplate(headerFilePath, strList, rtcTemplateTag);
		}
	}

	/**
	 * C++ヘッダのpublic_attributeを更新する
	 * 
	 * @param headerFilePath
	 * @param strList
	 */
	public static void updatePublicAttributeCppHeader(String headerFilePath, List<String> strList) {
		String rtcTemplateTag = "block=\"public_attribute\"";

		if (CollectionUtil.isNotEmpty(strList)) {
			for (int i = 0; i < strList.size(); i++) {
				String line = strList.get(i);
				strList.set(i, "  " + line);
			}
			autoGenerateRtcTemplate(headerFilePath, strList, rtcTemplateTag);
		}
	}

	/**
	 * C++ヘッダのpublic_operationを更新する
	 * 
	 * @param headerFilePath
	 * @param strList
	 */
	public static void updatePublicOperationCppHeader(String headerFilePath, List<String> strList) {
		String rtcTemplateTag = "block=\"public_operation\"";

		if (CollectionUtil.isNotEmpty(strList)) {
			for (int i = 0; i < strList.size(); i++) {
				String line = strList.get(i);
				strList.set(i, "  " + line);
			}
			autoGenerateRtcTemplate(headerFilePath, strList, rtcTemplateTag);
		}
	}

	/**
	 * C++ヘッダのprotected_attributeを更新する
	 * 
	 * @param headerFilePath
	 * @param strList
	 */
	public static void updateProtectedAttributeCppHeader(String headerFilePath, List<String> strList) {
		String rtcTemplateTag = "block=\"protected_attribute\"";

		if (CollectionUtil.isNotEmpty(strList)) {
			for (int i = 0; i < strList.size(); i++) {
				String line = strList.get(i);
				strList.set(i, "  " + line);
			}
			autoGenerateRtcTemplate(headerFilePath, strList, rtcTemplateTag);
		}
	}

	/**
	 * C++ヘッダのprotected_operationを更新する
	 * 
	 * @param headerFilePath
	 * @param strList
	 */
	public static void updateProtectedOperationCppHeader(String headerFilePath, List<String> strList) {
		String rtcTemplateTag = "block=\"protected_operation\"";

		if (CollectionUtil.isNotEmpty(strList)) {
			for (int i = 0; i < strList.size(); i++) {
				String line = strList.get(i);
				strList.set(i, "  " + line);
			}
			autoGenerateRtcTemplate(headerFilePath, strList, rtcTemplateTag);
		}
	}

	/**
	 * C++ヘッダのconfig_declareを更新する
	 * 
	 * @param headerFilePath
	 * @param strList
	 */
	public static void updateConfigDeclareCppHeader(String headerFilePath, List<String> strList) {
		String rtcTemplateTag = "block=\"config_declare\"";

		if (CollectionUtil.isNotEmpty(strList)) {
			for (int i = 0; i < strList.size(); i++) {
				String line = strList.get(i);
				strList.set(i, "  " + line);
			}
			autoGenerateRtcTemplate(headerFilePath, strList, rtcTemplateTag);
		}
	}

	/**
	 * C++ヘッダのinport_declareを更新する
	 * 
	 * @param headerFilePath
	 * @param strList
	 */
	public static void updateInportDeclareCppHeader(String headerFilePath, List<String> strList) {
		String rtcTemplateTag = "block=\"inport_declare\"";

		if (CollectionUtil.isNotEmpty(strList)) {
			for (int i = 0; i < strList.size(); i++) {
				String line = strList.get(i);
				strList.set(i, "  " + line);
			}
			autoGenerateRtcTemplate(headerFilePath, strList, rtcTemplateTag);
		}
	}

	/**
	 * C++ヘッダのoutport_declareを更新する
	 * 
	 * @param headerFilePath
	 * @param strList
	 */
	public static void updateOutportDeclareCppHeader(String headerFilePath, List<String> strList) {
		String rtcTemplateTag = "block=\"outport_declare\"";

		if (CollectionUtil.isNotEmpty(strList)) {
			for (int i = 0; i < strList.size(); i++) {
				String line = strList.get(i);
				strList.set(i, "  " + line);
			}
			autoGenerateRtcTemplate(headerFilePath, strList, rtcTemplateTag);
		}
	}

	/**
	 * C++ヘッダのcorbaport_declareを更新する
	 * 
	 * @param headerFilePath
	 * @param strList
	 */
	public static void updateCorbaportDeclareCppHeader(String headerFilePath, List<String> strList) {
		String rtcTemplateTag = "block=\"corbaport_declare\"";

		if (CollectionUtil.isNotEmpty(strList)) {
			for (int i = 0; i < strList.size(); i++) {
				String line = strList.get(i);
				strList.set(i, "  " + line);
			}
			autoGenerateRtcTemplate(headerFilePath, strList, rtcTemplateTag);
		}
	}

	/**
	 * C++ヘッダのservice_declareを更新する
	 * 
	 * @param headerFilePath
	 * @param strList
	 */
	public static void updateServiceDeclareCppHeader(String headerFilePath, List<String> strList) {
		String rtcTemplateTag = "block=\"service_declare\"";

		if (CollectionUtil.isNotEmpty(strList)) {
			for (int i = 0; i < strList.size(); i++) {
				String line = strList.get(i);
				strList.set(i, "  " + line);
			}
			autoGenerateRtcTemplate(headerFilePath, strList, rtcTemplateTag);
		}
	}

	/**
	 * C++ヘッダのconsumer_declareを更新する
	 * 
	 * @param headerFilePath
	 * @param strList
	 */
	public static void updateConsumerDeclareCppHeader(String headerFilePath, List<String> strList) {
		String rtcTemplateTag = "block=\"consumer_declare\"";

		if (CollectionUtil.isNotEmpty(strList)) {
			for (int i = 0; i < strList.size(); i++) {
				String line = strList.get(i);
				strList.set(i, "  " + line);
			}
			autoGenerateRtcTemplate(headerFilePath, strList, rtcTemplateTag);
		}
	}

	/**
	 * C++ヘッダのprivate_attributeを更新する
	 * 
	 * @param headerFilePath
	 * @param strList
	 */
	public static void updatePrivateAttributeCppHeader(String headerFilePath, List<String> strList) {
		String rtcTemplateTag = "block=\"private_attribute\"";

		if (CollectionUtil.isNotEmpty(strList)) {
			for (int i = 0; i < strList.size(); i++) {
				String line = strList.get(i);
				strList.set(i, "  " + line);
			}
			autoGenerateRtcTemplate(headerFilePath, strList, rtcTemplateTag);
		}
	}

	/**
	 * C++ヘッダのprivate_operationを更新する
	 * 
	 * @param headerFilePath
	 * @param strList
	 */
	public static void updatePrivateOperationCppHeader(String headerFilePath, List<String> strList) {
		String rtcTemplateTag = "block=\"private_operation\"";

		if (CollectionUtil.isNotEmpty(strList)) {
			for (int i = 0; i < strList.size(); i++) {
				String line = strList.get(i);
				strList.set(i, "  " + line);
			}
			autoGenerateRtcTemplate(headerFilePath, strList, rtcTemplateTag);
		}
	}

	/************************************************************
	 * Python用のソースコード自動反映処理（自動反映文字列生成）
	 ************************************************************/
	/**
	 * Python用のmodule_specを生成する
	 * 
	 * @param rtcProfile
	 * @return
	 */
	public static List<String> createModuleSpecForPyhon(RtcProfile rtcProfile) {
		List<String> stringList = new ArrayList<String>();

		// prefix
		stringList.add(rtcProfile.getBasicInfo().getModuleName() + "_spec = [");

		// 共通
		stringList.addAll(createModuleSpec(rtcProfile));

		// suffix
		stringList.add("\"language\", \"Python\",");
		stringList.add("\"lang_type\", \"SCRIPT\",");

		// コンフィギュレーション設定
		if (CollectionUtil.isNotEmpty(rtcProfile.getConfigurationSet().getConfigurations())) {
			stringList
					.addAll(createModuleSpecConfiguration(rtcProfile.getConfigurationSet().getConfigurations(), false));
		}

		stringList.add("\"\"]");

		return stringList;
	}

	/**
	 * Python用のIDLインポート文を生成する
	 * 
	 * @param rtcProfile
	 * @return
	 */
	public static List<String> createIdlImportForPython(RtcProfile rtcProfile) {
		List<String> idlImportList = new ArrayList<String>();
		if (CollectionUtil.isNotEmpty(rtcProfile.getServicePorts())) {
			for (ServicePort servicePort : rtcProfile.getServicePorts()) {
				if (CollectionUtil.isNotEmpty(servicePort.getServiceInterfaces())) {
					for (ServiceInterface serviceInterface : servicePort.getServiceInterfaces()) {
						idlImportList.add(createIdlImportPython(serviceInterface.getIdlFile()));
					}
				}
			}
		}
		return idlImportList;
	}

	/**
	 * Python用のservice_implを生成する
	 * 
	 * @param rtcProfile
	 * @return
	 */
	public static List<String> createServiceImplementForPython(RtcProfile rtcProfile) {
		List<String> serviceImplList = new ArrayList<String>();
		if (CollectionUtil.isNotEmpty(rtcProfile.getServicePorts())) {
			for (ServicePort servicePort : rtcProfile.getServicePorts()) {
				if (CollectionUtil.isNotEmpty(servicePort.getServiceInterfaces())) {
					for (ServiceInterface serviceInterface : servicePort.getServiceInterfaces()) {
						if (INTERFACE_DIRECTION.PROVIDED.equals(serviceInterface.getDirection())) {
							serviceImplList.add(createServiceImplementPython(serviceInterface.getIdlFile()));
						}
					}
				}
			}
		}
		return serviceImplList;
	}

	/**
	 * Python用のconsumer_importを生成する
	 * 
	 * @param rtcProfile
	 * @return
	 */
	public static List<String> createConsumerImportForPython(RtcProfile rtcProfile) {
		List<String> consumerImportList = new ArrayList<String>();
		if (CollectionUtil.isNotEmpty(rtcProfile.getServicePorts())) {
			for (ServicePort servicePort : rtcProfile.getServicePorts()) {
				if (CollectionUtil.isNotEmpty(servicePort.getServiceInterfaces())) {
					for (ServiceInterface serviceInterface : servicePort.getServiceInterfaces()) {
						if (INTERFACE_DIRECTION.PROVIDED.equals(serviceInterface.getDirection())) {
							consumerImportList.add(createConsumerImportPython(serviceInterface.getInterfaceType()));
						}
					}
				}
			}
		}
		return consumerImportList;
	}

	/**
	 * Python用のinit_conf_paramを生成する
	 * 
	 * @param rtcProfile
	 * @return
	 */
	public static List<String> createInitConfParamForPython(RtcProfile rtcProfile) {
		List<String> stringList = new ArrayList<String>();
		if (CollectionUtil.isNotEmpty(rtcProfile.getConfigurationSet().getConfigurations())) {
			for (Configuration configuration : rtcProfile.getConfigurationSet().getConfigurations()) {
				stringList.add("\"\"\"");
				if (StringUtil.isNotEmpty(configuration.getVariableName())) {
					stringList.add(" - Name:  " + configuration.getVariableName());
				} else {
					stringList.add(" - Name:  " + configuration.getName());
				}
				stringList.add(" - DefaultValue: " + configuration.getDefaultValue());
				stringList.add("\"\"\"");

				switch (configuration.getDataType()) {
				case CONFIGURATION_TYPE.SHORT:
				case CONFIGURATION_TYPE.INT:
				case CONFIGURATION_TYPE.LONG:
				case CONFIGURATION_TYPE.FLOAT:
				case CONFIGURATION_TYPE.DOUBLE:
					StringBuilder sbNumber = new StringBuilder();
					sbNumber.append(createConfigurationInsatancePython(configuration.getName(),
							configuration.getVariableName()));
					sbNumber.append(" = [");
					sbNumber.append(configuration.getDefaultValue());
					sbNumber.append("]");
					stringList.add(sbNumber.toString());
					break;
				case CONFIGURATION_TYPE.STRING:
					StringBuilder sbStr = new StringBuilder();
					sbStr.append(createConfigurationInsatancePython(configuration.getName(),
							configuration.getVariableName()));
					sbStr.append(" = ['");
					sbStr.append(configuration.getDefaultValue());
					sbStr.append("']");
					stringList.add(sbStr.toString());
					break;
				}
			}
		}
		return stringList;

	}

	/**
	 * Python用のコンストラクタを生成する
	 * 
	 * @param rtcProfile
	 * @return
	 */
	public static List<String> createConstructorForPython(RtcProfile rtcProfile) {
		List<String> stringList = new ArrayList<String>();

		// コンポーネント種類
		stringList.add("OpenRTM_aist." + rtcProfile.getBasicInfo().getComponentKind() + "Base.__init__(self, manager)");

		stringList.add("");
		// DataInPort
		if (CollectionUtil.isNotEmpty(rtcProfile.getDataInPorts())) {
			for (DataPort dataPort : rtcProfile.getDataInPorts()) {
				stringList.add(createPortDataTypeConstructorPython(dataPort.getName(), dataPort.getVariableName(),
						dataPort.getDataType()));
				stringList.add(createDataPortConstructorPython(dataPort.getName(), dataPort.getVariableName(), "In"));
			}
		}

		// DataOutPort
		if (CollectionUtil.isNotEmpty(rtcProfile.getDataOutPorts())) {
			for (DataPort dataPort : rtcProfile.getDataOutPorts()) {
				stringList.add(createPortDataTypeConstructorPython(dataPort.getName(), dataPort.getVariableName(),
						dataPort.getDataType()));
				stringList.add(createDataPortConstructorPython(dataPort.getName(), dataPort.getVariableName(), "Out"));
			}
		}

		// ServicePort
		List<String> providerList = new ArrayList<String>();
		List<String> consumerList = new ArrayList<String>();
		List<String> serviceList = new ArrayList<String>();
		if (CollectionUtil.isNotEmpty(rtcProfile.getServicePorts())) {
			for (ServicePort servicePort : rtcProfile.getServicePorts()) {
				serviceList.add(createServicePortConstructorPython(servicePort.getName()));

				if (CollectionUtil.isNotEmpty(servicePort.getServiceInterfaces())) {
					for (ServiceInterface serviceInterface : servicePort.getServiceInterfaces()) {
						if (INTERFACE_DIRECTION.PROVIDED.equals(serviceInterface.getDirection())) {
							providerList.add(createProviderCostructorPython(serviceInterface.getName(),
									serviceInterface.getInstanceName(), serviceInterface.getVariableName(),
									serviceInterface.getInterfaceType()));
						} else if (INTERFACE_DIRECTION.REQUIRED.equals(serviceInterface.getDirection())) {
							consumerList.add(createConsumerCostructorPython(serviceInterface.getName(),
									serviceInterface.getInstanceName(), serviceInterface.getVariableName(),
									serviceInterface.getInterfaceType()));
						}
					}
				}
			}
		}
		if (CollectionUtil.isNotEmpty(serviceList)) {
			stringList.addAll(serviceList);
		}
		if (CollectionUtil.isNotEmpty(providerList)) {
			stringList.addAll(providerList);
		}
		if (CollectionUtil.isNotEmpty(consumerList)) {
			stringList.addAll(consumerList);
		}

		return stringList;
	}

	/**
	 * Python用のOnInitializeを生成する
	 * 
	 * @param rtcProfile
	 * @return
	 */
	public static List<String> createOnIntializeForPython(RtcProfile rtcProfile) {
		List<String> stringList = new ArrayList<String>();

		// Configuration
		stringList.add("# Bind variables and configuration variable");
		if (CollectionUtil.isNotEmpty(rtcProfile.getConfigurationSet().getConfigurations())) {
			for (Configuration configuration : rtcProfile.getConfigurationSet().getConfigurations()) {
				stringList.add(createConfigurationOnInitializePython(configuration.getName(),
						configuration.getVariableName(), configuration.getDefaultValue()));
			}
		}
		stringList.add("");

		// DataInPort
		stringList.add("# Set InPort buffers");
		if (CollectionUtil.isNotEmpty(rtcProfile.getDataInPorts())) {
			for (DataPort dataPort : rtcProfile.getDataInPorts()) {
				stringList.add(createDataPortOnInitializePython(dataPort.getName(), "In"));
			}
		}
		stringList.add("");

		// DataOutPort
		stringList.add("# Set OutPort buffers");
		if (CollectionUtil.isNotEmpty(rtcProfile.getDataOutPorts())) {
			for (DataPort dataPort : rtcProfile.getDataOutPorts()) {
				stringList.add(createDataPortOnInitializePython(dataPort.getName(), "Out"));
			}
		}
		stringList.add("");

		// ServicePort
		List<String> providerList = new ArrayList<String>();
		List<String> consumerList = new ArrayList<String>();
		List<String> serviceList = new ArrayList<String>();
		if (CollectionUtil.isNotEmpty(rtcProfile.getServicePorts())) {
			for (ServicePort servicePort : rtcProfile.getServicePorts()) {
				if (CollectionUtil.isNotEmpty(servicePort.getServiceInterfaces())) {
					for (ServiceInterface serviceInterface : servicePort.getServiceInterfaces()) {
						if (INTERFACE_DIRECTION.PROVIDED.equals(serviceInterface.getDirection())) {
							providerList.add(createInterfaceOnInitializePython(servicePort.getName(), "Provider",
									serviceInterface.getInterfaceType(), serviceInterface.getName(),
									serviceInterface.getInstanceName(), serviceInterface.getVariableName()));
						} else if (INTERFACE_DIRECTION.REQUIRED.equals(serviceInterface.getDirection())) {
							consumerList.add(createInterfaceOnInitializePython(servicePort.getName(), "Consumer",
									serviceInterface.getInterfaceType(), serviceInterface.getName(),
									serviceInterface.getInstanceName(), serviceInterface.getVariableName()));
						}
					}
				}
				serviceList.add(createServicePortOnInitializePython(servicePort.getName()));
			}
		}

		stringList.add("# Set service providers to Ports");
		if (CollectionUtil.isNotEmpty(providerList)) {
			stringList.addAll(providerList);
		}
		stringList.add("");

		stringList.add("# Set service consumers to Ports");
		if (CollectionUtil.isNotEmpty(consumerList)) {
			stringList.addAll(consumerList);
		}
		stringList.add("");

		stringList.add("# Set CORBA Service Ports");
		if (CollectionUtil.isNotEmpty(serviceList)) {
			stringList.addAll(serviceList);
		}
		stringList.add("");

		return stringList;

	}

	/**
	 * Python用のIDLファイルのインポート文を生成する
	 * 
	 * @param idlFile
	 * @return
	 */
	private static String createIdlImportPython(String idlFile) {
		StringBuilder sb = new StringBuilder();
		if (StringUtil.isNotEmpty(idlFile)) {
			sb.append("import ");
			sb.append(idlFile.replace(".", "_"));
		}
		return sb.toString();
	}

	/**
	 * Python用のservice_implを生成する
	 * 
	 * @param idlFile
	 * @return
	 */
	private static String createServiceImplementPython(String idlFile) {
		StringBuilder sb = new StringBuilder();
		if (StringUtil.isNotEmpty(idlFile)) {
			sb.append("from ");
			sb.append(idlFile.replace(".", "_"));
			sb.append("_example import *");
		}
		return sb.toString();
	}

	/**
	 * Python用のconsumer_importを生成する
	 * 
	 * @param ifType
	 * @return
	 */
	private static String createConsumerImportPython(String ifType) {
		StringBuilder sb = new StringBuilder();
		if (StringUtil.isNotEmpty(ifType) && ifType.contains("::")) {
			String nameSpace = ifType.split("::")[0];
			sb.append("import ");
			sb.append(nameSpace);
			sb.append(", ");
			sb.append(nameSpace);
			sb.append("__POA");
		}
		return sb.toString();
	}

	/**
	 * Python用のポート型のコンストラクタを生成する
	 * 
	 * @param portName
	 * @param valName
	 * @param portDataType
	 * @return
	 */
	private static String createPortDataTypeConstructorPython(String portName, String valName, String portDataType) {
		StringBuilder sb = new StringBuilder();
		sb.append(createPortDataTypeInstancePython(portName, valName));
		sb.append(" = ");
		sb.append(getPythonCostructorFromDataPort(portDataType));
		return sb.toString();
	}

	/**
	 * Python用のデータポートのコンストラクタを生成する
	 * 
	 * @param portName
	 * @param valName
	 * @param portType
	 * @return
	 */
	private static String createDataPortConstructorPython(String portName, String valName, String portType) {
		StringBuilder sb = new StringBuilder();
		sb.append(createDataPortInstancePython(portName, portType));
		sb.append(" = OpenRTM_aist.");
		sb.append(portType);
		sb.append("Port(\"");
		sb.append(portName);
		sb.append("\", ");
		sb.append(createPortDataTypeInstancePython(portName, valName));
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Python用のサービスポートのコンストラクタを生成する
	 * 
	 * @param portName
	 * @return
	 */
	private static String createServicePortConstructorPython(String portName) {
		StringBuilder sb = new StringBuilder();
		sb.append(createServicePortInstancePython(portName));
		sb.append(" = OpenRTM_aist.CorbaPort(\"");
		sb.append(portName);
		sb.append("\")");
		return sb.toString();
	}

	/**
	 * Python用のProviderサービスインタフェースのコンストラクタを生成する
	 * 
	 * @param ifName
	 * @param insName
	 * @param valname
	 * @param ifType
	 * @return
	 */
	private static String createProviderCostructorPython(String ifName, String insName, String valName, String ifType) {
		StringBuilder sb = new StringBuilder();
		sb.append(createServiceInterfaceInstancePython(ifName, insName, valName));
		sb.append(" = ");
		sb.append(ifType != null && ifType.contains("::") ? (ifType.split("::")[0] + "_" + ifType.split("::")[1])
				: ifType);
		sb.append("_i()");
		return sb.toString();
	}

	/**
	 * Python用のConsumerサービスインタフェースのコンストラクタを生成する
	 * 
	 * @param ifName
	 * @param insName
	 * @param valname
	 * @param ifType
	 * @return
	 */
	private static String createConsumerCostructorPython(String ifName, String insName, String valName, String ifType) {
		StringBuilder sb = new StringBuilder();
		sb.append(createServiceInterfaceInstancePython(ifName, insName, valName));
		sb.append(" = OpenRTM_aist.CorbaConsumer(interfaceType=");
		sb.append(ifType != null ? ifType.replace("::", ".") : ifType);
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Python用のコンフィギュレーション用のOnInitializeを生成する
	 * 
	 * @param portName
	 * @param portType
	 * @return
	 */
	private static String createConfigurationOnInitializePython(String confName, String valName, String defaultValue) {
		StringBuilder sb = new StringBuilder();
		sb.append("self.bindParameter(\"");
		sb.append(confName);
		sb.append("\", ");
		sb.append(createConfigurationInsatancePython(confName, valName));
		sb.append(", \"");
		sb.append(defaultValue);
		sb.append("\")");
		return sb.toString();
	}

	/**
	 * Python用のデータポート用のOnInitializeを生成する
	 * 
	 * @param portName
	 * @param portType
	 * @return
	 */
	private static String createDataPortOnInitializePython(String portName, String portType) {
		StringBuilder sb = new StringBuilder();
		sb.append("self.add");
		sb.append(portType);
		sb.append("Port(\"");
		sb.append(portName);
		sb.append("\", ");
		sb.append(createDataPortInstancePython(portName, portType));
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Python用のサービスインタフェース用のOnInitializeを生成する
	 * 
	 * @param portName
	 * @param ifType
	 * @param ifName
	 * @param insName
	 * @param valName
	 * @return
	 */
	private static String createInterfaceOnInitializePython(String portName, String directon, String ifType,
			String ifName, String insName, String valName) {
		StringBuilder sb = new StringBuilder();
		sb.append(createServicePortInstancePython(portName));
		sb.append(".register");
		sb.append(directon);
		sb.append("(\"");
		sb.append(ifName);
		sb.append("\", \"");
		sb.append(ifType);
		sb.append("\", ");
		sb.append(createServiceInterfaceInstancePython(ifName, insName, valName));
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Python用のサービスポート用のOnInitializeを生成する
	 * 
	 * @param portName
	 * @return
	 */
	private static String createServicePortOnInitializePython(String portName) {
		StringBuilder sb = new StringBuilder();
		sb.append("self.addPort(\"");
		sb.append(createServicePortInstancePython(portName));
		sb.append("\")");
		return sb.toString();
	}

	/**
	 * Python用のデータポートのデータ型のインスタンス名を生成する
	 * 
	 * @param portName
	 * @param valName
	 * @return
	 */
	private static String createPortDataTypeInstancePython(String portName, String valName) {
		StringBuilder sb = new StringBuilder();
		sb.append("self._d_");
		if (StringUtil.isNotEmpty(valName)) {
			sb.append(valName);
		} else {
			sb.append(portName);
		}
		return sb.toString();
	}

	/**
	 * Python用のデータポートのインスタンス名を生成する
	 * 
	 * @param portName
	 * @param portType
	 * @return
	 */
	private static String createDataPortInstancePython(String portName, String portType) {
		StringBuilder sb = new StringBuilder();
		sb.append("self._");
		sb.append(portName);
		sb.append(portType);
		return sb.toString();
	}

	/**
	 * Python用のサービスポートのインスタンス名を生成する
	 * 
	 * @param portName
	 * @return
	 */
	private static String createServicePortInstancePython(String portName) {
		StringBuilder sb = new StringBuilder();
		sb.append("self._");
		sb.append(portName);
		sb.append("Port");
		return sb.toString();
	}

	/**
	 * Python用のサービスインタフェースのインスタンス名を生成する
	 * 
	 * @param ifName
	 * @param insName
	 * @param valName
	 * @return
	 */
	private static String createServiceInterfaceInstancePython(String ifName, String insName, String valName) {
		StringBuilder sb = new StringBuilder();
		sb.append("self._");
		if (StringUtil.isNotEmpty(valName)) {
			sb.append(valName);
		} else if (StringUtil.isNotEmpty(insName)) {
			sb.append(insName);
		} else {
			sb.append(ifName);
		}
		return sb.toString();
	}

	/**
	 * Python用のコンフィギュレーションのインスタンス名を生成する
	 * 
	 * @param configName
	 * @param valName
	 * @return
	 */
	private static String createConfigurationInsatancePython(String configName, String valName) {
		StringBuilder sb = new StringBuilder();
		sb.append("self._");
		if (StringUtil.isNotEmpty(valName)) {
			sb.append(valName);
		} else {
			sb.append(configName);
		}
		return sb.toString();
	}

	/************************************************************
	 * Python用のソースコード自動反映処理（自動反映文字列実行）
	 ************************************************************/
	/**
	 * Python用にservice_implを更新する
	 * 
	 * @param codeFilePath
	 * @param strList
	 */
	public static void updateServiceImplementPython(String codeFilePath, List<String> strList) {
		String rtcTemplateTag = "block=\"service_impl\"";

		if (CollectionUtil.isNotEmpty(strList)) {
			autoGenerateRtcTemplate(codeFilePath, strList, rtcTemplateTag);
		}
	}

	/**
	 * Python用にconsumer_importを更新する
	 * 
	 * @param codeFilePath
	 * @param strList
	 */
	public static void updateConsumerImportPython(String codeFilePath, List<String> strList) {
		String rtcTemplateTag = "block=\"consumer_import\"";

		if (CollectionUtil.isNotEmpty(strList)) {
			autoGenerateRtcTemplate(codeFilePath, strList, rtcTemplateTag);
		}
	}

	/**
	 * Python用にinit_conf_paramを更新する
	 * 
	 * @param codeFilePath
	 * @param strList
	 */
	public static void updateInitConfParamPython(String codeFilePath, List<String> strList) {
		String rtcTemplateTag = "block=\"init_conf_param\"";

		if (CollectionUtil.isNotEmpty(strList)) {
			for (int i = 0; i < strList.size(); i++) {
				String line = strList.get(i);
				strList.set(i, "\t\t" + line);
			}
			autoGenerateRtcTemplate(codeFilePath, strList, rtcTemplateTag);
		}
	}

	/**
	 * Python用にmodule_specを挿入する
	 * 
	 * @param codeFilePath
	 * @param strList
	 */
	public static void updateModuleSpecPython(String codeFilePath, List<String> strList) {
		String rtcTemplateTag = "block=\"module_spec\"";

		if (CollectionUtil.isNotEmpty(strList)) {
			for (int i = 0; i < strList.size(); i++) {
				if (i != 0) {
					String line = strList.get(i);
					strList.set(i, "\t\t" + line);
				}
			}
			autoGenerateRtcTemplate(codeFilePath, strList, rtcTemplateTag);
		}
	}

	/**
	 * Python用にidlのimport文を更新する
	 * 
	 * @param codeFilePath
	 * @param strList
	 */
	public static void updateIdlImportPython(String codeFilePath, List<String> strList) {
		if (CollectionUtil.isNotEmpty(strList)) {
			autoGenerateSourceCode(codeFilePath, strList, "import OpenRTM_aist",
					"# Import Service implementation class");
		}
	}

	/**
	 * Python用にクラス宣言を更新する
	 * 
	 * @param codeFilePath
	 * @param moduleName
	 * @param newComponentKind
	 */
	public static void udpateClassNamePython(String codeFilePath, String moduleName, String newComponentKind) {
		try {
			File file = new File(codeFilePath);
			ArrayList<String> strArr = new ArrayList<String>();

			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(file));
				String s;

				while ((s = br.readLine()) != null) {
					if (s.contains("class " + moduleName + "(OpenRTM_aist.")) {
						strArr.add("class " + moduleName + "(OpenRTM_aist." + newComponentKind + "Base):");
					} else {
						strArr.add(s);
					}
				}
			} catch (Exception e) {
				logger.error("exception handled. ", e);
			} finally {
				if (br != null) {
					br.close();
				}
			}

			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(new FileWriter(file));
				for (int i = 0; i < strArr.size(); i++) {
					bw.write(strArr.get(i) + "\n");
				}
			} catch (Exception e) {
				logger.error("exception handled.", e);
			} finally {
				if (bw != null) {
					bw.close();
				}
			}
		} catch (Exception e) {
			logger.error("exception handled.", e);
		}
	}

	/**
	 * Python用にconstructorを更新する
	 * 
	 * @param codeFilePath
	 * @param strList
	 */
	public static void updateConstructorPython(String codeFilePath, List<String> strList) {
		if (CollectionUtil.isNotEmpty(strList)) {
			for (int i = 0; i < strList.size(); i++) {
				String line = strList.get(i);
				strList.set(i, "\t\t" + line);
			}
			autoGenerateSourceCode(codeFilePath, strList, "def __init__(self, manager):",
					"# initialize of configuration-data.");
		}
	}

	/**
	 * Python用にOnInitializeを更新する
	 * 
	 * @param codeFilePath
	 * @param newConstructorList
	 */
	public static void updateOnInitializePython(String codeFilePath, List<String> strList) {
		if (CollectionUtil.isNotEmpty(strList)) {
			for (int i = 0; i < strList.size(); i++) {
				String line = strList.get(i);
				strList.set(i, "\t\t" + line);
			}
			// IDE用にBind endを追加している
			if (!autoGenerateSourceCode(codeFilePath, strList, "def onInitialize(self):", "Bind end")) {
				// 終了タグが見つからない場合はreturnまで
				autoGenerateSourceCode(codeFilePath, strList, "def onInitialize(self):", "return RTC.RTC_OK");
			}
		}
	}

	/**
	 * RTC-Template用ソースコード自動生成
	 * 
	 * @param filePath
	 * @param insertStr
	 * @param tagStr
	 */
	public static void autoGenerateRtcTemplate(String filePath, List<String> insertStr, String tagStr) {
		autoGenerateSourceCode(filePath, insertStr, tagStr, "/rtc-template");
	}

	/**
	 * ソースコード自動生成（複数行挿入）
	 * 
	 * @param filePath
	 * @param insertStr
	 * @param startStr
	 * @param endStr
	 */
	public static boolean autoGenerateSourceCode(String filePath, List<String> insertStr, String startStr,
			String endStr) {
		try {
			File file = new File(filePath);
			ArrayList<String> strArr = new ArrayList<String>();

			BufferedReader br = null;
			boolean targetFlg = false;
			try {
				br = new BufferedReader(new FileReader(file));
				String s;

				while ((s = br.readLine()) != null) {
					if (s.contains(startStr)) {
						strArr.add(s);
						strArr.addAll(insertStr);
						targetFlg = true;
					}

					if (s.contains(endStr)) {
						targetFlg = false;
					}

					if (!targetFlg) {
						strArr.add(s);
					}
				}
			} catch (Exception e) {
				logger.error("exception handled.", e);
			} finally {
				if (br != null) {
					br.close();
				}
			}

			if (targetFlg) {
				return false;
			}

			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(new FileWriter(file));
				for (int i = 0; i < strArr.size(); i++) {
					bw.write(strArr.get(i) + "\n");
				}
			} catch (Exception e) {
				logger.error("exception handled.", e);
			} finally {
				if (bw != null) {
					bw.close();
				}
			}
		} catch (Exception e) {
			logger.error("exception handled.", e);
		}
		return true;
	}

	/**
	 * Cppのヘッダファイルのアクティビティのコメント有無を切替える
	 * 
	 * @param filePath
	 * @param methodName
	 * @param isAdd
	 */
	public static void changeCommentMethodCppHeader(String filePath, String methodName, boolean isAdd) {
		try {
			File file = new File(filePath);
			ArrayList<String> strArr = new ArrayList<String>();

			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(file));
				String s;
				while ((s = br.readLine()) != null) {
					if (s.contains("virtual RTC::ReturnCode_t " + methodName)) {
						if (isAdd) {
							strArr.add(s.replace("   virtual", "  // virtual"));
						} else {
							strArr.add(s.replace("  // virtual", "   virtual"));
						}
					} else {
						strArr.add(s);
					}
				}
			} catch (Exception e) {
				logger.error("exception handled.", e);
			} finally {
				if (br != null) {
					br.close();
				}
			}

			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(new FileWriter(file));
				for (int i = 0; i < strArr.size(); i++) {
					bw.write(strArr.get(i) + "\n");
				}
			} catch (Exception e) {
				logger.error("exception handled.", e);
			} finally {
				if (bw != null) {
					bw.close();
				}
			}
		} catch (Exception e) {
			logger.error("exception handled.", e);
		}
	}

	/**
	 * Cppのソースファイルのアクティビティのコメント有無を切替える
	 * 
	 * @param filePath
	 * @param methodName
	 * @param isAdd
	 */
	public static void changeCommentMethodCppSource(String filePath, String methodName, boolean isAdd) {
		try {
			File file = new File(filePath);
			ArrayList<String> strArr = new ArrayList<String>();

			int commentStartLine = -1;
			int commentEndLine = -1;
			int methodLine = -1;

			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(file));
				String s;

				boolean targetFlg = false;
				int line = 0;
				while ((s = br.readLine()) != null) {
					if (s.startsWith("RTC::ReturnCode_t") && s.contains(methodName)) {
						methodLine = line;
						commentStartLine = line - 1;
						targetFlg = true;
					}

					if (s.startsWith("}") && targetFlg) {
						commentEndLine = line + 1;
						targetFlg = false;
					}

					strArr.add(s);
					line++;
				}
			} catch (Exception e) {
				logger.error("exception handled.", e);
			} finally {
				if (br != null) {
					br.close();
				}
			}

			if (commentStartLine < 0 || commentEndLine < 0 || methodLine < 0) {
				return;
			}

			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(new FileWriter(file));
				for (int i = 0; i < strArr.size(); i++) {
					if (i == commentStartLine || i == commentEndLine) {
						if (isAdd) {
							if (i == commentStartLine) {
								bw.write("/*\n");
							} else {
								bw.write("*/\n");
							}
						} else {
							bw.write("\n");
						}
					} else {
						bw.write(strArr.get(i) + "\n");
					}
				}
			} catch (Exception e) {
				logger.error("exception handled.", e);
			} finally {
				if (bw != null) {
					bw.close();
				}
			}
		} catch (Exception e) {
			logger.error("exception handled.", e);
		}
	}

	/**
	 * Pythonのソースコードのアクティビティのコメント有無を切替える
	 * 
	 * @param filePath
	 * @param methodName
	 * @param isAdd
	 */
	public static void changeCommentMethodPython(String filePath, String methodName, boolean isAdd) {
		try {
			File file = new File(filePath);
			ArrayList<String> strArr = new ArrayList<String>();

			int commentStartLine = -1;
			int commentEndLine = -1;
			int methodLine = -1;

			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(file));
				String s;

				boolean targetFlg = false;
				int line = 0;
				while ((s = br.readLine()) != null) {
					if (s.contains("def " + methodName)) {
						methodLine = line;
						targetFlg = true;
					}

					if (s.endsWith("##") && methodLine < 0) {
						commentStartLine = line;
					}

					if (s.contains("return RTC.RTC_OK") && targetFlg) {
						commentEndLine = line;
						targetFlg = false;
					}

					strArr.add(s);
					line++;
				}
			} catch (Exception e) {
				logger.error("exception handled.", e);
			} finally {
				if (br != null) {
					br.close();
				}
			}

			if (commentStartLine < 0 || commentEndLine < 0 || methodLine < 0) {
				return;
			}

			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(new FileWriter(file));
				for (int i = 0; i < strArr.size(); i++) {
					if (i >= commentStartLine && i <= commentEndLine) {
						if (isAdd) {
							// コメント追加
							if (i >= commentStartLine && i < methodLine) {
								// ヘッダコメントの場合はタブ＃を先頭に追加
								bw.write("\t#" + strArr.get(i) + "\n");
							} else {
								if (strArr.get(i).length() > 1) {
									// 2文字以上の場合
									// ＃を挿入
									bw.write("\t#" + strArr.get(i).substring(1) + "\n");
								} else {
									// 1文字以下の場合、タブor空なのでタブ＋＃のみ
									bw.write("\t#\n");
								}
							}
						} else {
							// コメント除去
							if (i >= commentStartLine && i < methodLine) {
								// ヘッダコメントの場合は先頭のタブ＃を削除
								bw.write(strArr.get(i).substring(2) + "\n");
							} else {
								if (strArr.get(i).length() > 1) {
									// 2文字以上の場合
									// ＃を削除
									bw.write("\t" + strArr.get(i).substring(2) + "\n");
								} else {
									// 1文字以下の場合、タブor空なのでタブのみ
									bw.write("\t\n");
								}
							}
						}
					} else {
						bw.write(strArr.get(i) + "\n");
					}
				}
			} catch (Exception e) {
				logger.error("exception handled.", e);
			} finally {
				if (bw != null) {
					bw.close();
				}
			}
		} catch (Exception e) {
			logger.error("exception handled.", e);
		}
	}

	/************************************************************
	 * IDLファイル自動反映処理
	 ************************************************************/

	/**
	 * C++用にIDLファイルのコピーおよびImplコードの生成を行う
	 * 
	 * @param rtcProfile
	 * @param rtcDirPath
	 */
	public static void createServiceProviderImplFileCpp(RtcProfile rtcProfile, String rtcDirPath) {
		// 全てのProviderに対して処理を行う
		if (rtcProfile != null && CollectionUtil.isNotEmpty(rtcProfile.getServicePorts())) {
			for (ServicePort servicePort : rtcProfile.getServicePorts()) {
				if (CollectionUtil.isNotEmpty(servicePort.getProvidedServiceInterfaces())) {
					for (ServiceInterface provider : servicePort.getProvidedServiceInterfaces()) {
						// IDLファイルの有無をチェックし、存在しない場合はコピーを行う
						copyIdlFile(rtcDirPath, provider.getIdlFile());

						// 今回コピーを行った場合、SVC_implのコードを生成する
						createProviderImplFileCpp(rtcDirPath, rtcProfile.getBasicInfo().getModuleName(),
								servicePort.getName(), provider.getName(), provider.getInterfaceType(),
								provider.getIdlFile());
					}
				}
			}
		}
	}

	/**
	 * 必要となるIDLファイルをIDLフォルダにコピーする
	 * 
	 * @param rtcDirPath
	 * @param idlFileName
	 */
	private static void copyIdlFile(String rtcDirPath, String idlFileName) {
		String rtcIdlDirPath = StringUtil.concatenate(File.separator, rtcDirPath, "idl");

		// IDLファイルが存在するかを調べる
		File targetFile = FileUtil.searchFileWithSubDir(rtcIdlDirPath, idlFileName, "idl");

		// 存在しない場合は探してコピーしてくる
		if (targetFile == null) {
			// OpenRTMのフォルダを調べる
			String openRtmDir = RtmEditorUtil.getOpenRtmDir();
			targetFile = FileUtil.searchFileWithSubDir(openRtmDir, idlFileName, "idl");

			if (targetFile != null) {
				// IDLフォルダに移動する
				File destFile = FileUtil.concatenateFilePath(rtcIdlDirPath, targetFile.getName());
				FileUtil.fileCopy(targetFile, destFile);

				// IDLファイルがIncludeしているファイルも持ってくる
				List<String> includeList = FileUtil.readAndSearchStr(destFile.getPath(), "#include");
				if (CollectionUtil.isNotEmpty(includeList)) {
					for (String line : includeList) {
						String includeFileName = line.replace("#include", "").replace("<", "").replace(">", "")
								.replace("\"", "").replace("\"", "").trim();
						// 階層的に呼び出す
						copyIdlFile(rtcDirPath, includeFileName);
					}
				}
			}
		}
	}

	/**
	 * C++用にProviderInterfaceのimplコードを生成する
	 * 
	 * @param moduleName
	 * @param targetDirPath
	 * @param portName
	 * @param ifName
	 * @param ifType
	 * @param idlFileName
	 */
	private static void createProviderImplFileCpp(String rtcDirPath, String moduleName, String portName, String ifName,
			String ifType, String idlFileName) {
		try {

			String tmpDirPath = StringUtil.concatenate(File.separator, rtcDirPath, "tmp");
			String sourceDirPath = StringUtil.concatenate(File.separator, rtcDirPath, "src");
			String headerDirPath = StringUtil.concatenate(File.separator, rtcDirPath, "include", moduleName);

			File idlSourceFile = FileUtil.concatenateFilePath(sourceDirPath,
					idlFileName.replace(".idl", "") + "SVC_impl.cpp");
			File idlHeaderFile = FileUtil.concatenateFilePath(headerDirPath,
					idlFileName.replace(".idl", "") + "SVC_impl.h");

			if (!idlSourceFile.exists() && !idlHeaderFile.exists()) {
				// 存在しない場合にのみ実施
				String moduleNameArgs = "--module-name=" + moduleName;
				String serviceArgs = "--service=" + portName + ":" + ifName + ":" + ifType.split("::")[1];
				String idlArgs = "--service-idl=../idl/" + idlFileName;

				// 一時領域を生成する
				File tmpDir = new File(tmpDirPath);
				tmpDir.mkdir();

				// rtc-templateを実行する
				ProcessUtil.startProcessNoReturnWithWorkingDerectory(tmpDirPath, "python", "/usr/bin/rtc-template",
						"-bcxx", moduleNameArgs, serviceArgs, idlArgs);

				// 作成されるファイル
				File srcIdlSourceFile = FileUtil.concatenateFilePath(tmpDirPath, "SVC_impl.cpp");
				File srcIdlHeaderFile = FileUtil.concatenateFilePath(tmpDirPath, "SVC_impl.h");
				if (srcIdlSourceFile.exists() && srcIdlHeaderFile.exists()) {
					// 作成されたファイルの"../idl/hoge.idl"を"hoge.idl"に置き換える
					FileUtil.renameAllFilesContent(tmpDirPath, "..\\/idl\\/" + idlFileName.replace(".idl", ""),
							idlFileName.replace(".idl", ""));
					FileUtil.renameAllFilesContent(tmpDirPath,
							"__\\/IDL\\/" + idlFileName.replace(".idl", "").toUpperCase(),
							idlFileName.replace(".idl", "").toUpperCase());

					// コピーする
					FileUtil.fileCopy(srcIdlSourceFile, idlSourceFile);
					FileUtil.fileCopy(srcIdlHeaderFile, idlHeaderFile);
				}

				// 一時領域を削除する
				FileUtil.deleteDirectory(tmpDir);
			}
		} catch (Exception e) {
			logger.error("exception handled.", e);
		}
	}

	/**
	 * Python用にIDLファイルのコピーおよびidl.pyの生成を行う
	 * 
	 * @param rtcProfile
	 * @param rtcDirPath
	 */
	public static void createServiceProviderConsumerIdlFilePython(RtcProfile rtcProfile, String rtcDirPath) {
		// 全てのProvider,Cosumerに対して処理を行う
		if (rtcProfile != null && CollectionUtil.isNotEmpty(rtcProfile.getServicePorts())) {

			// rtc-templateを呼び出すための引数
			List<String> args = new ArrayList<String>();
			String moduleNameArgs = "--module-name=" + rtcProfile.getBasicInfo().getModuleName();
			args.add(moduleNameArgs);

			for (ServicePort servicePort : rtcProfile.getServicePorts()) {
				// Provider
				if (CollectionUtil.isNotEmpty(servicePort.getProvidedServiceInterfaces())) {
					for (ServiceInterface provider : servicePort.getProvidedServiceInterfaces()) {
						// IDLファイルの有無をチェックし、存在しない場合はコピーを行う
						copyIdlFile(rtcDirPath, provider.getIdlFile());

						// 引数を生成する
						String serviceArgs = "--service=" + servicePort.getName() + ":" + provider.getName() + ":"
								+ provider.getInterfaceType().split("::")[1];
						String idlArgs = "--service-idl=../idl/" + provider.getIdlFile();
						args.add(serviceArgs);
						args.add(idlArgs);
					}
				}

				// Consumer
				if (CollectionUtil.isNotEmpty(servicePort.getRequiredServiceInterfaces())) {
					for (ServiceInterface consumer : servicePort.getRequiredServiceInterfaces()) {
						// IDLファイルの有無をチェックし、存在しない場合はコピーを行う
						copyIdlFile(rtcDirPath, consumer.getIdlFile());

						// 引数を生成する
						String serviceArgs = "--consumer=" + servicePort.getName() + ":" + consumer.getName() + ":"
								+ consumer.getInterfaceType().split("::")[1];
						String idlArgs = "--consumer-idl=../idl/" + consumer.getIdlFile();
						args.add(serviceArgs);
						args.add(idlArgs);
					}
				}
			}

			// Pythonの場合は一括で呼ぶ
			if (CollectionUtil.isNotEmpty(args) && args.size() > 1) {
				createIdlPython(rtcDirPath, rtcProfile.getBasicInfo().getModuleName(), args);
			}
		}
	}

	/**
	 * Python用にidl.pyを生成する
	 * 
	 * @param rtcDirPath
	 * @param moduleName
	 * @param argsList
	 */
	private static void createIdlPython(String rtcDirPath, String moduleName, List<String> argsList) {

		String tmpDirPath = StringUtil.concatenate(File.separator, rtcDirPath, "tmp");

		// 一時領域を生成する
		File tmpDir = new File(tmpDirPath);
		tmpDir.mkdir();

		// rtc-templateを実行する
		List<String> commandList = new ArrayList<String>();
		commandList.add("python");
		commandList.add("/usr/bin/rtc-template");
		commandList.add("-bpython");
		commandList.addAll(argsList);
		ProcessUtil.startProcessNoReturnWithWorkingDerectory(tmpDirPath,
				(String[]) commandList.toArray(new String[commandList.size()]));

		// 作成されたファイルの"../idl/hoge.idl"を"hoge.idl"に置き換える
		FileUtil.renameAllFilesContentToEmpty(tmpDirPath, "..\\/idl\\/");

		// 一時作業領域にできたファイルをコピーしていく
		File[] listFiles = tmpDir.listFiles();
		if (CollectionUtil.isNotEmpty(listFiles)) {
			for (File file : listFiles) {
				if (!file.isDirectory() && file.getName().contains("_idl")) {
					// idlファイルをコピーする
					FileUtil.fileCopy(file, FileUtil.concatenateFilePath(rtcDirPath, file.getName()));
				} else if (file.isDirectory()) {
					// ディレクトリは全てコピーする
					File target = FileUtil.concatenateFilePath(rtcDirPath, file.getName());
					if (target.exists()) {
						// 存在する場合は一度削除する
						FileUtil.deleteDirectory(target);
					}
					FileUtil.directoryCopy(file, new File(rtcDirPath));
				}
			}
		}

		// 一時領域を削除する
		FileUtil.deleteDirectory(tmpDir);
	}

	/************************************************************
	 * コンフィグファイル自動反映処理
	 ************************************************************/

	/**
	 * ExecutionRateを更新する
	 * 
	 * @param configFilePath
	 * @param newExecutionRate
	 */
	public static void updateExecutionRateRtcConfig(String configFilePath, Double newExecutionRate) {
		try {
			File file = new File(configFilePath);
			ArrayList<String> strArr = new ArrayList<String>();

			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(file));
				String s;

				while ((s = br.readLine()) != null) {
					if (s.startsWith("exec_cxt.periodic.rate:")) {
						strArr.add("exec_cxt.periodic.rate:" + BigDecimal.valueOf(newExecutionRate).toPlainString());
					} else {
						strArr.add(s);
					}
				}
			} catch (Exception e) {
				logger.error("exception handled.", e);
			} finally {
				if (br != null) {
					br.close();
				}
			}

			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(new FileWriter(file));
				for (int i = 0; i < strArr.size(); i++) {
					bw.write(strArr.get(i) + "\n");
				}
			} catch (Exception e) {
				logger.error("exception handled.", e);
			} finally {
				if (bw != null) {
					bw.close();
				}
			}
		} catch (Exception e) {
			logger.error("exception handled.", e);
		}
	}
}
