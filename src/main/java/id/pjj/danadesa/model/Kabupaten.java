package id.pjj.danadesa.model;

public class Kabupaten extends EntitasDasar {

    private String kode;
    private String nama;

    public Kabupaten() {
    }

    public Kabupaten(Integer id, String kode, String nama) {
        super(id);
        this.kode = kode;
        this.nama = nama;
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

    @Override
    public String toString() {
        return kode + " - " + nama;
    }
}
