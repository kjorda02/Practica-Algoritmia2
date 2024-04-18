package com.example.trobamot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLOutput;
import java.util.EmptyStackException;

public class pantallaFinal extends AppCompatActivity {
    private int widthDisplay;
    private int heightDisplay;
    private boolean acertada;
    private String restricciones;
    private String conjuntoPalVal;
    private String palabraCorrecta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_final);
        // Object to store display information
        DisplayMetrics metrics = new DisplayMetrics();
        // Get display information
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        widthDisplay = metrics.widthPixels;
        heightDisplay = metrics.heightPixels;

        Intent intent = getIntent();
        if (!intent.getStringExtra("acertada").equals("false"))
            acertada = true;
        else
            acertada = false;

        restricciones = intent.getStringExtra("restricciones");
        conjuntoPalVal = intent.getStringExtra("conjuntoPalVal");
        palabraCorrecta = intent.getStringExtra("nombrePalabra");
        setContentView(R.layout.activity_pantalla_final);

        crearPantallaFinal();
    }

    private void crearPantallaFinal(){
        ConstraintLayout l = new ConstraintLayout(this);

        // Titulo ---------------------------------------------------
        TextView titulo = new TextView(this);
        if (acertada)
            titulo.setText("Enhorabona!");
        else
            titulo.setText("Oh oh oh oh...");

        titulo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        ViewGroup.LayoutParams p = new ViewGroup.LayoutParams(widthDisplay-70, ViewGroup.LayoutParams.WRAP_CONTENT);

        titulo.setLayoutParams(p);
        //titulo.setY(50);
        titulo.setTextSize(30);
        titulo.setId(View.generateViewId());
        l.addView(titulo);

        // Palabra --------------------------------------------------
        TextView palabra = new TextView(this);
        palabra.setText(palabraCorrecta);
        palabra.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        palabra.setLayoutParams(p);
        //palabra.setY(titulo.g);
        palabra.setTextSize(20);
        palabra.setId(View.generateViewId());
        l.addView(palabra);

        // Definicion -----------------------------------------------
        TextView definicion = new TextView(this);
        String def = consultarDefinicion(palabraCorrecta);
        definicion.setText(Html.fromHtml(def, Html.FROM_HTML_MODE_COMPACT));
        definicion.setGravity(Gravity.LEFT);
        definicion.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        definicion.setLayoutParams(p);
        definicion.setId(View.generateViewId());
        definicion.setX(30);
        //ConstraintLayout.LayoutParams param = new ConstraintLayout.LayoutParams(l.getLayoutParams());  // aaaaaAAAaaAaaAAAAAAAAaAAA
        //param.leftMargin = 20;

        l.addView(definicion/*, param*/);

        // Restricciones -----------------------------------------------
        TextView r = new TextView(this);
        r.setText(Html.fromHtml("<b>Restriccions:</b> <p>"+restricciones+"</p>", Html.FROM_HTML_MODE_COMPACT));
        //r.setText(restricciones);
        r.setGravity(Gravity.LEFT);
        r.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        r.setLayoutParams(p);
        r.setId(View.generateViewId());
        r.setX(30);
        if (!acertada)
            l.addView(r);

        // Conjunto palabras validas --------------------------------
        TextView s = new TextView(this);
        s.setText(Html.fromHtml("<b>Paraules posibles:</b> <p>"+conjuntoPalVal+"</p>", Html.FROM_HTML_MODE_COMPACT));
        s.setGravity(Gravity.LEFT);
        s.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        s.setLayoutParams(p);
        s.setId(View.generateViewId());
        s.setX(30);
        s.setMovementMethod(new ScrollingMovementMethod());
        if (!acertada)
            l.addView(s);


        // Restricciones
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(l);

        // la parte superior del titulo concide con la parte superior del constraintLayout
        constraintSet.connect(titulo.getId(), ConstraintSet.TOP, l.getId(), ConstraintSet.TOP, 18);

        // la parte superior de la palabra concide con la parte inferior del titulo
        constraintSet.connect(palabra.getId(), ConstraintSet.TOP, titulo.getId(), ConstraintSet.BOTTOM, 18);

        // La parte superior de la definicion coincide con la parte inferior de la palabra
        constraintSet.connect(definicion.getId(), ConstraintSet.TOP, palabra.getId(), ConstraintSet.BOTTOM, 18);

        // La parte superior de las palabras posibles coincide con la parte inferior de las restricciones
        constraintSet.connect(r.getId(), ConstraintSet.TOP, definicion.getId(), ConstraintSet.BOTTOM, 18);

        constraintSet.connect(s.getId(), ConstraintSet.TOP, r.getId(), ConstraintSet.BOTTOM, 18);

        constraintSet.applyTo(l);

        ConstraintLayout cl = findViewById(R.id.pantFinal);
        ScrollView sv = new ScrollView(this);
        sv.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        sv.addView(l);
        cl.addView(sv);
    }

    private String consultarDefinicion(String pal){
        Def d = new Def(pal);
        Thread thread = new Thread(d);
        thread.start();
        try{
            thread.join();
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }

        return d.getDef();
    }

    private class Def implements Runnable {
        private String definicion;
        private String pal;
        @Override
        public void run(){
            try{
                String json = agafaHTML(pal);
                if(json.compareTo("[]")==0) {
                    definicion = "La paraula no te definicio";
                }
                else{
                    JSONObject JObject = new JSONObject(json);
                    definicion = JObject.getString("d");
                }
            }
            catch(Exception e){
                definicion = "No hay acceso a internet para consultar la definicion.";
            }
        }

        public Def(String p){
            pal = p;
        }

        public String getDef(){
            return definicion;
        }
    }

    private String agafaHTML(String paraula){
        try{
            URL definicio = new URL("https://www.vilaweb.cat/paraulogic/?diec="+paraula);
            BufferedReader in = new BufferedReader(new InputStreamReader(definicio.openStream()));
            StringBuffer buf = new StringBuffer();
            String s;
            while((s = in.readLine()) != null){
                buf.append(s);
            }

            return buf.toString();
        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return null;
    }
}