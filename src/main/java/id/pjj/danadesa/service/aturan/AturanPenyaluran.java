package id.pjj.danadesa.service.aturan;

import id.pjj.danadesa.exception.AturanBisnisException;

/**
 * Satu aturan bisnis penyaluran. Setiap implementasi memeriksa satu hal;
 * ValidatorPenyaluran memanggil semuanya lewat tipe interface ini (polimorfisme).
 */
public interface AturanPenyaluran {

    void periksa(KonteksPenyaluran k) throws AturanBisnisException;
}
