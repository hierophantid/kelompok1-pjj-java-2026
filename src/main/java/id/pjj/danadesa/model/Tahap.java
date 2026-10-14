package id.pjj.danadesa.model;

import java.util.Optional;

/** Tahap penyaluran Dana Desa. Disimpan di database sebagai angka 1..3. */
public enum Tahap {
    I(1), II(2), III(3);

    private final int urutan;

    Tahap(int urutan) {
        this.urutan = urutan;
    }

    public int getUrutan() {
        return urutan;
    }

    public String getLabel() {
        return "Tahap " + name();
    }

    /** Tahap tepat sebelum tahap ini; kosong untuk Tahap I. */
    public Optional<Tahap> sebelumnya() {
        return urutan == 1 ? Optional.empty() : Optional.of(values()[urutan - 2]);
    }

    /** Tahap tepat sesudah tahap ini; kosong untuk Tahap III. */
    public Optional<Tahap> berikutnya() {
        return urutan == values().length ? Optional.empty() : Optional.of(values()[urutan]);
    }

    public static Tahap dariUrutan(int urutan) {
        for (Tahap t : values()) {
            if (t.urutan == urutan) {
                return t;
            }
        }
        throw new IllegalArgumentException("Tahap tidak dikenal: " + urutan);
    }

    @Override
    public String toString() {
        return getLabel();
    }
}
