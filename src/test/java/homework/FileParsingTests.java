package homework;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import lecture.FileParsingTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileParsingTests {

    private final ClassLoader cl = FileParsingTest.class.getClassLoader();

    @DisplayName("Проверка содержимого PDF-файла из ZIP архива")
    @Test
    void pdfInZipFileShouldHaveCorrectAuthorTest() throws Exception {
        try (ZipInputStream zis = new ZipInputStream(cl.getResourceAsStream("zipfile.zip")
        )) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".pdf")) {
                    PDF pdf = new PDF(zis);
                    assertEquals("Евгений Зуев", pdf.author);
                    break;
                }
            }
        }
    }

    @DisplayName("Проверка содержимого XLSX-файла из ZIP архива")
    @Test
    void xlsxInZipFileShouldHaveSpecificTextTest() throws Exception {
        try (ZipInputStream zis = new ZipInputStream(cl.getResourceAsStream("zipfile.zip")
        )) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".xlsx")) {
                    XLS xls = new XLS(zis);
                    String testValue = xls.excel.getSheetAt(0).getRow(0).getCell(0).getStringCellValue();
                    Assertions.assertTrue(testValue.contains("Привет, мир"));
                    break;
                }
            }
        }
    }

    @DisplayName("Проверка содержимого CSV-файла из ZIP архива")
    @Test
    void csvInZipFileShouldHaveCorrectContentTest() throws Exception {
        try (ZipInputStream zis = new ZipInputStream(cl.getResourceAsStream("zipfile.zip")
        )) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".csv")) {
                    try (CSVReader csvReader = new CSVReader(new InputStreamReader(zis))) {

                        List<String[]> data = csvReader.readAll();
                        Assertions.assertEquals(2, data.size());
                        Assertions.assertArrayEquals(
                                new String[]{"Иван", "Иванов"},
                                data.get(0)
                        );
                        Assertions.assertArrayEquals(
                                new String[]{"Николай", "Николаев"},
                                data.get(1)
                        );
                    }
                    break;
                }
            }
        }
    }

}
