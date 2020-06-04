package dam.trackapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dam.trackapp.dao.GrupoUsuarioDAO;
import dam.trackapp.dao.UsuarioDAO;
import dam.trackapp.modelos.GrupoUsuario;
import dam.trackapp.modelos.Usuario;

public class BuscarUsuarioActivity extends AppCompatActivity {
    public final static String PARAM_GRUPO_ID = "GRUPO_ID";

    private String GrupoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_usuario);

        GrupoId = getIntent().getStringExtra(PARAM_GRUPO_ID);

        if (GrupoId == null || GrupoId.isEmpty()) {
            finish();

            return;
        }

        findViewById(R.id.buscar_usuario_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarUsuario();
            }
        });
    }

    private void buscarUsuario() {
        String email = ((EditText)findViewById(R.id.buscar_usuario_email)).getText().toString();

        if (!Usuario.validarEmail(email)) {
            Toast.makeText(getApplicationContext(), getString(R.string.error_email), Toast.LENGTH_SHORT).show();

            return;
        }

        new UsuarioDAO().obtenerConEmail(email, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_usuario_no_encontrado), Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Usuario> usuarios = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    usuarios.add(snapshot.getValue(Usuario.class));
                }

                BuscarUsuariosListItemAdapter adapter = new BuscarUsuariosListItemAdapter(
                        BuscarUsuarioActivity.this,
                        usuarios.toArray(new Usuario[usuarios.size()]),
                        GrupoId,
                        new ListItemAdapterOnClick<Usuario>() {
                            @Override
                            public void OnClick(int postition, Usuario[] items) {
                                guardar(items[postition].getId());
                            }
                        });

                ListView list = BuscarUsuarioActivity.this.findViewById(R.id.buscar_usuario_listview);

                list.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void guardar(String usuarioId) {
        new GrupoUsuarioDAO().crear(new GrupoUsuario(GrupoId, usuarioId));

        Toast.makeText(getApplicationContext(),  getString(R.string.usuario_agregado), Toast.LENGTH_SHORT).show();

        buscarUsuario();
    }
}
