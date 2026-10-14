package id.pjj.danadesa.ui.komponen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.swing.table.AbstractTableModel;

/** TableModel generik: kolom didefinisikan dengan lambda, data berupa List&lt;T&gt;. */
public class ListTableModel<T> extends AbstractTableModel {

    public record Kolom<T>(String judul, Class<?> tipe, Function<? super T, ?> nilai) {
    }

    private final List<Kolom<T>> kolom;
    private List<T> data = new ArrayList<>();

    public ListTableModel(List<Kolom<T>> kolom) {
        this.kolom = List.copyOf(kolom);
    }

    public static <T> Kolom<T> kolom(String judul, Class<?> tipe, Function<? super T, ?> nilai) {
        return new Kolom<>(judul, tipe, nilai);
    }

    public void setData(List<T> dataBaru) {
        this.data = new ArrayList<>(dataBaru);
        fireTableDataChanged();
    }

    public List<T> getData() {
        return List.copyOf(data);
    }

    public T getBaris(int indeksModel) {
        return data.get(indeksModel);
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return kolom.size();
    }

    @Override
    public String getColumnName(int c) {
        return kolom.get(c).judul();
    }

    @Override
    public Class<?> getColumnClass(int c) {
        return kolom.get(c).tipe();
    }

    @Override
    public Object getValueAt(int r, int c) {
        return kolom.get(c).nilai().apply(data.get(r));
    }
}
