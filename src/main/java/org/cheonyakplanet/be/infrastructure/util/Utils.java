package org.cheonyakplanet.be.infrastructure.util;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Component
public class Utils {

	private String getNodeValue(Node node, String tag) {
		NodeList nl = ((org.w3c.dom.Element)node).getElementsByTagName(tag);
		if (nl.getLength() > 0)
			return nl.item(0).getTextContent().trim();
		return null;
	}

	public Integer getInt(Node node, String tag) {
		String v = getNodeValue(node, tag);
		try {
			return v != null ? Integer.parseInt(v) : null;
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public BigDecimal getBigDecimal(Node node, String tag) {
		String v = getNodeValue(node, tag);
		try {
			return v != null ? new BigDecimal(v.replace(",", "")) : null;
		} catch (Exception e) {
			return null;
		}
	}

	public Date formatDate(int y, int m, int d) {
		return Date.from(
			java.time.LocalDate.of(y, m, d).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
	}
}