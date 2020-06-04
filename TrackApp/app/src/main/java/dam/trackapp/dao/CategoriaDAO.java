package dam.trackapp.dao;

import com.google.firebase.database.ValueEventListener;

import dam.trackapp.modelos.Categoria;

public class CategoriaDAO extends DAO<Categoria> {
    public CategoriaDAO() {
        super("Categorias");
    }

    public void obtenerDeUsuario(String idUsuario, ValueEventListener listener) {
        DB.orderByChild("usuario").equalTo(idUsuario).addValueEventListener(listener);
    }

    public void obtenerDeGrupo(String idGrupo, ValueEventListener listener) {
        DB.orderByChild("grupo").equalTo(idGrupo).addValueEventListener(listener);
    }
}
