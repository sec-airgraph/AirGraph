package com.sec.rtc.entity.rtc;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

/**
 * 環境設定
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Target {

	/**
	 * Version
	 */
	@XmlAttribute(name = "langVersion", namespace = "http://www.openrtp.org/namespaces/rtc_ext")
	private String langVersion = "";
	
	/**
	 * OS
	 */
	@XmlAttribute(name = "os", namespace = "http://www.openrtp.org/namespaces/rtc_ext")
	private String os = "";
	
	/**
	 * その他OS情報
	 */
	@XmlAttribute(name = "other", namespace = "http://www.openrtp.org/namespaces/rtc_ext")
	private String other = "";
	
	/**
	 * その他CPU情報
	 */
	@XmlAttribute(name = "cpuOther", namespace = "http://www.openrtp.org/namespaces/rtc_ext")
	private String cpuOther = "";
	
	/**
	 * 詳細情報-OSVersion
	 */
	@XmlElement(name = "osVersions", namespace = "http://www.openrtp.org/namespaces/rtc_ext")
	private List<String> osVersions = new ArrayList<String>();
	
	/**
	 * 詳細情報-CPU
	 */
	@XmlElement(name = "cpus", namespace = "http://www.openrtp.org/namespaces/rtc_ext")
	private List<String> cpus = new ArrayList<String>();
	
	/**
	 * ライブラリ情報
	 */
	@XmlElement(name = "libraries", namespace = "http://www.openrtp.org/namespaces/rtc_ext")
	private List<Library> libraries = new ArrayList<Library>();
}
