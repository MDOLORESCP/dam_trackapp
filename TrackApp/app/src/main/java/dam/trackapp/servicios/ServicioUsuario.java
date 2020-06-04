package dam.trackapp.servicios;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import dam.trackapp.LoginActivity;
import dam.trackapp.dao.UsuarioDAO;
import dam.trackapp.modelos.Usuario;

public class ServicioUsuario {
    private FirebaseAuth mAuth;

    public ServicioUsuario(Context context) {
        FirebaseApp.initializeApp(context);

        mAuth = FirebaseAuth.getInstance();
    }

    public Task<AuthResult> iniciarSesion(String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password);
    }

    public void cerrarSesion() {
        mAuth.signOut();
    }

    public boolean comprobarAutenticado() {
        FirebaseUser user = obtenerUsuario();
        return user != null && user.isEmailVerified();
    }

    public void enviarEmailRecuperacion(String email) {
        mAuth.sendPasswordResetEmail(email);
    }

    public FirebaseUser obtenerUsuario() {
        return mAuth.getCurrentUser();
    }

    public Task enviarEmailVerificacion() {
        return obtenerUsuario().sendEmailVerification();
    }

    public void crearUsuario(final Usuario usuario, String password, OnCompleteListener complete, OnFailureListener failure) {
        mAuth.createUserWithEmailAndPassword(usuario.getEmail(), password)
                .addOnCompleteListener(complete)
                .addOnFailureListener(failure);
    }
}
