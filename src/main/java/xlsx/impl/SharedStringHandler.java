package xlsx.impl;

import java.util.LinkedList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class SharedStringHandler extends DefaultHandler {

  final List<String> sharedStrings = new LinkedList<>();

  static final String SI_TAG = "si";

  // 找到si标签
  boolean foundSi = false;
  StringBuilder innerBuilder;

  @Override
  public void startElement(String uri,
      String localName,
      String qName, Attributes attributes)
      throws SAXException {
    if (SI_TAG.equals(qName)) {
      foundSi = true;
      innerBuilder = new StringBuilder();
    }
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    if (foundSi) {
      innerBuilder.append(ch, start, length);
    }
  }

  @Override
  public void endElement(String uri,
      String localName, String qName) throws SAXException {
    if (SI_TAG.equals(qName)) {
      foundSi = false;
      sharedStrings.add(innerBuilder.toString());
    }
  }

  List<String> getSharedStrings() {
    return sharedStrings;
  }
}