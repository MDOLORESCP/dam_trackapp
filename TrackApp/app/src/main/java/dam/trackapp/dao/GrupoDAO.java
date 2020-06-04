package dam.trackapp.dao;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dam.trackapp.modelos.Grupo;
import dam.trackapp.modelos.GrupoUsuario;

public class GrupoDAO extends DAO<Grupo> {
    public GrupoDAO() {
        super("Grupos");
    }

    public void obtenerDeUsuario(String idUsuario, ValueEventListener listener) {
        new GrupoUsuarioDAO().obtenerDeUsuario(idUsuario, listener);
    }

    public void agregarUsuario(String idGrupo, String idUsuario) {
        new GrupoUsuarioDAO().crear(new GrupoUsuario(idGrupo, idUsuario));
    }

    public void obtenerDeGrupo(String idGrupo, ValueEventListener listener) {
        new GrupoUsuarioDAO().obtenerDeGrupo(idGrupo, listener);
    }
}
