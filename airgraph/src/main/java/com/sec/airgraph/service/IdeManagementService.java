package com.sec.airgraph.service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sec.airgraph.util.GitUtil;
import com.sec.airgraph.util.PropUtil;
import com.sec.airgraph.util.StringUtil;
import com.sec.airgraph.util.WasanbonUtil;
import com.sec.airgraph.util.Const.COMMON.DIR_NAME;

/**
 * IDE管理用サービス
 * 
 * @author Tsuyoshi Hirose
 *
 */

@Service
public class IdeManagementService {

	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(IdeManagementService.class);
	
	/************************************************************
	 * Git関連
	 ************************************************************/
	
	/**
	 * PackageをローカルリポジトリにCommitする
	 * 
	 * @param workPackageName
	 * @param commitMessage
	 */
	public String commitPackage(String workPackageName, String commitMessage) {
		// 作業領域パス
		String workspaceDirPath = PropUtil.getValue("workspace.local.directory.path");
		String workPackageDirPath = workspaceDirPath + File.separator + workPackageName;
		
		// コンポーネントをパッケージにリンクする
		WasanbonUtil.syncRtcToPackage(workPackageDirPath);
		
		// Commitする
		String result = GitUtil.gitCommit(workPackageDirPath, commitMessage);
		
		logger.info("Commit Package. package[" + workPackageName + "]result[" + result + "]");
		
		return result;
	}
	
	/**
	 * PackageをリモートリポジトリにPushする
	 * 
	 * @param workPackageName
	 */
	public String pushPackage(String workPackageName, String userName, String password) {
		// 作業領域パス
		String workspaceDirPath = PropUtil.getValue("workspace.local.directory.path");
		String workPackageDirPath = workspaceDirPath + File.separator + workPackageName;
		
		// URLをID/Pass付きに変換する
		String gitUrl = GitUtil.getGitUrl(workPackageDirPath).trim();
		String newGitUrl = gitUrl;
		
		if (StringUtil.isNotEmpty(userName) && StringUtil.isNotEmpty(password)) {
			try {
				newGitUrl = gitUrl.replace("://", "://" + URLEncoder.encode(userName, "UTF-8") + ":" + URLEncoder.encode(password, "UTF-8") + "@").trim();
				GitUtil.changeRemoteUrl(workPackageDirPath, newGitUrl);
			} catch (UnsupportedEncodingException e) {
				logger.error("Exception Handled. ", e);
				newGitUrl = gitUrl;
			}
		}
		
		// Pushする
		String result = GitUtil.gitPush(workPackageDirPath);
		
		// URLを戻す
		GitUtil.changeRemoteUrl(workPackageDirPath, gitUrl);
		
		logger.info("Push Package. package[" + workPackageName + "]result[" + result + "]");
		
		return result;
	}
	
	/**
	 * PackageをリモートリポジトリからPullする
	 * 
	 * @param workPackageName
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
				newGitUrl = gitUrl.replace("://", "://" + URLEncoder.encode(userName, "UTF-8") + ":" + URLEncoder.encode(password, "UTF-8") + "@").trim();
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
	 * ComponentをローカルリポジトリにCommitする
	 * 
	 * @param workPackageName
	 * @param componentName
	 * @param commitMessage
	 */
	public String commitComponent(String workPackageName, String componentName, String commitMessage) {
		// 作業領域パス
		String workspaceDirPath = PropUtil.getValue("workspace.local.directory.path");
		String rtcDirPath = StringUtil.concatenate(File.separator, workspaceDirPath, workPackageName,
				DIR_NAME.PACKAGE_RTC_DIR_NAME, componentName);

		// Commitする
		String result = GitUtil.gitCommit(rtcDirPath, commitMessage);
				
		logger.info("Commit Component. package[" + workPackageName + "]componentName[" + componentName + "]result[" + result + "]");
		
		return result;
	}
	
	/**
	 * ComponentをリモートリポジトリにPushする
	 * 
	 * @param workPackageName
	 * @param componentName
	 * @param userName
	 * @param password
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
				newGitUrl = gitUrl.replace("://", "://" + URLEncoder.encode(userName, "UTF-8") + ":" + URLEncoder.encode(password, "UTF-8") + "@").trim();
				GitUtil.changeRemoteUrl(rtcDirPath, newGitUrl);
			} catch (UnsupportedEncodingException e) {
				logger.error("Exception Handled. ", e);
				newGitUrl = gitUrl;
			}
		}
		
		// Pushする
		String result = GitUtil.gitPush(rtcDirPath);
		
		// URLを戻す
		GitUtil.changeRemoteUrl(rtcDirPath, gitUrl);
		
		logger.info("Push Component. package[" + workPackageName + "]componentName[" + componentName + "]result[" + result + "]");
		
		return result;
	}
	
	/**
	 * ComponentをリモートリポジトリからPullする
	 * 
	 * @param workPackageName
	 * @param componentName
	 * @param userName
	 * @param password
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
				newGitUrl = gitUrl.replace("://", "://" + URLEncoder.encode(userName, "UTF-8") + ":" + URLEncoder.encode(password, "UTF-8") + "@").trim();
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
		
		logger.info("Pull Component. package[" + workPackageName + "]componentName[" + componentName + "]result[" + result + "]");
		
		return result;
	}
}
