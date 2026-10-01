package com.spf.document;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Legge un file caricato e ne restituisce il contenuto grezzo.
 * Non interpreta nulla: sapere "quale colonna è la data" sarà il lavoro dei template (fase 3).
 */
@Service
public class DocumentReader {

    public DocumentContent read(MultipartFile file) throws IOException {
        String name = file.getOriginalFilename() == null ? "sconosciuto" : file.getOriginalFilename();
        String lower = name.toLowerCase();

        if (lower.endsWith(".csv")) {
            return new DocumentContent(name, "csv", readCsv(file), null);
        }
        if (lower.endsWith(".xlsx")) {
            return new DocumentContent(name, "xlsx", readExcel(file), null);
        }
        if (lower.endsWith(".pdf")) {
            return new DocumentContent(name, "pdf", null, readPdf(file));
        }
        throw new IllegalArgumentException("Formato non supportato: " + name + " (usa csv, xlsx o pdf)");
    }

    private List<List<String>> readCsv(MultipartFile file) throws IOException {
        // Le banche italiane usano spesso ';' come separatore: lo rileviamo dalla prima riga
        byte[] bytes = file.getBytes();
        String firstLine = new String(bytes, StandardCharsets.UTF_8).lines().findFirst().orElse("");
        char delimiter = firstLine.chars().filter(c -> c == ';').count() > firstLine.chars().filter(c -> c == ',').count()
                ? ';' : ',';

        CSVFormat format = CSVFormat.DEFAULT.builder().setDelimiter(delimiter).build();
        List<List<String>> rows = new ArrayList<>();
        try (InputStreamReader reader = new InputStreamReader(new java.io.ByteArrayInputStream(bytes), StandardCharsets.UTF_8);
             CSVParser parser = format.parse(reader)) {
            for (CSVRecord record : parser) {
                rows.add(record.toList());
            }
        }
        return rows;
    }

    private List<List<String>> readExcel(MultipartFile file) throws IOException {
        List<List<String>> rows = new ArrayList<>();
        DataFormatter formatter = new DataFormatter(); // trasforma ogni cella in testo, come la vedi in Excel
        try (InputStream in = file.getInputStream(); Workbook workbook = WorkbookFactory.create(in)) {
            Sheet sheet = workbook.getSheetAt(0); // per ora solo il primo foglio
            for (Row row : sheet) {
                List<String> cells = new ArrayList<>();
                for (int i = 0; i < row.getLastCellNum(); i++) {
                    Cell cell = row.getCell(i);
                    cells.add(cell == null ? "" : formatter.formatCellValue(cell));
                }
                rows.add(cells);
            }
        }
        return rows;
    }

    private String readPdf(MultipartFile file) throws IOException {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            return new PDFTextStripper().getText(document);
        }
    }
}
