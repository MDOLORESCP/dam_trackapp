package dam.trackapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dam.trackapp.dao.CategoriaDAO;
import dam.trackapp.dao.GrupoUsuarioDAO;
import dam.trackapp.dao.TareaEventoDAO;
import dam.trackapp.dao.UsuarioDAO;
import dam.trackapp.modelos.Categoria;
import dam.trackapp.modelos.GrupoUsuario;
import dam.trackapp.modelos.TareaEvento;
import dam.trackapp.modelos.Usuario;
import dam.trackapp.servicios.ServicioNotificaciones;
import dam.trackapp.servicios.ServicioUsuario;

public class TareaEventoDetalleActivity extends AppCompatActivity {
    public final static int ACTIVITY_RESULT_CALENDARIO = 1;
    public final static String ACTIVITY_RESULT_CALENDARIO_VALUE = "VALUE";

    public final static String PARAM_ID = "TAREA_ID";
    public final static String PARAM_ID_GRUPO = "GRUPO_ID";

    private String TareaId = null;
    private String GrupoId = null;
    private double Lat;
    private double Lon;
    private String Ubicacion;

    private List<Categoria> Categorias;
    private List<Usuario> Usuarios;

    private int DiaSeleccionado;
    private int MesSeleccionado;
    private int AnioSeleccionado;
    private int HoraSeleccionada = 0;
    private int MinutoSeleccionado = 0;

    private Calendar getSelectedDate() {
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.YEAR, AnioSeleccionado);
        cal.set(Calendar.MONTH, MesSeleccionado);
        cal.set(Calendar.DAY_OF_MONTH, DiaSeleccionado);
        cal.set(Calendar.HOUR_OF_DAY, HoraSeleccionada);
        cal.set(Calendar.MINUTE, MinutoSeleccionado);
        cal.set(Calendar.SECOND, 0);

        return cal;
    }


    private void setSelectedDate(long milliseconds) {
        Calendar cal = Calendar.getInstance();

        cal.setTimeInMillis(milliseconds);

        AnioSeleccionado = cal.get(Calendar.YEAR);
        MesSeleccionado = cal.get(Calendar.MONTH);
        DiaSeleccionado = cal.get(Calendar.DAY_OF_MONTH);
        HoraSeleccionada = cal.get(Calendar.HOUR_OF_DAY);
        MinutoSeleccionado = cal.get(Calendar.MINUTE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarea_evento_detalle);

        Intent intent = getIntent();

        TareaId = intent.getStringExtra(PARAM_ID);
        GrupoId = intent.getStringExtra(PARAM_ID_GRUPO);



        if (GrupoId == null) {
            // Como no tiene grupo, el usuario siempre va a ser el autenticado
            findViewById(R.id.usuario_dropdown).setVisibility(View.INVISIBLE);
            new CategoriaDAO().obtenerDeUsuario(new ServicioUsuario(getApplicationContext()).obtenerUsuario().getUid(), CategoriasEventListener(this));
        } else {
            new CategoriaDAO().obtenerDeGrupo(GrupoId, CategoriasEventListener(this));
            new GrupoUsuarioDAO().obtenerDeGrupo(GrupoId, UsuariosGrupoEventListener(TareaEventoDetalleActivity.this));
        }

        if (TareaId != null && !TareaId.isEmpty()) {
            if (!cargarTarea(TareaId)) {
                Toast.makeText(getBaseContext(), getString(R.string.error_carga_datos), Toast.LENGTH_SHORT).show();
                cerrar();
            }
        }
//        } else {
//
//        }

        final Button finalizarButton = findViewById(R.id.tarea_detalle_finalizar_button);
        final Button guardarButton = findViewById(R.id.tarea_detalle_guardar_button);
        final EditText fechaFinalizacionField = findViewById(R.id.tarea_detalle_fecha_finalizacion);
        final EditText horaField = findViewById(R.id.tarea_detalle_fecha_hora);
        final EditText ubicacionField = findViewById(R.id.tarea_detalle_ubicacion_field);

        fechaFinalizacionField.setEnabled(true);
        fechaFinalizacionField.setFocusable(false);
        horaField.setEnabled(true);
        horaField.setFocusable(false);

        finalizarButton.setEnabled(TareaId != null && !TareaId.isEmpty());

        finalizarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (finalizar()) {
                cerrarFinalizado();
            } else {
                cerrarConErrorFinalizado();
            }
            }
        });

        guardarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (guardar()) {
                cerrarGuardado();
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.error_guardado), Toast.LENGTH_SHORT).show();
            }
            }
        });

        fechaFinalizacionField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoFecha();
            }
        });

        ubicacionField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);

                intent.putExtra(MapsActivity.PARAM_LAT, Lat);
                intent.putExtra(MapsActivity.PARAM_LON, Lon);

            startActivityForResult(intent, 0);

            }
        });

        horaField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoHora();
            }
        });
    }

    private void mostrarDialogoFecha() {
        final Calendar newCalendar = Calendar.getInstance();

        DatePickerDialog recogerFecha = new DatePickerDialog(TareaEventoDetalleActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                DiaSeleccionado = dayOfMonth;
                MesSeleccionado = month;
                AnioSeleccionado = year;

                setFechaFinalizacion();
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        recogerFecha.getDatePicker().setMinDate(newCalendar.getTimeInMillis());

        recogerFecha.show();
    }

    private void mostrarDialogoHora() {
        final Calendar newCalendar = Calendar.getInstance();

        TimePickerDialog recogerHora = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                HoraSeleccionada = hourOfDay;
                MinutoSeleccionado = minute;

                setFechaFinalizacion();


            }
        }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), false);

        recogerHora.show();
    }

    private boolean guardar() {
        return guardar(false);
    }

    private boolean guardar(boolean finalizar) {

        TareaEvento tarea = new TareaEvento();

        Date date = new Date(getSelectedDate().getTimeInMillis());
        tarea.setId(TareaId);

        tarea.setNombre(((EditText)findViewById(R.id.tarea_detalle_nombre_text)).getText().toString());
        tarea.setFechaFinalizacion(date.getTime());
        tarea.setGrupo(GrupoId);
        tarea.setUbicacion(Ubicacion);
        tarea.setCoordLat(Lat);
        tarea.setCoordLon(Lon);
        tarea.setDescripcion(((EditText)findViewById(R.id.tarea_detalle_descripcion)).getText().toString());

        String categoriaSeleccionada =
                ((AutoCompleteTextView)findViewById(R.id.categoria_dropdown)).getText().toString();

        String usuarioSeleccionado =
                ((AutoCompleteTextView)findViewById(R.id.usuario_dropdown)).getText().toString();

        String catSel = null;
        String usuarioSel = GrupoId == null ? new ServicioUsuario(getApplicationContext()).obtenerUsuario().getUid() : null;

        for (Categoria categoria : Categorias) {
            if (categoria.getNombre().equals(categoriaSeleccionada)) {
                catSel = categoria.getId();
                break;
            }
        }

        if (GrupoId != null) {
            for (Usuario usuario : Usuarios) {
                if (usuario.getNombre().equals(usuarioSeleccionado)) {
                    usuarioSel = usuario.getId();
                    break;
                }
            }
        }

        tarea.setUsuarioAsignado(usuarioSel);
        tarea.setCategoria(catSel);

        if (
                tarea.getNombre() == null ||
                tarea.getNombre().isEmpty() ||
                tarea.getNombre().length() < 4 ||
                tarea.getFechaFinalizacion() == 0 ||
                tarea.getUsuarioAsignado() == null ||
                tarea.getUsuarioAsignado().isEmpty()
        ) {
            Toast.makeText(getApplicationContext(), getString(R.string.error_rellenar_todos_los_campos), Toast.LENGTH_LONG).show();

            return false;
        }


        if (finalizar) {
            tarea.setFinalizada(true);
        }

        ServicioNotificaciones servicioNotificaciones = new ServicioNotificaciones();

        tarea.setUsuarioCreador(new ServicioUsuario(getBaseContext()).obtenerUsuario().getUid());

        boolean correcto = false;

        // Guardar
        if (TareaId == null) {
            correcto = new TareaEventoDAO().crear(tarea) != null;
        }
        else {
            correcto = new TareaEventoDAO().actualizar(tarea) != null;
        }

        return correcto;
    }

    private boolean finalizar() {
        if (!guardar(true)) {
            return false;
        }

        return true;
    }

    private boolean cargarTarea(final String id) {
        try {
            final Activity activity = TareaEventoDetalleActivity.this;

            new TareaEventoDAO().obtenerPorId(id, new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (!dataSnapshot.exists()) {
                            cerrarConErrorCargaDatos();
                            return;
                        }

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            TareaEvento tarea = snapshot.getValue(TareaEvento.class);

                            ((TextView)findViewById(R.id.tarea_detalle_nombre_text)).setText(tarea.getNombre());

                            if (tarea.getFinalizada()) {
                                findViewById(R.id.tarea_detalle_finalizar_button).setEnabled(false);
                                findViewById(R.id.tarea_detalle_guardar_button).setEnabled(false);
                            }

                            ((EditText)findViewById(R.id.tarea_detalle_descripcion)).setText(tarea.getDescripcion());
                            ((EditText)findViewById(R.id.tarea_detalle_ubicacion_field)).setText(tarea.getUbicacion());

                            if (tarea.getCategoria() != null) {
                                new CategoriaDAO().obtenerPorId(tarea.getCategoria(), new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot != null) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                Categoria categoria = snapshot.getValue(Categoria.class);

                                                ((AutoCompleteTextView)findViewById(R.id.categoria_dropdown)).setText(categoria.getNombre());

                                                break;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }



                            if (tarea.getUsuarioAsignado() != null && GrupoId != null && !GrupoId.isEmpty()) {
                                new UsuarioDAO().obtenerPorId(tarea.getUsuarioAsignado(), new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot != null) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                Usuario usuario = snapshot.getValue(Usuario.class);

                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                                    ((AutoCompleteTextView)findViewById(R.id.usuario_dropdown)).setText(usuario.getNombre(), false);
                                                }

                                                break;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }

                            Lat = tarea.getCoordLat();
                            Lon = tarea.getCoordLon();

                            Ubicacion = tarea.getUbicacion();

                            setSelectedDate(tarea.getFechaFinalizacion());

                            // Si tiene grupo, hay que cargar categorías y usuarios del grupo
                            if (tarea.getGrupo() != null && !tarea.getGrupo().isEmpty()) {
                                new CategoriaDAO().obtenerDeGrupo(tarea.getGrupo(), CategoriasEventListener(activity));
                            }
                            else {
                                // Como no tiene grupo, el usuario siempre va a ser el autenticado
                                findViewById(R.id.usuario_dropdown).setEnabled(false);

                                new CategoriaDAO().obtenerDeUsuario(new ServicioUsuario(getApplicationContext()).obtenerUsuario().getUid(), CategoriasEventListener(activity));
                            }

                            setFechaFinalizacion();

                            break;
                        }
                    } catch (Exception e) {
                        cerrarConErrorCargaDatos();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    cerrarConErrorCargaDatos();
                }
            });

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void setFechaFinalizacion() {
        String horaFormateada =  (HoraSeleccionada < 10)? String.valueOf("0" + HoraSeleccionada) : String.valueOf(HoraSeleccionada);

        String minutoFormateado = (MinutoSeleccionado < 10)? String.valueOf("0" + MinutoSeleccionado) : String.valueOf(MinutoSeleccionado);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        ((EditText) findViewById(R.id.tarea_detalle_fecha_finalizacion)).setText(dateFormat.format(new Date(getSelectedDate().getTimeInMillis())));
        ((EditText) findViewById(R.id.tarea_detalle_fecha_hora)).setText(horaFormateada + ":" + minutoFormateado);
    }

    private void cerrarConErrorCargaDatos() {
        cerrarConMensaje(getString(R.string.error_carga_datos));
    }

    private void cerrarGuardado() {
        cerrarConMensaje(getString(R.string.guardado));
    }

    private void cerrarFinalizado() {
        cerrarConMensaje(getString(R.string.finalizado));
    }

    private void cerrarConErrorFinalizado() {
        cerrarConMensaje(getString(R.string.error_finalizar));
    }

    private void cerrarConMensaje(String mensaje) {
        Toast.makeText(getBaseContext(), mensaje, Toast.LENGTH_SHORT).show();

        cerrar();
    }

    private void cerrar() {
        Intent intent = new Intent(TareaEventoDetalleActivity.this, MainActivity.class);

        if (GrupoId != null && !GrupoId.isEmpty()) {
            intent.putExtra(MainActivity.PARAM_GRUPO_ID, GrupoId);
        }

        startActivity(intent);

        finish();
    }

    @Override
    public void onBackPressed() {
        cerrar();
    }

    private ValueEventListener CategoriasEventListener(final Activity activity) {

        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<Categoria> categorias = new ArrayList<>();
                final List<String> nombres = new ArrayList<>();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        categorias.add(snapshot.getValue(Categoria.class));
                        nombres.add(snapshot.getValue(Categoria.class).getNombre());
                    }
                }

                Categorias = categorias;

                final AutoCompleteTextView editTextFilledExposedDropdown =
                        findViewById(R.id.categoria_dropdown);

                editTextFilledExposedDropdown.setAdapter(new ArrayAdapter(activity, R.layout.categoria_dropdown_list_item, R.id.categoria_dropdown_text, nombres.toArray(new String[nombres.size()])));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_carga_datos), Toast.LENGTH_SHORT);
            }
        };
    }

    private ValueEventListener UsuariosGrupoEventListener(final Activity activity) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<GrupoUsuario> usuariosGrupo = new ArrayList<>();
                final List<Usuario> usuarios = new ArrayList<>();
                final List<String> nombres = new ArrayList<>();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        usuariosGrupo.add(snapshot.getValue(GrupoUsuario.class));
                    }
                }

                for (final GrupoUsuario usuarioGrupo : usuariosGrupo) {
                    new UsuarioDAO().obtenerPorId(usuarioGrupo.getUsuario(), new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                return;
                            }

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                usuarios.add(snapshot.getValue(Usuario.class));
                                nombres.add(snapshot.getValue(Usuario.class).getNombre());

                                if (usuarios.size() == usuariosGrupo.size()) {
                                    Usuarios = usuarios;

                                    final AutoCompleteTextView editTextFilledExposedDropdown =
                                            findViewById(R.id.usuario_dropdown);

                                    editTextFilledExposedDropdown.setAdapter(new ArrayAdapter(activity, R.layout.usuario_dropdown_list_item, R.id.usuario_dropdown_text, nombres.toArray(new String[nombres.size()])));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_carga_datos), Toast.LENGTH_SHORT);
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if(resultCode == Activity.RESULT_OK){
                Ubicacion = data.getStringExtra(MapsActivity.PARAM_NOMBRE);
                Lat = data.getDoubleExtra(MapsActivity.PARAM_LAT, 0);
                Lon = data.getDoubleExtra(MapsActivity.PARAM_LON, 0);
                ((EditText)findViewById(R.id.tarea_detalle_ubicacion_field)).setText(Ubicacion);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
}
