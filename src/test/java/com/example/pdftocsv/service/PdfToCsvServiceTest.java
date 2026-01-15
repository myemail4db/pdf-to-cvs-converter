package com.example.pdftocsv.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.apache.pdfbox.cos.COSName;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PdfToCsvServiceTest {

    private final PdfToCsvService service = new PdfToCsvService();

    /**
     * Create an AcroForm with default resources and appearance string so that
     * PDFBox allows setting field values without throwing "/DA is a required entry".
     */
    private PDAcroForm createAcroFormWithDefaultAppearance(PDDocument document) {
        PDAcroForm acroForm = new PDAcroForm(document);
        document.getDocumentCatalog().setAcroForm(acroForm);

        // Set default resources and a font alias "Helv"
        PDResources resources = new PDResources();
        resources.put(COSName.getPDFName("Helv"), PDType1Font.HELVETICA);
        acroForm.setDefaultResources(resources);

        // Set default appearance: use "Helv" font, size 10, black color (0 g)
        acroForm.setDefaultAppearance("/Helv 10 Tf 0 g");

        return acroForm;
    }


    @Test
    void convertFormFieldsToCsv_multipartNull_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.convertFormFieldsToCsv((MockMultipartFile) null));

        assertTrue(ex.getMessage().toLowerCase().contains("empty"));
    }

    @Test
    void convertFormFieldsToCsv_multipartEmpty_throwsIllegalArgumentException() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "empty.pdf", "application/pdf", new byte[0]);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.convertFormFieldsToCsv(emptyFile));

        assertTrue(ex.getMessage().toLowerCase().contains("empty"));
    }

    @Test
    void convertFormFieldsToCsv_fileNotFound_throwsFileNotFoundException() {
        File missing = new File("this-file-does-not-exist-12345.pdf");

        Exception ex = assertThrows(Exception.class,
                () -> service.convertFormFieldsToCsv(missing));

        assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    void convertFormFieldsToCsv_noAcroForm_throwsIOException() throws Exception {
        Path tempPdf = Files.createTempFile("test-no-acroform", ".pdf");

        try (PDDocument document = new PDDocument()) {
            // Intentionally do NOT set an AcroForm
            document.save(tempPdf.toFile());
        }

        Exception ex = assertThrows(Exception.class,
                () -> service.convertFormFieldsToCsv(tempPdf.toFile()));

        assertTrue(ex.getMessage().contains("No AcroForm"), "Expected message about missing AcroForm");
    }

    @Test
    void convertFormFieldsToCsv_validSingleField_producesCsv() throws Exception {
        Path tempPdf = Files.createTempFile("test-single-field", ".pdf");

        try (PDDocument document = new PDDocument()) {
            PDAcroForm acroForm = createAcroFormWithDefaultAppearance(document);

            PDTextField field = new PDTextField(acroForm);
            field.setPartialName("FirstName");
            field.setValue("Dominick");

            acroForm.getFields().add(field);

            document.save(tempPdf.toFile());
        }


        byte[] csvBytes = service.convertFormFieldsToCsv(tempPdf.toFile());
        String csv = new String(csvBytes, StandardCharsets.UTF_8);

        // Header row and value row covered
        assertTrue(csv.contains("FirstName"), "CSV should contain the field name");
        assertTrue(csv.contains("Dominick"), "CSV should contain the field value");
    }

    @Test
    void convertFormFieldsToCsv_escapingSpecialCharacters_producesQuotedCsv() throws Exception {
        Path tempPdf = Files.createTempFile("test-special-chars", ".pdf");

        try (PDDocument document = new PDDocument()) {
            PDAcroForm acroForm = createAcroFormWithDefaultAppearance(document);

            PDTextField field1 = new PDTextField(acroForm);
            field1.setPartialName("Comment");
            field1.setValue("Hello, \"World\"\nNext line");

            acroForm.getFields().add(field1);

            document.save(tempPdf.toFile());
        }


        byte[] csvBytes = service.convertFormFieldsToCsv(tempPdf.toFile());
        String csv = new String(csvBytes, StandardCharsets.UTF_8);

        // We expect quotes and escaped quotes in CSV
        assertTrue(csv.split("\n").length >= 2, "Should have at least header + value row");
        assertTrue(csv.contains("\"Hello, \"\"World\"\""), "CSV should escape quotes and comma correctly");
    }
}
