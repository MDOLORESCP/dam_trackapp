package dam.trackapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import dam.trackapp.dao.UsuarioDAO;
import dam.trackapp.modelos.Usuario;
import dam.trackapp.servicios.ServicioUsuario;
import com.google.firebase.auth.AuthResult;

public class RegistrarActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        findViewById(R.id.registrar_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Usuario usuario = new Usuario(
                    ((EditText)findViewById(R.id.registrar_email)).getText().toString(),
                    ((EditText)findViewById(R.id.registrar_password)).getText().toString(),
                    ((EditText)findViewById(R.id.registrar_nombre)).getText().toString()
                );

                if (!Usuario.validarEmail(usuario.getEmail())) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_email), Toast.LENGTH_LONG).show();

                    return;
                }

                if (!Usuario.validarContrasena(usuario.getPassword())) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_contrasena), Toast.LENGTH_LONG).show();

                    return;
                }

                if (usuario.getNombre() == null || usuario.getNombre().isEmpty() || usuario.getNombre().length() < 4) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_nombre_usuario), Toast.LENGTH_LONG).show();

                    return;
                }

                try {
                    new ServicioUsuario(getApplicationContext()).crearUsuario(usuario, usuario.getPassword(),
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    try {
                                        final ServicioUsuario servicioUsuario = new ServicioUsuario(getApplicationContext());

                                        servicioUsuario.iniciarSesion(usuario.getEmail(), usuario.getPassword())
                                                .addOnCompleteListener(RegistrarActivity.this, new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                                        new UsuarioDAO().crear(usuario, task.getResult().getUser().getUid());

                                                        servicioUsuario.enviarEmailVerificacion();

                                                        Toast.makeText(RegistrarActivity.this, getString(R.string.error_email_no_verificado), Toast.LENGTH_LONG).show();

                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(RegistrarActivity.this, new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(RegistrarActivity.this, getString(R.string.error_crear_usuario), Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                    } catch (Exception e) {
                                        Toast.makeText(getApplicationContext(), getString(R.string.error_crear_usuario), Toast.LENGTH_LONG).show();
                                    }
                                }
                            },
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), getString(R.string.error_crear_usuario), Toast.LENGTH_LONG).show();
                                }
                            });
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_crear_usuario), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
