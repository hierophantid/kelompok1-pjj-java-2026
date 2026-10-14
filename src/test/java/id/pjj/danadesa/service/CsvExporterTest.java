package id.pjj.danadesa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import id.pjj.danadesa.model.BarisCsv;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CsvExporterTest {

    @Test
    void escapeKolomBerisiPemisahDanKutip() {
        assertEquals("biasa", CsvExporter.escape("biasa"));
        assertEquals("\"a;b\"", CsvExporter.escape("a;b"));
        assertEquals("\"kata \"\"kutip\"\"\"", CsvExporter.escape("kata \"kutip\""));
    }

    @Test
    void tulisFile(@TempDir Path dir) throws Exception {
        Path f = dir.resolve("x.csv");
        BarisCsv baris = () -> new String[]{"Desa; Satu", "100,00"};
        new CsvExporter().tulis(f, new String[]{"Nama", "Nilai"}, List.of(baris));

        List<String> isi = Files.readAllLines(f, StandardCharsets.UTF_8);
        assertEquals("﻿Nama;Nilai", isi.get(0));
        assertEquals("\"Desa; Satu\";100,00", isi.get(1));
    }
}
