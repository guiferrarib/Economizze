package com.ferrariapps.economizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ferrariapps.economizze.R;
import com.ferrariapps.economizze.config.ConfiguracaoFirebase;
import com.ferrariapps.economizze.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail, editSenha;
    private Button btnEntrar;
    private Usuario usuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);
        btnEntrar = findViewById(R.id.btnEntrar);

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String txtEmail = editEmail.getText().toString();
                String txtSenha = editSenha.getText().toString();

                if (!txtEmail.isEmpty()) {

                    if (!txtSenha.isEmpty()) {
                        usuario = new Usuario();
                        usuario.setEmail(txtEmail);
                        usuario.setSenha(txtSenha);
                        validarLogin();
                    } else {
                        editSenha.setError("*");
                        editSenha.requestFocus();
                        Toast.makeText(LoginActivity.this, "Preencha a senha!", Toast.LENGTH_LONG).show();
                    }

                } else {
                    editEmail.setError("*");
                    editEmail.requestFocus();
                    Toast.makeText(LoginActivity.this, "Preencha o email!", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    public void validarLogin(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
          usuario.getEmail(),
          usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    abrirTelaPrincipal();
                }else{

                    String excecao = "";

                    try {
                        throw Objects.requireNonNull(task.getException());
                    }catch (FirebaseAuthInvalidUserException e) {
                        excecao = "Usuário não está cadastrado.";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "Senha não corresponde ao usuário informado.";
                    }catch (Exception e){
                        excecao = "Erro ao cadastrar usuário: "+e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(LoginActivity.this, excecao, Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public void abrirTelaPrincipal(){
        startActivity(new Intent(this, PrincipalActivity.class));
        finish();
    }

}