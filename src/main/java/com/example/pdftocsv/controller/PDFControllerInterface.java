package com.example.pdftocsv.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import com.example.pdftocsv.service.PdfToCsvService;
import java.util.List;

public interface PDFControllerInterface {

    public ResponseEntity<List<String>> listPdfs();
    public ResponseEntity<Resource> convertPdfFromDir(String filename) throws Exception;
}