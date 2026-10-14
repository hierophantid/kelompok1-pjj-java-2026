package id.pjj.danadesa.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Penyaluran extends EntitasDasar {

    private int desaId;
    private Tahap tahap;
    private LocalDate tanggal;
    private BigDecimal nilai;

    public Penyaluran() {
    }

    public Penyaluran(Integer id, int desaId, Tahap tahap, LocalDate tanggal, BigDecimal nilai) {
        super(id);
        this.desaId = desaId;
        this.tahap = tahap;
        this.tanggal = tanggal;
        this.nilai = nilai;
    }

    public int getDesaId() {
        return desaId;
    }

    public void setDesaId(int desaId) {
        this.desaId = desaId;
    }

    public Tahap getTahap() {
        return tahap;
    }

    public void setTahap(Tahap tahap) {
        this.tahap = tahap;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public void setTanggal(LocalDate tanggal) {
        this.tanggal = tanggal;
    }

    public BigDecimal getNilai() {
        return nilai;
    }

    public void setNilai(BigDecimal nilai) {
        this.nilai = nilai;
    }
}
