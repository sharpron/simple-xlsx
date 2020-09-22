package xlsx.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import xlsx.ExcelReader;
import xlsx.Row;

/**
 * @author ron 2020/9/22
 */
public class SimpleExcelReader implements ExcelReader {

  private static final Logger LOGGER = Logger.getLogger(SimpleExcelReader.class.getName());

  private final Path path;

  public SimpleExcelReader(Path path) {
    this.path = path;
  }


  @Override
  public Iterable<Row> read(int sheetIndex) {
    final FileSystem fileSystem;
    try {
      fileSystem = FileSystems.newFileSystem(path, null);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }

    Path path = fileSystem.getPath("/xl/sharedStrings.xml");
    SharedStringHandler handler = new SharedStringHandler();
    parse(path, handler);
    LOGGER.info(String.format(
        "解析shardStrings的值%s",
        handler.getSharedStrings()
    ));

    Path sheetPath = fileSystem.getPath(
        String.format("/xl/worksheets/sheet%d.xml", (sheetIndex + 1))
    );
    XMLEventReader reader = createReader(sheetPath);
    return () -> new WorkSheetHandler(reader, handler.getSharedStrings());
  }


  private static void parse(Path path, DefaultHandler handler) {
    //1.或去SAXParserFactory实例
    SAXParserFactory factory = SAXParserFactory.newInstance();
    try {
      InputStream inputStream = Files.newInputStream(path);
      factory.newSAXParser().parse(inputStream, handler);
    } catch (ParserConfigurationException | SAXException e) {
      throw new AssertionError(e);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static XMLEventReader createReader(Path path) {
    try {
      return XMLInputFactory.newInstance()
          .createXMLEventReader(Files.newInputStream(path));
    } catch (XMLStreamException | IOException e) {
      throw new IllegalStateException(e);
    }
  }

}
