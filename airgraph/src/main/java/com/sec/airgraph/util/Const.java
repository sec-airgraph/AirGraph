package com.sec.airgraph.util;

/**
 * 定数定義クラス
 * 
 * @author Tsuyoshi Hirose
 *
 */
public class Const {

	/**
	 * 共通定義
	 */
	public interface COMMON {
		/**
		 * フラグ共通
		 */
		public interface FLAG {
			/** TRUE */
			static final String TRUE = "1";

			/** FALSE */
			static final String FALSE = "0";
		}

		/**
		 * ファイル名定義
		 */
		public interface FILE_NAME {
			/** Binder定義用ファイル */
			static final String BINDER_SETTING_FILE_NAME = "setting.yaml";
			/** DefaultSystem.xml */
			static final String PACKAGE_XML_FILE_NAME = "DefaultSystem.xml";
			/** RTC.xml */
			static final String RTC_XML_FILE_NAME = "RTC.xml";
			/** rtc_cpp.log */
			static final String APP_CPP_LOG = "rtc_cpp.log";
			/** rtc_py.log */
			static final String APP_PYTHON_LOG = "rtc_py.log";
			/** rtc_java.log */
			static final String APP_JAVA_LOG = "rtc_java.log";
			/** wasanbon.log */
			static final String WASANBON_LOG = "wasanbon.log";
			/** ホスト定義ファイル */
			static final String WASANBON_HOST_CONFIG = "wasanbonHostConfig.yaml";
			/** ホスト定義ファイル */
			static final String AIRGRAPH_HOST_CONFIG = "AirGraphHostConfig.yaml";
			/** GitHub設定ファイル */
			static final String GITHUB_CONFIG = "gitHubConfig.yaml";
			/** Basic認証設定ファイル */
			static final String BASIC_AUTH = "user_info.yml";
		}

		/**
		 * ディレクトリ名定義
		 */
		public interface DIR_NAME {
			/** Packageディレクトリ名 */
			static final String BINDER_PACKAGE_DIR_NAME = "packages";
			/** RTCディレクトリ名 */
			static final String BINDER_RTC_DIR_NAME = "rtcs";
			/** パッケージ内System定義ディレクトリ名 */
			static final String PACKAGE_SYSTEM_DIR_NAME = "system";
			/** パッケージ内RTCディレクトリ名 */
			static final String PACKAGE_RTC_DIR_NAME = "rtc";
			/** パッケージログディレクトリ名 */
			static final String PACKAGE_LOG_DIR_NAME = "log";
			/** パッケージコンフィグディレクトリ名 */
			static final String PACKAGE_CONF_DIR_NAME = "conf";
			/** コンポーネントCMAKEディレクトリ名 */
			static final String COMP_CMAKE_DIR_NAME = "cmake";
			/** コンポーネントincludeディレクトリ名 */
			static final String COMP_INCLUDE_DIR_NAME = "include";
			/** コンポーネントsrcディレクトリ名 */
			static final String COMP_SRC_DIR_NAME = "src";
			/** コンポーネントidlディレクトリ名 */
			static final String COMP_IDL_DIR_NAME = "idl";
		}

		/**
		 * ファイル拡張子
		 *
		 */
		public interface FILE_SUFFIX {
			/** RTC */
			static final String SUFFIX_RTC = ".rtc";
			/** C++ */
			static final String SUFFIX_CPP = ".cpp";
			/** C++ header */
			static final String SUFFIX_CPP_HEADER = ".h";
			/** Python */
			static final String SUFFIX_PYTHON = ".py";
			/** Java */
			static final String SUFFIX_JAVA = ".java";
			/** Config */
			static final String SUFFIX_CONFIG = ".conf";
		}
	}

	/**
	 * RTコンポーネント定義
	 */
	public interface RT_COMPONENT {

		public interface PACKAGE_NAME {
			/** 新規 */
			static final String NEW = "New Pkg.";
			/** 新規 */
			static final String NEW_ID = "rts_blank";
		}

		/**
		 * モジュール名
		 */
		public interface MODULE_NAME {
			/** 新規(C++) */
			static final String NEW_CPP = "New Comp(C++).";
			/** 新規(C++) */
			static final String NEW_CPP_ID = "blank_cpp";
			/** 新規(C++) */
			static final String NEW_CPP_TEMPLATE_NAME = "template_cpp";
			/** 新規(Python) */
			static final String NEW_PYTHON = "New Comp(Python).";
			/** 新規(Python) */
			static final String NEW_PYTHON_ID = "blank_python";
			/** 新規(Python) */
			static final String NEW_PYTHON_TEMPLATE_NAME = "template_python";
			/** 新規(Java) */
			static final String NEW_JAVA = "New Comp(Java).";
			/** 新規(Java) */
			static final String NEW_JAVA_ID = "blank_java";
			/** 新規(Java) */
			static final String NEW_JAVA_TEMPLATE_NAME = "template_java";
		}

		/**
		 * コンポーネント型
		 */
		public interface COMPONENT_TYPE {
			/** STATIC */
			static final String STATIC = "STATIC";
			/** UNIQUE */
			static final String UNIQUE = "UNIQUE";
			/** COMMUTATIVE */
			static final String COMMUTATIVE = "COMMUTATIVE";
		}

		/**
		 * アクティビティ型
		 */
		public interface ACTIVITY_TYPE {
			/** PERIODIC */
			static final String PERIODIC = "PERIODIC";
			/** SPORADIC */
			static final String SPORADIC = "SPORADIC";
			/** EVENTDRIVEN */
			static final String EVENTDRIVEN = "EVENTDRIVEN";
		}

		/**
		 * コンポーネント種類
		 */
		public interface COMPONENT_KIND {
			/** DataFlow */
			static final String DATA_FROW = "DataFlow";
			/** FiniteStateMachine */
			static final String FINITE_STATE_MACHINE = "FiniteStateMachine";
			/** MultiMode */
			static final String MULTI_MODE = "MultiMode";
		}

		/**
		 * 実行型
		 */
		public interface EXECUTION_TYPE {
			/** PeriodicExecutionContext */
			static final String PERIODIC_EXECUTION_CONTEXT = "PeriodicExecutionContext";
			/** ExtTrigExecutionContext */
			static final String EXT_TRIG_EXECUTION_CONTEXT = "ExtTrigExecutionContext";
		}

		/**
		 * ドキュメント種別
		 */
		public interface DOCUMENT_TYPE {
			/** ドキュメント生成 */
			static final int DOCUMENT_VAL = 0;
			/** アクション設定 */
			static final int ACTION_VAL = 1;
			/** データポート設定 */
			static final int DATA_PORT_VAL = 2;
			/** サービスポート設定 */
			static final int SERVICE_PORT_VAL = 3;
			/** サービスインタフェース設定 */
			static final int SERVICE_INTERFACE_VAL = 4;
			/** コンフィギュレーション設定 */
			static final int CONFIGURATION_VAL = 5;
		}

		/**
		 * ポート種別
		 */
		public interface PORT_TYPE {
			/** 入力ポート */
			static final String IN = "DataInPort";
			/** ドキュメント生成 */
			static final String OUT = "DataOutPort";
		}

		/**
		 * ポートデータ型
		 */
		public interface PORT_DATA_TYPE {

			/**
			 * IMAGE関連
			 */
			public interface IMG {
				static final String CAMERA_DEVICE_PROFILE = "CameraDeviceProfile";
				static final String CAMERA_IMAGE = "CameraImage";
				static final String CAMERA_INTRINSIC_PARAMETER = "CameraIntrinsicParameter";
				static final String IMAGE_DATA = "ImageData";
				static final String MULTI_CAMERA_IMAGE = "MultiCameraImage";
				static final String NAMED_VALUE = "NamedValue";
				static final String TIMED_CAMERA_IMAGE = "TimedCameraImage";
				static final String TIMED_MULTI_CAMERA_IMAGE = "TimedMultiCameraImage";
			}

			public interface JARA_ARM {
				static final String ALARM = "Alarm";
				static final String CAR_POS_WITH_ELBOW = "CarPosWithElbow";
				static final String CARTESIAN_SPEED = "CartesianSpeed";
				static final String LIMIT_VALUE = "LimitValue";
				static final String MANIP_INFO = "ManipInfo";
				static final String RETURN_ID = "RETURN_ID";
				static final String TIMED_JOINT_POS = "TimedJointPos";
			}

			/**
			 * RTC関連
			 */
			public interface RTC {
				static final String ACCELERATION_2D = "Acceleration2D";
				static final String ACCELERATION_3D = "Acceleration3D";
				static final String ACT_ARRAY_ACUTUATOR_CURRENT = "ActArrayActuatorCurrent";
				static final String ACT_ARRAY_ACTUATOR_GEOMETRY = "ActArrayActuatorGeometry";
				static final String ACT_ARRAY_ACUTUATOR_POS = "ActArrayActuatorPos";
				static final String ACT_ARRAY_ACUTUATOR_SPEED = "ActArrayActuatorSpeed";
				static final String ACT_ARRAY_GEOMETRY = "ActArrayGeometry";
				static final String ACT_ARRAY_STATE = "ActArrayState";
				static final String ACTUATOR = "Actuator";
				static final String ANGULAR_ACCELERATION_3D = "AngularAcceleration3D";
				static final String ANGULAR_VELOCITY_3D = "AngularVelocity3D";
				static final String BUMPER_ARRAY_GEOMETRY = "BumperArrayGeometry";
				static final String BUMPER_GEOMETRY = "BumperGeometry";
				static final String CAMERA_IMAGE = "CameraImage";
				static final String CAMERA_INFO = "CameraInfo";
				static final String CARLIKE = "Carlike";
				static final String COMPONENT_PROFILE = "ComponentProfile";
				static final String CONNECTOR_PROFILE = "ConnectorProfie";
				static final String COVARIANCE_2D = "Covariance2D";
				static final String COVARIANCE_3D = "Covariance3D";
				static final String EXECUTION_CONTEXT_PROFILE = "ExecutionContextProfile";
				static final String FEATURES = "Features";
				static final String FIDUCIAL_FOV = "FiducialFOV";
				static final String FIDUCIAL_INFO = "FiducialInfo";
				static final String FIDUCIALS = "Fiducials";
				static final String FSM_BEHAVIOR_PROFILE = "FsmBehaviorProfile";
				static final String FSM_PROFILE = "FsmProfile";
				static final String GPS_DATA = "GPSData";
				static final String GPS_TIME = "GPSTime";
				static final String GEOMETRY_2D = "Geometry2D";
				static final String GEOMETRY_3D = "Geometry3D";
				static final String GRIPPER_GEOMETRY = "GripperGeometry";
				static final String GRIPPER_STATE = "GripperState";
				static final String HYPOTHESES_2D = "Hypotheses2D";
				static final String HYPOTHESES_3D = "Hypotheses3D";
				static final String HYPOTHESIS_2D = "Hypothesis2D";
				static final String HYPOTHESIS_3D = "Hypothesis3D";
				static final String INS_DATA = "INSData";
				static final String INTENSITY_DATA = "IntensityData";
				static final String LIMB_STATE = "LimbState";
				static final String LINE_FEATURE = "LineFeature";
				static final String MULTI_CAMERA_IMAGES = "MultiCameraImages";
				static final String MULTI_CAMERA_GEOMETRY = "MuitiCameraGeometry";
				static final String OAP = "OAP";
				static final String OG_MAP_CONFIG = "OGMapConfig";
				static final String OG_MAP_TILE = "OGMapTile";
				static final String PRIENTATION_3D = "Orientation3D";
				static final String PAN_TILT_ANGLES = "PanTiltAngles";
				static final String PAN_TILT_STATE = "PanTiltState";
				static final String PATH_2D = "Path2D";
				static final String PATH_3D = "Path3D";
				static final String POINT_2D = "Point2D";
				static final String POINT_3D = "Point3D";
				static final String POINT_CLOUD = "PointCloud";
				static final String POINT_CLOUD_POINT = "PointCloudPoint";
				static final String POINT_COVARIANCE_2D = "PointCovariance2D";
				static final String POINT_FEATURE = "PointFeature";
				static final String PORT_INTERFACE_PROFILE = "PortInterfaceProfie";
				static final String PORT_PROFILE = "PortProfile";
				static final String POSE_2D = "Pose2D";
				static final String POSE_3D = "Pose3D";
				static final String POSE_FEATURE = "PoseFeature";
				static final String POSE_VEL_2D = "PoseVel2D";
				static final String POSE_VEL_3D = "PoseVel3D";
				static final String QUATERNION = "Quaternion";
				static final String RGB_COLOUR = "RGBColour";
				static final String RANGE_DATA = "RangeData";
				static final String RANGER_CONFIG = "RangerConfig";
				static final String RANGER_GEOMETRY = "RangerGeometry";
				static final String SIZE_2D = "Size2D";
				static final String SIZE_3D = "Size3D";
				static final String SPEED_HEADING_2D = "SpeedHeading2D";
				static final String SPEED_HEADING_3D = "SpeedHeading3D";
				static final String TIME = "Time";
				static final String TIMED_ACCELERATION_2D = "TimedAcceleration2D";
				static final String TIMED_ACCELERATION_3D = "TimedAcceleration3D";
				static final String TIMED_ANGULAR_ACCELERATION_3D = "TimedAngularAcceleration3D";
				static final String TIMED_ANGULAR_VELOCITY_3D = "TimedAngularVelocity3D";
				static final String TIMED_BOOLEAN = "TimedBoolean";
				static final String TIMED_BOOLEAN_SEQ = "TimedBooleanSeq";
				static final String TIMED_CARLIKE = "TimedCarlike";
				static final String TIMED_CHAR = "TimedChar";
				static final String TIMED_CHAR_SEQ = "TimedCharSeq";
				static final String TIMED_CONVARIANCE_2D = "TimedConvariance2D";
				static final String TIMED_CONVARIANCE_3D = "TimedConvariance3D";
				static final String TIMED_DOUBLE = "TimedDouble";
				static final String TIMED_DOUBLE_SEQ = "TimedDoubleSeq";
				static final String TIMED_FLOAT = "TimedFloat";
				static final String TIMED_FLOAT_SEQ = "TimedFloatSeq";
				static final String TIMED_GEOMETRY_2D = "TimedGeometry2D";
				static final String TIMED_GEOMETRY_3D = "TimedGeometry3D";
				static final String TIMED_LONG = "TimedLong";
				static final String TIMED_LONG_SEQ = "TimedLongSeq";
				static final String TIMED_OAP = "TimedOAP";
				static final String TIMED_OCTET = "TimedOctet";
				static final String TIMED_OCTET_SEQ = "TimecOctedSeq";
				static final String TIMED_ORIENTATION_3D = "TimedOrientation3D";
				static final String TIMED_POINT_2D = "TimedPoint2D";
				static final String TIMED_POINT_3D = "TimedPoint3D";
				static final String TIMED_POINT_CONVARIANCE_2D = "TimedPointConvariance2D";
				static final String TIMED_POSE_2D = "TimedPose2D";
				static final String TIMED_POSE_3D = "TimedPose3D";
				static final String TIMED_POSE_VEL_2D = "TimedPoseVel2D";
				static final String TIMED_POSE_VEL_3D = "TimedPostVel3D";
				static final String TIMED_QUATERNION = "TimedQuaternion";
				static final String TIMED_RGB_COLOUR = "TimedRGBColour";
				static final String TIMED_SHORT = "TimedShort";
				static final String TIMED_SHORT_SEQ = "TimedShortSeq";
				static final String TIMED_SIZE_2D = "TimedSize2D";
				static final String TIMED_SIZE_3D = "TimedSize3D";
				static final String TIMED_SPEED_HEADING_2D = "TimedSpeedHeading2D";
				static final String TIMED_SPEED_HEADING_3D = "TimedSpeedHeading3D";
				static final String TIMED_STATE = "TimedState";
				static final String TIMED_STRING = "TimedString";
				static final String TIMED_STRING_SEQ = "TimedStringSeq";
				static final String TIMED_ULONG = "TimedULong";
				static final String TIMED_ULONG_SEQ = "TimedULongSeq";
				static final String TIMED_USHORT = "TimedUShort";
				static final String TIMED_USHORT_SEQ = "TimedUShortSeq";
				static final String TIMED_VECTOR_2D = "TimedVector2D";
				static final String TIMED_VECTOR_3D = "TimedVector3D";
				static final String TIMED_VELOCITY_2D = "TimedVelocity2D";
				static final String TIMED_VELOCITY_3D = "TimedVelocity3D";
				static final String TIMED_WCHRR = "TimedWChar";
				static final String TIMED_WCHAR_SEQ = "TimedWCharSeq";
				static final String TIMED_WSTRING = "TimedWString";
				static final String TIMED_WSTRING_SEQ = "TimedWStringSeq";
				static final String VECTOR_2D = "Vector2D";
				static final String VECTOR_3D = "Vector3D";
				static final String VELOCITY_2D = "Velocity2D";
				static final String VELOCITY_3D = "Velocity3D";
				static final String WAYPOINT_2D = "Waypoint2D";
				static final String WAYPOINT_3D = "Waypoint3D";
				static final String MANAGER_PROFILE = "ManagerProfile";
				static final String MODULE_PROFILE = "ModuleProfile";
			}

			/**
			 * SDPPackage関連
			 */
			public interface SDO_PACKAGE {
				static final String CONFIGURATION_SET = "ConfigurationSet";
				static final String DEVICE_PROFILE = "DeviceProfile";
				static final String ENUMERATION_TYPE = "EnumerationType";
				static final String INTERVAL_TYPE = "IntervalType";
				static final String NAME_VALUE = "NameValue";
				static final String ORGANIZATION_PROPERTY = "OrganizationProperty";
				static final String PARAMETER = "Parameter";
				static final String RANGE_TYPE = "RangeType";
				static final String SERVICE_PROFILE = "ServiceProfile";
			}
		}

		/**
		 * 表示位置
		 */
		public interface PORT_POSITION {
			/** 左 */
			static final String LEFT = "LEFT";
			/** 右 */
			static final String RIGHT = "RIGHT";
			/** 上 */
			static final String TOP = "TOP";
			/** 下 */
			static final String BOTTOM = "BOTTOM";
		}

		/**
		 * インタフェース方向
		 */
		public interface INTERFACE_DIRECTION {
			/** Provided */
			static final String PROVIDED = "Provided";
			/** Required */
			static final String REQUIRED = "Required";
		}

		/**
		 * インタフェース種別
		 */
		public interface INTERFACE_TYPE {
			/** corba_cdr */
			static final String CORBA_CDR = "corba_cdr";

		}

		/**
		 * データフロー種別
		 */
		public interface DATAFLOW_TYPE {
			/** push */
			static final String PUSH = "push";
			/** pull */
			static final String PULL = "pull";
		}

		/**
		 * サブスクリプション種別
		 */
		public interface SUBSCRIPTION_TYPE {
			/** flush */
			static final String FLUSH = "flush";
			/** new */
			static final String NEW = "new";
			/** periodic */
			static final String PERIODIC = "periodic";
		}

		/**
		 * コンフィギュレーション種別
		 */
		public interface CONFIGURATION_TYPE {
			/** short */
			static final String SHORT = "short";
			/** int */
			static final String INT = "int";
			/** long */
			static final String LONG = "long";
			/** float */
			static final String FLOAT = "float";
			/** double */
			static final String DOUBLE = "double";
			/** string */
			static final String STRING = "string";
		}

		/**
		 * Widget種別
		 */
		public interface WIDGET_TYPE {
			/** text */
			static final String TEXT = "text";
			/** slider */
			static final String SLIDER = "slider";
			/** spin */
			static final String SPIN = "spin";
			/** radio */
			static final String RADIO = "radio";
			/** checkbox */
			static final String CHECKBOX = "checkbox";
			/** ordered_list */
			static final String ORDERED_LIST = "ordered_list";
		}

		/**
		 * 言語区分
		 */
		public interface LANGUAGE_KIND {
			/** C++ */
			static final String CPP = "C++";
			/** Java */
			static final String JAVA = "Java";
			/** Python */
			static final String PYTHON = "Python";
			/** Ruby */
			static final String RUBY = "Ruby";
		}

		/**
		 * コンポーネント・ロガー種別
		 */
		public interface COMPONENT_CONNECTOR_TYPE {
			/** Component */
			static final String COMPONENT = "component";
			/** Connector */
			static final String CONNECTOR = "connector";
			/** Logger */
			static final String LOGGER = "logger";
		}
	}

	/**
	 * コンポーネント領域定義
	 */
	public interface COMPONENT_FIELD {

		/**
		 * タブ名
		 */
		public interface TAB_NAME {
			/** 新規 */
			static final String NEW = "New";
			/** Package */
			static final String PACKAGE = "Package";
			/** Rtc */
			static final String RTC = "Rtc";
			/** 履歴 */
			static final String RECENT = "Recent";
		}
	}
	
	/**
	 * Airgraphバージョン定義
	 */
	public interface AIRGRAPH_VERSION {
		/** airgraph_version */
		static final String AIRGRAPH_VERSION = "2.0.0";

	}

	/**
	 * ホスト定義
	 */
	public interface HOST_CONFIG {
		/** ローカルホストのID */
		static final String LOCALHOST_ID = "local";
	}

}
