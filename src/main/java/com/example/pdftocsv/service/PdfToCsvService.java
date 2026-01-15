package com.example.pdftocsv.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTerminalField;
import org.apache.pdfbox.pdmodel.interactive.form.PDNonTerminalField;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for converting PDF documents with AcroForm fields
 * into CSV content.
 *
 * This implementation is for PDFBox 2.x (2.0.30).
 */
@Service
public class PdfToCsvService {

    /**
     * Convert a PDF form uploaded as a MultipartFile into CSV bytes.
     *
     * @param pdfFile uploaded PDF file
     * @return CSV content as UTF-8 bytes
     * @throws IOException if PDF cannot be read
     */
    public byte[] convertFormFieldsToCsv(MultipartFile pdfFile) throws IOException {
        if (pdfFile == null || pdfFile.isEmpty()) {
            throw new IllegalArgumentException("PDF file is empty");
        }
        try (InputStream in = pdfFile.getInputStream()) {
            return convertFormFieldsToCsv(in);
        }
    }

    /**
     * Convert a PDF form stored as a File into CSV bytes.
     *
     * @param pdfFile PDF file on disk
     * @return CSV content as UTF-8 bytes
     * @throws IOException if file cannot be read or does not exist
     */
    public byte[] convertFormFieldsToCsv(File pdfFile) throws IOException {
        if (pdfFile == null || !pdfFile.exists()) {
            throw new FileNotFoundException("PDF file not found: " + pdfFile);
        }
        try (InputStream in = new FileInputStream(pdfFile)) {
            return convertFormFieldsToCsv(in);
        }
    }

    /**
     * Core logic: convert a PDF InputStream into CSV bytes.
     */
    private byte[] convertFormFieldsToCsv(InputStream in) throws IOException {
        try (PDDocument document = PDDocument.load(in)) {

            PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
            if (acroForm == null) {
                throw new IOException("No AcroForm (form fields) found in this PDF.");
            }

            List<PDTerminalField> terminalFields = new ArrayList<>();
            for (PDField field : acroForm.getFields()) {
                collectTerminalFields(field, terminalFields);
            }

            if (terminalFields.isEmpty()) {
                throw new IOException("No terminal form fields found in this PDF.");
            }

            StringBuilder sb = new StringBuilder();

            // Header row: field names
            for (int i = 0; i < terminalFields.size(); i++) {
                String name = terminalFields.get(i).getFullyQualifiedName();
                sb.append(toCsvCell(name));
                if (i < terminalFields.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append("\n");

            // Values row: field values
            for (int i = 0; i < terminalFields.size(); i++) {
                String value = terminalFields.get(i).getValueAsString();
                sb.append(toCsvCell(value));
                if (i < terminalFields.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append("\n");

            return sb.toString().getBytes(StandardCharsets.UTF_8);
        }
    }

    /**
     * Recursively collect all terminal fields (actual input fields) from a field tree.
     * Uses PDFBox 2.x API: getChildren().
     */
    private void collectTerminalFields(PDField field, List<PDTerminalField> result) {
        if (field instanceof PDTerminalField) {
            // Actual input field that can hold a value
            result.add((PDTerminalField) field);
        } else if (field instanceof PDNonTerminalField) {
            // Non-terminal node that can have children
            PDNonTerminalField nonTerminal = (PDNonTerminalField) field;
            List<PDField> children = nonTerminal.getChildren();
            if (children != null) {
                for (PDField child : children) {
                    collectTerminalFields(child, result);
                }
            }
        }
    }


    /**
     * Escape a value so it is safe in a CSV cell.
     */
    private String toCsvCell(String value) {
        if (value == null) {
            return "";
        }
        boolean hasSpecial = value.contains(",")
                || value.contains("\"")
                || value.contains("\n")
                || value.contains("\r");

        String escaped = value.replace("\"", "\"\"");

        if (hasSpecial) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}
