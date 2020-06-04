
package dam.trackapp.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import dam.trackapp.ListItemAdapterOnClick;
import dam.trackapp.R;
import dam.trackapp.TareaEventoDetalleActivity;
import dam.trackapp.TareasListItemAdapter;
import dam.trackapp.dao.TareaEventoDAO;
import dam.trackapp.modelos.TareaEvento;
import dam.trackapp.servicios.ServicioUsuario;

public class ListadoTareasFragment extends Fragment {
    public static final String PARAM_GRUPO_ID = "GRUPO_ID";

    private String GrupoID;

    private ListadoTareasViewModel homeViewModel;

    private List<TareaEvento> Tareas = new ArrayList<>();
    private TareasListItemAdapter Adapter = null;
    private ListView List;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(ListadoTareasViewModel.class);
        View root = inflater.inflate(R.layout.fragment_listado_tareas, container, false);


        Bundle bundle = getArguments();

        if (bundle != null) {
            GrupoID = bundle.getString(PARAM_GRUPO_ID);
        }



        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        prepList();
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        prepList();
//
////        Adapter.notifyDataSetChanged();
//    }

    public void prepList() {
        final Activity activity = getActivity();

        if (activity == null) {
            return;
        }

        if (List == null) {
            List = getActivity().findViewById(R.id.listview_tareas);
        }

        Tareas.clear();

        if (Adapter != null) {
            Adapter.notifyDataSetChanged();
        }

        if (GrupoID == null || GrupoID.isEmpty()) {
            new TareaEventoDAO().obtenerDeUsuario(new ServicioUsuario(getContext()).obtenerUsuario().getUid(), TareasEventListener(activity));
        }
        else {
            new TareaEventoDAO().obtenerDeGrupo(GrupoID, TareasEventListener(activity));
        }
    }

    private ValueEventListener TareasEventListener(final Activity activity) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Tareas.add(snapshot.getValue(TareaEvento.class));
                    }
                }

                Collections.sort(Tareas, new Comparator<TareaEvento>() {
                    @Override
                    public int compare(TareaEvento o1, TareaEvento o2) {
                        return new Date(o2.getFechaFinalizacion()).compareTo(new Date(o1.getFechaFinalizacion()));
                    }
                });

                if (Adapter == null) {
                    Adapter = new TareasListItemAdapter(activity, Tareas.toArray(new TareaEvento[Tareas.size()]), new ListItemAdapterOnClick<TareaEvento>() {
                        @Override
                        public void OnClick(int postition, TareaEvento[] items) {
                            Intent intent = new Intent(getContext(), TareaEventoDetalleActivity.class);

                            intent.putExtra(TareaEventoDetalleActivity.PARAM_ID, items[postition].getId());
                            intent.putExtra(TareaEventoDetalleActivity.PARAM_ID_GRUPO, GrupoID);

                            startActivity(intent);

                            activity.finish();
                        }
                    });

                    List.setAdapter(Adapter);
                }

                Adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Ocurrió un error al cargar los datos", Toast.LENGTH_SHORT);
            }
        };
    }
}
