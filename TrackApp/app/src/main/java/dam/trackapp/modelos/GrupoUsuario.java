package dam.trackapp.modelos;

public class GrupoUsuario extends Modelo {
    private String _usuario;
    private String _grupo;

    public GrupoUsuario() {

    }

    public GrupoUsuario(String idGrupo, String idUsuario) {
        setGrupo(idGrupo);
        setUsuario(idUsuario);
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
