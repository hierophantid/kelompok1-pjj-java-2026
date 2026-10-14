package id.pjj.danadesa.service;

import id.pjj.danadesa.exception.AplikasiException;
import id.pjj.danadesa.exception.AturanBisnisException;
import id.pjj.danadesa.exception.ValidasiException;
import id.pjj.danadesa.model.Desa;

/** Validasi data desa yang tidak butuh database. */
public final class ValidatorDesa {

    private ValidatorDesa() {
    }

    public static void validasi(Desa d) throws AplikasiException {
        if (d.getKode() == null || d.getKode().isBlank()) {
            throw new ValidasiException("Kode desa wajib diisi.");
        }
        if (!d.getKode().matches("\\d{10}")) {
            throw new ValidasiException("Kode desa harus 10 digit angka, contoh: 9901012001");
        }
        if (d.getNama() == null || d.getNama().isBlank()) {
            throw new ValidasiException("Nama desa wajib diisi.");
        }
        if (d.getNama().length() > 100) {
            throw new ValidasiException("Nama desa maksimal 100 karakter.");
        }
        if (d.getKabupaten() == null) {
            throw new ValidasiException("Kabupaten wajib dipilih.");
        }
        if (d.getPagu() == null || d.getPagu().signum() <= 0) {
            throw new ValidasiException("Pagu harus lebih dari 0.");
        }
        if (!d.getKode().startsWith(d.getKabupaten().getKode())) {
            throw new AturanBisnisException("4 digit awal kode desa harus sama dengan kode kabupaten ("
                    + d.getKabupaten().getKode() + ").");
        }
    }
}
