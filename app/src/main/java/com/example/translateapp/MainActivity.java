package com.example.translateapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.translateapp.Modelo.Idioma;
import com.google.android.material.button.MaterialButton;
import com.google.mlkit.nl.translate.TranslateLanguage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText Et_Idioma_Origen;
    TextView Tv_Idioma_Destino;
    MaterialButton Btn_Elegir_Idioma, Btn_Idioma_Elegido, Btn_Traducir;

    private ArrayList<Idioma> IdiomasrrayList;

    private static final String REGISTROS = "Mis_registros";

    private String codigo_idioma_origen = "es";
    private String titulo_idioma_origien = "Español";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InicializarVistas();
        IdiomasDisponibles();

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
                Toast.makeText(MainActivity.this, "Idioma elegido", Toast.LENGTH_SHORT).show();
            }
        });

        Btn_Traducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Traducir", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void InicializarVistas() {
        Et_Idioma_Origen = findViewById(R.id.Et_Idioma_Origen);
        Tv_Idioma_Destino = findViewById(R.id.Tv_Idioma_Destino);
        Btn_Elegir_Idioma = findViewById(R.id.Btn_Elegir_Idioma);
        Btn_Idioma_Elegido = findViewById(R.id.Btn_Idioma_Elegido);
        Btn_Traducir = findViewById(R.id.Btn_Traducir);
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
}