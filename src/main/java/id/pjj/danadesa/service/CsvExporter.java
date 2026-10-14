package id.pjj.danadesa.service;

import id.pjj.danadesa.model.BarisCsv;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Menulis data ke file CSV (pemisah titik koma, UTF-8 dengan BOM)
 * sehingga langsung terbaca rapi di Excel berbahasa Indonesia.
 */
public class CsvExporter {

    private static final char PEMISAH = ';';

    public void tulis(Path file, String[] header, List<? extends BarisCsv> baris) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            w.write('﻿');
            tulisBaris(w, header);
            for (BarisCsv b : baris) {
                tulisBaris(w, b.kolomCsv());     // polimorfik: RekapDesa / RekapKabupaten / RekapTotal
            }
        }
    }

    private void tulisBaris(BufferedWriter w, String[] kolom) throws IOException {
        w.write(Arrays.stream(kolom).map(CsvExporter::escape).collect(Collectors.joining(String.valueOf(PEMISAH))));
        w.newLine();
    }

    static String escape(String nilai) {
        if (nilai == null) {
            return "";
        }
        boolean perluKutip = nilai.indexOf(PEMISAH) >= 0 || nilai.contains("\"")
                || nilai.contains("\n") || nilai.contains("\r");
        return perluKutip ? "\"" + nilai.replace("\"", "\"\"") + "\"" : nilai;
    }
}
