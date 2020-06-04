package dam.trackapp.modelos;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Usuario extends Modelo {
    private String _email;
    private String _password;
    private String _nombre;

    public String getEmail() {
        return _email;
    }

    public void setEmail(String email) {
        _email = email;
    }

    public String getPassword() {
        return _password;
    }

    public void setPassword(String password) {
        _password = password;
    }

    public String getNombre() {
        return _nombre;
    }

    public void setNombre(String nombre) {
        _nombre = nombre;
    }

    public Usuario() {

    }

    public Usuario(String email, String password) {
        setEmail(email);
        setPassword(password);
    }

    public Usuario(String email, String password, String nombre) {
        setEmail(email);
        setPassword(password);
        setNombre(nombre);
    }

    public boolean validarCredenciales() {
        return  validarEmail(_email) && validarContrasena(_password);
    }

    public static boolean validarEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        return email != null && !email.isEmpty() && email.matches(emailPattern);
    }

    public static boolean validarContrasena(String contrasena) {
        String contrasenaPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";

        return contrasena != null && !contrasena.isEmpty() && contrasena.matches(contrasenaPattern);
    }
}
