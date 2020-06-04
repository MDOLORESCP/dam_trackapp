package dam.trackapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dam.trackapp.dao.GrupoDAO;
import dam.trackapp.dao.GrupoUsuarioDAO;
import dam.trackapp.modelos.Grupo;
import dam.trackapp.modelos.GrupoUsuario;
import dam.trackapp.servicios.ServicioNotificaciones;
import dam.trackapp.servicios.ServicioUsuario;

public class GrupoActivity extends AppCompatActivity {
    public static String PARAM_GRUPO_ID = "GRUPO_ID";

    private String GrupoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo);

        Intent intent = getIntent();

        GrupoId = intent.getStringExtra(PARAM_GRUPO_ID);

        Button guardarButton = findViewById(R.id.guardar_grupo_button);

        guardarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = ((TextInputEditText)findViewById(R.id.grupo_nombre_field)).getText().toString();

                if (nombre == null || nombre.isEmpty() || nombre.length() < 4) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_nombre_grupo), Toast.LENGTH_LONG).show();

                    return;
                }

                Grupo grupo = new Grupo();

                grupo.setId(GrupoId);
                grupo.setNombre(nombre);

                if (GrupoId != null && !GrupoId.isEmpty()) {
                    grupo = new GrupoDAO().actualizar(grupo);
                } else {
                    grupo = new GrupoDAO().crear(grupo);

                    new ServicioNotificaciones().suscribirATopicGrupo(grupo.getId());

                    new GrupoDAO().agregarUsuario(grupo.getId(), new ServicioUsuario(getBaseContext()).obtenerUsuario().getUid().toString());
                }

                volver(grupo.getId());
            }
        });

        findViewById(R.id.aregar_usuario_button).setVisibility(GrupoId != null && !GrupoId.isEmpty() ? View.VISIBLE : View.INVISIBLE);
        findViewById(R.id.aregar_usuario_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), BuscarUsuarioActivity.class);

            intent.putExtra(BuscarUsuarioActivity.PARAM_GRUPO_ID, GrupoId);

            startActivity(intent);
            }
        });

        Button eliminar = findViewById(R.id.eliminar_grupo_button);

        eliminar.setEnabled(GrupoId != null && !GrupoId.isEmpty());

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            new GrupoDAO().borrar(GrupoId);

            new ServicioNotificaciones().borrarSuscripcionTopicGrupo(GrupoId);

            volver();
            }
        });

        if (GrupoId != null && !GrupoId.isEmpty()) {
            new GrupoDAO().obtenerPorId(GrupoId, new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        return;
                    }

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Grupo grupo = snapshot.getValue(Grupo.class);

                        ((TextView)findViewById(R.id.grupo_nombre_field)).setText(grupo.getNombre());

                        break;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            cargarGrupos();
        }
    }

    @Override
    public void onBackPressed() {
        volver(GrupoId);
    }

    private void volver() {
        volver("");
    }

    private void volver(String id) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        intent.putExtra(MainActivity.PARAM_GRUPO_ID, id == null ? GrupoId : id);

        startActivity(intent);

        finish();
    }

    private void cargarGrupos() {
        new GrupoDAO().obtenerDeGrupo(GrupoId, new ValueEventListener() {
            List<String> usuarios = new ArrayList<>();
            List<GrupoUsuario> grupoUsuarios = new ArrayList<>();

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    return;
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    usuarios.add(snapshot.getValue(GrupoUsuario.class).getUsuario());
                    grupoUsuarios.add(snapshot.getValue(GrupoUsuario.class));
                }


                UsuariosGruposListItemAdapter adapter = new UsuariosGruposListItemAdapter(GrupoActivity.this, usuarios.toArray(new String[usuarios.size()]), new ListItemAdapterOnClick<String>() {
                    @Override
                    public void OnClick(int postition, String[] items) {
                        for (GrupoUsuario grupoUsuario : grupoUsuarios) {
                            if (grupoUsuario.getGrupo().equals(GrupoId) && grupoUsuario.getUsuario().equals(items[postition])) {
                                new GrupoUsuarioDAO().borrar(grupoUsuario.getId());

                                if (grupoUsuario.getUsuario().equals(new ServicioUsuario(getApplicationContext()).obtenerUsuario().getUid()))
                                {
                                    volver();

                                    break;
                                }

                                cargarGrupos();

                                break;
                            }
                        }
                    }
                });

                ListView list = GrupoActivity.this.findViewById(R.id.list_usuarios);

                list.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
