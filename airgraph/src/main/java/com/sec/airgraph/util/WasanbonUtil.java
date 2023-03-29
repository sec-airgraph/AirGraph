package com.sec.airgraph.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sec.rtc.entity.BuildRunDTO;
import com.sec.rtc.entity.yaml.HostSettingYaml;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Wasanbon関連Utility.
 *
 * @author Tsuyoshi Hirose
 *
 */
public class WasanbonUtil {

	/**
	 * logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(WasanbonUtil.class);
	
	/**
	 * Binderの一覧を取得する.
	 *
	 * @param hostId ホストID
	 * @return Binderの一覧
	 */
	public static Map<String, Map<String, String>> getBinderList(String hostId) {
		String url = WasanbonUtil.getUrl(hostId, "binders");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		// リクエストの送信
		ResponseEntity<String> response = HttpUtil.sendHttpRequestAndGetResponse(url, HttpMethod.GET, entity, String.class);
		
		// 型の変換
		Map<String, List<Map<String, Map<String, String>>>> tmpMap = null;
		Map<String, Map<String, String>> responseMap = new HashMap<String, Map<String, String>>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			tmpMap = mapper.readValue(response.getBody(), new TypeReference<Map<String, List<Map<String, Map<String, String>>>>>(){});
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		List<Map<String, Map<String, String>>> list = new ArrayList<>();
		list = tmpMap.get("binders");
		
		for (int i = 0; i < list.size(); i++) {
			String[] contents = list.get(i).toString().replaceAll("\\{", "").replaceAll("\\}", "").split("=");
			Map<String, String> map =  new HashMap<String, String>();
			map.put(contents[1], contents[2]);
			responseMap.put(contents[0], map);
		}

		return responseMap;
	}
	
	/**
	 * Binderに定義されているPackageの一覧を取得する.
	 *
	 * @param hostId ホストID
	 * @return Packageの一覧
	 */
	public static List<String> getPackagesListFromBinder(String hostId) {
		String url = WasanbonUtil.getUrl(hostId, "binders", "packages");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		// リクエストの送信
		ResponseEntity<String> response = HttpUtil.sendHttpRequestAndGetResponse(url, HttpMethod.GET, entity, String.class);
		
		// 型の変換
		Map<String, List<Map<String, Object>>> tmpMap = null;
		List<String> responseList = new ArrayList<String>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			tmpMap = mapper.readValue(response.getBody(), new TypeReference<Map<String, List<Map<String, Object>>>>(){});
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		List<Map<String, Object>> packages = tmpMap.get("packages");
		for (int i = 0; i < packages.size(); i++) {
			responseList.add(packages.get(i).keySet().toString().replace("[", "").replace("]", ""));
		}
		return responseList;
	}
	
	/**
	 * Binderに定義されているRtcの一覧を取得する.
	 *
	 * @param hostId ホストID
	 * @return Rtcの一覧
	 */
	public static List<String> getRtcsListFromBinder(String hostId) {
		String url = WasanbonUtil.getUrl(hostId, "binders", "rtcs");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		// リクエストの送信
		ResponseEntity<String> response = HttpUtil.sendHttpRequestAndGetResponse(url, HttpMethod.GET, entity, String.class);

		// 型の変換
		Map<String, Object> tmpMap = null;
		List<String> responseList = new ArrayList<String>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			tmpMap = mapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>(){});
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		Set<String> rtcs = tmpMap.keySet();
		responseList = new ArrayList<>(rtcs);
		
		return responseList;
	}

	/**
	 * packageに組み込まれているRtcの一覧を取得する.
	 *
	 * @param packageName パッケージ名
	 * @return Rtcの一覧
	 */
	public static List<String> getRtcsListFromPackage(String packageName) {
		String hostId = Const.HOST_CONFIG.LOCALHOST_ID;
		String url = WasanbonUtil.getUrl(hostId, "dev", "packages", packageName, "rtcs");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		// リクエストの送信
		ResponseEntity<String> response = HttpUtil.sendHttpRequestAndGetResponse(url, HttpMethod.GET, entity, String.class);
		
		// 型の変換
		Map<String, List<Map<String, Map<String, Object>>>> tmpMap = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			tmpMap = mapper.readValue(response.getBody(), new TypeReference<Map<String, List<Map<String, Map<String, Object>>>>>(){});
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		List<Map<String, Map<String, Object>>> list = new ArrayList<>();
		list = tmpMap.get("rtcs");
		List<String> rtcs = new ArrayList<String>();
		
		for (int i = 0; i < list.size(); i++) {
			String rtc = list.get(i).toString().replaceAll("\\{", "").replaceAll("\\}", "").split("=")[0];
			rtcs.add(rtc);
		}

		return rtcs;
	}


	/**
	 * Packageを指定されたディレクトリにCloneする.
	 *
	 * @param packagesLocalDirPath バインダーのパッケージがクローンされているディレクトリ
	 * @param packageRepositoryName パッケージのリポジトリ名
	 */
	public static void clonePackageFromRepositoryToDirectory(String packagesLocalDirPath, String packageRepositoryName) {
		// Cloneする
		ProcessUtil.startProcessNoReturnWithWorkingDerectory(packagesLocalDirPath, "wasanbon-admin.py", "repository",
				"clone", packageRepositoryName);
	}
	
	/**
	 * PackageをdevディレクトリまたはexecディレクトリにCloneする.
	 *
	 * @param packageRepositoryName パッケージのリポジトリ名
	 */
	public static void clonePackageFromRepository(String packageRepositoryName) {
		String hostId = Const.HOST_CONFIG.LOCALHOST_ID;
		String url = WasanbonUtil.getUrl(hostId, "dev", "packages", packageRepositoryName, "git", "clone");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		HttpUtil.sendHttpRequestAndGetResponse(url, HttpMethod.POST, entity, String.class);

	}

	/**
	 * 新規Packageを作成する.
	 *
	 * @param packagesLocalDirPath バインダーのパッケージがクローンされているディレクトリ
	 * @param newPackageName パッケージ名
	 * @param hostId ホストID
	 */
	public static void createPackage(String packagesLocalDirPath, String newPackageName, String hostId) {
		String url = WasanbonUtil.getUrl(hostId, "dev", "packages");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		headers.setContentType(MediaType.APPLICATION_JSON);
		Map<String, String> requestBody = new HashMap<String, String>();
		requestBody.put("name", newPackageName);
		HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);
		// リクエストの送信
		HttpUtil.sendHttpRequestAndGetResponse(url, HttpMethod.POST, entity, String.class);
	}

	/**
	 * 指定されたPackageを削除する.
	 *
	 * @param packageRepositoryName パッケージのリポジトリ名
	 */
	public static void deletePackage(String packageRepositoryName) {
		String packageName = packageRepositoryName.replace("rts_", "");
		String hostId = Const.HOST_CONFIG.LOCALHOST_ID;
		String url = WasanbonUtil.getUrl(hostId, "dev", "packages", packageName);
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		HttpUtil.sendHttpRequestAndGetResponse(url, HttpMethod.DELETE, entity, String.class);
	}

	/**
	 * 指定されたPackageにRtcを組み込む.
	 *
	 * @param packageName パッケージ名
	 * @param rtcName コンポーネント名
	 * @return 実行結果
	 */
	public static String cloneRtcToPackage(String packageName, String rtcName) {
		String hostId = Const.HOST_CONFIG.LOCALHOST_ID;
		String url = WasanbonUtil.getUrl(hostId, "dev", "packages", packageName, "rtcs", rtcName, "git", "clone");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		ResponseEntity<String> response = HttpUtil.sendHttpRequestAndGetResponse(url, HttpMethod.POST, entity, String.class);
		
		return response.getBody();
	}

	/**
	 * 指定されたPackageにRtcに同期化する.
	 *
	 * @param packageName パッケージ名
	 */
	public static void syncRtcToPackage(String packageName) {
		String hostId = Const.HOST_CONFIG.LOCALHOST_ID;
		String url = WasanbonUtil.getUrl(hostId, "dev", "packages", packageName, "sync_rtc");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		HttpUtil.sendHttpRequestAndGetResponse(url, HttpMethod.POST, entity, String.class);

	}

	/**
	 * 指定されたPackageからRtcを削除する.
	 *
	 * @param packageName パッケージ名
	 * @param rtcName RTC名
	 */
	public static void deleteRtcFromPackage(String packageName, String rtcName) {
		String hostId = Const.HOST_CONFIG.LOCALHOST_ID;
		String url = WasanbonUtil.getUrl(hostId, "dev", "packages", packageName, "rtcs", rtcName);
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		HttpUtil.sendHttpRequestAndGetResponse(url, HttpMethod.DELETE, entity, String.class);

	}

	/********************************************************************
	 * ビルド・実行関連
	 ********************************************************************/
	/**
	 * 指定されたPackageのすべてのRtcをビルドする.
	 *
	 * @param buildRunDto ビルドに必要な情報
	 */
	public static void buildPackageAll(BuildRunDTO buildRunDto) {
		String hostId = buildRunDto.getHostId();
		String url = WasanbonUtil.getUrl(hostId, buildRunDto.getWs(), "packages", buildRunDto.getPackageName(), "build");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		HttpUtil.sendHttpRequestAndGetResponseAndWriteLog(url, HttpMethod.POST, entity, String.class, buildRunDto.getExecuteLogFilePath());
		
	}

	/**
	 * 指定されたPackageのすべてのRtcをCleanする.
	 *
	 * @param buildRunDto クリーンに必要な情報
	 */
	public static void cleanPackageAll(BuildRunDTO buildRunDto) {
		String hostId = buildRunDto.getHostId();
		String url = WasanbonUtil.getUrl(hostId, buildRunDto.getWs(), "packages", buildRunDto.getPackageName(), "clean");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		HttpUtil.sendHttpRequestAndGetResponseAndWriteLog(url, HttpMethod.POST, entity, String.class, buildRunDto.getExecuteLogFilePath());

	}

	/**
	 * 指定されたPackageのSystemを実行する.
	 *
	 * @param buildRunDto 実行に必要な情報
	 */
	public static void runSystem(BuildRunDTO buildRunDto) {
		String hostId = buildRunDto.getHostId();
		String url = WasanbonUtil.getUrl(hostId, buildRunDto.getWs(), "packages", buildRunDto.getPackageName(), "rts", "run");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		HttpEntity<String> entity = new HttpEntity<String>(headers);

		HttpUtil.sendHttpRequestAndGetResponseAndWriteLog(url, HttpMethod.POST, entity, String.class, buildRunDto.getExecuteLogFilePath());
		
	}
	
	/**
	 * スタートする.
	 *
	 * @param buildRunDto 実行に必要な情報
	 */
	public static void startRtcs(BuildRunDTO buildRunDto) {
		String hostId = buildRunDto.getHostId();
		String url = WasanbonUtil.getUrl(hostId, buildRunDto.getWs(), "packages", buildRunDto.getPackageName(), "rts", "start");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(headers);

		HttpUtil.sendHttpRequestAndGetResponseAndWriteLog(url, HttpMethod.POST, entity, String.class, buildRunDto.getExecuteLogFilePath());

	}
	
	/**
	 * コネクトする.
	 *
	 * @param buildRunDto 実行に必要な情報
	 */
	public static void connectPorts(BuildRunDTO buildRunDto) {
		String hostId = buildRunDto.getHostId();
		String url = WasanbonUtil.getUrl(hostId, buildRunDto.getWs(), "packages", buildRunDto.getPackageName(), "rts", "connect");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		HttpUtil.sendHttpRequestAndGetResponseAndWriteLog(url, HttpMethod.POST, entity, String.class, buildRunDto.getExecuteLogFilePath());

	}
	
	/**
	 * アクティベイトかディアクティベイトをする.
	 *
	 * @param buildRunDto 実行に必要な情報
	 * @param isActivate アクティベイトかどうか
	 */
	public static void activateOrDeactivateRtcs(BuildRunDTO buildRunDto, boolean isActivate) {
		String hostId = buildRunDto.getHostId();
		String url;
		if (isActivate) {
			url = WasanbonUtil.getUrl(hostId, buildRunDto.getWs(), "packages", buildRunDto.getPackageName(), "rts", "activate");
		} else {
			url = WasanbonUtil.getUrl(hostId, buildRunDto.getWs(), "packages", buildRunDto.getPackageName(), "rts", "deactivate");
		}
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		HttpUtil.sendHttpRequestAndGetResponseAndWriteLog(url, HttpMethod.POST, entity, String.class, buildRunDto.getExecuteLogFilePath());

	}

	/**
	 * 指定されたPackageのSystemを停止する.
	 *
	 * @param buildRunDto 実行に必要な情報
	 */
	public static void terminateSystem(BuildRunDTO buildRunDto) {
		String hostId = buildRunDto.getHostId();
		String url = WasanbonUtil.getUrl(hostId, buildRunDto.getWs(), "packages", buildRunDto.getPackageName(), "rts", "terminate");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		HttpUtil.sendHttpRequestAndGetResponseAndWriteLog(url, HttpMethod.POST, entity, String.class, buildRunDto.getExecuteLogFilePath());

	}

	/**
	 * Packageの実行状況を確認する.
	 *
	 * @param ws ワークスペース名
	 * @param packageName パッケージ名
	 * @param hostId ホストID
	 * @return Packageの実行状況
	 */
	public static Map<String, Object> isRunningPackage(String ws, String packageName, String hostId) {
		String url = WasanbonUtil.getUrl(hostId, ws, "packages", packageName, "rts", "status");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		ResponseEntity<String> response = HttpUtil.sendHttpRequestAndGetResponse(url, HttpMethod.GET, entity, String.class);
		
		// 型の変換
		Map<String, Object> responseMap = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			responseMap = mapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>(){});
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		logger.info(responseMap.toString());
		return responseMap;
	}
	
	/**
	 * Binderにパッケージを追加する.
	 *
	 * @param packageName パッケージ名
	 * @param binderName バインダー名
	 * @return 実行結果
	 */
	public static String addPackageToBinder(String packageName, String binderName) {
		String hostId = Const.HOST_CONFIG.LOCALHOST_ID;
		String url = WasanbonUtil.getUrl(hostId, "binders", binderName, "packages");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		Map<String, String> body = new HashMap<String, String>();
		body.put("ws", "dev");
		body.put("package_name", packageName);
		
		HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
		
		ResponseEntity<String> response = HttpUtil.sendHttpRequestAndGetResponse(url, HttpMethod.POST, entity, String.class);
		
		return response.getBody();
	}

	/**
	 * Binderのパッケージを更新する.
	 *
	 * @param ws ワークスペース名
	 * @param packageName パッケージ名
	 * @param binderName バインダー名
	 * @return 実行結果
	 */
	public static String updatePackageToBinder(String ws, String packageName, String binderName) {
		String hostId = Const.HOST_CONFIG.LOCALHOST_ID;
		String url = WasanbonUtil.getUrl(hostId, "binders", binderName, "packages", ws, packageName);
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		ResponseEntity<String> response = HttpUtil.sendHttpRequestAndGetResponse(url, HttpMethod.PUT, entity, String.class);
		
		return response.getBody();
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
	public static String addRtcToBinder(String ws, String packageName, String rtcName, String binderName) {
		String hostId = Const.HOST_CONFIG.LOCALHOST_ID;
		String url = WasanbonUtil.getUrl(hostId, "binders", binderName, "rtcs");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		Map<String, String> body = new HashMap<String, String>();
		body.put("ws", ws);
		body.put("package_name", packageName);
		body.put("rtc_name", rtcName);
		
		HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
		
		ResponseEntity<String> response = HttpUtil.sendHttpRequestAndGetResponse(url, HttpMethod.POST, entity, String.class);
		
		return response.getBody();
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
	public static String updateRtcToBinder(String ws, String packageName, String rtcName, String binderName) {
		String hostId = Const.HOST_CONFIG.LOCALHOST_ID;
		String url = WasanbonUtil.getUrl(hostId, "binders", binderName, "rtcs", ws, packageName, rtcName);
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		ResponseEntity<String> response = HttpUtil.sendHttpRequestAndGetResponse(url, HttpMethod.PUT, entity, String.class);
		
		return response.getBody();
	}

	/**
	 * Binderをcommitする.
	 *
	 * @param binderName バインダー名
	 * @param comment コメント
	 * @return 実行結果
	 */
	public static String commitBinder(String binderName, String comment) {
		String hostId = Const.HOST_CONFIG.LOCALHOST_ID;
		String url = WasanbonUtil.getUrl(hostId, "binders", binderName, "commit");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		Map<String, String> body = new HashMap<String, String>();
		body.put("comment", comment);
		
		HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
		
		ResponseEntity<String> response = HttpUtil.sendHttpRequestAndGetResponse(url, HttpMethod.POST, entity, String.class);
		
		return response.getBody();
	}
	
	/**
	 * Binderをpushする.
	 *
	 * @param binderName バインダー名
	 * @param comment コメント
	 * @return 実行結果
	 */
	public static String pushBinder(String binderName, String comment) {
		String hostId = Const.HOST_CONFIG.LOCALHOST_ID;
		String url = WasanbonUtil.getUrl(hostId, "binders", binderName, "push");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		Map<String, String> body = new HashMap<String, String>();
		body.put("comment", comment);
		
		HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
		
		ResponseEntity<String> response = HttpUtil.sendHttpRequestAndGetResponse(url, HttpMethod.POST, entity, String.class);
		
		return response.getBody();
	}
	
	/**
	 * nameserverを起動する.
	 *
	 * @param hostId ホストID
	 */
	public static void startNameserver(String hostId) {
		//hostIdからホストを取得する
		HostSettingYaml host = YamlUtil.getHostFromHostId(hostId);
		MultiValueMap<String, String> qparams = new LinkedMultiValueMap<String, String>();
		qparams.add("port", host.getNsport());
		String url = WasanbonUtil.getUrlWithQueryParams(hostId, qparams, "nameserver", "start");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		
		HttpEntity<String> entity = new HttpEntity<String>(headers);

		HttpUtil.sendHttpRequestAndGetResponse(url, HttpMethod.POST, entity, String.class);

	}
	
	/**
	 * nameserverのstatusを確認する.
	 *
	 * @param hostId ホストID
	 * @return nameserverのstatus
	 */
	public static boolean checkNameserverStatus(String hostId)  {
		//hostIdからホストを取得する
		HostSettingYaml host = YamlUtil.getHostFromHostId(hostId);
		MultiValueMap<String, String> qparams = new LinkedMultiValueMap<String, String>();
		qparams.add("port", host.getNsport());
		String url = WasanbonUtil.getUrlWithQueryParams(hostId, qparams, "nameserver");
		HttpHeaders headers = WasanbonUtil.getDefaultHeader(hostId);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		ResponseEntity<String> response = HttpUtil.sendHttpRequestAndGetResponse(url, HttpMethod.GET, entity, String.class);
		
		if (response.getBody().contains("Not")) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Wasanbon WebframeworkのURLを取得する.
	 * 
	 * @param hostId ホストID
	 * @param path   URLパス
	 * @return URL
	 * @throws RuntimeException
	 */
	public static String getUrl(String hostId, String... path) throws RuntimeException {
		HostSettingYaml host = YamlUtil.getHostFromHostId(hostId);
		if (host == null) {
			throw new RuntimeException("Host definition not found");
		}
		return UriComponentsBuilder.newInstance()
				.scheme("http")
				.host(host.getIp())
				.port(host.getWwport())
				.pathSegment(path)
				.build()
				.encode()
				.toUriString();
	}

	/**
	 * Wasanbon WebframeworkのURLを取得する.
	 * 
	 * @param hostId      ホストID
	 * @param queryParams クエリパラメータ
	 * @param path        URLパス
	 * @return URL
	 * @throws RuntimeException
	 */
	public static String getUrlWithQueryParams(String hostId, MultiValueMap<String, String> queryParams, String... path)
			throws RuntimeException {
		HostSettingYaml host = YamlUtil.getHostFromHostId(hostId);
		if (host == null) {
			throw new RuntimeException("Host definition not found");
		}
		return UriComponentsBuilder.newInstance()
				.scheme("http")
				.host(host.getIp())
				.port(host.getWwport())
				.queryParams(queryParams)
				.pathSegment(path)
				.build()
				.encode()
				.toUriString();
	}

	/**
	 * Wasanbon Webframeworkにアクセスするためのデフォルトヘッダを取得する.
	 * 
	 * @param hostId
	 * @return HTTPヘッダ
	 * @throws RuntimeException
	 */
	public static HttpHeaders getDefaultHeader(String hostId) throws RuntimeException {
		HostSettingYaml host = YamlUtil.getHostFromHostId(hostId);
		if (host == null) {
			throw new RuntimeException("Host definition not found");
		}
		HttpHeaders headers = HttpUtil.createBasicAuthenticationHeader(host.getId(), host.getPassword());
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}
}
