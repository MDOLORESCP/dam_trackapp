package dam.trackapp;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import dam.trackapp.dao.CategoriaDAO;
import dam.trackapp.dao.UsuarioDAO;
import dam.trackapp.modelos.Categoria;
import dam.trackapp.modelos.TareaEvento;
import dam.trackapp.modelos.Usuario;
import dam.trackapp.servicios.ServicioUsuario;

public class TareasListItemAdapter  extends ArrayAdapter<TareaEvento> {
    private final Activity _context;
    private final TareaEvento[] Tareas;
    private final ListItemAdapterOnClick<TareaEvento> OnClick;

    public TareasListItemAdapter(Activity context, TareaEvento[] tareas, ListItemAdapterOnClick<TareaEvento> onClick) {
        super(context, R.layout.tareas_list_item, tareas);

        _context = context;
        Tareas = tareas;
        OnClick = onClick;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = _context.getLayoutInflater();

        final View rowView = inflater.inflate(R.layout.tareas_list_item, null, true);

        TareaEvento tarea = Tareas[position];

        ((TextView) rowView.findViewById(R.id.tareas_list_item_nombre_tarea)).setText(tarea.getNombre());
        final String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date(tarea.getFechaFinalizacion()));

        if (Tareas[position].getFinalizada()) {
            ((MaterialCardView)rowView.findViewById(R.id.tareas_list_item_card)).setCardBackgroundColor(Color.parseColor("#E5E5E5"));
        } else {
            if (Tareas[position].getUsuarioAsignado().equals(new ServicioUsuario(getContext()).obtenerUsuario().getUid())) {
                ((MaterialCardView)rowView.findViewById(R.id.tareas_list_item_card)).setStrokeColor(Color.parseColor("#44AFD1"));
//                #44AFD1
                ((MaterialCardView)rowView.findViewById(R.id.tareas_list_item_card)).setStrokeWidth(5);
            }
        }

        new CategoriaDAO().obtenerPorId(tarea.getCategoria(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    ((TextView) rowView.findViewById(R.id.tareas_list_item_categoria)).setText("?");
                    return;
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Categoria categoria = snapshot.getValue(Categoria.class);

                    ((TextView) rowView.findViewById(R.id.tareas_list_item_categoria)).setText(categoria.getNombre());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        new UsuarioDAO().obtenerPorId(Tareas[position].getUsuarioAsignado(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    ((TextView) rowView.findViewById(R.id.tareas_list_item_usuario_fecha)).setText("? - " + fecha);
                    return;
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Usuario usuario = snapshot.getValue(Usuario.class);

                    ((TextView) rowView.findViewById(R.id.tareas_list_item_usuario_fecha)).setText(usuario.getNombre() + " - " + fecha);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnClick.OnClick(position, Tareas);
            }
        });

        return rowView;
    }
}
