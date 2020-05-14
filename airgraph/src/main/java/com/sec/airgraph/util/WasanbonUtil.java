package com.sec.airgraph.util;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wasanbon関連Utility
 * 
 * @author Tsuyoshi Hirose
 *
 */
public class WasanbonUtil {

	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(WasanbonUtil.class);

	/**
	 * Binderの一覧を取得する
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Map<String, String>> getBinderList() {
		// パッケージの一覧を取得
		String binderStr = ProcessUtil.startProcessReturnString("wasanbon-admin.py", "binder", "list");
		logger.debug("wasanbon binder list : " + binderStr);
		return (Map<String, Map<String, String>>) YamlUtil.loadYamlFromString(binderStr);
	}

	/**
	 * Binderに定義されているPackageの一覧を取得する
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getPackagesListFromBinder() {
		// パッケージの一覧を取得
		String packageStr = ProcessUtil.startProcessReturnString("wasanbon-admin.py", "binder", "packages");
		logger.debug("wasanbon packages list : " + packageStr);
		return (List<String>) YamlUtil.loadYamlFromString(packageStr);
	}

	/**
	 * Binderに定義されているRtcの一覧を取得する
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getRtcsListFromBinder() {
		// rtcの一覧を取得
		String rtcStr = ProcessUtil.startProcessReturnString("wasanbon-admin.py", "binder", "rtcs");
		logger.debug("wasanbon rtcs list : " + rtcStr);
		return (List<String>) YamlUtil.loadYamlFromString(rtcStr);
	}

	/**
	 * packageに組み込まれているRtcの一覧を取得する
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getRtcsListFromPackage(String packagesLocalDirPath, String packageRepositoryName) {

		// RTCの一覧を取得
		String rtcStr = ProcessUtil.startProcessReturnStringWithWorkingDerectory(
				packagesLocalDirPath + File.separator + packageRepositoryName, "python", "mgr.py", "rtc", "list");
		logger.debug("package :" + packageRepositoryName + ". rtcs list : " + rtcStr);
		return (List<String>) YamlUtil.loadYamlFromString(rtcStr);
	}

	/**
	 * Packageを指定されたディレクトリにCloneする
	 * 
	 * @param packagesLocalDirPath
	 * @param packageRepositoryName
	 */
	public static void clonePackageFromRepository(String packagesLocalDirPath, String packageRepositoryName) {
		// Cloneする
		ProcessUtil.startProcessNoReturnWithWorkingDerectory(packagesLocalDirPath, "wasanbon-admin.py", "repository",
				"clone", packageRepositoryName);
	}

	/**
	 * 新規Packageを作成する
	 * 
	 * @param packagesLocalDirPath
	 * @param packageRepositoryName
	 */
	public static void createPackage(String packagesLocalDirPath, String packageRepositoryName) {
		// 新規パッケージ生成
		ProcessUtil.startProcessNoReturnWithWorkingDerectory(packagesLocalDirPath, "wasanbon-admin.py", "package",
				"create", packageRepositoryName);
	}

	/**
	 * 指定されたPackageを削除する
	 * 
	 * @param packageRepositoryName
	 */
	public static void deletePackage(String packageRepositoryName) {
		// パッケージを削除する
		ProcessUtil.startProcessNoReturn("wasanbon-admin.py", "package", "delete", packageRepositoryName, "-r");
	}

	/**
	 * 指定されたPackageにRtcを組み込む
	 * 
	 * @param packagesLocalDirPath
	 * @param rtcRepositoryName
	 */
	public static String cloneRtcToPackage(String packagesLocalDirPath, String rtcRepositoryName) {
		// RTCをCloneする
		String result = ProcessUtil.startProcessReturnStringWithWorkingDerectory(packagesLocalDirPath, "python",
				"mgr.py", "repository", "clone", rtcRepositoryName, "-v");
		return result;
	}

	/**
	 * 指定されたPackageにRtcに同期化する
	 * 
	 * @param packagesLocalDirPath
	 */
	public static String syncRtcToPackage(String packagesLocalDirPath) {
		// RTCをCloneする
		String result = ProcessUtil.startProcessReturnStringWithWorkingDerectory(packagesLocalDirPath, "python",
				"mgr.py", "repository", "sync");
		return result;
	}

	/**
	 * 指定されたPackageからRtcを削除する
	 * 
	 * @param packagesLocalDirPath
	 * @param rtcRepositoryName
	 */
	public static void deleteRtcFromPackage(String packagesLocalDirPath, String rtcRepositoryName) {
		// RTCを削除する
		ProcessUtil.startProcessNoReturnWithWorkingDerectory(packagesLocalDirPath, "python", "mgr.py", "rtc", "delete",
				rtcRepositoryName);
	}

	/********************************************************************
	 * ビルド・実行関連
	 ********************************************************************/
	/**
	 * 指定されたPackageのすべてのRtcをビルドする
	 * 
	 * @param logFile
	 * @param packageDirPath
	 */
	public static void buildPackageAll(File logFile, String packageDirPath) {
		// ビルドする
		ProcessUtil.startProcessNoReturnWithWorkingDerectoryAndLog(packageDirPath, logFile, "python", "mgr.py", "rtc",
				"build", "all", "-v");
	}

	/**
	 * 指定されたPackageのすべてのRtcをCleanする
	 * 
	 * @param logFile
	 * @param packageDirPath
	 */
	public static void cleanPackageAll(File logFile, String packageDirPath) {
		// クリーンする
		ProcessUtil.startProcessNoReturnWithWorkingDerectoryAndLog(packageDirPath, logFile, "python", "mgr.py", "rtc",
				"clean", "all", "-v");
	}

	/**
	 * 指定されたPackageを実行する
	 * 
	 * @param logFile
	 * @param packagesLocalDirPath
	 * @param packageDirPath
	 */
	public static void runPackage(File logFile, String packageDirPath) {
		// 実行する
		ProcessUtil.startProcessNoReturnWithWorkingDerectoryAndLog(packageDirPath, logFile, "python", "mgr.py",
				"system", "run", "-v");
	}

	/**
	 * 指定されたPackageを停止する
	 * 
	 * @param logFile
	 * @param packageDirPath
	 */
	public static void terminatePackage(File logFile, String packageDirPath) {
		// 停止する
		ProcessUtil.startProcessNoReturnWithWorkingDerectoryAndLog(packageDirPath, logFile, "python", "mgr.py",
				"system", "terminate", "-v");
	}

	/**
	 * Packageの実行状況を確認する
	 * 
	 * @param packageDirPath
	 * @return
	 */
	public static boolean isRunningPackage(String packageDirPath) {
		boolean result = false;
		String ret = ProcessUtil.startProcessReturnStringWithWorkingDerectory(packageDirPath, "python", "mgr.py",
				"system", "is_running");
		if (StringUtil.isNotEmpty(ret) && ret.trim().toUpperCase().equals("TRUE")) {
			result = true;
		}
		return result;
	}
}
