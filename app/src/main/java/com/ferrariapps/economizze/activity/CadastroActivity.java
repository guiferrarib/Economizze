package com.ferrariapps.economizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ferrariapps.economizze.R;
import com.ferrariapps.economizze.config.ConfiguracaoFirebase;
import com.ferrariapps.economizze.helper.Base64Custom;
import com.ferrariapps.economizze.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.Objects;

public class CadastroActivity extends AppCompatActivity {

    private EditText editNome, editEmail, editSenha;
    private Button btnCadastrar;
    private FirebaseAuth autenticacao;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Cadastro");

        editNome = findViewById(R.id.editNome);
        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);
        btnCadastrar = findViewById(R.id.btnCadastrar);

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String txtNome = editNome.getText().toString();
                String txtEmail = editEmail.getText().toString();
                String txtSenha = editSenha.getText().toString();

                if (!txtNome.isEmpty()) {

                    if (!txtEmail.isEmpty()) {

                        if (!txtSenha.isEmpty()) {
                            usuario = new Usuario();
                            usuario.setNome(txtNome);
                            usuario.setEmail(txtEmail);
                            usuario.setSenha(txtSenha);
                            cadastrarUsuario();
                        } else {
                            editSenha.setError("*");
                            editSenha.requestFocus();
                            Toast.makeText(CadastroActivity.this, "Preencha a senha!", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        editEmail.setError("*");
                        editEmail.requestFocus();
                        Toast.makeText(CadastroActivity.this, "Preencha o email!", Toast.LENGTH_LONG).show();
                    }

                } else {
                    editNome.setError("*");
                    editNome.requestFocus();
                    Toast.makeText(CadastroActivity.this, "Preencha o nome!", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public void cadastrarUsuario(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
          usuario.getEmail(),usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    String idUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                    usuario.setIdUsuario(idUsuario);
                    usuario.salvar();
                    Toast.makeText(CadastroActivity.this, "Cadastro efetuado com sucesso!", Toast.LENGTH_LONG).show();
                    finish();
                }else{

                    String excecao = "";

                    try {
                        throw Objects.requireNonNull(task.getException());
                    }catch (FirebaseAuthWeakPasswordException e){
                        excecao = "Digite uma senha com mais de 6 dígitos!";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "Por favor, digite um e-mail válido!";
                    }catch (FirebaseAuthUserCollisionException e){
                        excecao = "Esse e-mail já está sendo utilizado por outro usuário!";
                    }catch (Exception e){
                        excecao = "Erro ao cadastrar usuário: "+e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroActivity.this, excecao, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}