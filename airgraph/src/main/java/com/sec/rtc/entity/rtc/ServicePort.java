package com.sec.rtc.entity.rtc;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.sec.airgraph.util.CollectionUtil;
import com.sec.airgraph.util.NumberUtil;
import com.sec.airgraph.util.StringUtil;
import com.sec.airgraph.util.Const.RT_COMPONENT.DOCUMENT_TYPE;
import com.sec.airgraph.util.Const.RT_COMPONENT.INTERFACE_DIRECTION;
import com.sec.airgraph.util.Const.RT_COMPONENT.PORT_POSITION;

import lombok.Data;

/**
 * サービスポート設定
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ServicePort {

	/**
	 * 種別
	 */
	@XmlAttribute(name = "type", namespace = "http://www.w3.org/2001/XMLSchema-instance")
	private final String type = "rtcExt:serviceport_ext";

	/**
	 * ポート名称
	 */
	@XmlAttribute(name = "name", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String name = "";

	/**
	 * 表示位置
	 */
	@XmlAttribute(name = "position", namespace = "http://www.openrtp.org/namespaces/rtc_ext")
	private String position = PORT_POSITION.LEFT;
	
	/**
	 * サービスインタフェース設定
	 */
	@XmlElement(name = "ServiceInterface", namespace = "http://www.openrtp.org/namespaces/rtc")
	private List<ServiceInterface> serviceInterfaces = new ArrayList<ServiceInterface>();

	/**
	 * ドキュメント
	 */
	@XmlElement(name = "Doc", namespace = "http://www.openrtp.org/namespaces/rtc_doc")
	private Doc doc = new Doc(DOCUMENT_TYPE.SERVICE_PORT_VAL);

	/****************************************
	 * IDE独自プロパティ
	 ****************************************/
	/**
	 * Providedのサービスインタフェース設定
	 */
	public List<ServiceInterface> getProvidedServiceInterfaces() {
		return CollectionUtil.filter(this.serviceInterfaces,
				sInterface -> INTERFACE_DIRECTION.PROVIDED.equals(sInterface.getDirection()));
	}

	/**
	 * Requiredのサービスインタフェース設定
	 */
	public List<ServiceInterface> getRequiredServiceInterfaces() {
		return CollectionUtil.filter(this.serviceInterfaces,
				sInterface -> INTERFACE_DIRECTION.REQUIRED.equals(sInterface.getDirection()));
	}
	
	/**
	 * サービスポート宣言が一致するかを判定する
	 * 
	 * @param target
	 * @return
	 */
	public boolean equalsServiceportDeclare(ServicePort target) {
		boolean result = true;
		result &= StringUtil.equals(this.name, target.getName());
		return result;
	}
	
	/**
	 * Providedのサービスインタフェース宣言が一致するかを判定する
	 * 
	 * @param target
	 * @return
	 */
	public boolean equalsProvidedIntefaceDeclare(ServicePort target) {
		List<ServiceInterface> orgList = this.getProvidedServiceInterfaces();
		List<ServiceInterface> newList = target.getProvidedServiceInterfaces();
		return equalsInterfaceDeclare(orgList, newList);
	}
	
	/**
	 * Requiredのサービスインタフェースの宣言が一致するかを判定する
	 * 
	 * @param target
	 * @return
	 */
	public boolean equalsRequiredIntefaceDeclare(ServicePort target) {
		List<ServiceInterface> orgList = this.getRequiredServiceInterfaces();
		List<ServiceInterface> newList = target.getRequiredServiceInterfaces();
		return equalsInterfaceDeclare(orgList, newList);
	}

	/**
	 * サービスインタフェース宣言が一致するかを判定する
	 * 
	 * @param orgList
	 * @param newList
	 * @return
	 */
	private boolean equalsInterfaceDeclare(List<ServiceInterface> orgList, List<ServiceInterface> newList) {
		boolean result = true;
		if (CollectionUtil.isNotEmpty(orgList) && CollectionUtil.isNotEmpty(newList)) {
			// 両方存在する
			if (NumberUtil.equals(orgList.size(), newList.size())) {
				// 件数が等しい
				for (int i = 0; i < orgList.size(); i++) {
					ServiceInterface orgInterface = orgList.get(i);
					ServiceInterface newInterface = newList.get(i);
					result &= orgInterface.equalsServiceInterfaceDeclare(newInterface);
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
