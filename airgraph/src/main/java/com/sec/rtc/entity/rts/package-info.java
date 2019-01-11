@XmlSchema(
		xmlns = {
				@XmlNs(namespaceURI = "http://www.openrtp.org/namespaces/rts", prefix = "rts"),
				@XmlNs(namespaceURI = "http://www.openrtp.org/namespaces/rts_ext", prefix = "rtsExt"),
				@XmlNs(namespaceURI = "http://www.w3.org/2001/XMLSchema-instance", prefix = "xsi"),
				},
		elementFormDefault = XmlNsForm.QUALIFIED
)
package com.sec.rtc.entity.rts;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;