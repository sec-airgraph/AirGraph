package com.sec.airgraph.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * プロセス実行Utility.
 *
 * @author Tsuyoshi Hirose
 *
 */
public class ProcessUtil {

	/**
	 * logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(ProcessUtil.class);

	/**
	 * 戻り値無しでプロセスを実行する.
	 *
	 * @param command コマンド
	 */
	public static void startProcessNoReturn(String... command) {
		// 置換する
		ProcessBuilder pb = new ProcessBuilder(command);
		// 標準出力と標準エラーをマージ
		pb.redirectErrorStream(true);

		try {
			// 実行
			Process process = pb.start();
			process.waitFor();
		} catch (IOException e) {
			String methodName = new Object() {
			}.getClass().getEnclosingMethod().getName();
			logger.error("In Function " + methodName + ", process builder 例外発生:" + e);
		} catch (InterruptedException e) {
			String methodName = new Object() {
			}.getClass().getEnclosingMethod().getName();
			logger.error("In Function " + methodName + ", process builder 例外発生:" + e);
		}
	}

	/**
	 * 実行フォルダを指定して、戻り値無しでプロセスを実行する.
	 *
	 * @param workingDirPath 実行フォルダ
	 * @param command コマンド
	 */
	public static void startProcessNoReturnWithWorkingDerectory(String workingDirPath, String... command) {
		// 置換する
		ProcessBuilder pb = new ProcessBuilder(command);
		// 標準出力と標準エラーをマージ
		pb.redirectErrorStream(true);
		// 作業ディレクトリ指定
		pb.directory(new File(workingDirPath));

		try {
			// 実行
			Process process = pb.start();
			process.waitFor();
		} catch (IOException e) {
			String methodName = new Object() {
			}.getClass().getEnclosingMethod().getName();
			logger.error("In Function " + methodName + ", process builder 例外発生:" + e);
		} catch (InterruptedException e) {
			String methodName = new Object() {
			}.getClass().getEnclosingMethod().getName();
			logger.error("In Function " + methodName + ", process builder 例外発生:" + e);
		}
	}

	/**
	 * 実行フォルダを指定して、戻り値無しでプロセスを実行し結果を指定されたファイルに出力する.
	 *
	 * @param workingDirPath 実行フォルダ
	 * @param logFile ログファイル 
	 * @param command コマンド
	 */
	public static void startProcessNoReturnWithWorkingDerectoryAndLog(String workingDirPath, File logFile,
			String... command) {
		// 置換する
		ProcessBuilder pb = new ProcessBuilder(command);
		// 標準出力と標準エラーをマージ
		pb.redirectErrorStream(true);
		// ログに出力する
		pb.redirectOutput(logFile);
		// 作業ディレクトリ指定
		pb.directory(new File(workingDirPath));

		try {
			// 実行
			Process process = pb.start();
			process.waitFor();
		} catch (IOException e) {
			String methodName = new Object() {
			}.getClass().getEnclosingMethod().getName();
			logger.error("In Function " + methodName + ", process builder 例外発生:" + e);
		} catch (InterruptedException e) {
			String methodName = new Object() {
			}.getClass().getEnclosingMethod().getName();
			logger.error("In Function " + methodName + ", process builder 例外発生:" + e);
		}
	}

	/**
	 * 戻り値ありでプロセスを実行する.
	 *
	 * @param command コマンド
	 * @return 戻り値
	 */
	public static String startProcessReturnString(String... command) {
		// 置換する
		ProcessBuilder pb = new ProcessBuilder(command);
		// 標準出力と標準エラーをマージ
		pb.redirectErrorStream(true);

		String result = null;
		try {
			Process process = pb.start();
			process.waitFor();
			try (InputStream in = process.getInputStream();
					InputStreamReader isr = new InputStreamReader(in, "UTF-8");
					BufferedReader reader = new BufferedReader(isr)) {
				StringBuilder builder = new StringBuilder();
				int c;
				while ((c = reader.read()) != -1) {
					builder.append((char) c);
				}
				result = builder.toString();

			}
		} catch (IOException e) {
			String methodName = new Object() {
			}.getClass().getEnclosingMethod().getName();
			logger.error("In Function " + methodName + ", 例外発生:" + e);
		} catch (InterruptedException e) {
			String methodName = new Object() {
			}.getClass().getEnclosingMethod().getName();
			logger.error("In Function " + methodName + ", 例外発生:" + e);
		}
		return result;
	}

	/**
	 * 実行フォルダを指定して、戻り値ありでプロセスを実行する.
	 *
	 * @param workingDirPath 実行フォルダ
	 * @param command コマンド
	 * @return 戻り値
	 */
	public static String startProcessReturnStringWithWorkingDerectory(String workingDirPath, String... command) {
		// 置換する
		ProcessBuilder pb = new ProcessBuilder(command);
		// 標準出力と標準エラーをマージ
		pb.redirectErrorStream(true);
		// 作業ディレクトリ指定
		pb.directory(new File(workingDirPath));

		String result = null;
		try {
			Process process = pb.start();
			process.waitFor();
			try (InputStream in = process.getInputStream();
					InputStreamReader isr = new InputStreamReader(in, "UTF-8");
					BufferedReader reader = new BufferedReader(isr)) {
				StringBuilder builder = new StringBuilder();
				int c;
				while ((c = reader.read()) != -1) {
					builder.append((char) c);
				}
				result = builder.toString();

			}
		} catch (IOException e) {
			String methodName = new Object() {
			}.getClass().getEnclosingMethod().getName();
			logger.error("In Function " + methodName + ", 例外発生:" + e);
		} catch (InterruptedException e) {
			String methodName = new Object() {
			}.getClass().getEnclosingMethod().getName();
			logger.error("In Function " + methodName + ", 例外発生:" + e);
		}
		return result;
	}
}
