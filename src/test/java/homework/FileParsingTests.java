package homework;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.opencsv.CSVReader;
import lecture.FileParsingTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


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
                    Assertions.assertEquals("Евгений Зуев", pdf.author);
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

    @Test
    void jsonFileShouldBeValid() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        try (InputStream is = cl.getResourceAsStream("clients.json")) {

            JsonNode root = mapper.readTree(is);
            JsonNode clients = root.get("clients");

            JsonNode firstClient = clients.get(0);
            Assertions.assertEquals(1, firstClient.get("id").asInt());
            Assertions.assertEquals("Иван Иванов", firstClient.get("name").asText());
            Assertions.assertEquals(1500, firstClient.get("accountBalance").asInt());
            Assertions.assertEquals("Savings", firstClient.get("accountType").asText());

            JsonNode secondClient = clients.get(1);
            Assertions.assertEquals(2, secondClient.get("id").asInt());
            Assertions.assertEquals("Мария Петрова", secondClient.get("name").asText());
            Assertions.assertEquals(3200, secondClient.get("accountBalance").asInt());
            Assertions.assertEquals("Checking", secondClient.get("accountType").asText());
        }
    }
}