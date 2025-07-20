package lecture;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class SelenideFilesTest {

    @Test
    void downLoadFileTest() throws Exception {
        open("https://github.com/junit-team/junit-framework/blob/main/README.md");
        File download = $(".react-blob-header-edit-and-raw-actions [href='https://github.com/junit-team/junit-framework/raw/refs/heads/main/README.md']").download();

        try (InputStream is = new FileInputStream(download)) {
            byte[] data = is.readAllBytes();
            String dataAsString = new String(data, StandardCharsets.UTF_8);
            Assertions.assertTrue(dataAsString.contains("Contributions to JUnit are both welcomed and appreciated"));
        }
    }

    @Test
    void uploadFileTest(){
        open("https://tellibus.com/fineuploader/#demo");
        $("input[type='file']").uploadFromClasspath("coolcat.jpg");
        $(".qq-upload-file").shouldHave(text("coolcat.jpg"));
    }
}
