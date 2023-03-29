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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;


/**
 * List,Array,Map関連Utility.
 *
 * @author Tsuyoshi Hirose
 *
 */
public class CollectionUtil {

	/**
	 * ListがNullまはた空かを判定する.
	 *
	 * @param list リスト 
	 * @return ListがNullまはた空かどうか
	 */
	public static boolean isEmpty(List<?> list) {
		return CollectionUtils.isEmpty(list);
	}
	
	/**
	 * 配列がNullまはた空かを判定する.
	 *
	 * @param array 配列
	 * @return 配列がNullまはた空かどうか
	 */
	public static boolean isEmpty(Object[] array) {
		return ArrayUtils.isEmpty(array);
	}
	
	/**
	 * 配列がNullまはた空かを判定する.
	 *
	 * @param array 配列
	 * @return 配列がNullまはた空かどうか
	 */
	public static boolean isEmpty(byte[] array) {
		return ArrayUtils.isEmpty(array);
	}

	/**
	 * MapがNullまはた空かを判定する.
	 *
	 * @param map マップ
	 * @return MapがNullまはた空かどうか
	 */
	public static boolean isEmpty(Map<?, ?> map) {
		return MapUtils.isEmpty(map);
	}
	
	/**
	 * CollectionがNullまはた空かを判定する.
	 *
	 * @param colelction コレクション
	 * @return CollectionがNullまはた空かどうか
	 */
	public static boolean isEmpty(Collection<?> colelction) {
		return CollectionUtils.isEmpty(colelction);
	}
	
	/**
	 * ListがNullまはた空でないかを判定する.
	 *
	 * @param list リスト
	 * @return ListがNullまはた空かどうか
	 */
	public static boolean isNotEmpty(List<?> list) {
		return CollectionUtils.isNotEmpty(list);
	}

	/**
	 * 配列がNullまはた空かを判定する.
	 *
	 * @param array 配列
	 * @return 配列がNullまはた空かどうか
	 */
	public static boolean isNotEmpty(Object[] array) {
		return ArrayUtils.isNotEmpty(array);
	}

	/**
	 * 配列がNullまはた空かを判定する.
	 *
	 * @param array 配列
	 * @return 配列がNullまはた空かどうか
	 */
	public static boolean isNotEmpty(byte[] array) {
		return ArrayUtils.isNotEmpty(array);
	}

	/**
	 * MapがNullまはた空でないを判定する.
	 *
	 * @param map マップ
	 * @return MapがNullまはた空かどうか
	 */
	public static boolean isNotEmpty(Map<?, ?> map) {
		return MapUtils.isNotEmpty(map);
	}

	/**
	 * CollectionがNullまはた空でないかを判定する.
	 *
	 * @param colelction コレクション
	 * @return CollectionがNullまはた空かどうか
	 */
	public static boolean isNotEmpty(Collection<?> colelction) {
		return CollectionUtils.isNotEmpty(colelction);
	}

	/**
	 * 配列をリストに変換する.
	 *
	 * @param <T> ジェネリクス
	 * @param array 配列
	 * @return 変換されたリスト
	 */
	public static <T> List<T> toList(T[] array) {
		if (isNotEmpty(array)) {
			return Arrays.stream(array).collect(Collectors.toList());
		}
		return null;
	}

	/**
	 * マップをリストに変換する.
	 *
	 * @param <T> ジェネリクス
	 * @param map マップ
	 * @return 変換されたマップ
	 */
	public static <T> List<T> toList(Map<?, List<T>> map) {
		List<T> result = new ArrayList<T>();
		if (isNotEmpty(map)) {
			result = map.values().stream().flatMap(list -> list.stream()).collect(Collectors.toList());
		}
		return result;
	}

	/**
	 * リストをマップに変換する.
	 *
	 * @param <T> ジェネリクス
	 * @param <K> ジェネリクス
	 * @param list リスト
	 * @param classifier classifier
	 * @return 変換されたマップ
	 */
	public static <T, K> Map<K, List<T>> toMap(List<T> list, Function<? super T, ? extends K> classifier) {
		Map<K, List<T>> result = new HashMap<>();
		if (isNotEmpty(list)) {
			result = list.stream().collect(Collectors.groupingBy(classifier));
		}
		return result;
	}

	/**
	 * ソートする.
	 *
	 * @param <T> ジェネリクス
	 * @param <U> ジェネリクス
	 * @param list リスト
	 * @param keyExtractor keyExtractor
	 * @return 実行結果
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
	 * 逆順ソートする.
	 *
	 * @param <T> ジェネリクス
	 * @param <U> ジェネリクス
	 * @param list リスト
	 * @param keyExtractor keyExtractor
	 * @return 実行結果
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
	 * フィルタする.
	 *
	 * @param <T> ジェネリクス
	 * @param list リスト
	 * @param predicate predicate
	 * @return 実行結果
	 */
	public static <T> List<T> filter(List<T> list, Predicate<? super T> predicate) {
		List<T> result = new ArrayList<T>();
		if (isNotEmpty(list)) {
			result = list.stream().filter(predicate).collect(Collectors.toList());
		}
		return result;
	}
}
