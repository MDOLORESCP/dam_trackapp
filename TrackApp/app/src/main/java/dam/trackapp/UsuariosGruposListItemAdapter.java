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

import dam.trackapp.dao.UsuarioDAO;
import dam.trackapp.modelos.Usuario;

public class UsuariosGruposListItemAdapter extends ArrayAdapter<String> {
    private final Activity _context;
    private final String[] Usuarios;
    private final ListItemAdapterOnClick<String> OnDeleteClick;

    public UsuariosGruposListItemAdapter(Activity context, String[] usuarios, ListItemAdapterOnClick<String> onDeleteClick) {
        super(context, R.layout.tareas_list_item, usuarios);

        _context = context;
        Usuarios = usuarios;
        OnDeleteClick = onDeleteClick;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = _context.getLayoutInflater();

        final View rowView = inflater.inflate(R.layout.usuarios_list_item, null, true);

        new UsuarioDAO().obtenerPorId(Usuarios[position], new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    return;
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Usuario usuario = snapshot.getValue(Usuario.class);

                    ((TextView) rowView.findViewById(R.id.list_usuarios_item_nombre)).setText(usuario.getNombre());
                    rowView.findViewById(R.id.list_usuarios_item_borrar_button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            OnDeleteClick.OnClick(position, Usuarios);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return rowView;
    }
}
