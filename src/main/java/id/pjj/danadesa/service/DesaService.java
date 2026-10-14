package id.pjj.danadesa.service;

import id.pjj.danadesa.dao.DesaDao;
import id.pjj.danadesa.dao.KabupatenDao;
import id.pjj.danadesa.dao.PenyaluranDao;
import id.pjj.danadesa.exception.AplikasiException;
import id.pjj.danadesa.exception.AturanBisnisException;
import id.pjj.danadesa.model.Desa;
import id.pjj.danadesa.model.Kabupaten;
import id.pjj.danadesa.util.Format;
import id.pjj.danadesa.util.InputParser;
import java.math.BigDecimal;
import java.util.List;

/** Logika CRUD desa: validasi input dan aturan bisnis sebelum ke database. */
public class DesaService {

    private final DesaDao desaDao;
    private final KabupatenDao kabupatenDao;
    private final PenyaluranDao penyaluranDao;

    public DesaService(DesaDao desaDao, KabupatenDao kabupatenDao, PenyaluranDao penyaluranDao) {
        this.desaDao = desaDao;
        this.kabupatenDao = kabupatenDao;
        this.penyaluranDao = penyaluranDao;
    }

    public List<Desa> semuaDesa() {
        return desaDao.findAll();
    }

    public List<Kabupaten> semuaKabupaten() {
        return kabupatenDao.findAll();
    }

    public Desa tambah(String kode, String nama, Kabupaten kabupaten, String paguTeks)
            throws AplikasiException {
        Desa d = dariInput(null, kode, nama, kabupaten, paguTeks);
        if (desaDao.kodeSudahDipakai(d.getKode(), null)) {
            throw new AturanBisnisException("Kode desa " + d.getKode() + " sudah terdaftar.");
        }
        return desaDao.insert(d);
    }

    public Desa ubah(int id, String kode, String nama, Kabupaten kabupaten, String paguTeks)
            throws AplikasiException {
        Desa d = dariInput(id, kode, nama, kabupaten, paguTeks);
        if (desaDao.kodeSudahDipakai(d.getKode(), id)) {
            throw new AturanBisnisException("Kode desa " + d.getKode() + " sudah dipakai desa lain.");
        }
        BigDecimal tersalur = penyaluranDao.totalPerDesa(id);
        if (d.getPagu().compareTo(tersalur) < 0) {
            throw new AturanBisnisException("Pagu baru " + Format.rupiah(d.getPagu())
                    + " lebih kecil dari total yang sudah disalurkan " + Format.rupiah(tersalur) + ".");
        }
        desaDao.update(d);
        return d;
    }

    public void hapus(Desa desa) throws AplikasiException {
        int jumlah = penyaluranDao.hitungPerDesa(desa.getId());
        if (jumlah > 0) {
            throw new AturanBisnisException("Desa " + desa.getNama() + " tidak bisa dihapus karena sudah punya "
                    + jumlah + " data penyaluran. Hapus penyalurannya dulu.");
        }
        desaDao.delete(desa.getId());
    }

    private Desa dariInput(Integer id, String kode, String nama, Kabupaten kabupaten, String paguTeks)
            throws AplikasiException {
        Desa d = new Desa(id,
                InputParser.wajib(kode, "Kode desa"),
                InputParser.wajib(nama, "Nama desa"),
                kabupaten,
                InputParser.rupiah(paguTeks, "Pagu"));
        ValidatorDesa.validasi(d);
        return d;
    }
}
