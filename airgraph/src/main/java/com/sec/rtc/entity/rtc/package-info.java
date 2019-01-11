@XmlSchema(
		xmlns = {
				@XmlNs(namespaceURI = "http://www.openrtp.org/namespaces/rtc", prefix = "rtc"),
				@XmlNs(namespaceURI = "http://www.openrtp.org/namespaces/rtc_doc", prefix = "rtcDoc"),
				@XmlNs(namespaceURI = "http://www.openrtp.org/namespaces/rtc_ext", prefix = "rtcExt"),
				@XmlNs(namespaceURI = "http://www.w3.org/2001/XMLSchema-instance", prefix = "xsi"),
				@XmlNs(namespaceURI = "http://www.sec.co.jp/namespaces/ide_ext", prefix = "ideExt")
				},
		elementFormDefault = XmlNsForm.QUALIFIED
)
package com.sec.rtc.entity.rtc;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;