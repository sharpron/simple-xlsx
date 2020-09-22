package xlsx;

/**
 * @author ron 2020/9/22
 */
public interface ExcelReader {

  Iterable<Row> read(int sheetIndex);
}
