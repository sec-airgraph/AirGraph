package com.sec.airgraph.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.sec.rtc.entity.rtc.Configuration;
import com.sec.rtc.entity.rtc.NeuralNetworkInfo;
import com.sec.rtc.entity.rtc.RtcProfile;
import com.sec.airgraph.util.Const.COMMON.DIR_NAME;
import com.sec.airgraph.util.Const.RT_COMPONENT.ACTIVITY_TYPE;
import com.sec.airgraph.util.Const.RT_COMPONENT.COMPONENT_KIND;
import com.sec.airgraph.util.Const.RT_COMPONENT.COMPONENT_TYPE;
import com.sec.airgraph.util.Const.RT_COMPONENT.CONFIGURATION_TYPE;
import com.sec.airgraph.util.Const.RT_COMPONENT.EXECUTION_TYPE;
import com.sec.airgraph.util.Const.RT_COMPONENT.INTERFACE_DIRECTION;
import com.sec.airgraph.util.Const.RT_COMPONENT.PORT_DATA_TYPE;
import com.sec.airgraph.util.Const.RT_COMPONENT.PORT_POSITION;

/**
 * IDE関連Utility
 * 
 * @author Tsuyoshi Hirose
 *
 */
public class RtmEditorUtil {

	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(RtmEditorUtil.class);

	/************************************************************
	 * 選択肢用Map作成処理関連
	 ************************************************************/
	/**
	 * コンポーネント型Mapを生成する
	 * 
	 * @return
	 */
	public static Map<String, String> createComponentTypeMap() {
		Map<String, String> map = new LinkedHashMap<>();

		map.put(COMPONENT_TYPE.STATIC, COMPONENT_TYPE.STATIC);
		map.put(COMPONENT_TYPE.UNIQUE, COMPONENT_TYPE.UNIQUE);
		map.put(COMPONENT_TYPE.COMMUTATIVE, COMPONENT_TYPE.COMMUTATIVE);

		return map;
	}

	/**
	 * アクティビティ型Mapを生成する
	 * 
	 * @return
	 */
	public static Map<String, String> createActivityTypeMap() {
		Map<String, String> map = new LinkedHashMap<>();

		map.put(ACTIVITY_TYPE.PERIODIC, ACTIVITY_TYPE.PERIODIC);
		map.put(ACTIVITY_TYPE.SPORADIC, ACTIVITY_TYPE.SPORADIC);
		map.put(ACTIVITY_TYPE.EVENTDRIVEN, ACTIVITY_TYPE.EVENTDRIVEN);

		return map;
	}

	/**
	 * コンポーネント種類Mapを生成する
	 * 
	 * @return
	 */
	public static Map<String, String> createComponentKindMap() {
		Map<String, String> map = new LinkedHashMap<>();

		map.put(COMPONENT_KIND.DATA_FROW, COMPONENT_KIND.DATA_FROW);
		map.put(COMPONENT_KIND.FINITE_STATE_MACHINE, COMPONENT_KIND.FINITE_STATE_MACHINE);
		map.put(COMPONENT_KIND.MULTI_MODE, COMPONENT_KIND.MULTI_MODE);

		return map;
	}

	/**
	 * コンポーネント種類Mapを生成する
	 * 
	 * @return
	 */
	public static Map<String, String> createExecutionTypeMap() {
		Map<String, String> map = new LinkedHashMap<>();

		map.put(EXECUTION_TYPE.PERIODIC_EXECUTION_CONTEXT, EXECUTION_TYPE.PERIODIC_EXECUTION_CONTEXT);
		map.put(EXECUTION_TYPE.EXT_TRIG_EXECUTION_CONTEXT, EXECUTION_TYPE.EXT_TRIG_EXECUTION_CONTEXT);

		return map;
	}

	/**
	 * 各ポート型に対応する文字列を取得する
	 * 
	 * @param portDataType
	 * @return
	 */
	public static String getPythonConstructorForImg(String portDataType) {
		// Pythonのコンストラクタ
		String pythonConstructor = "";

		switch (portDataType) {
		case PORT_DATA_TYPE.IMG.CAMERA_DEVICE_PROFILE:
			pythonConstructor = "Img.CameraDeviceProfile(\"\",\"\",0,\"\",\"\",Img.CameraIntrinsicParameter([0.0]*5, []),[])";
			break;
		case PORT_DATA_TYPE.IMG.CAMERA_IMAGE:
			pythonConstructor = "RTC.CameraImage(RTC.Time(0,0), 0,0,0,\"\",0.0,\"\")";
			break;
		case PORT_DATA_TYPE.IMG.CAMERA_INTRINSIC_PARAMETER:
			pythonConstructor = "Img.CameraIntrinsicParameter([0.0]*5, [])";
			break;
		case PORT_DATA_TYPE.IMG.IMAGE_DATA:
			pythonConstructor = "Img.ImageData(0,0, Img.CF_UNKNOWN, \"\")";
			break;
		case PORT_DATA_TYPE.IMG.MULTI_CAMERA_IMAGE:
			pythonConstructor = "Img.MultiCameraImage([],0)";
			break;
		case PORT_DATA_TYPE.IMG.NAMED_VALUE:
			pythonConstructor = "Img.NamedValue(\"\",\"\")";
			break;
		case PORT_DATA_TYPE.IMG.TIMED_CAMERA_IMAGE:
			pythonConstructor = "Img.TimedCameraImage(RTC.Time(0,0), Img.CameraImage(RTC.Time(0,0), Img.ImageData(0,0, Img.CF_UNKNOWN, \"\"), Img.CameraIntrinsicParameter([0.0]*5, []), [[0.0]*4]*4), 0)";
			break;
		case PORT_DATA_TYPE.IMG.TIMED_MULTI_CAMERA_IMAGE:
			pythonConstructor = "Img.TimedMultiCameraImage(RTC.Time(0,0), Img.MultiCameraImage([],0), 0)";
			break;
		}
		return pythonConstructor;
	}

	/**
	 * 各ポート型に対応する文字列を取得する
	 * 
	 * @param portDataType
	 * @return
	 */
	public static String getPythonConstructorForRtc(String portDataType) {
		// Pythonのコンストラクタ
		String pythonConstructor = "";

		switch (portDataType) {
		case PORT_DATA_TYPE.RTC.COMPONENT_PROFILE:
		case PORT_DATA_TYPE.RTC.CONNECTOR_PROFILE:
		case PORT_DATA_TYPE.RTC.EXECUTION_CONTEXT_PROFILE:
		case PORT_DATA_TYPE.RTC.FSM_BEHAVIOR_PROFILE:
		case PORT_DATA_TYPE.RTC.FSM_PROFILE:
		case PORT_DATA_TYPE.RTC.PORT_INTERFACE_PROFILE:
		case PORT_DATA_TYPE.RTC.PORT_PROFILE:
		case PORT_DATA_TYPE.RTC.GRIPPER_GEOMETRY:
		case PORT_DATA_TYPE.RTC.MULTI_CAMERA_GEOMETRY:
		case PORT_DATA_TYPE.RTC.PAN_TILT_STATE:
		case PORT_DATA_TYPE.RTC.TIMED_CONVARIANCE_2D:
		case PORT_DATA_TYPE.RTC.TIMED_CONVARIANCE_3D:
		case PORT_DATA_TYPE.RTC.TIMED_POINT_CONVARIANCE_2D:
		case PORT_DATA_TYPE.RTC.TIMED_POSE_VEL_3D:
		case PORT_DATA_TYPE.RTC.MANAGER_PROFILE:
		case PORT_DATA_TYPE.RTC.MODULE_PROFILE:
			pythonConstructor = "RTC." + portDataType + "()";
			break;
		case PORT_DATA_TYPE.RTC.GPS_TIME:
			pythonConstructor = "RTC." + portDataType + "(0,0)";
			break;
		case PORT_DATA_TYPE.RTC.ACCELERATION_2D:
		case PORT_DATA_TYPE.RTC.CARLIKE:
		case PORT_DATA_TYPE.RTC.POINT_2D:
		case PORT_DATA_TYPE.RTC.SIZE_2D:
		case PORT_DATA_TYPE.RTC.SPEED_HEADING_2D:
		case PORT_DATA_TYPE.RTC.VECTOR_2D:
			pythonConstructor = "RTC." + portDataType + "(0.0,0.0)";
			break;
		case PORT_DATA_TYPE.RTC.ACCELERATION_3D:
		case PORT_DATA_TYPE.RTC.ANGULAR_ACCELERATION_3D:
		case PORT_DATA_TYPE.RTC.ANGULAR_VELOCITY_3D:
		case PORT_DATA_TYPE.RTC.FIDUCIAL_FOV:
		case PORT_DATA_TYPE.RTC.PRIENTATION_3D:
		case PORT_DATA_TYPE.RTC.POINT_3D:
		case PORT_DATA_TYPE.RTC.POINT_COVARIANCE_2D:
		case PORT_DATA_TYPE.RTC.SIZE_3D:
		case PORT_DATA_TYPE.RTC.VECTOR_3D:
		case PORT_DATA_TYPE.RTC.VELOCITY_2D:
			pythonConstructor = "RTC." + portDataType + "(0.0,0.0,0.0)";
			break;
		case PORT_DATA_TYPE.RTC.TIMED_LONG:
		case PORT_DATA_TYPE.RTC.TIMED_OCTET:
		case PORT_DATA_TYPE.RTC.TIMED_SHORT:
		case PORT_DATA_TYPE.RTC.TIMED_STATE:
		case PORT_DATA_TYPE.RTC.TIMED_ULONG:
		case PORT_DATA_TYPE.RTC.TIMED_USHORT:
			pythonConstructor = "RTC." + portDataType + "(RTC.Time(0,0), 0)";
			break;
		case PORT_DATA_TYPE.RTC.TIMED_DOUBLE:
		case PORT_DATA_TYPE.RTC.TIMED_FLOAT:
			pythonConstructor = "RTC." + portDataType + "(RTC.Time(0,0), 0.0)";
			break;
		case PORT_DATA_TYPE.RTC.ACT_ARRAY_ACUTUATOR_CURRENT:
		case PORT_DATA_TYPE.RTC.ACT_ARRAY_ACUTUATOR_POS:
		case PORT_DATA_TYPE.RTC.ACT_ARRAY_ACUTUATOR_SPEED:
			pythonConstructor = "RTC." + portDataType + "(RTC.Time(0,0), 0, 0.0)";
			break;
		case PORT_DATA_TYPE.RTC.ACT_ARRAY_STATE:
		case PORT_DATA_TYPE.RTC.FIDUCIALS:
		case PORT_DATA_TYPE.RTC.HYPOTHESES_2D:
		case PORT_DATA_TYPE.RTC.MULTI_CAMERA_IMAGES:
		case PORT_DATA_TYPE.RTC.PATH_3D:
		case PORT_DATA_TYPE.RTC.POINT_CLOUD:
		case PORT_DATA_TYPE.RTC.TIMED_BOOLEAN_SEQ:
		case PORT_DATA_TYPE.RTC.TIMED_CHAR_SEQ:
		case PORT_DATA_TYPE.RTC.TIMED_DOUBLE_SEQ:
		case PORT_DATA_TYPE.RTC.TIMED_FLOAT_SEQ:
		case PORT_DATA_TYPE.RTC.TIMED_LONG_SEQ:
		case PORT_DATA_TYPE.RTC.TIMED_OCTET_SEQ:
		case PORT_DATA_TYPE.RTC.TIMED_SHORT_SEQ:
		case PORT_DATA_TYPE.RTC.TIMED_STRING_SEQ:
		case PORT_DATA_TYPE.RTC.TIMED_ULONG_SEQ:
		case PORT_DATA_TYPE.RTC.TIMED_USHORT_SEQ:
		case PORT_DATA_TYPE.RTC.TIMED_WCHAR_SEQ:
		case PORT_DATA_TYPE.RTC.TIMED_WSTRING_SEQ:
			pythonConstructor = "RTC." + portDataType + "(RTC.Time(0,0), [])";
			break;
		case PORT_DATA_TYPE.RTC.ACT_ARRAY_ACTUATOR_GEOMETRY:
			pythonConstructor = "RTC.ActArrayActuatorGeometry(RTC.ACTARRAY_ACTUATORTYPE_LINEAR, 0.0, RTC.Orientation3D(0.0,0.0,0.0), RTC.Vector3D(0.0,0.0,0.0), 0.0, 0.0, 0.0, 0.0, False)";
			break;
		case PORT_DATA_TYPE.RTC.ACT_ARRAY_GEOMETRY:
			pythonConstructor = "RTC.ActArrayGeometry(RTC.TimedGeometry3D(RTC.Time(0,0), RTC.Geometry3D(RTC.Pose3D(RTC.Point3D(0.0,0.0,0.0), RTC.Orientation3D(0.0,0.0,0.0)), RTC.Size3D(0.0,0.0,0.0)), [])";
			break;
		case PORT_DATA_TYPE.RTC.ACTUATOR:
			pythonConstructor = "RTC.Actuator(0.0,0.0,0.0,0.0, RTC.ACTUATOR_STATUS_IDLE)";
			break;
		case PORT_DATA_TYPE.RTC.BUMPER_ARRAY_GEOMETRY:
			pythonConstructor = "RTC.BumperArrayGeometry(RTC.Geometry3D(RTC.Pose3D(RTC.Point3D(0.0,0.0,0.0), RTC.Orientation3D(0.0,0.0,0.0)), RTC.Size3D(0.0,0.0,0.0)), [])";
			break;
		case PORT_DATA_TYPE.RTC.BUMPER_GEOMETRY:
			pythonConstructor = "RTC.BumperGeometry(RTC.Pose3D(RTC.Point3D(0.0,0.0,0.0), RTC.Orientation3D(0.0,0.0,0.0)), RTC.Size3D(0.0,0.0,0.0), 0.0)";
			break;
		case PORT_DATA_TYPE.RTC.CAMERA_IMAGE:
			pythonConstructor = "RTC.CameraImage(RTC.Time(0,0), 0,0,0,\"\",0.0,\"\")";
			break;
		case PORT_DATA_TYPE.RTC.CAMERA_INFO:
			pythonConstructor = "RTC.CameraInfo(RTC.Vector2D(0.0,0.0), RTC.Point2D(0.0,0.0), 0.0,0.0,0.0,0.0)";
			break;
		case PORT_DATA_TYPE.RTC.COVARIANCE_2D:
			pythonConstructor = "RTC.Covariance2D(*([0.0]*6))";
			break;
		case PORT_DATA_TYPE.RTC.COVARIANCE_3D:
			pythonConstructor = "RTC.Covariance3D(*([0.0]*21))";
			break;
		case PORT_DATA_TYPE.RTC.FEATURES:
			pythonConstructor = "RTC.Features(RTC.Time(0,0), [],[],[])";
			break;
		case PORT_DATA_TYPE.RTC.FIDUCIAL_INFO:
			pythonConstructor = "RTC.FiducialInfo(0, RTC.Pose3D(RTC.Point3D(0.0,0.0,0.0), RTC.Orientation3D(0.0,0.0,0.0)), RTC.Pose3D(RTC.Point3D(0.0,0.0,0.0), RTC.Orientation3D(0.0,0.0,0.0)), RTC.Size3D(0.0,0.0,0.0), RTC.Size3D(0.0,0.0,0.0))";
			break;
		case PORT_DATA_TYPE.RTC.GPS_DATA:
			pythonConstructor = "RTC.GPSData(RTC.Time(0,0), RTC.GPSTime(0,0), 0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0, RTC.GPS_FIX_NONE)";
			break;
		case PORT_DATA_TYPE.RTC.GEOMETRY_2D:
			pythonConstructor = "RTC.Geometry2D(RTC.Pose2D(RTC.Point2D(0.0,0.0), 0.0), RTC.Size2D(0.0, 0.0))";
			break;
		case PORT_DATA_TYPE.RTC.GEOMETRY_3D:
			pythonConstructor = "RTC.Geometry3D(RTC.Pose3D(RTC.Point3D(0.0,0.0,0.0), RTC.Orientation3D(0.0,0.0,0.0)), RTC.Size3D(0.0,0.0,0.0))";
			break;
		case PORT_DATA_TYPE.RTC.GRIPPER_STATE:
			pythonConstructor = "RTC.GripperState(RTC.Time(0,0), RTC.GRIPPER_STATE_UNKNOWN)";
			break;
		case PORT_DATA_TYPE.RTC.HYPOTHESES_3D:
			pythonConstructor = "RTC.Hypotheses3D(RTC.Time(0,0), [RTC.Hypothesis3D(RTC.Pose3D(RTC.Point3D(0.0,0.0,0.0), RTC.Orientation3D(0.0,0.0,0.0)), RTC.Covariance3D(*([0.0]*21)), 0.0)])";
			break;
		case PORT_DATA_TYPE.RTC.HYPOTHESIS_2D:
			pythonConstructor = "RTC.Hypothesis2D(RTC.Pose2D(RTC.Point2D(0.0,0.0), 0.0), RTC.Covariance2D(*([0.0]*6)), 0.0)";
			break;
		case PORT_DATA_TYPE.RTC.HYPOTHESIS_3D:
			pythonConstructor = "RTC.Hypothesis3D(RTC.Pose3D(RTC.Point3D(0.0,0.0,0.0), RTC.Orientation3D(0.0,0.0,0.0)), RTC.Covariance3D(*([0.0]*21)), 0.0)";
			break;
		case PORT_DATA_TYPE.RTC.INS_DATA:
			pythonConstructor = "RTC.INSData(RTC.Time(0,0), 0.0,0.0,0.0,0.0, RTC.Velocity3D(*([0.0]*6)), RTC.Orientation3D(0.0,0.0,0.0))";
			break;
		case PORT_DATA_TYPE.RTC.INTENSITY_DATA:
			pythonConstructor = "RTC.IntensityData(RTC.Time(0,0), [], RTC.RangerGeometry(RTC.Geometry3D(RTC.Pose3D(RTC.Point3D(0.0,0.0,0.0), RTC.Orientation3D(0.0,0.0,0.0)), RTC.Size3D(0.0,0.0,0.0)), []), RTC.RangerConfig(*([0.0]*7)))";
			break;
		case PORT_DATA_TYPE.RTC.LIMB_STATE:
			pythonConstructor = "RTC.LimbState(RTC.Time(0,0), RTC.OAP(*([RTC.Vector3D(0.0,0.0,0.0)]*3)), RTC.LIMB_STATUS_IDLE)";
			break;
		case PORT_DATA_TYPE.RTC.LINE_FEATURE:
			pythonConstructor = "RTC.LineFeature(0.0,0.0,0.0, RTC.PointCovariance2D(0.0,0.0,0.0), RTC.Point2D(0.0,0.0), RTC.Point2D(0.0,0.0), False, False)";
			break;
		case PORT_DATA_TYPE.RTC.OAP:
			pythonConstructor = "RTC.OAP(*([RTC.Vector3D(0.0,0.0,0.0)]*3))";
			break;
		case PORT_DATA_TYPE.RTC.OG_MAP_CONFIG:
			pythonConstructor = "RTC.OGMapConfig(0.0,0.0,0,0, RTC.Pose2D(RTC.Point2D(0.0,0.0), 0.0))";
			break;
		case PORT_DATA_TYPE.RTC.OG_MAP_TILE:
			pythonConstructor = "RTC.OGMapTile(0,0,0,0,\"\")";
			break;
		case PORT_DATA_TYPE.RTC.PAN_TILT_ANGLES:
			pythonConstructor = "RTC.PanTiltAngles(RTC.Time(0,0), 0.0,0.0)";
			break;
		case PORT_DATA_TYPE.RTC.PATH_2D:
			pythonConstructor = "RTC.Path2D(RTC.Time(0,0), [RTC.Waypoint2D(RTC.Pose2D(RTC.Point2D(0.0,0.0), 0.0), 0.0,0.0, RTC.Time(0,0), RTC.Velocity2D(0.0,0.0,0.0))])";
			break;
		case PORT_DATA_TYPE.RTC.POINT_CLOUD_POINT:
			pythonConstructor = "RTC.PointCloudPoint(RTC.Point3D(0.0,0.0,0.0), RTC.RGBColour(0.0,0.0,0.0))";
			break;
		case PORT_DATA_TYPE.RTC.POINT_FEATURE:
			pythonConstructor = "RTC.PointFeature(0.0, RTC.Point2D(0.0,0.0), RTC.PointCovariance2D(0.0,0.0,0.0))";
			break;
		case PORT_DATA_TYPE.RTC.POSE_2D:
			pythonConstructor = "RTC.Pose2D(RTC.Point2D(0.0,0.0), 0.0)";
			break;
		case PORT_DATA_TYPE.RTC.POSE_3D:
			pythonConstructor = "RTC.Pose3D(RTC.Point3D(0.0,0.0,0.0), RTC.Orientation3D(0.0,0.0,0.0))";
			break;
		case PORT_DATA_TYPE.RTC.POSE_FEATURE:
			pythonConstructor = "RTC.PoseFeature(0.0, RTC.Pose2D(RTC.Point2D(0.0,0.0), 0.0), RTC.Covariance2D(*([0.0]*6)))";
			break;
		case PORT_DATA_TYPE.RTC.POSE_VEL_2D:
			pythonConstructor = "RTC.PoseVel2D(RTC.Pose2D(RTC.Point2D(0.0,0.0), 0.0), RTC.Velocity2D(0.0,0.0,0.0))";
			break;
		case PORT_DATA_TYPE.RTC.POSE_VEL_3D:
			pythonConstructor = "RTC.PoseVel3D(RTC.Pose3D(RTC.Point3D(0.0,0.0,0.0), RTC.Orientation3D(0.0,0.0,0.0)), RTC.Velocity3D(*([0.0]*6)))";
			break;
		case PORT_DATA_TYPE.RTC.QUATERNION:
			pythonConstructor = "RTC.Quaternion(*([0.0]*4))";
			break;
		case PORT_DATA_TYPE.RTC.RGB_COLOUR:
			pythonConstructor = "RTC.PointCloudPoint(RTC.Point3D(0.0,0.0,0.0), RTC.RGBColour(0.0,0.0,0.0))";
			break;
		case PORT_DATA_TYPE.RTC.RANGE_DATA:
			pythonConstructor = "RTC.RangeData(RTC.Time(0,0), [], RTC.RangerGeometry(RTC.Geometry3D(RTC.Pose3D(RTC.Point3D(0.0,0.0,0.0), RTC.Orientation3D(0.0,0.0,0.0)), RTC.Size3D(0.0,0.0,0.0)), []), RTC.RangerConfig(*([0.0]*7)))";
			break;
		case PORT_DATA_TYPE.RTC.RANGER_CONFIG:
			pythonConstructor = "RTC.RangerConfig(*([0.0]*7))";
			break;
		case PORT_DATA_TYPE.RTC.RANGER_GEOMETRY:
			pythonConstructor = "RTC.RangerGeometry(RTC.Geometry3D(RTC.Pose3D(RTC.Point3D(0.0,0.0,0.0), RTC.Orientation3D(0.0,0.0,0.0)), RTC.Size3D(0.0,0.0,0.0)), [])";
			break;
		case PORT_DATA_TYPE.RTC.SPEED_HEADING_3D:
			pythonConstructor = "RTC.SpeedHeading3D(0.0, RTC.Orientation3D(0.0,0.0,0.0))";
			break;
		case PORT_DATA_TYPE.RTC.TIME:
			pythonConstructor = "RTC.Time(0,0)";
			break;
		case PORT_DATA_TYPE.RTC.TIMED_ACCELERATION_2D:
			pythonConstructor = "RTC.TimedAcceleration2D(RTC.Time(0,0), RTC.Acceleration2D(0.0,0.0))";
			break;
		case PORT_DATA_TYPE.RTC.TIMED_ACCELERATION_3D:
			pythonConstructor = "RTC.TimedAcceleration3D(RTC.Time(0,0), RTC.Acceleration3D(0.0,0.0,0.0))";
			break;
		case PORT_DATA_TYPE.RTC.TIMED_ANGULAR_ACCELERATION_3D:
			pythonConstructor = "RTC.TimedAngularAcceleration3D(RTC.Time(0,0), RTC.AngularAcceleration3D(0.0,0.0,0.0))";
			break;
		case PORT_DATA_TYPE.RTC.TIMED_ANGULAR_VELOCITY_3D:
			pythonConstructor = "RTC.TimedAngularVelocity3D(RTC.Time(0,0), RTC.AngularVelocity3D(0.0,0.0,0.0))";
			break;
		case PORT_DATA_TYPE.RTC.TIMED_BOOLEAN:
			pythonConstructor = "RTC.TimedBoolean(RTC.Time(0,0), False)";
			break;
		case PORT_DATA_TYPE.RTC.TIMED_CARLIKE:
			pythonConstructor = "RTC.TimedCarlike(RTC.Time(0,0), RTC.Carlike(0.0,0.0))";
			break;
		case PORT_DATA_TYPE.RTC.TIMED_CHAR:
			pythonConstructor = "RTC.TimedChar(RTC.Time(0,0), chr(0))";
			break;
		case PORT_DATA_TYPE.RTC.TIMED_GEOMETRY_2D:
			pythonConstructor = "RTC.TimedGeometry2D(RTC.Time(0,0), RTC.Geometry2D(RTC.Pose2D(RTC.Point2D(0.0,0.0), 0.0), RTC.Size2D(0.0, 0.0)))";
			break;
		case PORT_DATA_TYPE.RTC.TIMED_GEOMETRY_3D:
			pythonConstructor = "RTC.TimedGeometry3D(RTC.Time(0,0), RTC.Geometry3D(RTC.Pose3D(RTC.Point3D(0.0,0.0,0.0), RTC.Orientation3D(0.0,0.0,0.0)), RTC.Size3D(0.0,0.0,0.0)))";
			break;
		case PORT_DATA_TYPE.RTC.TIMED_OAP:
			pythonConstructor = "RTC.TimedOAP(RTC.Time(0,0), RTC.OAP(*([RTC.Vector3D(0.0,0.0,0.0)]*3)))";
			break;
		case PORT_DATA_TYPE.RTC.TIMED_ORIENTATION_3D:
			pythonConstructor = "RTC.TimedOrientation3D(RTC.Time(0,0), RTC.Orientation3D(0.0,0.0,0.0))";
			break;
		case PORT_DATA_TYPE.RTC.TIMED_POINT_2D:
			pythonConstructor = "RTC.TimedPoint2D(RTC.Time(0,0), RTC.Point2D(0.0,0.0))";
			break;
		case PORT_DATA_TYPE.RTC.TIMED_POINT_3D:
			pythonConstructor = "RTC.TimedPoint3D(RTC.Time(0,0), RTC.Point3D(0.0,0.0,0.0))";
			break;
		case PORT_DATA_TYPE.RTC.TIMED_POSE_2D:
			pythonConstructor = "RTC.TimedPose2D(RTC.Time(0,0), RTC.Pose2D(RTC.Point2D(0.0,0.0), 0.0))";
			break;
		case PORT_DATA_TYPE.RTC.TIMED_POSE_3D:
			pythonConstructor = "RTC.TimedPose3D(RTC.Time(0,0), RTC.Pose3D(RTC.Point3D(0.0,0.0,0.0), RTC.Orientation3D(0.0,0.0,0.0)))";
			break;
		case PORT_DATA_TYPE.RTC.TIMED_POSE_VEL_2D:
			pythonConstructor = "RTC.TimedPoseVel2D(RTC.Time(0,0), RTC.PoseVel2D(RTC.Pose2D(RTC.Point2D(0.0,0.0), 0.0), RTC.Velocity2D(0.0,0.0,0.0)))";
			break;
		case PORT_DATA_TYPE.RTC.TIMED_QUATERNION:
			pythonConstructor = "RTC.TimedQuaternion(RTC.Time(0,0), RTC.Quaternion(*([0.0]*4)))";
			break;
		case PORT_DATA_TYPE.RTC.TIMED_RGB_COLOUR:
			pythonConstructor = "RTC.TimedRGBColour(RTC.Time(0,0), RTC.RGBColour(0.0,0.0,0.0))";
			break;
		case PORT_DATA_TYPE.RTC.TIMED_SIZE_2D:
			pythonConstructor = "RTC.TimedSize2D(RTC.Time(0,0), RTC.Size2D(0.0, 0.0))";
			break;
		case PORT_DATA_TYPE.RTC.TIMED_SIZE_3D:
			pythonConstructor = "RTC.TimedSize3D(RTC.Time(0,0), RTC.Size3D(0.0,0.0,0.0))";
			break;
		case PORT_DATA_TYPE.RTC.TIMED_SPEED_HEADING_2D:
			pythonConstructor = "RTC.TimedSpeedHeading2D(RTC.Time(0,0), RTC.SpeedHeading2D(0.0,0.0))";
			break;
		case PORT_DATA_TYPE.RTC.TIMED_SPEED_HEADING_3D:
			pythonConstructor = "RTC.TimedSpeedHeading3D(RTC.Time(0,0), RTC.SpeedHeading3D(0.0, RTC.Orientation3D(0.0,0.0,0.0)))";
			break;
		case PORT_DATA_TYPE.RTC.TIMED_STRING:
			pythonConstructor = "RTC.TimedString(RTC.Time(0,0), \"\")";
			break;
		case PORT_DATA_TYPE.RTC.TIMED_VECTOR_2D:
			pythonConstructor = "RTC.TimedVector2D(RTC.Time(0,0), RTC.Vector2D(0.0,0.0))";
			break;
		case PORT_DATA_TYPE.RTC.TIMED_VECTOR_3D:
			pythonConstructor = "RTC.TimedVector3D(RTC.Time(0,0), RTC.Vector3D(0.0,0.0,0.0))";
			break;
		case PORT_DATA_TYPE.RTC.TIMED_VELOCITY_2D:
			pythonConstructor = "RTC.TimedVelocity2D(RTC.Time(0,0), RTC.Velocity2D(0.0,0.0,0.0))";
			break;
		case PORT_DATA_TYPE.RTC.TIMED_VELOCITY_3D:
			pythonConstructor = "RTC.TimedVelocity3D(RTC.Time(0,0), RTC.Velocity3D(*([0.0]*6)))";
			break;
		case PORT_DATA_TYPE.RTC.TIMED_WCHRR:
			pythonConstructor = "RTC.TimedWChar(RTC.Time(0,0), u\"00\")";
			break;
		case PORT_DATA_TYPE.RTC.TIMED_WSTRING:
			pythonConstructor = "RTC.TimedWString(RTC.Time(0,0), u\"\")";
			break;
		case PORT_DATA_TYPE.RTC.VELOCITY_3D:
			pythonConstructor = "RTC.Velocity3D(*([0.0]*6))";
			break;
		case PORT_DATA_TYPE.RTC.WAYPOINT_2D:
			pythonConstructor = "RTC.Waypoint2D(RTC.Pose2D(RTC.Point2D(0.0,0.0), 0.0), 0.0,0.0, RTC.Time(0,0), RTC.Velocity2D(0.0,0.0,0.0))";
			break;
		case PORT_DATA_TYPE.RTC.WAYPOINT_3D:
			pythonConstructor = "RTC.Waypoint3D(RTC.Pose3D(RTC.Point3D(0.0,0.0,0.0), RTC.Orientation3D(0.0,0.0,0.0)), 0.0,0.0, RTC.Time(0,0), RTC.Velocity3D(*([0.0]*6)))";
			break;
		}

		return pythonConstructor;
	}

	/**
	 * ポート表示位置Mapを生成する
	 * 
	 * @return
	 */
	public static Map<String, String> createPortPositionMap() {
		Map<String, String> map = new LinkedHashMap<>();

		map.put(PORT_POSITION.LEFT, PORT_POSITION.LEFT);
		map.put(PORT_POSITION.RIGHT, PORT_POSITION.RIGHT);

		return map;
	}

	/**
	 * インタフェースの向きMapを生成する
	 * 
	 * @return
	 */
	public static Map<String, String> createIfDirectionMap() {
		Map<String, String> map = new LinkedHashMap<>();

		map.put(INTERFACE_DIRECTION.PROVIDED, INTERFACE_DIRECTION.PROVIDED);
		map.put(INTERFACE_DIRECTION.REQUIRED, INTERFACE_DIRECTION.REQUIRED);

		return map;
	}

	/**
	 * 指定されたRTCに設定しているIDLファイルを含めて、IDLファイルの一覧を取得する
	 * 
	 * @param workPackageName
	 * @param componentName
	 * @return
	 */
	public static Map<String, String> createIdlFileMap(String workPackageName, String componentName) {
		Map<String, String> map = new LinkedHashMap<>();

		// 作業領域パス
		String workspaceDirPath = PropUtil.getValue("workspace.local.directory.path");
		File rtcDir = FileUtil.concatenateFilePath(workspaceDirPath + workPackageName, DIR_NAME.PACKAGE_RTC_DIR_NAME,
				componentName);

		// 指定されたフォルダからIDLのファイルを探す
		File directoryRtc = new File(rtcDir.getPath() + File.separator + "idl/");
		// 後方一致で"idl"
		IOFileFilter fileFilterRtc = FileFilterUtils.suffixFileFilter("idl");
		// サブディレクトリも検索する（しない場合はnull）
		IOFileFilter dirFilterRtc = FileFilterUtils.trueFileFilter();
		// 検索開始
		Collection<File> rtcList = FileUtils.listFiles(directoryRtc, fileFilterRtc, dirFilterRtc);
		if (CollectionUtil.isNotEmpty(rtcList)) {
			for (File file : rtcList) {
				map.put(file.getAbsolutePath(), file.getAbsolutePath());
			}
		}

		// openRTMのフォルダからIDLのファイルを探す
		// 作業領域パス
		String openRtmDir = getOpenRtmDir();

		File directoryOpenRtm = new File(openRtmDir);
		// 後方一致で"idl"
		IOFileFilter fileFilterOpenRtm = FileFilterUtils.suffixFileFilter("idl");
		// サブディレクトリも検索する（しない場合はnull）
		IOFileFilter dirFilterOpenRtm = FileFilterUtils.trueFileFilter();
		// 検索開始
		Collection<File> OpenRtmList = FileUtils.listFiles(directoryOpenRtm, fileFilterOpenRtm, dirFilterOpenRtm);
		if (CollectionUtil.isNotEmpty(OpenRtmList)) {
			for (File file : OpenRtmList) {
				map.put(file.getAbsolutePath(), file.getAbsolutePath());
			}
		}

		return map;
	}

	/**
	 * 指定されたRTCに設定しているIDLファイルを含めて、全てのIDLファイルのDataType型を取得する
	 * 
	 * @param workPackageName
	 * @param componentName
	 * @return
	 */
	public static Map<String, String> createDataTypeMap(String workPackageName, String componentName,
			boolean isDataType) {
		Map<String, String> map = new LinkedHashMap<>();

		// 作業領域パス
		String workspaceDirPath = PropUtil.getValue("workspace.local.directory.path");
		File rtcDir = FileUtil.concatenateFilePath(workspaceDirPath + workPackageName, DIR_NAME.PACKAGE_RTC_DIR_NAME,
				componentName);

		// rtcのIDLフォルダから探す
		String idlDirPath = rtcDir.getPath() + File.separator + "idl/";

		List<File> targetFileList = new ArrayList<>();
		List<File> componentList = FileUtil.searchFileListWithSubDir(idlDirPath, "idl");
		if (CollectionUtil.isNotEmpty(componentList)) {
			targetFileList.addAll(componentList);
		}

		String openRtmDir = getOpenRtmDir();
		List<File> openRtmList = FileUtil.searchFileListWithSubDir(openRtmDir, "idl");
		if (CollectionUtil.isNotEmpty(openRtmList)) {
			targetFileList.addAll(openRtmList);
		}

		if (!CollectionUtils.isEmpty(targetFileList)) {
			for (File targetFile : targetFileList) {
				List<String> structList = FileUtil.readAndSearchStr(targetFile.getPath(), "struct");
				if (CollectionUtil.isNotEmpty(structList)) {
					// structが存在した場合、module名行を取得する
					List<String> moduleList = FileUtil.readAndSearchStr(targetFile.getPath(), "module");
					String moduleName = null;
					if (CollectionUtil.isNotEmpty(moduleList)) {
						moduleName = moduleList.get(0).replace("module", "").replace("{", "").trim();
					}
					for (String str : structList) {
						if (StringUtil.isEmpty(str) || str.contains("/") || str.contains("*")) {
							continue;
						}
						String structName = str.replace("struct", "").replace("{", "").trim();

						if (isDataType) {
							if (StringUtil.isNotEmpty(moduleName)) {
								map.put(moduleName + "::" + structName, moduleName + "::" + structName);
							} else {
								map.put(structName, structName);
							}
						} else {
							if (StringUtil.isNotEmpty(moduleName)) {
								map.put("IDL:" + moduleName + "/" + structName, "IDL:" + moduleName + "/" + structName);
							} else {
								map.put("IDL:" + structName, "IDL:" + structName);
							}
						}
					}
				}
			}
		}
		return map;
	}

	/**
	 * 指定されたRTCに設定しているIDLファイルを含めて、指定されたIDLファイルのinterface型を取得する
	 * 
	 * @param workPackageName
	 * @param componentName
	 * @param idlFileName
	 * @return
	 */
	public static Map<String, String> createInterfaceTypeMap(String workPackageName, String componentName,
			String idlFileName) {
		Map<String, String> map = new LinkedHashMap<>();

		File targetFile = new File(idlFileName);

		// ファイルが見つかった場合、interface定義の行をファイルから取得する
		if (targetFile != null) {
			List<String> ifList = FileUtil.readAndSearchStr(targetFile.getPath(), "interface");
			if (CollectionUtil.isNotEmpty(ifList)) {
				// interfaceが存在した場合、module名行を取得する
				List<String> moduleList = FileUtil.readAndSearchStr(targetFile.getPath(), "module");
				String moduleName = null;
				if (CollectionUtil.isNotEmpty(moduleList)) {
					moduleName = moduleList.get(0).replace("module", "").replace("{", "").trim();
				}
				for (String str : ifList) {
					if (StringUtil.isEmpty(str) || str.contains("/") || str.contains("*")) {
						continue;
					}
					String ifName = str.replace("interface", "").replace("{", "").trim();
					if (StringUtil.isNotEmpty(moduleName)) {
						map.put(moduleName + "::" + ifName, moduleName + "::" + ifName);
					} else {
						map.put(ifName, ifName);
					}
				}
			}
		}
		return map;
	}

	/**
	 * アップロードしたIDLファイルを保存する
	 * 
	 * @param workPackageName
	 * @param componentName
	 * @param idlFile
	 */
	public static void saveIdlFile(String workPackageName, String componentName, MultipartFile idlFile) {
		// 作業領域パス
		String workspaceDirPath = PropUtil.getValue("workspace.local.directory.path");
		File rtcDir = FileUtil.concatenateFilePath(workspaceDirPath + workPackageName, DIR_NAME.PACKAGE_RTC_DIR_NAME,
				componentName);

		// 指定されたフォルダからIDLのファイルを探す
		File orgFile = new File(idlFile.getOriginalFilename());
		File saveIdlFile = FileUtil.concatenateFilePath(rtcDir.getPath(), "idl", orgFile.getName());

		FileUtil.saveUploadFile(idlFile, saveIdlFile);
	}

	/**
	 * NN設定情報を反映する
	 * 
	 * @param newNN
	 * @param oldNN
	 * @param rtcProfile
	 */
	public static void updateNeuralNetworkInfo(NeuralNetworkInfo newNN, NeuralNetworkInfo oldNN,
			RtcProfile rtcProfile) {
		int dnnModelPathIndex = -1;

		if (CollectionUtil.isNotEmpty(rtcProfile.getConfigurationSet().getConfigurations())) {
			for (int i = 0; i < rtcProfile.getConfigurationSet().getConfigurations().size(); i++) {
				Configuration configuration = rtcProfile.getConfigurationSet().getConfigurations().get(i);
				if (configuration.getName().equals("dnn_model_path")) {
					dnnModelPathIndex = i;
				}
			}
		}

		// DNNモデル名をコンフィギュレーションに反映する
		if (StringUtil.isNotEmpty(newNN.getModelName())) {
			// モデル名が設定されている
			String kerasModelDirPath = PropUtil.getValue("models.keras.directory.path") + newNN.getModelName() + "/"
					+ newNN.getModelName() + ".json";
			if (dnnModelPathIndex >= 0) {
				// パスを更新
				rtcProfile.getConfigurationSet().getConfigurations().get(dnnModelPathIndex)
						.setDefaultValue(kerasModelDirPath);
			} else {
				// 追加
				Configuration configuration = new Configuration();
				configuration.setName("dnn_model_path");
				configuration.setDefaultValue(kerasModelDirPath);
				configuration.setDataType(CONFIGURATION_TYPE.STRING);
				rtcProfile.getConfigurationSet().getConfigurations().add(configuration);
			}
		} else {
			// モデル名が削除されている
			if (dnnModelPathIndex >= 0) {
				rtcProfile.getConfigurationSet().getConfigurations().remove(dnnModelPathIndex);
			}
		}

		int datasetPathIndex = -1;
		if (CollectionUtil.isNotEmpty(rtcProfile.getConfigurationSet().getConfigurations())) {
			for (int i = 0; i < rtcProfile.getConfigurationSet().getConfigurations().size(); i++) {
				Configuration configuration = rtcProfile.getConfigurationSet().getConfigurations().get(i);
				if (configuration.getName().equals("dataset_dir_path")) {
					datasetPathIndex = i;
				}
			}
		}

		// データセット名をコンフィギュレーションに反映する
		if (StringUtil.isNotEmpty(newNN.getDatasetName())) {
			// データセット名が設定されている
			String dataSetDirPath = PropUtil.getValue("dataset.directory.path") + newNN.getDatasetName() + "/";
			if (datasetPathIndex >= 0) {
				// パスを更新
				rtcProfile.getConfigurationSet().getConfigurations().get(datasetPathIndex)
						.setDefaultValue(dataSetDirPath);
			} else {
				// 追加
				Configuration configuration = new Configuration();
				configuration.setName("dataset_dir_path");
				configuration.setDefaultValue(dataSetDirPath);
				configuration.setDataType(CONFIGURATION_TYPE.STRING);
				rtcProfile.getConfigurationSet().getConfigurations().add(configuration);
			}
		} else {
			// データセット名が削除されている
			if (datasetPathIndex >= 0) {
				rtcProfile.getConfigurationSet().getConfigurations().remove(datasetPathIndex);
			}
		}

		// データセットのディレクトリを生成する
		updateDatasetDirectory(newNN.getDatasetName(), oldNN == null ? null : oldNN.getDatasetName());
	}

	/**
	 * データセットディレクトリを更新する
	 * 
	 * @param newDatasetName
	 * @param oldDatasetName
	 */
	private static void updateDatasetDirectory(String newDatasetName, String oldDatasetName) {
		String newDirectoryPath = PropUtil.getValue("dataset.directory.path") + newDatasetName;
		String oldDirectoryPath = PropUtil.getValue("dataset.directory.path") + oldDatasetName;

		if (StringUtil.isEmpty(newDatasetName) && StringUtil.isEmpty(oldDatasetName)) {
			// 新旧データセット名が空の場合は何もしない
			return;
		} else if (StringUtil.isEmpty(newDatasetName)) {
			// 新データセット名が空の場合はデータセットを使わなくなったが、フォルダは削除しない
			return;
		} else if (StringUtil.isNotEmpty(newDatasetName) && !StringUtil.equals(newDatasetName, oldDatasetName)) {
			// データセット名が変更されている場合
			File newDatasetDir = new File(newDirectoryPath);
			if (StringUtil.isEmpty(oldDatasetName)) {
				// 旧データセット名が空の場合は新規作成
				logger.info("Create Dataset Directory. path[" + newDirectoryPath + "]");
				FileUtil.createDirectory(newDatasetDir);
			} else {
				// 旧データセット名が存在する場合はコピー
				File oldDatasetDir = new File(oldDirectoryPath);
				FileUtil.directoryCopy(oldDatasetDir, newDatasetDir);
			}
		}
	}

	/**
	 * RTMのディレクトリを取得する
	 * 
	 * @return
	 */
	public static String getOpenRtmDir() {
		// OpenRTMのフォルダを調べる
		String openRtmDir = PropUtil.getValue("openrtm.rtm.dir");
		if (!(new File(openRtmDir).exists())) {
			openRtmDir = PropUtil.getValue("openrtm.rtm.local.dir");
		}
		return openRtmDir;
	}
}
