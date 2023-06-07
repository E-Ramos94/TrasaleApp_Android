package com.example.translateapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.translateapp.Modelo.Idioma;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText Et_Idioma_Origen;
    TextView Tv_Idioma_Destino;
    MaterialButton Btn_Elegir_Idioma, Btn_Idioma_Elegido, Btn_Traducir;
    private ProgressDialog progressDialog;

    private ArrayList<Idioma> IdiomasrrayList;

    private static final String REGISTROS = "Mis_registros";

    private String codigo_idioma_origen = "es";
    private String titulo_idioma_origien = "Español";
    private String codigo_idioma_destino = "en";
    private String titulo_idioma_destino = "Ingles";

    private TranslatorOptions translatorOptions;
    private Translator translator;
    private String texto_idioma_origen = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InicializarVistas();
        IdiomasDisponibles();

        //getSupportActionBar().show();

        Btn_Elegir_Idioma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Elegir idioma", Toast.LENGTH_SHORT).show();
                ElegirIdiomaOrigen();
            }
        });

        Btn_Idioma_Elegido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Idioma elegido", Toast.LENGTH_SHORT).show();
                ElegirIdiomaDestino();
            }
        });

        Btn_Traducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Traducir", Toast.LENGTH_SHORT).show();
                ValidarDatos();
            }
        });
    }

    private void InicializarVistas() {
        Et_Idioma_Origen = findViewById(R.id.Et_Idioma_Origen);
        Tv_Idioma_Destino = findViewById(R.id.Tv_Idioma_Destino);
        Btn_Elegir_Idioma = findViewById(R.id.Btn_Elegir_Idioma);
        Btn_Idioma_Elegido = findViewById(R.id.Btn_Idioma_Elegido);
        Btn_Traducir = findViewById(R.id.Btn_Traducir);

        progressDialog = new ProgressDialog(this);

        progressDialog.setTitle("Espere por favor...");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void IdiomasDisponibles() {
        IdiomasrrayList = new ArrayList<>();
        List<String> ListaCodigoIdioma = TranslateLanguage.getAllLanguages();

        for (String codigo_lenguaje : ListaCodigoIdioma){
            String titulo_lenguaje = new Locale(codigo_lenguaje).getDisplayLanguage();

            //Log.d(REGISTROS, "IdiomasDisponibles: codigo_lenguaje: " + codigo_lenguaje);
            //Log.d(REGISTROS, "IdiomasDisponibles: titulo_lenguaje: " + titulo_lenguaje);

            Idioma modeloIdioma = new Idioma(codigo_lenguaje, titulo_lenguaje);
            IdiomasrrayList.add(modeloIdioma);
        }
    }

    private void ElegirIdiomaOrigen(){
        PopupMenu popupMenu = new PopupMenu(this, Btn_Elegir_Idioma);
        for(int i = 0; i<IdiomasrrayList.size(); i++){
            popupMenu.getMenu().add(Menu.NONE, i, i, IdiomasrrayList.get(i).getTitulo_idioma());
        }

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int position = item.getItemId();

                codigo_idioma_origen = IdiomasrrayList.get(position).getCodigo_idioma();
                titulo_idioma_origien = IdiomasrrayList.get(position).getTitulo_idioma();

                Btn_Elegir_Idioma.setText(titulo_idioma_origien);
                Et_Idioma_Origen.setHint("Ingrese texto en: " + titulo_idioma_origien);

                Log.d(REGISTROS, "onMenuItemClick: codigo_idioma_origen: " + codigo_idioma_origen);
                Log.d(REGISTROS, "onMenuItemClick: titulo_idioma_origien: " + titulo_idioma_origien);

                return false;
            }
        });
    }

    private void ElegirIdiomaDestino(){
        PopupMenu popupMenu = new PopupMenu(this, Btn_Idioma_Elegido);
        for(int i = 0; i<IdiomasrrayList.size(); i++){
            popupMenu.getMenu().add(Menu.NONE, i, i, IdiomasrrayList.get(i).getTitulo_idioma());
        }

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int position = item.getItemId();

                codigo_idioma_destino = IdiomasrrayList.get(position).getCodigo_idioma();
                titulo_idioma_destino = IdiomasrrayList.get(position).getTitulo_idioma();

                Btn_Idioma_Elegido.setText(titulo_idioma_destino);

                Log.d(REGISTROS, "onMenuItemClick: codigo_idioma_destino: " + codigo_idioma_destino);
                Log.d(REGISTROS, "onMenuItemClick: titulo_idioma_destino: " + titulo_idioma_destino);

                return false;
            }
        });
    }

    private void ValidarDatos() {
        texto_idioma_origen = Et_Idioma_Origen.getText().toString().trim();
        Log.d(REGISTROS, "ValidarDatos: texto_idioma_origen: " + texto_idioma_origen);

        if (texto_idioma_origen.isEmpty()){
            Toast.makeText(MainActivity.this, "Ingrese texto", Toast.LENGTH_SHORT).show();
        } else {
            TraducirTexto();
        }
    }

    private void TraducirTexto() {
        progressDialog.setMessage("Porcesando...");
        progressDialog.show();

        translatorOptions = new TranslatorOptions.Builder()
                .setSourceLanguage(codigo_idioma_origen)
                .setTargetLanguage(codigo_idioma_destino)
                .build();

        translator = Translation.getClient(translatorOptions);

        DownloadConditions downloadConditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();

        translator.downloadModelIfNeeded(downloadConditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //Los paquetes de traduccion se descargaron con exito
                        Log.d(REGISTROS, "onSuccess: El paquete se ha descargado con exito");
                        progressDialog.setMessage("Traduciendo texto...");

                        translator.translate(texto_idioma_origen)
                                .addOnSuccessListener(new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(String texto_traducido) {
                                        progressDialog.dismiss();
                                        Log.d(REGISTROS, "onSuccess: texto_traducido: " + texto_traducido);
                                        Tv_Idioma_Destino.setText(texto_traducido);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Log.d(REGISTROS, "onFailure "+e);
                                        Toast.makeText(MainActivity.this, "" +e, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Los paquetes no se descargaron
                        progressDialog.dismiss();
                        Log.d(REGISTROS, "onFailure "+e);
                        Toast.makeText(MainActivity.this, "" +e, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mi_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.Menu_limpiar_texto){
            String String_texto = "Traducción";
            Et_Idioma_Origen.setText("");
            Et_Idioma_Origen.setHint("Ingrese texto");
            Tv_Idioma_Destino.setText(String_texto);
        }
        return super.onOptionsItemSelected(item);
    }
}