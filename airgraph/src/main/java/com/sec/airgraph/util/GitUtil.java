package com.sec.airgraph.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Git関連Utility
 * 
 * @author Tsuyoshi Hirose
 *
 */
public class GitUtil {

	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(GitUtil.class);

	/**
	 * 指定されたリポジトリを指定されたディレクトリにクローンする
	 * 
	 * @param remoteRepositry
	 * @param localDirPath
	 * @param rtcDirPath
	 */
	public static void gitClone(String remoteRepositry, String localDirPath, String rtcDirPath) {
		ProcessUtil.startProcessNoReturnWithWorkingDerectory(rtcDirPath, "git", "clone", "--recursive", remoteRepositry,
				localDirPath);
	}

	/**
	 * ローカルリポジトリにCommitする
	 * 
	 * @param gitDirPath
	 * @param commitMessage
	 */
	public static String gitCommit(String gitDirPath, String commitMessage) {
		StringBuilder sb = new StringBuilder();

		// add
		String rsltAdd = ProcessUtil.startProcessReturnStringWithWorkingDerectory(gitDirPath, "git", "add", ".");
		// commit
		String rsltCommit = ProcessUtil.startProcessReturnStringWithWorkingDerectory(gitDirPath, "git", "commit", "-m",
				"\"" + commitMessage + "\"");
		if (StringUtil.isNotEmpty(rsltAdd)) {
			sb.append(rsltAdd);
		}
		if (StringUtil.isNotEmpty(rsltCommit)) {
			if (sb.length() > 0) {
				sb.append(System.lineSeparator());
			}
			sb.append(rsltCommit);
		}
		if (sb.length() == 0) {
			sb.append("Success.");
		}
		return sb.toString();
	}

	/**
	 * リモートリポジトリにPushする
	 * 
	 * @param gitDirPath
	 */
	public static String gitPush(String gitDirPath) {
		StringBuilder sb = new StringBuilder();
		String rslt = "";

		// リモートブランチの数を調べる
		int remoteBranchCount = getRemoteBranchCount(gitDirPath);
		if (remoteBranchCount <= 0) {
			// 一度もPushしていない場合はMasterにPushする
			rslt = ProcessUtil.startProcessReturnStringWithWorkingDerectory(gitDirPath, "git", "push", "-u", "origin",
					"master");
		} else {
			// ブランチを取得
			String branch = getCurrentBranch(gitDirPath);
			if (StringUtil.isEmpty(branch)) {
				branch = "master";
			}
			// Pushする
			rslt = ProcessUtil.startProcessReturnStringWithWorkingDerectory(gitDirPath, "git", "push", "origin",
					branch);
		}

		if (StringUtil.isNotEmpty(rslt)) {
			sb.append(rslt);
		}
		if (sb.length() == 0) {
			sb.append("Success.");
		}
		return sb.toString();
	}

	/**
	 * リモートブランチの数を取得する
	 * 
	 * @param gitDirPath
	 * @return
	 */
	public static int getRemoteBranchCount(String gitDirPath) {
		int result = 0;
		String rslt = ProcessUtil.startProcessReturnStringWithWorkingDerectory(gitDirPath, "git", "branch", "--remote");
		if (StringUtil.isNotEmpty(rslt)) {
			result = rslt.trim().split("\n").length;
		}
		return result;
	}

	/**
	 * 現在のブランチを取得する
	 * 
	 * @param gitDirPath
	 * @return
	 */
	public static String getCurrentBranch(String gitDirPath) {
		String rslt = ProcessUtil.startProcessReturnStringWithWorkingDerectory(gitDirPath, "git", "branch",
				"--contains=HEAD");
		if (StringUtil.isNotEmpty(rslt)) {
			rslt = rslt.trim().replace("* ", "");
		}
		return rslt;
	}

	/**
	 * リモートリポジトリからPullする
	 * 
	 * @param gitDirPath
	 */
	public static String gitPull(String gitDirPath) {
		StringBuilder sb = new StringBuilder();
		String rslt = ProcessUtil.startProcessReturnStringWithWorkingDerectory(gitDirPath, "git", "pull", "origin");
		if (StringUtil.isNotEmpty(rslt)) {
			sb.append(rslt);
		}
		if (sb.length() == 0) {
			sb.append("Success.");
		}
		return sb.toString();
	}

	/**
	 * ディレクトリパスとGitURLからディレクトリパスを作成する
	 * 
	 * @param basePath
	 * @param gitUrl
	 * @return
	 */
	public static String createGitDirPath(String basePath, String gitUrl) {

		String path = basePath;
		if (StringUtil.isNotEmpty(gitUrl)) {
			String[] gitUrlArr = gitUrl.split(File.separator);
			String gitName = gitUrlArr[gitUrlArr.length - 2] + "_" + gitUrlArr[gitUrlArr.length - 1];
			String[] gitNameArr = gitName.split("\\.git");
			path += gitNameArr[0] + File.separator;
		}
		return path;
	}

	/**
	 * ディレクトリパスとGitURLからディレクトリパスを作成する
	 * 
	 * @param gitUrl
	 * @return
	 */
	public static String createGitName(String gitUrl) {

		String name = gitUrl;
		if (StringUtil.isNotEmpty(gitUrl)) {
			String[] gitUrlArr = gitUrl.split("/");
			String gitName = gitUrlArr[gitUrlArr.length - 1];
			String[] gitNameArr = gitName.split("\\.");
			name = gitNameArr[0];
		}
		return name;
	}

	/**
	 * 対象のディレクトリからGitのURLを取得する
	 * 
	 * @param gitDirPah
	 * @return
	 */
	public static String getGitUrl(String gitDirPath) {
		// GitのURLを取得
		return ProcessUtil.startProcessReturnStringWithWorkingDerectory(gitDirPath, "git", "config", "--get",
				"remote.origin.url");
	}

	/**
	 * Gitの初期化を行う
	 * 
	 * @param gitDirPath
	 */
	public static void gitInit(String gitDirPath) {
		ProcessUtil.startProcessReturnStringWithWorkingDerectory(gitDirPath, "git", "init");
	}

	/**
	 * リモートリポジトリを追加する
	 * 
	 * @param gitDirPath
	 * @param remoteUrl
	 */
	public static void gitAddRemote(String gitDirPath, String remoteUrl) {
		ProcessUtil.startProcessReturnStringWithWorkingDerectory(gitDirPath, "git", "remote", "add", "origin",
				remoteUrl);
	}

	/**
	 * リモートリポジトリの内容をマージする
	 * 
	 * @param gitDirPath
	 */
	public static void gitFetchOrigin(String gitDirPath, boolean isForce) {
		ProcessUtil.startProcessReturnStringWithWorkingDerectory(gitDirPath, "git", "fetch", "origin");
		if (isForce) {
			// 強制マージ
			ProcessUtil.startProcessReturnStringWithWorkingDerectory(gitDirPath, "git", "reset", "--hard",
					"origin/master");
		}
	}

	/**
	 * リモートリポジトリを変更する
	 * 
	 * @param gitDirPath
	 * @param remoteUrl
	 */
	public static void changeRemoteUrl(String gitDirPath, String remoteUrl) {
		ProcessUtil.startProcessReturnStringWithWorkingDerectory(gitDirPath, "git", "remote", "set-url", "origin",
				remoteUrl);
	}

	/**
	 * パッケージ向けにgitignoreを生成する
	 * 
	 * @param gitDirPath
	 */
	public static void createGitIgnoreForPackage(String gitDirPath) {
		String gitIgnorePath = gitDirPath + File.separator + ".gitignore";
		File file = new File(gitIgnorePath);
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file));

			bw.write("*.pyc");
			bw.newLine();
			bw.write("*~");
			bw.newLine();
			bw.write("bin/*");
			bw.newLine();
			bw.write("*.bak");
			bw.newLine();
			bw.write("*.BAK");
			bw.newLine();
			bw.write("*.log");
			bw.newLine();
			bw.write("*.lck");
			bw.newLine();
			bw.write("rtc/*");
			bw.newLine();
			bw.write("!rtc/repository.yaml");
			bw.newLine();
			bw.write("log/*");
			bw.newLine();
			bw.write("system/*");
			bw.newLine();
			bw.write("!system/DefaultSystem.xml");
		} catch (Exception e) {

		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (Exception e) {
					logger.error("exception handled. ex:", e);
				}
			}
		}
	}

	/**
	 * コンポーネント向けにgitignoreを生成する
	 * 
	 * @param gitDirPath
	 */
	public static void createGitIgnoreForComponent(String gitDirPath) {
		String gitIgnorePath = gitDirPath + File.separator + ".gitignore";
		File file = new File(gitIgnorePath);
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file));

			bw.write("*.pyc");
			bw.newLine();
			bw.write("*~");
			bw.newLine();
			bw.write("*.bak");
			bw.newLine();
			bw.write("*.BAK");
			bw.newLine();
			bw.write("build-*");
			bw.newLine();
			bw.write("*.log");
			bw.newLine();
			bw.write("*.lck");
		} catch (Exception e) {

		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (Exception e) {
					logger.error("exception handled. ex:", e);
				}
			}
		}
	}
}
