package com.example.pdftocsv.controller;

import com.example.pdftocsv.service.PdfToCsvService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "pdf.input.dir=src/test/resources/test-pdfs"
})
class PdfDirectoryControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PdfToCsvService pdfToCsvService;

    @BeforeEach
    void setUp() throws Exception {
        // Ensure the test directory exists with at least one .pdf file
        File dir = new File("src/test/resources/test-pdfs");
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IllegalStateException("Unable to create test-pdfs directory");
        }

        File samplePdf = new File(dir, "form1.pdf");
        if (!samplePdf.exists() && !samplePdf.createNewFile()) {
            throw new IllegalStateException("Unable to create sample form1.pdf");
        }
    }

    @Test
    void listPdfs_returnsListOfPdfNames() throws Exception {
        mockMvc.perform(get("/api/dir/pdfs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]", containsString("form1.pdf")));
    }

    @Test
    void convertPdfFromDir_returnsCsvAsAttachment() throws Exception {
        byte[] csvBytes = "FirstName,LastName\nJohn,Doe\n".getBytes(StandardCharsets.UTF_8);

        // When controller invokes service, return fake CSV bytes
        BDDMockito.given(pdfToCsvService.convertFormFieldsToCsv(
                        org.mockito.ArgumentMatchers.any(File.class)))
                .willReturn(csvBytes);

        mockMvc.perform(get("/api/dir/pdf-to-csv")
                        .param("fileName", "form1.pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        containsString("attachment; filename=\"form1.csv\"")))
                .andExpect(content().contentType("text/csv"))
                .andExpect(content().string(containsString("FirstName,LastName")));
    }

    @Test
    void convertPdfFromDir_whenFileMissing_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/dir/pdf-to-csv")
                        .param("fileName", "does-not-exist.pdf"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain"))
                .andExpect(content().string(containsString("PDF file not found")));
    }
}
