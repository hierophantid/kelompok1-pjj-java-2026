package id.pjj.danadesa.model;

/**
 * Kontrak untuk semua entitas yang disimpan di database (punya id).
 * Dipakai sebagai batas tipe pada Repository&lt;T extends Entitas&gt;.
 */
public interface Entitas {

    Integer getId();

    void setId(Integer id);
}
