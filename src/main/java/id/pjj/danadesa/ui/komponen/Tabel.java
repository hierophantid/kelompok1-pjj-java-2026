package id.pjj.danadesa.ui.komponen;

import id.pjj.danadesa.util.Format;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.function.Function;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/** Pengaturan standar JTable: renderer rupiah, persen, tanggal, dan sorting. */
public final class Tabel {

    /** Penanda tipe kolom persentase (nilai tetap BigDecimal supaya sorting benar). */
    public static final class Persen {
        private Persen() {
        }
    }

    /** Penanda kolom angka rupiah tanpa awalan "Rp" (untuk tabel dengan banyak kolom). */
    public static final class Angka {
        private Angka() {
        }
    }

    private Tabel() {
    }

    public static JTable buat(TableModel model) {
        JTable t = new JTable(model);
        t.setRowSorter(sorter(model));
        t.setFillsViewportHeight(true);
        t.getTableHeader().setReorderingAllowed(false);
        t.getTableHeader().setFont(t.getTableHeader().getFont().deriveFont(java.awt.Font.BOLD));
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setDefaultRenderer(BigDecimal.class, renderer(v -> Format.rupiah((BigDecimal) v), SwingConstants.RIGHT));
        t.setDefaultRenderer(Persen.class, new BilahPersen());
        t.setDefaultRenderer(Angka.class, renderer(v -> Format.angka((BigDecimal) v), SwingConstants.RIGHT));
        t.setDefaultRenderer(LocalDate.class, renderer(v -> Format.tanggal((LocalDate) v), SwingConstants.CENTER));
        t.setDefaultRenderer(Integer.class, renderer(String::valueOf, SwingConstants.RIGHT));
        return t;
    }

    /** Bungkus tabel dengan scroll pane bergaris tipis. */
    public static JScrollPane gulir(JTable t) {
        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(BorderFactory.createLineBorder(Tema.GARIS));
        sp.getViewport().setBackground(Tema.KARTU);
        return sp;
    }

    /** Atur lebar kolom relatif (piksel); kolom diskalakan agar pas dengan lebar tabel. */
    public static void lebarKolom(JTable t, int... lebar) {
        t.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        for (int i = 0; i < lebar.length && i < t.getColumnCount(); i++) {
            t.getColumnModel().getColumn(i).setPreferredWidth(lebar[i]);
        }
    }

    /**
     * Sorter yang mengurutkan kolom Persen dan Angka secara numerik.
     * Tanpa ini, kolom bertipe non-Comparable diurutkan sebagai teks ("100,00" &lt; "45,50").
     */
    public static <M extends TableModel> TableRowSorter<M> sorter(M model) {
        return new TableRowSorter<>(model) {
            @Override
            public Comparator<?> getComparator(int kolom) {
                if (numerik(model.getColumnClass(kolom))) {
                    return Comparator.<BigDecimal>naturalOrder();
                }
                return super.getComparator(kolom);
            }

            @Override
            protected boolean useToString(int kolom) {
                return !numerik(model.getColumnClass(kolom)) && super.useToString(kolom);
            }
        };
    }

    private static boolean numerik(Class<?> tipe) {
        return tipe == Persen.class || tipe == Angka.class;
    }

    private static DefaultTableCellRenderer renderer(Function<Object, String> format, int rataan) {
        DefaultTableCellRenderer r = new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                setText(value == null ? "" : format.apply(value));
            }
        };
        r.setHorizontalAlignment(rataan);
        return r;
    }
}
