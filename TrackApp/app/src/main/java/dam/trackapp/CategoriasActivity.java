package dam.trackapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dam.trackapp.dao.CategoriaDAO;
import dam.trackapp.modelos.Categoria;
import dam.trackapp.servicios.ServicioUsuario;

public class CategoriasActivity extends AppCompatActivity {
    public static String PARAM_GRUPO_ID = "GRUPO_ID";

    private String GrupoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);

        Intent intent = getIntent();

        GrupoId = intent.getStringExtra(PARAM_GRUPO_ID);

        findViewById(R.id.categorias_crear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CategoriaActivity.class);

                intent.putExtra(CategoriaActivity.PARAM_GRUPO_ID, GrupoId);

                startActivity(intent);
            }
        });

        prepList();
    }

    @Override
    public void onResume() {
        super.onResume();
        prepList();
    }

    private void prepList() {
        if (GrupoId != null && !GrupoId.isEmpty()) {
            new CategoriaDAO().obtenerDeGrupo(GrupoId, CategoriasEventListener(CategoriasActivity.this));
        } else {
            new CategoriaDAO().obtenerDeUsuario(new ServicioUsuario(getApplicationContext()).obtenerUsuario().getUid(), CategoriasEventListener(CategoriasActivity.this));
        }
    }

    private ValueEventListener CategoriasEventListener(final Activity activity) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Categoria> categorias = new ArrayList<>();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        categorias.add(snapshot.getValue(Categoria.class));
                    }
                }

                CategoriasListItemAdapter adapter = new CategoriasListItemAdapter(activity, categorias.toArray(new Categoria[categorias.size()]), new ListItemAdapterOnClick<Categoria>() {
                    @Override
                    public void OnClick(int postition, Categoria[] items) {
                        Intent intent = new Intent(getApplicationContext(), CategoriaActivity.class);

                        intent.putExtra(CategoriaActivity.PARAM_GRUPO_ID, GrupoId);
                        intent.putExtra(CategoriaActivity.PARAM_CATEGORIA_ID, items[postition].getId());

                        startActivity(intent);
                    }
                }, new ListItemAdapterOnClick<Categoria>() {
                    @Override
                    public void OnClick(int postition, Categoria[] items) {
                        new CategoriaDAO().borrar(items[postition].getId());
                    }
                });

                ListView list = activity.findViewById(R.id.list_categorias);

                list.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_carga_datos), Toast.LENGTH_SHORT);
            }
        };
    }
}
