package dam.trackapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import dam.trackapp.servicios.ServicioUsuario;

public class RecuperarActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar);

        findViewById(R.id.recuperar_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ((EditText)findViewById(R.id.recuperar_email)).getText().toString();

                if (email == null || email.isEmpty() || email.length() < 4) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_email), Toast.LENGTH_SHORT).show();

                    return;
                }

                new ServicioUsuario(getApplicationContext()).enviarEmailRecuperacion(email);

                Toast.makeText(getApplicationContext(), getString(R.string.email_recuperacion_enviado), Toast.LENGTH_SHORT).show();

                finish();
            }
        });
    }
}
