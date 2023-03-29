package com.sec.airgraph.util;

import java.sql.Timestamp;
import java.util.Base64;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

/**
 * Http関連Utility.
 *
 * @author Tatsuya Ide
 *
 */
public class HttpUtil {

	/**
	 * logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

	/**
	 * REST APIアクセスに利用する RestTemplate を取得する.
	 * 
	 * @return RestTemplate
	 */
	private static RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		final HttpClient httpClient = HttpClientBuilder.create()
				.setRedirectStrategy(new LaxRedirectStrategy())
				.build();
		factory.setHttpClient(httpClient);
		restTemplate.setRequestFactory(factory);
		return restTemplate;
	}

	/**
	 * Httpリクエストを送信し、レスポンスを受け取る.
	 *
	 * @param <T>           指定した型
	 * @param url           APIのURL
	 * @param httpMethod    HTTPメソッド
	 * @param requestEntity HTTPエンティティ
	 * @param responseType  レスポンスタイプ
	 * @return レスポンス
	 */
	public static <T> ResponseEntity<T> sendHttpRequestAndGetResponse(String url, HttpMethod httpMethod,
			HttpEntity<?> requestEntity, Class<T> responseType) {
		try {
			// リクエストの送信
			RestTemplate restTemplate = getRestTemplate();
			ResponseEntity<T> response = (ResponseEntity<T>) restTemplate.exchange(url, httpMethod, requestEntity,
					responseType);
			logger.info(
					httpMethod + " " + url + " [status: " + response.getStatusCode() + "] body: " + response.getBody());
			return response;
		} catch (HttpClientErrorException e) {
			// 4xx系のエラー
			logger.error(httpMethod + " " + url + " " + e.getMessage(), e);
			if (e.getResponseBodyAsString() != null) {
				logger.error(e.getResponseBodyAsString());
			}
		} catch (HttpServerErrorException e) {
			// 5xx系のエラー
			logger.error(httpMethod + " " + url + " " + e.getMessage(), e);
			if (e.getResponseBodyAsString() != null) {
				logger.error(e.getResponseBodyAsString());
			}
		} catch (Exception e) {
			// その他のエラー
			logger.error(httpMethod + " " + url + " " + e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 例外ログをwasanbon.logに出力するためのヘルパー.
	 * 
	 * @param sb             StringBuilder
	 * @param e              例外
	 * @param additionalText 追加テキスト
	 */
	private static void appendExeptionTextForWriteLog(StringBuilder sb, Exception e,
			String... additionalText) {
		sb.append(e.getMessage());
		sb.append("\n");
		if (e instanceof HttpStatusCodeException && ((HttpStatusCodeException) e).getResponseBodyAsString() != null) {
			sb.append(((HttpStatusCodeException) e).getResponseBodyAsString().replace("\\n", "\n"));
		}
		for (String s : additionalText) {
			sb.append(s);
		}
	}

	/**
	 * Httpリクエストを送信し、レスポンスを受け取る。レスポンスはログファイルに書き込む.
	 *
	 * @param <T>           指定した型
	 * @param url           APIのURL
	 * @param httpMethod    HTTPメソッド
	 * @param requestEntity HTTPエンティティ
	 * @param responseType  レスポンスタイプ
	 * @param logFilePath   ログファイルパス
	 * @return レスポンス
	 */
	public static <T> ResponseEntity<T> sendHttpRequestAndGetResponseAndWriteLog(String url, HttpMethod httpMethod,
			HttpEntity<?> requestEntity, Class<T> responseType, String logFilePath) {
		StringBuilder sb = new StringBuilder();
		try {
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			sb.append("# ");
			sb.append(timestamp.toString());
			sb.append("################################################################################\n");
			sb.append(httpMethod + " " + url + "\n");
			// リクエストの送信
			RestTemplate restTemplate = getRestTemplate();
			ResponseEntity<T> response = (ResponseEntity<T>) restTemplate.exchange(url, httpMethod, requestEntity,
					responseType);
			logger.info(
					httpMethod + " " + url + " [status: " + response.getStatusCode() + "] body: " + response.getBody());
			sb.append(" [status: " + response.getStatusCode() + "]");
			if (response.getBody() != null) {
				sb.append(" body: " + response.getBody().toString().replace("\\n", "\n"));
			}
			return response;
		} catch (HttpClientErrorException e) {
			// 4xx系のエラー
			logger.error(httpMethod + " " + url + " " + e.getMessage(), e);
			if (e.getResponseBodyAsString() != null) {
				logger.error(e.getResponseBodyAsString());
			}
			appendExeptionTextForWriteLog(sb, e,
					"\n\nCheck whether wasanbon-webframework is running.  \nOr check url of your request.");
		} catch (HttpServerErrorException e) {
			// 5xx系のエラー
			logger.error(httpMethod + " " + url + " " + e.getMessage(), e);
			if (e.getResponseBodyAsString() != null) {
				logger.error(e.getResponseBodyAsString());
			}
			appendExeptionTextForWriteLog(sb, e,
					"\n\nThis may be wasanbon level error.  \nRethink your code or check airgraph.log.",
					"\n\nairgraph.log File Path: " + PropUtil.getValue("airgraph.log.path"));
		} catch (Exception e) {
			// その他のエラー
			logger.error(httpMethod + " " + url + " " + e.getMessage(), e);
			appendExeptionTextForWriteLog(sb, e, "\n\nThis may be AirGraph Error. check airgraph.log.",
					"\n\nairgraph.log File Path: " + PropUtil.getValue("airgraph.log.path"));
		} finally {
			sb.append("\n");
			FileUtil.writeAll(logFilePath, sb.toString(), false);
		}

		return null;
	}

	/**
	 * ユーザー名・パスワードを受けとり、basic認証に対応したヘッダーを作成する.
	 *
	 * @param hostId   ホストID
	 * @param password パスワード
	 * @return basic認証に対応したヘッダー
	 */
	public static HttpHeaders createBasicAuthenticationHeader(String hostId, String password) {

		HttpHeaders httpheader = new HttpHeaders();

		String userPass = hostId + ":" + password;
		String encoded = new String(Base64.getEncoder().encode(userPass.getBytes()));

		StringBuilder basicAuth = new StringBuilder();
		basicAuth.append("Basic ").append(encoded);
		httpheader.add("Authorization", basicAuth.toString());
		return httpheader;
	}

}
