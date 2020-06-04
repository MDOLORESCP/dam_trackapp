package dam.trackapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import dam.trackapp.modelos.Usuario;
import dam.trackapp.servicios.ServicioUsuario;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final ServicioUsuario servicioUsuario = new ServicioUsuario(getApplicationContext());

        if (servicioUsuario.comprobarAutenticado()) {
            abrirApp();

            return;
        }

        final TextInputEditText emailTextField = findViewById(R.id.emailTextField);
        final TextInputEditText passwordTextField = findViewById(R.id.passwordTextField);

        final Button iniciarSesionButton = findViewById(R.id.iniciarSesionButton);

        findViewById(R.id.login_recuperar_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RecuperarActivity.class);

                startActivity(intent);
            }
        });

        findViewById(R.id.login_crear_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegistrarActivity.class);

                startActivity(intent);
            }
        });

        final Activity activity = this;

        iniciarSesionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String email = emailTextField.getText().toString();
                    String password = passwordTextField.getText().toString();

                    if (!new Usuario(email, password).validarCredenciales()) {
                        Toast.makeText(getApplicationContext(), getString(R.string.error_datos_usuario_login), Toast.LENGTH_LONG).show();
                        return;
                    }

                    servicioUsuario.iniciarSesion(email, password)
                        .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                FirebaseUser user = servicioUsuario.obtenerUsuario();

                                if (user == null) {
                                    Toast.makeText(activity, getString(R.string.error_usuario_no_encontrado), Toast.LENGTH_LONG).show();
                                    return;
                                }

                                if (!user.isEmailVerified()) {
                                    Toast.makeText(activity, getString(R.string.error_email_no_verificado), Toast.LENGTH_LONG).show();
                                    servicioUsuario.enviarEmailVerificacion();
                                    return;
                                }

                                abrirApp();

                            }
                        })
                        .addOnFailureListener(activity, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(activity, getString(R.string.error_inicio_sesion), Toast.LENGTH_LONG).show();
                            }
                        });
                } catch (Exception e) {
                    Toast.makeText(activity, getString(R.string.error_inicio_sesion), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void abrirApp() {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);

        startActivity(intent);

        finish();
    }
}
