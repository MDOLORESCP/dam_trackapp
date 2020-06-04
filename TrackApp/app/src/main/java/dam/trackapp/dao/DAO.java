package dam.trackapp.dao;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

import dam.trackapp.modelos.Modelo;

public abstract class DAO<T extends Modelo> {
    private String Coleccion;
    protected DatabaseReference DB;

    public DAO(String coleccion) {
        Coleccion = coleccion;
        DB = FirebaseDatabase.getInstance().getReference().child(Coleccion);
    }

    public T crear(T modelo) {
        try {
            String uuid = UUID.randomUUID().toString();

            modelo.setId(uuid);

            DB.child(modelo.getId()).setValue(modelo);

            return modelo;
        } catch (Exception e) {
            return null;
        }
    }

    public T crear(T modelo, String id) {
        try {
            modelo.setId(id);

            DB.child(modelo.getId()).setValue(modelo);

            return modelo;
        } catch (Exception e) {
            return null;
        }
    }

    public T actualizar(T modelo) {
        try {
            DB.child(modelo.getId()).setValue(modelo);

            return modelo;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean borrar(String id) {
        try {
            DB.child(id).removeValue();
        } catch (Exception e)
        {
            return false;
        }

        return true;
    }

    public void obtenerPorId(String id, ValueEventListener eventListener) {
        DB.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(eventListener);
    }
}
