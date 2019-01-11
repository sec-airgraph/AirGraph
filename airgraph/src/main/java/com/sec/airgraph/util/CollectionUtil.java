package com.sec.airgraph.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

/**
 * List,Array,Map関連Utility
 * 
 * @author Tsuyoshi Hirose
 *
 */
public class CollectionUtil {

	/**
	 * ListがNullまはた空かを判定する
	 * 
	 * @param list
	 * @return
	 */
	public static boolean isEmpty(List<?> list) {
		return CollectionUtils.isEmpty(list);
	}

	/**
	 * ListがNullまはた空でないかを判定する
	 * 
	 * @param list
	 * @return
	 */
	public static boolean isNotEmpty(List<?> list) {
		return CollectionUtils.isNotEmpty(list);
	}

	/**
	 * 配列がNullまはた空かを判定する
	 * 
	 * @param list
	 * @return
	 */
	public static boolean isEmpty(Object[] array) {
		return ArrayUtils.isEmpty(array);
	}

	/**
	 * 配列がNullまはた空かを判定する
	 * 
	 * @param list
	 * @return
	 */
	public static boolean isNotEmpty(Object[] array) {
		return ArrayUtils.isNotEmpty(array);
	}

	/**
	 * 配列がNullまはた空かを判定する
	 * 
	 * @param list
	 * @return
	 */
	public static boolean isEmpty(byte[] array) {
		return ArrayUtils.isEmpty(array);
	}

	/**
	 * 配列がNullまはた空かを判定する
	 * 
	 * @param list
	 * @return
	 */
	public static boolean isNotEmpty(byte[] array) {
		return ArrayUtils.isNotEmpty(array);
	}

	/**
	 * MapがNullまはた空かを判定する
	 * 
	 * @param map
	 * @return
	 */
	public static boolean isEmpty(Map<?, ?> map) {
		return MapUtils.isEmpty(map);
	}

	/**
	 * MapがNullまはた空でないを判定する
	 * 
	 * @param map
	 * @return
	 */
	public static boolean isNotEmpty(Map<?, ?> map) {
		return MapUtils.isNotEmpty(map);
	}

	/**
	 * CollectionがNullまはた空かを判定する
	 * 
	 * @param colelction
	 * @return
	 */
	public static boolean isEmpty(Collection<?> colelction) {
		return CollectionUtils.isEmpty(colelction);
	}

	/**
	 * CollectionがNullまはた空でないかを判定する
	 * 
	 * @param colelction
	 * @return
	 */
	public static boolean isNotEmpty(Collection<?> colelction) {
		return CollectionUtils.isNotEmpty(colelction);
	}

	/**
	 * 配列をリストに変換する
	 * 
	 * @param array
	 * @return
	 */
	public static <T> List<T> toList(T[] array) {
		if (isNotEmpty(array)) {
			return Arrays.stream(array).collect(Collectors.toList());
		}
		return null;
	}

	/**
	 * マップをリストに変換する
	 * 
	 * @param list
	 * @param classifier
	 * @return
	 */
	public static <T> List<T> toList(Map<?, List<T>> map) {
		List<T> result = new ArrayList<T>();
		if (isNotEmpty(map)) {
			result = map.values().stream().flatMap(list -> list.stream()).collect(Collectors.toList());
		}
		return result;
	}

	/**
	 * リストをマップに変換する
	 * 
	 * @param list
	 * @param classifier
	 * @return
	 */
	public static <T, K> Map<K, List<T>> toMap(List<T> list, Function<? super T, ? extends K> classifier) {
		Map<K, List<T>> result = new HashMap<>();
		if (isNotEmpty(list)) {
			result = list.stream().collect(Collectors.groupingBy(classifier));
		}
		return result;
	}

	/**
	 * ソートする
	 * 
	 * @param list
	 * @param keyExtractor
	 * @return
	 */
	public static <T, U extends Comparable<? super U>> List<T> sort(List<T> list,
			Function<? super T, ? extends U> keyExtractor) {
		List<T> result = new ArrayList<T>();
		if (isNotEmpty(list)) {
			result = list.stream().sorted(Comparator.comparing(keyExtractor)).collect(Collectors.toList());
		}
		return result;
	}

	/**
	 * 逆順ソートする
	 * 
	 * @param list
	 * @param keyExtractor
	 * @return
	 */
	public static <T, U extends Comparable<? super U>> List<T> reversedSort(List<T> list,
			Function<? super T, ? extends U> keyExtractor) {
		List<T> result = new ArrayList<T>();
		if (isNotEmpty(list)) {
			result = list.stream().sorted(Comparator.comparing(keyExtractor).reversed()).collect(Collectors.toList());
		}
		return result;
	}

	/**
	 * フィルタする
	 * 
	 * @param list
	 * @param predicate
	 * @return
	 */
	public static <T> List<T> filter(List<T> list, Predicate<? super T> predicate) {
		List<T> result = new ArrayList<T>();
		if (isNotEmpty(list)) {
			result = list.stream().filter(predicate).collect(Collectors.toList());
		}
		return result;
	}
}
