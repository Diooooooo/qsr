package com.qsr.sdk.util;

import com.qsr.sdk.exception.PaymentException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class XmlUtil {

    public static Map<String, String> xml2map(String xmlContent) throws PaymentException {
        Map<String, String> result = new HashMap<String, String>();

        SAXReader reader = new SAXReader();
        try {
            Document doc = reader.read(new StringReader(xmlContent));
            Element root = doc.getRootElement();
            for (Iterator<?> i = root.elementIterator(); i.hasNext();) {
                Element el = (Element) i.next();
                String name = el.getName();

                String text = el.getTextTrim();
                result.put(name, text);
            }
            return result;
        } catch (DocumentException e) {
            throw new PaymentException(ErrorCode.THIRD_SERVICE_EXCEPTIOIN, "解析数据错误", e);
        }
    }

	public static String map2xml(Map<String, ?> input) throws PaymentException {
		Document doc = DocumentHelper.createDocument();
		Element rootElement = DocumentHelper.createElement("xml");

		for (Map.Entry<String, ?> entry : input.entrySet()) {
			Element elm = rootElement.addElement(entry.getKey());
			if (entry.getValue() != null) {
				elm.setText(entry.getValue().toString());
			}
		}
		doc.setRootElement(rootElement);
		StringWriter sw = new StringWriter();
		XMLWriter xmlWriter = new XMLWriter(sw);
		try {
			xmlWriter.write(doc);
		} catch (IOException e) {
			throw new PaymentException(ErrorCode.THIRD_SERVICE_EXCEPTIOIN, "生成数据错误", e);
		}

		return sw.getBuffer().toString();
	}

}
