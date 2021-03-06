package com.sec.airgraph.util;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 文字列操作関連Utility
 * 
 * @author Tsuyoshi Hirose
 *
 */
public class StringUtil {

	/**
	 * 指定文字列がNullまたは空かを判定する
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return StringUtils.isEmpty(str);
	}

	/**
	 * 指定文字列がNullまたは空でないかを判定する
	 * 
	 * @param strpath2
	 * @return
	 */
	public static boolean isNotEmpty(String str) {
		return !StringUtils.isEmpty(str);
	}

	/**
	 * 指定可変長文字列がNullまたは空かを判定する
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String... strings) {
		return ArrayUtils.isEmpty(strings);
	}

	/**
	 * 指定可変長文字列がNullまたは空でないかを判定する
	 * 
	 * @param strpath2
	 * @return
	 */
	public static boolean isNotEmpty(String... strings) {
		return !ArrayUtils.isEmpty(strings);
	}

	/**
	 * 指定文字列が一致しているかを判定する<br>
	 * null,空文字は同じものとみなす
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static boolean equals(String str1, String str2) {
		if (isEmpty(str1) && isEmpty(str2)) {
			return true;
		} else if (isNotEmpty(str1) && isNotEmpty(str2)) {
			return str1.equals(str2);
		} else {
			return false;
		}
	}

	/**
	 * 文字列を結合する
	 * 
	 * @param separetor
	 * @param strings
	 * @return
	 */
	public static String concatenate(String separetor, String... strings) {
		if (isNotEmpty(strings)) {
			return Arrays.stream(strings).collect(Collectors.joining(separetor));
		}
		return null;
	}

	/**
	 * ブラウザ上のモデル名称からPackage名称を取得する
	 * 
	 * @param modelName
	 */
	public static String getPackageNameFromModelName(String modelName) {
		String packageName = modelName;
		if (isNotEmpty(packageName)) {
			if (packageName.contains("rts_")) {
				packageName = packageName.replace("rts_", "");
			}
		}
		return packageName;
	}
}
