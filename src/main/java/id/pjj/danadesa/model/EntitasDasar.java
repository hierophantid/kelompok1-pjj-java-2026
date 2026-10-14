package id.pjj.danadesa.model;

import java.util.Objects;

/**
 * Kelas induk (inheritance) untuk Kabupaten, Desa, dan Penyaluran.
 * Menyimpan id dan aturan kesamaan objek berdasarkan id.
 */
public abstract class EntitasDasar implements Entitas {

    private Integer id;

    protected EntitasDasar() {
    }

    protected EntitasDasar(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isBaru() {
        return id == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass() || id == null) {
            return false;
        }
        return id.equals(((EntitasDasar) o).id);
    }

    @Override
    public int hashCode() {
        return id == null ? System.identityHashCode(this) : Objects.hash(getClass(), id);
    }
}
