package com.sec.rtc.entity.rtc;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.sec.airgraph.util.CollectionUtil;
import com.sec.airgraph.util.NumberUtil;

import lombok.Data;

/**
 * コンフィギュレーション設定
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ConfigurationSet {

	/**
	 * コンフィギュレーション設定
	 */
	@XmlElement(name = "Configuration", namespace = "http://www.openrtp.org/namespaces/rtc")
	private List<Configuration> configurations = new ArrayList<Configuration>();

	/****************************************
	 * IDE独自プロパティ
	 ****************************************/
	
	/**
	 * コンフィギュレーション設定が全て一致するかを判定する
	 * 
	 * @param target
	 * @return
	 */
	public boolean equalsConfigurations(ConfigurationSet target) {
		List<Configuration> orgList = this.configurations;
		List<Configuration> newList = target.getConfigurations();
		boolean result = true;
		if (CollectionUtil.isNotEmpty(orgList) && CollectionUtil.isNotEmpty(newList)) {
			// 両方存在する
			if (NumberUtil.equals(orgList.size(), newList.size())) {
				for (int i = 0; i < orgList.size(); i++) {
					Configuration orgConfig = orgList.get(i);
					Configuration newConfig = newList.get(i);
					result &= orgConfig.equalsConfiguration(newConfig);
				}
			} else {
				// 件数が異なる
				result = false;
			}
		} else if (CollectionUtil.isEmpty(orgList) && CollectionUtil.isEmpty(newList)) {
			// 両方存在しない
			result = true;
		} else {
			// どちらかが存在しない
			result = false;
		}

		return result;
	}
}
