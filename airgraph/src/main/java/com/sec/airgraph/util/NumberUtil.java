package com.sec.airgraph.util;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * 数値関連Utility
 * 
 * @author Tsuyoshi Hirose
 *
 */
public class NumberUtil {

	/**
	 * 指定数値が空かを判定する
	 * 
	 * @param target
	 * @return
	 */
	public static boolean isEmpty(Integer target) {
		return target == null;
	}

	/**
	 * 指定数値が空でないかを判定する
	 * 
	 * @param target
	 * @return
	 */
	public static boolean isNotEmpty(Integer target) {
		return target != null;
	}

	/**
	 * 指定数値が空かを判定する
	 * 
	 * @param target
	 * @return
	 */
	public static boolean isEmpty(Long target) {
		return target == null;
	}

	/**
	 * 指定数値が空でないかを判定する
	 * 
	 * @param target
	 * @return
	 */
	public static boolean isNotEmpty(Long target) {
		return target != null;
	}

	/**
	 * 指定数値が一致しているかを判定する
	 * 
	 * @param i1
	 * @param i2
	 * @return
	 */
	public static boolean equals(Integer i1, Integer i2) {
		if (isEmpty(i1) && isEmpty(i1)) {
			return true;
		} else if (isNotEmpty(i1) && isNotEmpty(i2)) {
			return i1 == i2;
		} else {
			return false;
		}
	}

	/**
	 * 指定数値が一致しているかを判定する
	 * 
	 * @param i1
	 * @param i2
	 * @return
	 */
	public static boolean equals(Long l1, Long l2) {
		if (isEmpty(l1) && isEmpty(l2)) {
			return true;
		} else if (isNotEmpty(l1) && isNotEmpty(l2)) {
			return l1 == l2;
		} else {
			return false;
		}
	}

	/**
	 * 文字列数字をint型に変換する
	 * 
	 * @param str
	 * @return
	 */
	public static int toInt(String str) {
		return NumberUtils.createInteger(str);
	}

	/**
	 * 文字列数字をlong型に変換する
	 * 
	 * @param str
	 * @return
	 */
	public static long toLong(String str) {
		return NumberUtils.createLong(str);
	}
}
