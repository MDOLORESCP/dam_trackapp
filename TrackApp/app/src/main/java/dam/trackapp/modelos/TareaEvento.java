package dam.trackapp.modelos;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class TareaEvento extends Modelo {
    private String _categoria;
    private double _coordLat;
    private double _coordLon;
    private String _descripcion;
    public long _fechaFinalizacion;
    private boolean _finalizada;
    public String _grupo;
    public String _nombre;
    private String _usuarioCreador;
    private String _usuarioGrupo;
    private String _usuarioAsignado;
    private String _ubicacion;

    public void setDescripcion(String descripcion)  {
        _descripcion = descripcion;
    }

    public String getDescripcion() {
        return _descripcion;
    }

    public void setCategoria(String categoria) {
        _categoria = categoria;
    }

    public String getCategoria() {
        return _categoria;
    }

    public void setUbicacion(String ubicacion) {
        _ubicacion = ubicacion;
    }

    public String getUbicacion() {
        return _ubicacion;
    }

    public void setCoordLat(double coord) {
        _coordLat = coord;
    }

    public void setCoordLon(double coord) {
        _coordLon = coord;
    }

    public double getCoordLat() {
        return _coordLat;
    }

    public double getCoordLon() {
        return _coordLon;
    }

    private void setUsuarioGrupo() {
        _usuarioGrupo = getUsuarioAsignado() + "_" + getGrupo();
    }

    public String getUsuarioGrupo() {
        return _usuarioGrupo;
    }

    public String getUsuarioAsignado() {
        return _usuarioAsignado;
    }

    public void setUsuarioAsignado(String usuario) {
        _usuarioAsignado = usuario;
        setUsuarioGrupo();
    }

    public long getFechaFinalizacion() {
        return _fechaFinalizacion;
    }

    public void setFechaFinalizacion(long fecha) {
        _fechaFinalizacion = fecha;
    }

    public boolean getFinalizada() {
        return _finalizada;
    }

    public void setFinalizada(boolean finalizada) {
        _finalizada = finalizada;
    }

    public String getGrupo() {
        return _grupo;
    }

    public void setGrupo(String grupo) {
        _grupo = grupo;
        setUsuarioGrupo();
    }

    public String getNombre() {
        return _nombre;
    }

    public void setNombre(String nombre) {
        _nombre = nombre;
    }

    public String getUsuarioCreador() {
        return _usuarioCreador;
    }

    public void setUsuarioCreador(String usuario) {
        _usuarioCreador = usuario;
    }
}
