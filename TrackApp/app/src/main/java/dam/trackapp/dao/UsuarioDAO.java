package dam.trackapp.dao;

import com.google.firebase.database.ValueEventListener;

import dam.trackapp.modelos.Usuario;

public class UsuarioDAO extends DAO<Usuario> {
    public UsuarioDAO() {
        super("usuario");
    }

    public void obtenerConEmail(String email, ValueEventListener listener) {
        DB.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(listener);
    }
}
