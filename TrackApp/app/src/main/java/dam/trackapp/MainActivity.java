package dam.trackapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import dam.trackapp.dao.GrupoDAO;
import dam.trackapp.dao.UsuarioDAO;
import dam.trackapp.modelos.Grupo;
import dam.trackapp.modelos.GrupoUsuario;
import dam.trackapp.modelos.Usuario;
import dam.trackapp.servicios.ServicioNotificaciones;
import dam.trackapp.servicios.ServicioUsuario;
import dam.trackapp.ui.home.ListadoTareasFragment;

import static androidx.navigation.Navigation.findNavController;

public class MainActivity extends AppCompatActivity {
    public static String PARAM_GRUPO_ID = "GRUPO_ID";

    private AppBarConfiguration mAppBarConfiguration;

    public String GrupoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ServicioUsuario servicioUsuario = new ServicioUsuario(getApplicationContext());

        if (!servicioUsuario.comprobarAutenticado()) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

            startActivity(intent);

            return;
        }

        ServicioNotificaciones servicioNotificaciones = new ServicioNotificaciones();

        servicioNotificaciones.borrarInstanciaNotificaciones();

        servicioNotificaciones.suscribirATopicUsuario(servicioUsuario.obtenerUsuario().getUid());

        Intent intent = getIntent();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        GrupoId = intent.getStringExtra(PARAM_GRUPO_ID);

        findViewById(R.id.group_options_button).setVisibility(GrupoId == null ? View.INVISIBLE : View.VISIBLE);

        findViewById(R.id.group_options_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GrupoActivity.class);

                intent.putExtra(GrupoActivity.PARAM_GRUPO_ID, GrupoId);

                startActivity(intent);

                finish();
            }
        });

        findViewById(R.id.tags_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CategoriasActivity.class);

                intent.putExtra(CategoriasActivity.PARAM_GRUPO_ID, GrupoId);

                startActivity(intent);
            }
        });
        toolbar.setTitleTextColor(0xFFFFFFFF);

        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TareaEventoDetalleActivity.class);

                intent.putExtra(TareaEventoDetalleActivity.PARAM_ID_GRUPO, GrupoId);

                startActivity(intent);
            }
        });
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);

        new GrupoDAO().obtenerDeUsuario(servicioUsuario.obtenerUsuario().getUid(), ObtenerGruposEventListener);

        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_listado_tareas)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.nav_personal:
                        Toolbar toolbar = findViewById(R.id.toolbar);

                        toolbar.setTitle(getString(R.string.menu_personal));
                        GrupoId = null;
                        findViewById(R.id.group_options_button).setVisibility(GrupoId == null ? View.INVISIBLE : View.VISIBLE);
                        break;
                    case R.id.nav_nuevo_grupo:
                        Intent intent = new Intent(getApplicationContext(), GrupoActivity.class);

                        startActivity(intent);

                        finish();
                        findViewById(R.id.group_options_button).setVisibility(GrupoId == null ? View.INVISIBLE : View.VISIBLE);
                        return true;
                    default:
                        return true;
                }

                handleMenu(item, null, null);

                return true;
            }
        });

        new UsuarioDAO().obtenerPorId(new ServicioUsuario(getApplicationContext()).obtenerUsuario().getUid(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    new ServicioUsuario(getApplicationContext()).cerrarSesion();
                    return;
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final Usuario usuario = snapshot.getValue(Usuario.class);

                    ((TextView)navigationView.getHeaderView(0).findViewById(R.id.usuario_nombre_drawer)).setText(usuario.getNombre());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                new ServicioUsuario(getApplicationContext()).cerrarSesion();
            }
        });

        navigationView.getHeaderView(0).findViewById(R.id.usuario_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GestionUsuarioActivity.class);

                startActivity(intent);

                finish();
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
                        final Grupo grupo = snapshot.getValue(Grupo.class);
                        cambiarFragmento(GrupoId, grupo.getNombre());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            cambiarFragmento(GrupoId, null);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {

    }

    private ValueEventListener ObtenerGruposEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (!dataSnapshot.exists()) {
                return;
            }

            NavigationView navigationView = findViewById(R.id.nav_view);
            final Menu menu = navigationView.getMenu();

            menu.removeGroup(1);


            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                GrupoUsuario gp = snapshot.getValue(GrupoUsuario.class);

                new GrupoDAO().obtenerPorId(gp.getGrupo(), new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            return;
                        }

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            final Grupo grupo = snapshot.getValue(Grupo.class);

                            new ServicioNotificaciones().suscribirATopicGrupo(grupo.getId());

                            MenuItem item = menu.add(1, R.id.nav_listado_tareas, 0, grupo.getNombre())

                            .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    handleMenu(item, grupo.getId(), grupo.getNombre());

                                    return false;
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            menu.setGroupCheckable(1, true, true);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }


    };

    private void handleMenu(MenuItem item, String grupo, String grupoName) {
        GrupoId = grupo;

        findViewById(R.id.group_options_button).setVisibility(GrupoId == null ? View.INVISIBLE : View.VISIBLE);

        cambiarFragmento(grupo, grupoName);

        // Highlight the selected item has been done by NavigationView
        NavigationView navigationView = findViewById(R.id.nav_view);
        final Menu menu = navigationView.getMenu();

        int count = menu.size() - 1;

        while (count >= 0) {
            MenuItem mItem = menu.getItem(count);

            if (mItem != null) {
                mItem.setChecked(false);
            }

            count--;
        }

        item.setChecked(true);


    }

    private void cambiarFragmento(String grupo, String grupoName) {
        Fragment fragment = null;
        Class fragmentClass = ListadoTareasFragment.class;;

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }


        Bundle arguments = new Bundle();

        arguments.putString(ListadoTareasFragment.PARAM_GRUPO_ID, grupo);

        fragment.setArguments(arguments);


        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, fragment).commit();

        // Set action bar title
        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle(grupoName != null ? grupoName : getString(R.string.menu_personal));

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        // Close the navigation drawer
        drawer.closeDrawers();
    }
}
