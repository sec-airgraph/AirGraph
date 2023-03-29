package com.sec.airgraph.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;


/**
 * Git関連Utility.
 *
 * @author Tsuyoshi Hirose
 *
 */
public class GitUtil {

	/**
	 * logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(GitUtil.class);

	/**
	 * 指定されたリポジトリを指定されたディレクトリにクローンする.
	 *
	 * @param remoteRepositry リモートリポジトリ
	 * @param localDirPath ローカルディレクトリパス
	 * @param rtcDirPath RTCディレクトリパス
	 */
	public static void gitClone(String remoteRepositry, String localDirPath, String rtcDirPath) {
		ProcessUtil.startProcessNoReturnWithWorkingDerectory(rtcDirPath, "git", "clone", "--recursive", remoteRepositry,
				localDirPath);
	}
	
	/**
	 * ローカルリポジトリにCommitする(package).
	 *
	 * @param gitDirPath gitディレクトリパス
	 * @param packageName パッケージ名
	 * @param commitMessage コミットメッセージ
	 * @return コミット結果
	 */
	public static String gitPackageCommit(String gitDirPath, String packageName, String commitMessage) {
		// add 
		ProcessUtil.startProcessReturnStringWithWorkingDerectory(gitDirPath, "git", "add", ".");
		
		// commit
		String hostId = Const.HOST_CONFIG.LOCALHOST_ID;
		String url = WasanbonUtil.getUrl(hostId, "dev", "packages", packageName, "git", "commit");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		Map<String, String> requestBody = new HashMap<String, String>();
		requestBody.put("comment", commitMessage);
		HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);
		
		// リクエストの送信
		ResponseEntity<String> response = HttpUtil.sendHttpRequestAndGetResponse(url, HttpMethod.POST, entity, String.class);
		
		String result = response.getBody();
		return result;
	}

	/**
	 * リモートリポジトリにPushする(package).
	 *
	 * @param packageName パッケージ名
	 * @return push結果
	 */
	public static String gitPackagePush(String packageName) {
		String hostId = Const.HOST_CONFIG.LOCALHOST_ID;
		String url = WasanbonUtil.getUrl(hostId, "dev", "packages", packageName, "git", "push");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		// リクエストの送信
		try {
			ResponseEntity<String> response = HttpUtil.sendHttpRequestAndGetResponse(url, HttpMethod.POST, entity, String.class);
			String result = response.getBody();
			return result;
		}
		catch (HttpServerErrorException e) {
			logger.error(e.getMessage(), e);
		}
		return url;
	}

	/**
	 * ローカルリポジトリにCommitする(component).
	 *
	 * @param gitDirPath gitディレクトリ名
	 * @param packageName パッケージ名
	 * @param rtcName コンポーネント名
	 * @param commitMessage コミットメッセージ
	 * @return コミット結果
	 */
	public static String gitComponentCommit(String gitDirPath, String packageName, String rtcName, String commitMessage) {
		// add
		ProcessUtil.startProcessReturnStringWithWorkingDerectory(gitDirPath, "git", "add", ".");
		// commit
		String hostId = Const.HOST_CONFIG.LOCALHOST_ID;
		String url = WasanbonUtil.getUrl(hostId, "dev", "packages", packageName, "rtcs", rtcName, "git", "commit");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		Map<String, String> body = new HashMap<String, String>();
		body.put("comment", commitMessage);
		HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
		
		// リクエストの送信
		ResponseEntity<String> response = HttpUtil.sendHttpRequestAndGetResponse(url, HttpMethod.POST, entity, String.class);

		return response.getBody();
	}
	
	/**
	 * リモートリポジトリにPushする(component).
	 *
	 * @param packageName パッケージ名
	 * @param rtcName コンポーネント名
	 * @return push結果
	 */
	public static String gitComponentPush(String packageName, String rtcName) {
		String hostId = Const.HOST_CONFIG.LOCALHOST_ID;
		String url = WasanbonUtil.getUrl(hostId, "dev", "packages", packageName, "rtcs", rtcName, "git", "push");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		// リクエストの送信
		ResponseEntity<String> response = HttpUtil.sendHttpRequestAndGetResponse(url, HttpMethod.POST, entity, String.class);

		return response.getBody();
	}

	/**
	 * リモートブランチの数を取得する.
	 *
	 * @param gitDirPath gitディレクトリパス
	 * @return リモートブランチの数
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
	 * 現在のブランチを取得する.
	 *
	 * @param gitDirPath gitディレクトリパス
	 * @return 現在のブランチ
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
	 * リモートリポジトリからPullする.
	 *
	 * @param gitDirPath gitディレクトリパス
	 * @return pull結果
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
	 * ディレクトリパスとGitURLからディレクトリパスを作成する.
	 *
	 * @param basePath ベースパス
	 * @param gitUrl gitURL
	 * @return ディレクトリパス
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
	 * ディレクトリパスとGitURLからディレクトリパスを作成する.
	 *
	 * @param gitUrl gitURL
	 * @return ディレクトリパス
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
	 * 対象のディレクトリからGitのURLを取得する.
	 *
	 * @param gitDirPath gitディレクトリパス
	 * @return GitのURL
	 */
	public static String getGitUrl(String gitDirPath) {
		// GitのURLを取得
		return ProcessUtil.startProcessReturnStringWithWorkingDerectory(gitDirPath, "git", "config", "--get",
				"remote.origin.url");
	}

	/**
	 * Gitの初期化を行う(package).
	 *
	 * @param newPackageName パッケージ名
	 */
	public static void gitPackageInit(String newPackageName) {
		String hostId = Const.HOST_CONFIG.LOCALHOST_ID;
		String url = WasanbonUtil.getUrl(hostId, "dev", "packages", newPackageName, "git", "init");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		// リクエストの送信
		HttpUtil.sendHttpRequestAndGetResponse(url, HttpMethod.POST, entity, String.class);

	}
	
	/**
	 * Gitの初期化を行う(component).
	 *
	 * @param packageName パッケージ名
	 * @param rtcName コンポーネント名
	 */
	public static void gitComponentInit(String packageName, String rtcName) {
		String hostId = Const.HOST_CONFIG.LOCALHOST_ID;
		String url = WasanbonUtil.getUrl(hostId, "dev", "packages", packageName, "rtcs", rtcName, "git", "init");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		// リクエストの送信
		HttpUtil.sendHttpRequestAndGetResponse(url, HttpMethod.POST, entity, String.class);

	}

	/**
	 * リモートリポジトリを追加する(package).
	 *
	 * @param newPackageName パッケージ名
	 * @param remoteUrl リモートリポジトリのURL
	 */
	public static void gitAddPackageRemote(String newPackageName, String remoteUrl) {
		String hostId = Const.HOST_CONFIG.LOCALHOST_ID;
		String url = WasanbonUtil.getUrl(hostId, "dev", "packages", newPackageName, "git", "remote");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		Map<String, String> body = new HashMap<String, String>();
		body.put("url", remoteUrl);
		HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
		
		HttpUtil.sendHttpRequestAndGetResponse(url, HttpMethod.PUT, entity, String.class);
	}
	
	/**
	 * リモートリポジトリを追加する(component).
	 *
	 * @param packageName パッケージ名
	 * @param rtcName コンポーネント名
	 * @param remoteUrl リモートリポジトリのURL
	 */
	public static void gitAddComponentRemote(String packageName, String rtcName, String remoteUrl) {
		String hostId = Const.HOST_CONFIG.LOCALHOST_ID;
		String url = WasanbonUtil.getUrl(hostId, "dev", "packages", packageName, "rtcs", rtcName, "git", "remote");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		Map<String, String> body = new HashMap<String, String>();
		body.put("url", remoteUrl);
		HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
		
		HttpUtil.sendHttpRequestAndGetResponse(url, HttpMethod.PUT, entity, String.class);
	}

	/**
	 * リモートリポジトリの内容をマージする.
	 *
	 * @param gitDirPath gitディレクトリパス
	 * @param isForce 強制マージかどうか
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
	 * リモートリポジトリを変更する.
	 *
	 * @param gitDirPath gitディレクトリパス
	 * @param remoteUrl リモートリポジトリのURL
	 */
	public static void changeRemoteUrl(String gitDirPath, String remoteUrl) {
		ProcessUtil.startProcessReturnStringWithWorkingDerectory(gitDirPath, "git", "remote", "set-url", "origin",
				remoteUrl);
	}

	/**
	 * パッケージ向けにgitignoreを生成する.
	 *
	 * @param gitDirPath gitディレクトリパス
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
	 * コンポーネント向けにgitignoreを生成する.
	 *
	 * @param gitDirPath gitディレクトリパス
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
	
	/**
	 * commit hashを取得する.
	 *
	 * @param workspaceDirPath パッケージディレクトリパス
	 * @param packageName パッケージ名
	 * @return コミットハッシュ
	 */
	public static String getCommitHash(String workspaceDirPath, String packageName) {
		String gitReferenceFilePath = workspaceDirPath + packageName + "/.git/refs/heads/master";
		String commitHash = FileUtil.readAll(gitReferenceFilePath);
		
		logger.info("commitHash: " + commitHash);
		return commitHash;
	}
	
	/**
	 * packageのstatusを確認する.
	 *
	 * @param ws ワークスペース
	 * @param packageName パッケージ名
	 * @return packageのstatus
	 */
	public static String checkPackageStatus(String ws, String packageName) {
		String hostId = Const.HOST_CONFIG.LOCALHOST_ID;
		String url = WasanbonUtil.getUrl(hostId, ws, "packages", packageName, "git", "status");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		ResponseEntity<String> response = HttpUtil.sendHttpRequestAndGetResponse(url, HttpMethod.GET, entity, String.class);
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Map<String, String>> map = null;
		String result = null;
		try {
			// キーがString、値がObjectのマップに読み込みます。
			map = mapper.readValue(response.getBody(), new TypeReference<Map<String, Map<String, String>>>(){});
			result = map.get("status").get(packageName);
			
		} catch (Exception e) {
			// エラー！
			logger.error(e.getMessage(), e);
		}

		return result;
		
	}
	
	/**
	 * RTCのstatusを確認する.
	 *
	 * @param ws ワークスペース
	 * @param packageName パッケージ名
	 * @return RTCのstatus
	 */
	public static String checkRtcsStatus(String ws, String packageName) {
		String hostId = Const.HOST_CONFIG.LOCALHOST_ID;
		String url = WasanbonUtil.getUrl(hostId, ws, "packages", packageName, "rtcs", "git", "status");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		ResponseEntity<String> response = HttpUtil.sendHttpRequestAndGetResponse(url, HttpMethod.GET, entity, String.class);
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = null;
		String result = null;
		try {
			// キーがString、値がObjectのマップに読み込む
			map = mapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>(){});
			if (map.get("status") == null) {
				return "null";
			}
			result = map.get("status").toString();
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return result;
		
	}
}
