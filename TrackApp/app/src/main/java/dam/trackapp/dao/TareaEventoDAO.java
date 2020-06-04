package dam.trackapp.dao;

import com.google.firebase.database.ValueEventListener;

import dam.trackapp.modelos.TareaEvento;

public class TareaEventoDAO extends DAO<TareaEvento> {
    public TareaEventoDAO() {
        super("Tareas");
    }

    public void obtenerDeUsuario(String idUsuario, ValueEventListener event) {
        TareaEvento tarea = new TareaEvento();

        tarea.setUsuarioAsignado(idUsuario);

        DB.orderByChild("usuarioGrupo").equalTo(tarea.getUsuarioGrupo()).addValueEventListener(event);
    }

    public void obtenerDeGrupo(String idGrupo, ValueEventListener event) {
        DB.orderByChild("grupo").equalTo(idGrupo).addValueEventListener(event);
    }
}
