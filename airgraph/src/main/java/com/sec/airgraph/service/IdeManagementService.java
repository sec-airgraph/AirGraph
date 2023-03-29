package com.sec.airgraph.service;

import com.sec.airgraph.util.Const.COMMON.DIR_NAME;
import com.sec.airgraph.util.GitUtil;
import com.sec.airgraph.util.PropUtil;
import com.sec.airgraph.util.StringUtil;
import com.sec.airgraph.util.WasanbonUtil;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * IDE管理用サービス.
 * 
 * @author Tsuyoshi Hirose
 *
 */

@Service
public class IdeManagementService {

	/**
	 * logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(IdeManagementService.class);

	/************************************************************
	 * Git関連
	 ************************************************************/
	/**
	 * PackageをローカルリポジトリにCommitする.
	 *
	 * @param workPackageName パッケージ名
	 * @param packageName パッケージ名
	 * @param commitMessage コミットメッセージ
	 * @return commit結果
	 */
	public String commitPackage(String workPackageName, String packageName, String commitMessage) {
		// 作業領域パス
		String workspaceDirPath = PropUtil.getValue("workspace.local.directory.path");
		String workPackageDirPath = workspaceDirPath + File.separator + workPackageName;
		
		//packageNameを取得する
		packageName = packageName.replace("rts_", "");
		
		// コンポーネントをパッケージにリンクする
		WasanbonUtil.syncRtcToPackage(packageName);
		
		// Commitする
		String result = GitUtil.gitPackageCommit(workPackageDirPath, packageName, commitMessage);

		logger.info("Commit Package. package[" + workPackageName + "]result[" + result + "]");

		return result;
	}

	/**
	 * PackageをリモートリポジトリにPushする.
	 *
	 * @param workPackageName パッケージ名
	 * @param packageName パッケージ名
	 * @param userName GitHubユーザー名
	 * @param password GitHubパスワード
	 * @return push結果
	 */
	public String pushPackage(String workPackageName, String packageName, String userName, String password) {
		// 作業領域パス
		String workspaceDirPath = PropUtil.getValue("workspace.local.directory.path");
		String workPackageDirPath = workspaceDirPath + File.separator + workPackageName;

		// URLをID/Pass付きに変換する
		String gitUrl = GitUtil.getGitUrl(workPackageDirPath).trim();
		String newGitUrl = gitUrl;
		
		//packageNameを取得する
		packageName = packageName.replace("rts_", "");

		if (StringUtil.isNotEmpty(userName) && StringUtil.isNotEmpty(password)) {
			try {
				newGitUrl = gitUrl.replace("://",
						"://" + URLEncoder.encode(userName, "UTF-8") + ":" + URLEncoder.encode(password, "UTF-8") + "@")
						.trim();
				GitUtil.changeRemoteUrl(workPackageDirPath, newGitUrl);
			} catch (UnsupportedEncodingException e) {
				logger.error("Exception Handled. ", e);
				newGitUrl = gitUrl;
			}
		}
		
		// Pushする
		String result = GitUtil.gitPackagePush(packageName);

		// URLを戻す
		GitUtil.changeRemoteUrl(workPackageDirPath, gitUrl);

		logger.info("Push Package. package[" + workPackageName + "]result[" + result + "]");

		return result;
	}

	/**
	 * PackageをリモートリポジトリからPullする.
	 *
	 * @param workPackageName パッケージ名
	 * @param userName GitHubユーザー名
	 * @param password GitHubパスワード
	 * @return pull結果
	 */
	public String pullPackage(String workPackageName, String userName, String password) {
		// 作業領域パス
		String workspaceDirPath = PropUtil.getValue("workspace.local.directory.path");
		String workPackageDirPath = workspaceDirPath + File.separator + workPackageName;

		// URLをID/Pass付きに変換する
		String gitUrl = GitUtil.getGitUrl(workPackageDirPath).trim();
		String newGitUrl = gitUrl;

		if (StringUtil.isNotEmpty(userName) && StringUtil.isNotEmpty(password)) {
			try {
				newGitUrl = gitUrl.replace("://",
						"://" + URLEncoder.encode(userName, "UTF-8") + ":" + URLEncoder.encode(password, "UTF-8") + "@")
						.trim();
				GitUtil.changeRemoteUrl(workPackageDirPath, newGitUrl);
			} catch (UnsupportedEncodingException e) {
				logger.error("Exception Handled. ", e);
				newGitUrl = gitUrl;
			}
		}

		// Pushする
		String result = GitUtil.gitPull(workPackageDirPath);

		// URLを戻す
		GitUtil.changeRemoteUrl(workPackageDirPath, gitUrl);

		logger.info("Pull Package. package[" + workPackageName + "]result[" + result + "]");

		return result;
	}

	/**
	 * ComponentをローカルリポジトリにCommitする.
	 * 
	 * @param workPackageName パッケージ名
	 * @param componentName コンポーネント名
	 * @param commitMessage コミットメッセージ
	 * @return commit結果
	 */
	public String commitComponent(String workPackageName, String componentName, String commitMessage) {
		// 作業領域パス
		String workspaceDirPath = PropUtil.getValue("workspace.local.directory.path");
		String rtcDirPath = StringUtil.concatenate(File.separator, workspaceDirPath, workPackageName,
				DIR_NAME.PACKAGE_RTC_DIR_NAME, componentName);
		
		String packageName = workPackageName.replace("rts_", "");
		
		// Commitする
		String result = GitUtil.gitComponentCommit(rtcDirPath, packageName, componentName, commitMessage);

		logger.info("Commit Component. package[" + workPackageName + "]componentName[" + componentName + "]result["
				+ result + "]");

		return result;
	}

	/**
	 * ComponentをリモートリポジトリにPushする.
	 * 
	 * @param workPackageName パッケージ名
	 * @param componentName コンポーネント名
	 * @param userName GitHubユーザー名
	 * @param password GitHubパスワード
	 * @return push結果
	 */
	public String pushComponent(String workPackageName, String componentName, String userName, String password) {
		// 作業領域パス
		String workspaceDirPath = PropUtil.getValue("workspace.local.directory.path");
		String rtcDirPath = StringUtil.concatenate(File.separator, workspaceDirPath, workPackageName,
				DIR_NAME.PACKAGE_RTC_DIR_NAME, componentName);

		// URLをID/Pass付きに変換する
		String gitUrl = GitUtil.getGitUrl(rtcDirPath).trim();
		String newGitUrl = gitUrl;

		if (StringUtil.isNotEmpty(userName) && StringUtil.isNotEmpty(password)) {
			try {
				newGitUrl = gitUrl.replace("://",
						"://" + URLEncoder.encode(userName, "UTF-8") + ":" + URLEncoder.encode(password, "UTF-8") + "@")
						.trim();
				GitUtil.changeRemoteUrl(rtcDirPath, newGitUrl);
			} catch (UnsupportedEncodingException e) {
				logger.error("Exception Handled. ", e);
				newGitUrl = gitUrl;
			}
		}
		
		String packageName = workPackageName.replace("rts_", "");

		// Pushする
		String result = GitUtil.gitComponentPush(packageName, componentName);

		// URLを戻す
		GitUtil.changeRemoteUrl(rtcDirPath, gitUrl);

		logger.info("Push Component. package[" + workPackageName + "]componentName[" + componentName + "]result["
				+ result + "]");

		return result;
	}

	/**
	 * ComponentをリモートリポジトリからPullする.
	 *
	 * @param workPackageName パッケージ名
	 * @param componentName コンポーネント名
	 * @param userName GitHubユーザー名
	 * @param password GitHubパスワード
	 * @return pull結果
	 */
	public String pullComponent(String workPackageName, String componentName, String userName, String password) {
		// 作業領域パス
		String workspaceDirPath = PropUtil.getValue("workspace.local.directory.path");
		String rtcDirPath = StringUtil.concatenate(File.separator, workspaceDirPath, workPackageName,
				DIR_NAME.PACKAGE_RTC_DIR_NAME, componentName);

		// URLをID/Pass付きに変換する
		String gitUrl = GitUtil.getGitUrl(rtcDirPath).trim();
		String newGitUrl = gitUrl;

		if (StringUtil.isNotEmpty(userName) && StringUtil.isNotEmpty(password)) {
			try {
				newGitUrl = gitUrl.replace("://",
						"://" + URLEncoder.encode(userName, "UTF-8") + ":" + URLEncoder.encode(password, "UTF-8") + "@")
						.trim();
				GitUtil.changeRemoteUrl(rtcDirPath, newGitUrl);
			} catch (UnsupportedEncodingException e) {
				logger.error("Exception Handled. ", e);
				newGitUrl = gitUrl;
			}
		}

		// Pushする
		String result = GitUtil.gitPull(rtcDirPath);

		// URLを戻す
		GitUtil.changeRemoteUrl(rtcDirPath, gitUrl);

		logger.info("Pull Component. package[" + workPackageName + "]componentName[" + componentName + "]result["
				+ result + "]");

		return result;
	}
	
	/**
	 * コミットハッシュを取得する.
	 *
	 * @param packageName パッケージ名
	 * @return コミットハッシュ
	 */
	public String getCommitHash(String packageName) {
		// 作業領域パス
		String workspaceDirPath = PropUtil.getValue("workspace.local.directory.path");
		logger.info(workspaceDirPath);
		
		// commit hashを取得する
		return GitUtil.getCommitHash(workspaceDirPath, packageName);

	}
	
	/**
	 * packageのstatusを確認する.
	 * 
	 * @param ws ワークスペース名
	 * @param rtsName パッケージ名
	 * @return 実行結果
	 */
	public String checkPackageStatus(String ws, String rtsName) {
			
		String packageName = rtsName.replace("rts_", "");
		
		return GitUtil.checkPackageStatus(ws, packageName);
	}
	
	/**
	 * RTCのstatusを確認する.
	 * 
	 * @param ws ワークスペース名
	 * @param rtsName パッケージ名
	 * @return 実行結果
	 */
	public String checkRtcsStatus(String ws, String rtsName) {
			
		String packageName = rtsName.replace("rts_", "");
		
		return GitUtil.checkRtcsStatus(ws, packageName);
	}
}
