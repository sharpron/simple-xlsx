package xlsx;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import xlsx.impl.SimpleExcelReader;

/**
 * @author ron 2020/9/23
 */
public class App {

  public static void main(String[] args) throws URISyntaxException {
    URL resource = App.class.getResource("/test.xlsx");
    SimpleExcelReader simpleExcelReader = new SimpleExcelReader(Path.of(resource.toURI()));
    Iterable<Row> read = simpleExcelReader.read(0);
    for (Row row : read) {

      System.out.println(1);
    }
  }
}
