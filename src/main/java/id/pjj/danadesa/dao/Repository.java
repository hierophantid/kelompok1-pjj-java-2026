package id.pjj.danadesa.dao;

import id.pjj.danadesa.model.Entitas;
import java.util.List;
import java.util.Optional;

/** Kontrak CRUD generik untuk semua entitas. */
public interface Repository<T extends Entitas> {

    List<T> findAll();

    Optional<T> findById(int id);

    T insert(T entitas);

    void update(T entitas);

    void delete(int id);
}
