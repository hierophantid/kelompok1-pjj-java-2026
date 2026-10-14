package id.pjj.danadesa.model;

import java.math.BigDecimal;

public class Desa extends EntitasDasar {

    private String kode;
    private String nama;
    private Kabupaten kabupaten;
    private BigDecimal pagu;

    public Desa() {
    }

    public Desa(Integer id, String kode, String nama, Kabupaten kabupaten, BigDecimal pagu) {
        super(id);
        this.kode = kode;
        this.nama = nama;
        this.kabupaten = kabupaten;
        this.pagu = pagu;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public Kabupaten getKabupaten() {
        return kabupaten;
    }

    public void setKabupaten(Kabupaten kabupaten) {
        this.kabupaten = kabupaten;
    }

    public BigDecimal getPagu() {
        return pagu;
    }

    public void setPagu(BigDecimal pagu) {
        this.pagu = pagu;
    }

    @Override
    public String toString() {
        return kode + " - " + nama;
    }
}
