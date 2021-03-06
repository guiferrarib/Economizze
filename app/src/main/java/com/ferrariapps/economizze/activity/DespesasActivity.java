package com.ferrariapps.economizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.ferrariapps.economizze.R;
import com.ferrariapps.economizze.config.ConfiguracaoFirebase;
import com.ferrariapps.economizze.helper.Base64Custom;
import com.ferrariapps.economizze.helper.DateCustom;
import com.ferrariapps.economizze.model.Movimentacao;
import com.ferrariapps.economizze.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.santalu.maskara.widget.MaskEditText;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Objects;

public class DespesasActivity extends AppCompatActivity {

    private TextInputEditText editCategoria, editDescricao;
    private CurrencyEditText editValor;
    private MaskEditText editData;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Double despesaTotal;
    private Double despesaAtualizada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas);

        editValor = findViewById(R.id.editValor);
        editData = findViewById(R.id.editData);
        editCategoria = findViewById(R.id.editCategoria);
        editDescricao = findViewById(R.id.editDescricao);
        Locale locale = new Locale("pt","BR");
        editValor.setLocale(locale);
        editData.setText(DateCustom.dataAtual());
        recuperarDespesaTotal();


    }

    public void salvarDespesa(View view){

        if (validarCamposDespesa()){
        movimentacao = new Movimentacao();
        String data = Objects.requireNonNull(editData.getText().toString());
        Double valorRecuperado = Double.parseDouble(String.valueOf(editValor.getRawValue()));

        movimentacao.setValor(valorRecuperado);
        movimentacao.setCategoria(Objects.requireNonNull(editCategoria.getText()).toString());
        movimentacao.setDescricao(Objects.requireNonNull(editDescricao.getText()).toString());
        movimentacao.setData(data);
        movimentacao.setTipo("d");

        despesaAtualizada = despesaTotal + valorRecuperado;
        atualizarDespesa(despesaAtualizada);

        movimentacao.salvar(data);
        finish();
        }

    }

    public void atualizarDespesa(Double despesa){
        String emailUsuario = Objects.requireNonNull(autenticacao.getCurrentUser()).getEmail();
        assert emailUsuario != null;
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        usuarioRef.child("despesaTotal").setValue(despesa);

    }

    public boolean validarCamposDespesa(){

        String txtValor = String.valueOf(editValor.getRawValue());
        String txtData = Objects.requireNonNull(editData.getText().toString());
        String txtCategoria = Objects.requireNonNull(editCategoria.getText()).toString();
        String txtDescricao = Objects.requireNonNull(editDescricao.getText()).toString();

        if (!txtValor.isEmpty() && !txtValor.equals("0")) {

            if (!txtData.isEmpty()) {

                if (!txtCategoria.isEmpty()) {

                    if (!txtDescricao.isEmpty()) {

                        return true;

                    } else {
                        editDescricao.setError("*");
                        editDescricao.requestFocus();
                        Toast.makeText(DespesasActivity.this, "Preencha a descrição!", Toast.LENGTH_LONG).show();
                        return false;
                    }
                } else {
                    editCategoria.setError("*");
                    editCategoria.requestFocus();
                    Toast.makeText(DespesasActivity.this, "Preencha a categoria!", Toast.LENGTH_LONG).show();
                    return false;
                }

            } else {
                editData.setError("*");
                editData.requestFocus();
                Toast.makeText(DespesasActivity.this, "Preencha a data!", Toast.LENGTH_LONG).show();
                return false;
            }

        } else {
            editValor.setError("*");
            editValor.requestFocus();
            Toast.makeText(DespesasActivity.this, "Preencha o valor!", Toast.LENGTH_LONG).show();
            return false;
        }

    }

    public void recuperarDespesaTotal(){
        String emailUsuario = Objects.requireNonNull(autenticacao.getCurrentUser()).getEmail();
        assert emailUsuario != null;
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                assert usuario != null;
                despesaTotal = usuario.getDespesaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}