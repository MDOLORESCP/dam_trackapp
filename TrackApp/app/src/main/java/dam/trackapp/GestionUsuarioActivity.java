package dam.trackapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import dam.trackapp.servicios.ServicioUsuario;

public class GestionUsuarioActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_usuario);

        AlertDialog.Builder builder = new AlertDialog.Builder(GestionUsuarioActivity.this);

        builder.setMessage(getString(R.string.mensaje_usuario_eliminar))
                .setTitle(getString(R.string.usuario_eliminar));

        final AlertDialog dialog = builder.create();

        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.si), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new ServicioUsuario(getApplicationContext()).obtenerUsuario().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), getString(R.string.usuario_eliminado), Toast.LENGTH_LONG).show();
                        cerrarSesion();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.error_eliminar_usuario), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });



        findViewById(R.id.cerrar_sesion_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
            }
        });

        findViewById(R.id.eliminar_cuenta_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        findViewById(R.id.cambiar_password_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServicioUsuario servicioUsuario = new ServicioUsuario(getApplicationContext());

                servicioUsuario.enviarEmailRecuperacion(servicioUsuario.obtenerUsuario().getEmail());

                Toast.makeText(getApplicationContext(), getString(R.string.email_cambio_contrasena_enviado), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        startActivity(intent);

        finish();
    }

    private void cerrarSesion() {
        new ServicioUsuario(getApplicationContext()).cerrarSesion();

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

        startActivity(intent);


        finish();
    }
}
