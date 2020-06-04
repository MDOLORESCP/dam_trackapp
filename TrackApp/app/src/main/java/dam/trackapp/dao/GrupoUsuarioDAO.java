package dam.trackapp.dao;

import com.google.firebase.database.ValueEventListener;

import dam.trackapp.modelos.GrupoUsuario;

public class GrupoUsuarioDAO extends DAO<GrupoUsuario> {
    public GrupoUsuarioDAO() {
        super("GrupoUsuarios");
    }

    public void obtenerDeUsuario(String idUsuario, ValueEventListener listener) {
        DB.orderByChild("usuario").equalTo(idUsuario)
                .addValueEventListener(listener);
    }

    public void obtenerDeGrupo(String idGrupo, ValueEventListener listener) {
        DB.orderByChild("grupo").equalTo(idGrupo)
                .addValueEventListener(listener);
    }
}
