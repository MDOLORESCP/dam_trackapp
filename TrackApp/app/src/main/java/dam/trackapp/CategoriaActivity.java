package dam.trackapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import dam.trackapp.dao.CategoriaDAO;
import dam.trackapp.modelos.Categoria;
import dam.trackapp.servicios.ServicioUsuario;

public class CategoriaActivity extends AppCompatActivity {
    public static String PARAM_GRUPO_ID = "GRUPO_ID";
    public static String PARAM_CATEGORIA_ID = "CATEGORIA_ID";

    private String GrupoId;
    private String CategoriaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria);

        Intent intent = getIntent();

        GrupoId = intent.getStringExtra(PARAM_GRUPO_ID);
        CategoriaId = intent.getStringExtra(PARAM_CATEGORIA_ID);

        findViewById(R.id.guardar_Categoria_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();

                finish();
            }
        });

        if (CategoriaId != null && !CategoriaId.isEmpty()) {
            new CategoriaDAO().obtenerPorId(CategoriaId, new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Categoria categoria = snapshot.getValue(Categoria.class);

                            ((TextView)findViewById(R.id.categoria_nombre)).setText(categoria.getNombre());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void guardar() {
        String nombre = ((EditText)findViewById(R.id.categoria_nombre)).getText().toString();

        if (nombre == null || nombre.isEmpty() || nombre.length() < 4) {

            Toast.makeText(getApplicationContext(), getString(R.string.error_nombre_categoria), Toast.LENGTH_LONG).show();

            return;
        }

        Categoria categoria = new Categoria();

        categoria.setNombre(nombre);

        if (GrupoId != null && !GrupoId.isEmpty()) {
            categoria.setGrupo(GrupoId);
        } else {
            categoria.setUsuario(new ServicioUsuario(getApplicationContext()).obtenerUsuario().getUid());
        }

        if (CategoriaId != null && !CategoriaId.isEmpty()) {
            categoria.setId(CategoriaId);

            new CategoriaDAO().actualizar(categoria);
        } else {
            new CategoriaDAO().crear(categoria);
        }
    }
}
