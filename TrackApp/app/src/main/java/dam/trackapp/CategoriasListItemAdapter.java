package dam.trackapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import dam.trackapp.modelos.Categoria;

public class CategoriasListItemAdapter extends ArrayAdapter<Categoria> {
    private final Activity _context;
    private final Categoria[] Categorias;
    private final ListItemAdapterOnClick<Categoria> OnEditClick;
    private final ListItemAdapterOnClick<Categoria> OnDeleteClick;

    public CategoriasListItemAdapter(Activity context, Categoria[] categorias, ListItemAdapterOnClick<Categoria> onEditCLick, ListItemAdapterOnClick<Categoria> onDeleteClick) {
        super(context, R.layout.tareas_list_item, categorias);

        _context = context;
        Categorias = categorias;
        OnEditClick = onEditCLick;
        OnDeleteClick = onDeleteClick;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = _context.getLayoutInflater();

        View rowView = inflater.inflate(R.layout.categorias_list_item, null, true);

        Categoria categoria = Categorias[position];

        ((TextView) rowView.findViewById(R.id.categorias_list_item_nombre_categoria)).setText(categoria.getNombre());

        rowView.findViewById(R.id.categoria_borrar_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnDeleteClick.OnClick(position, Categorias);
            }
        });

        rowView.findViewById(R.id.categoria_editar_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnEditClick.OnClick(position, Categorias);
            }
        });

        return rowView;
    }
}
