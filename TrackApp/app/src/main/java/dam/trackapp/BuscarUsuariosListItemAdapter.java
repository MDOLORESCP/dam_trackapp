package dam.trackapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import dam.trackapp.dao.GrupoUsuarioDAO;
import dam.trackapp.modelos.GrupoUsuario;
import dam.trackapp.modelos.Usuario;

public class BuscarUsuariosListItemAdapter extends ArrayAdapter<Usuario> {
    private final Activity _context;
    private final Usuario[] Usuarios;
    private final ListItemAdapterOnClick<Usuario> OnClick;
    private final String GrupoId;

    public BuscarUsuariosListItemAdapter(Activity context, Usuario[] usuarios, String grupo, ListItemAdapterOnClick<Usuario> onClick) {
        super(context, R.layout.tareas_list_item, usuarios);

        _context = context;
        Usuarios = usuarios;
        OnClick = onClick;
        GrupoId = grupo;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = _context.getLayoutInflater();

        final View rowView = inflater.inflate(R.layout.buscar_usuario_list_item, null, true);

        new GrupoUsuarioDAO().obtenerDeGrupo(GrupoId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    return;
                }

                boolean estaEnElGrupo = false;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getValue(GrupoUsuario.class).getGrupo().equals(GrupoId) && snapshot.getValue(GrupoUsuario.class).getUsuario().equals(Usuarios[position].getId())) {
                        estaEnElGrupo = true;
                        break;
                    }
                }

                if (!estaEnElGrupo) {
                    rowView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            OnClick.OnClick(position, Usuarios);
                        }
                    });
                }

                ((TextView)rowView.findViewById(R.id.buscar_usuario_list_item_name)).setText(Usuarios[position].getNombre() + (estaEnElGrupo ? " - EN GRUPO" : ""));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return rowView;
    }
}
