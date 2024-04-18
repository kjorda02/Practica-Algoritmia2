package com.example.trobamot;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLOutput;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    // Variables de lògica del joc
    private int lengthWord = 5;
    private int maxTry = 6;
    private int sizeCasilla = 140;
    int idxCasilla;

    // Variables de construcció de la interfície
    public static String grayColor = "#D9E1E8";
    public static String orangeColor = "#f77a2d";
    public static int redColor = 0xfff00000;
    public static int greenColor = 0xff2bff84;
    public static int yellowColor = 0xffffed2b;
    private int widthDisplay;
    private int heightDisplay;
    TextView numSol;
    private static Alfabeto alfabeto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Object to store display information
        DisplayMetrics metrics = new DisplayMetrics();
        // Get display information
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        widthDisplay = metrics.widthPixels;
        heightDisplay = metrics.heightPixels;

        crearInterficie();
    }

    @Override
    protected void onStart() {
        super.onStart();
        hideSystemUI();
    }

    private void crearInterficie() {
        System.out.println("[crearInterficie() -> Creando rejilla de casillas]");
        crearGraella();
        System.out.println("[crearInterficie() -> Creando textView de numero de soluciones y clase alfabeto]");
        crearNumSol();
        System.out.println("[crearInterficie() -> Creando teclado]");
        crearTeclat();
        System.out.println("[crearInterficie() -> Creando botones enviar y esborrar]");
        crearBotons();
    }

    private void crearBotons() {
        ConstraintLayout cl = findViewById(R.id.layout);
        Button esborrar = new Button(this);
        esborrar.setText("ESBORRAR");
        esborrar.setX(200);
        esborrar.setY(maxTry*sizeCasilla + 450);
        // Afegir la funcionalitat al botó
        esborrar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                esborrar();
            }
        });

        Button enviar = new Button(this);
        enviar.setText("ENVIAR");
        enviar.setX(600);
        enviar.setY(maxTry*sizeCasilla + 450);
        // Afegir la funcionalitat al botó
        enviar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                enviar();
            }
        });

        cl.addView(esborrar);
        cl.addView(enviar);
    }

    private void crearNumSol() {
        if (alfabeto == null){
            alfabeto = new Alfabeto(getResources().openRawResource(R.raw.paraules), lengthWord);
        }
        else{
            alfabeto.reiniciar(lengthWord);  // Nos ahorramos leer todas las palabras otra vez
        }

        ConstraintLayout cl = findViewById(R.id.layout);
        numSol= new TextView(this);

        //numSol.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        numSol.setY(maxTry*sizeCasilla + 350);
        ViewGroup.LayoutParams p = new ViewGroup.LayoutParams(widthDisplay, ViewGroup.LayoutParams.WRAP_CONTENT);  // ¿Por que tiene que ser tan complicado?

        numSol.setLayoutParams(p);
        numSol.setText("Hi ha "+alfabeto.getCantPalValidas()+" solucions posibles");
        numSol.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        cl.addView(numSol);
    }

    private void crearGraella() {
        ConstraintLayout constraintLayout = findViewById(R.id.layout);

        GridLayout rejilla = new GridLayout(this);
        rejilla.setColumnCount(lengthWord);
        rejilla.setRowCount(maxTry);

        // Definir les característiques del "pinzell"
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(5);
        gd.setStroke(3, Color.parseColor(grayColor));

        // Creamos los textviews para todas las casillas
        for (int i = 0; i < maxTry*lengthWord; i++) {
            // Crear un TextView
            TextView textView = new TextView(rejilla.getContext());

            textView.setText("");
            textView.setBackground(gd);
            textView.setId(i);
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_VERTICAL);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            //textView.setTextColor(Color.RED);
            textView.setTextSize(30);

            // Creamos esta cosa que da asco (ew)
            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.height = 0;
            param.width = 0;
            param.rightMargin = 5;
            param.topMargin = 5;
            param.leftMargin = 5;
            param.bottomMargin = 5;
            param.columnSpec = GridLayout.spec(i%lengthWord, 1, 1);
            param.rowSpec = GridLayout.spec(i/lengthWord, 1, 1);

            // Afegir el TextView al layout
            rejilla.addView(textView, param);
        }

        ViewGroup.LayoutParams p = new ViewGroup.LayoutParams(sizeCasilla*lengthWord, sizeCasilla*maxTry);

        rejilla.setX((widthDisplay-sizeCasilla*lengthWord)/2);
        rejilla.setY(300);
        rejilla.setLayoutParams(p);
        constraintLayout.addView(rejilla);
        selectCasilla(0, true);
    }

    private void crearTeclat() {
        ConstraintLayout constraintLayout = findViewById(R.id.layout);
        GridLayout rejilla = new GridLayout(this);

        rejilla.setColumnCount(9);
        rejilla.setRowCount(((alfabeto.getLetras().size()-1)/rejilla.getColumnCount())+1);
        uaMap<Character, ullSet<Integer>> letras = alfabeto.getLetras();
        Iterator it = letras.iterator();

        // Creamos los botones
        for (int i = 0; it.hasNext(); i++) {
            Button botonLetra = new Button(rejilla.getContext());
            botonLetra.setText(String.valueOf(Character.toUpperCase((char)((uaMap.Pair) it.next()).getKey())));  // Feisimo pero bueno
            botonLetra.setMinimumHeight(25);
            botonLetra.setId(100+i);

            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.height = 0;
            param.width = 0;
            param.topMargin = 5;
            param.bottomMargin = 5;
            param.leftMargin = 2;
            param.rightMargin = 2;
            param.columnSpec = GridLayout.spec(i % rejilla.getColumnCount(), 1, 1);
            param.rowSpec = GridLayout.spec(i / rejilla.getColumnCount(), 1, 1);

            rejilla.addView(botonLetra, param);

            // Afegir la funcionalitat al botó
            botonLetra.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    addLetra(((Button) v).getText());
                }
            });
        }

        ViewGroup.LayoutParams p = new ViewGroup.LayoutParams(widthDisplay, rejilla.getRowCount()*150);
        rejilla.setLayoutParams(p);
        rejilla.setX(0);
        rejilla.setY(heightDisplay-p.height-32);
        constraintLayout.addView(rejilla);
    }

    private void addLetra(CharSequence c){
        TextView casilla = findViewById(idxCasilla);
        if (casilla != null) casilla.setText(c);

        selectCasilla(idxCasilla, false);
        if ((idxCasilla+1)%lengthWord != 0)  // Si no estamos en la ultima posicion
            idxCasilla++;
        selectCasilla(idxCasilla, true);
    }

    private void esborrar(){
        TextView casilla = findViewById(idxCasilla);
        casilla.setText("");
        selectCasilla(idxCasilla, false);
        if (idxCasilla%lengthWord != 0)
            idxCasilla--;

        selectCasilla(idxCasilla, true);
    }

    private void enviar(){
        char[] palabra = new char[lengthWord];

        // Comprobamos que la palabra este completa y si no es asi mostramos el aviso correspondiente
        if ((idxCasilla+1) % lengthWord != 0 || ((TextView)findViewById(idxCasilla)).getText().length() != 1){
            Context context = getApplicationContext();
            CharSequence text = "Paraula Incompleta!";
            int duration = Toast.LENGTH_LONG;

            Toast palIncompleta = Toast.makeText(context, text, duration);
            palIncompleta.show();
            return;
        }

        int comienzo = idxCasilla - (idxCasilla%lengthWord);
        for (int i = 0; i < lengthWord; i++){  // Recuperamos la palabra de los valores de cada casilla
            palabra[i] = Character.toLowerCase(((TextView) findViewById(comienzo+i)).getText().charAt(0));
        }

        if (String.valueOf(palabra).equals(alfabeto.getPalabra())){  // Si la palabra es correcta
            alfabeto.updRestricciones(palabra);
            pantallaFinal(true);
            return;
        }

        if (!alfabeto.palValida(String.valueOf(palabra))){  // Si la palabra no esta en el diccionario
            Context context = getApplicationContext();
            CharSequence text = "Paraula no valida!";
            int duration = Toast.LENGTH_LONG;

            Toast palIncompleta = Toast.makeText(context, text, duration);
            palIncompleta.show();
            return;
        }

        // Si llegamos hasta aqui, la palabra esta en el diccionario (y tiene la longitud adecuada), pero no es la correcta

        if (idxCasilla >= lengthWord*maxTry-1){  // Si hemos agotado todos los intentos
            alfabeto.updRestricciones(palabra);
            pantallaFinal(false);
        }

        // Saltamos a la siguiente fila
        selectCasilla(idxCasilla, false);
        idxCasilla++;
        selectCasilla(idxCasilla, true);

        // Actualizamos las restricciones y conjunto de palabras posibles, y obtenemos el color para cada casilla
        int[] colores = alfabeto.updRestricciones(palabra);
        for (int i = 0; i < lengthWord; i++){  // Recorremos las casillas y les damos el color adecuado
            if (colores[i] == 1)
                ((TextView) findViewById(comienzo+i)).setBackgroundColor(greenColor);
            else if (colores[i] == 0)
                ((TextView) findViewById(comienzo+i)).setBackgroundColor(yellowColor);
            else if (colores[i] == -1)
                ((TextView) findViewById(comienzo+i)).setBackgroundColor(redColor);
        }

        numSol.setText("Hi ha "+alfabeto.getCantPalValidas()+" solucions posibles");

        // Actualiza los colores de las letras de los botones
        for (int i = 0; i < alfabeto.size(); i++){  // Recorremos los botones
            Button b = (Button) findViewById(100+i);
            for (int j = 0; j < palabra.length; j++){  // Recorremos las letras de la palabra
                if (b.getText().charAt(0) == Character.toUpperCase(palabra[j])){
                    if (colores[j] == 1){
                        b.setTextColor(greenColor);
                        break;
                    }
                    else if (colores[j] == 0)
                        b.setTextColor(yellowColor);
                    else if (colores[j] == -1)
                        b.setTextColor(redColor);
                }
            }
        }
    }

    private void pantallaFinal(boolean acertada){
        //System.out.println("RESTRICCIONES: ["+alfabeto.restrToString()+"]");
        //System.out.println("PAL VALIDAS: ["+alfabeto.palValSetToString()+"]");
        Intent intent = new Intent(this, pantallaFinal.class);
        intent.putExtra("nombrePalabra", alfabeto.getPalabraAcento());
        intent.putExtra("restricciones", alfabeto.restrToString());
        intent.putExtra("conjuntoPalVal", alfabeto.palValSetToString());
        if (acertada)
            intent.putExtra("acertada", "true");
        else
            intent.putExtra("acertada", "false");

        startActivity(intent);
    }

    private void selectCasilla(int id, boolean selected) {
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(5);
        if (selected)
            gd.setStroke(5, Color.parseColor(orangeColor));
        else
            gd.setStroke(3, Color.parseColor(grayColor));

        TextView casilla = findViewById(id);
        if (casilla != null) casilla.setBackground(gd);
        idxCasilla = id;
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE  // no posar amb notch
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}