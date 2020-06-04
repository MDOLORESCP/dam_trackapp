package dam.trackapp.modelos;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Categoria extends Modelo {
    private String _nombre;
    private String _usuario;
    private String _grupo;

    public String getNombre() {
        return _nombre;
    }

    public void setNombre(String nombre) {
        _nombre = nombre;
    }

    public String getUsuario() {
        return _usuario;
    }

    public void setUsuario(String usuario) {
        _usuario = usuario;
    }

    public String getGrupo() {
        return _grupo;
    }

    public void setGrupo(String grupo) {
        _grupo = grupo;
    }
}
