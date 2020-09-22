package xlsx.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import xlsx.Row;

/**
 * @author ron 2020/9/22
 */
class WorkSheetHandler implements Iterator<Row> {

  private static final String ROW_TAG = "row";

  private static final String CELL_TAG = "c";

  private static final QName CELL_LOCATION_ATTR = new QName("r");

  private static final QName CELL_TYPE_ATTR = new QName("t");

  private static final String CELL_TYPE_VAL_STRING = "s";

  private final XMLEventReader xmlEventReader;

  private final List<String> sharedStrings;


  WorkSheetHandler(XMLEventReader xmlEventReader,
      List<String> sharedStrings) {
    this.xmlEventReader = xmlEventReader;
    this.sharedStrings = sharedStrings;
  }


  @Override
  public boolean hasNext() {
    return xmlEventReader.hasNext();
  }

  @Override
  public Row next() {
    final Map<String, String> rowData = new HashMap<>();
    boolean foundRow = false;
    boolean foundCell = false;
    String cellLocation = null;
    String cellType = null;
    String cellData = null;
    while (xmlEventReader.hasNext()) {
      XMLEvent event = (XMLEvent) xmlEventReader.next();
      if (event.isStartElement()) {
        StartElement startElement = event.asStartElement();
        String tagName = startElement.getName().getLocalPart();
        if (tagName.equalsIgnoreCase(ROW_TAG)) {
          foundRow = true;
        } else if (tagName.equalsIgnoreCase(CELL_TAG) && foundRow) {
          Attribute location = startElement
              .getAttributeByName(CELL_LOCATION_ATTR);
          cellLocation = location.getValue();
          Attribute type = startElement
              .getAttributeByName(CELL_TYPE_ATTR);
          cellType = type == null ? null : type.getValue();
          foundCell = true;
        }
      } else if (event.isCharacters() && foundCell) {
        cellData = event.asCharacters().getData();
      } else if (event.isEndElement()) {
        EndElement endElement = event.asEndElement();
        String tagName = endElement.getName().getLocalPart();
        if (tagName.equalsIgnoreCase(ROW_TAG)) {
          foundRow = false;
          break;
        } else if (tagName.equalsIgnoreCase(CELL_TAG)) {
          foundCell = false;
          if (cellData != null) {
            String finalData = CELL_TYPE_VAL_STRING.equals(cellType) ?
                sharedStrings.get(Integer.parseInt(cellData)) : cellData;
            rowData.put(cellLocation, finalData);
          }
        }
      }
    }
    return rowData::get;
  }


}
