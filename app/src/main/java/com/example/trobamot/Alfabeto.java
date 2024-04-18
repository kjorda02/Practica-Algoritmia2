package com.example.trobamot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.TreeMap;

// ATENCION! EL CONJUNTO DE PALABRAS POSIBLES NO CONTINE PALABRAS QUE CONTIENEN LETRAS EN POSICIONES
// DONDE ESA LETRA YA HA SALIDO EN AMARILLO. PARA NO TENER EN CUENTA ESTA RESTRICCION EN EL CONJUNTO
// COMENTAR LA LINEA 215
public class Alfabeto {  // Guarda todo lo relacionado con letras, palabras, restricciones, etc
    String palabra;  // Palabra a adivinar

    // Mapping de letras del alfabeto a un conjunto con las posiciones en las que aparece esa
    // letra. Si el conjunto esta vacio, no aparece.
    private uaMap<Character, ullSet<Integer>> letras;

    // Mapping de letras del alfabeto, a un conjunto de restricciones. Si el conjunto esta vacio, la
    // letra no puede aparecer. Si es null, no sabemos nada. Si contiene un numero positivo, la
    // letra tiene que aparecer en esa posicion. Si contiene un numero negativo, la letra tiene
    // que aparecer, pero no en esa posicion (por lo tanto, empezaremos a contar por 1)
    private TreeMap<Character, ullSet<Integer>> restricciones;

    // Mapping de palabras sin acento a palabras con acento. Contiene todas las palabras del diccionario
    private HashMap<String, String> palabras;

    // Conjunto de palabras que cumplen todas las restricciones. Empieza siendo igual a el conjunto de
    // llaves del mapping palabras, y vamos reduciendo el contenido a medida que se descubren restricciones.
    // Utilizamos un HashSet y ordenamos solo al final, ya que es mas eficiente que TreeSet (O(1) vs O(log(n))
    // en el mejor caso
    private HashSet<String> palabrasValidas;

    public Alfabeto(InputStream is, int lengthWord){  // Constructor alfabeto
        System.out.println("[constructor Alfabeto() -> Leyendo palabras de paraules.dic]");
        palabras = new HashMap<>();
        String lin;
        try{  // Leemos las palabras del fichero diccionario y las guardamos en el HashMap palabras
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while ((lin = br.readLine()) != null){
                StringTokenizer st = new StringTokenizer(lin, ";");
                String conAcento = st.nextToken();
                String sinAcento = st.nextToken();
                palabras.put(sinAcento, conAcento);
            }
            br.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        letras = new uaMap<>(27);
        restricciones = new TreeMap<>();
        for (char c : new char[]{  // Añadimos las letras del alfabeto
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
                'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'ç'}) {

            letras.put(c, null);
        }

        reiniciar(lengthWord);  // Añadimos las palabras validas al conjunto de palbras validas
    }

    // Recorremos las palabras leidas y añadimos las de longtitud adecuada al conjunto palabrasPosibles
    // Ademas, escogemos una palabra aleatoria que sera la que hay que adivinar
    // Finalmente, actualizamos las posiciones en las que aparece cada letra dada esa palabra
    public void reiniciar(int lengthWord){
        System.out.println("[Alfabeto.reiniciar() -> Añadiendo las palabras de longitud adecuada al conjunto]");
        palabrasValidas = new HashSet<>();
        Iterator it = palabras.keySet().iterator();

        // Recorremos las palabraas del alfabeto
        while (it.hasNext()) {
            String palabra = (String) it.next();
            if (palabra.length() == lengthWord){  // Si es una palabra valida
                palabrasValidas.add(palabra);  // La añadimos
            }
        }

        // Ahora que ya sabemos cuantas palabras validas hay, podemos escoger una aleatoriamente
        System.out.println("[Alfabeto.reiniciar() -> Escogiendo una palabra aleatoria de entre las validas]");
        int palEscogida = (new Random()).nextInt(palabrasValidas.size());
        it = palabrasValidas.iterator();
        for (int i = 0; i < palEscogida; i++) {it.next();}
        palabra = (String) it.next();
        //palabra = "blana";
        System.out.println("PALABRA ESCOGIDA    : "+palabra);

        // Actualizamos el mapping letras, para poner las posiciones donde aparece cada letra en la palabra
        System.out.println("[Alfabeto.reiniciar() -> Calculando las posiciones donde aparece cada letra]");
        char[] pal = palabra.toCharArray();
        it = letras.iterator();

        while(it.hasNext()) {  // Recorremos las letras del alfabeto
            char letra = (Character) ((uaMap.Pair) it.next()).getKey();
            ullSet<Integer> posiciones = new ullSet<>();

            for (int i = 0; i < pal.length; i++) {  // Recorremos las posicones de la palabra
                if (pal[i] == letra){
                    posiciones.add(i);
                }
            }
            letras.put(letra, posiciones);
        }

        // re-inicializamos las restricciones
        restricciones = new TreeMap<>();
    }

    // Actualiza las restricciones en base a la palabra pasada por parametro, y devuelve un array
    // indicando de que color se debe marcar cada casilla (amarillo=0, rojo=-1, verde=1)
    public int[] updRestricciones(char[] pal){
        System.out.println("ACtualizando restricciones. Palabra: "+ Arrays.toString(pal));
        int[] colores = new int[pal.length];

        for (int i = 0; i < pal.length; i++){
            ullSet<Integer> posLetra = letras.get(pal[i]); // Posiciones donde aparece la letra en la palabra correcta
            if (posLetra.contains(i)){  // Verde
                colores[i] = 1;
                System.out.println("[Restriccion encontrada: "+palabra+" contiene la letra "+pal[i]+" en la posicion "+i+"]");
            }
            else if (posLetra.isEmpty()){  // Rojo
                System.out.println("[Restriccion encontrada: "+palabra+" no contiene la letra "+pal[i]+"]");
                colores[i] = -1;
            }
            else{  // Amarillo
                System.out.println("[Restriccion encontrada: "+palabra+" contiene la letra "+pal[i]+" pro no en la posicion "+i+"]");
                colores[i] = 0;
            }
        }

        // Guardaremos cuales son las restricciones que se han añadido para no tener que comprobarlas
        // todas (las palabras que ya estaban en el conjunto ya cumplian las anteriores restricciones)
        uaMap<Character, ullSet<Integer>> nuevasRestr = new uaMap<>(letras.size());

        // Para cada letra de la palabra, comprobamos si esta en la posicion adecuada, y guardamos
        // la restriccion descubierta en el conjunto asociado a esa letra
        for (int i = 0; i < pal.length; i++){
            if (!restricciones.containsKey(pal[i])){  // Si esa letra no tenia ninguna restriccion
                restricciones.put(pal[i], new ullSet<Integer>());  // Inicializamos el conjunto de restricciones
            }
            if (!nuevasRestr.containsKey(pal[i])){
                nuevasRestr.put(pal[i], new ullSet<Integer>());
            }
            ullSet<Integer> restrLetra = restricciones.get(pal[i]);  // Obtenemos el conjunto de restricciones para esta letra

            if (colores[i] == 1){
                restrLetra.add(i+1);
                nuevasRestr.get(pal[i]).add(i+1);
            }
            else if (colores[i] == 0){
                restrLetra.add(-(i+1));
                nuevasRestr.get(pal[i]).add(-(i+1));
            }
            // else no hacemos nada (si el conjunto esta vacio la letra no puede aparecer)
        }
        updSetPalValidas(nuevasRestr);

        return colores;
    }

    // Actualiza el conjunto de palabras validas. Para cada palabra del conjunto, primero, comprueba
    // que no contenga ninguna letra que no puede estar contenida. Luego, para cada letra del alfabeto
    // comprueba que este en las posiciones que tiene que estar y que no este en las que no puede estar
    private void updSetPalValidas(uaMap<Character, ullSet<Integer>> restr){
        Iterator<String> itPal = palabrasValidas.iterator();
        itPalabras:
        while (itPal.hasNext()){  // Recorremos el conjunto de palabras validas
            String palValida = itPal.next();
            char[] pal = palValida.toCharArray();
            //System.out.println("Comprobando la palabra: "+Arrays.toString(pal));

            // Comprobamos que no contiene ninguna letra 'prohibida'
            HashSet<Character> letrasPal = new HashSet<>();
            for (int i = 0; i < pal.length; i++){
                if (restr.containsKey(pal[i]) && ( (ullSet) restr.get(pal[i])).isEmpty()) {
                    itPal.remove();
                    //System.out.println("[Eliminado la palabra "+palValida+" por contener la letra '"+pal[i]+"']");
                    continue itPalabras;
                }
                letrasPal.add(pal[i]);
            }

            // Ahora, para cada letra, comprobamos que este en las posiciones donde debe estar, y
            // que no este en las posiciones donde no puede estar, pero tambien que si no puede estar
            // una posicion, que este en alguna otra posicion

            // Iterador para iterar el mapa de restricciones (las diferenes letras)
            Iterator<uaMap.Pair> itLetra = restr.iterator();
            while (itLetra.hasNext()){
                uaMap.Pair restrLetra = itLetra.next();
                Iterator itRes = ((ullSet) restrLetra.getValue()).getIterator();  // Iterador para iterar las restricciones de cada letra
                char letra = (char) restrLetra.getKey();

                while (itRes.hasNext()){  // Iteramos las restricciones de la letra 'letra'
                    int res = (Integer) itRes.next();

                    if (res > 0){  // Tiene que contener la letra en la posicion res-1
                        if (pal[res-1] != letra){
                            itPal.remove();
                            //System.out.println("[Eliminado la palabra "+palValida+" por no contener la letra '"+letra+"' en la posicion "+(res-1)+"]");
                            continue itPalabras;
                        }
                    }
                    if (res < 0){
                        if (pal[(-res)-1] == letra){  // No puede contener la letra en la posicion -res-1
                            itPal.remove();
                            //System.out.println("[Eliminado la palabra "+palValida+" por contener la letra '"+letra+"' en la posicion "+(-res-1)+"]");
                            continue itPalabras;
                        }
                        if (!letrasPal.contains(letra)){  // Pero la tiene que contener en alguna otra posicion
                            //System.out.println("Eliminado la palabra "+palValida+" por no contener la letra '"+letra+"']");
                            itPal.remove();
                            continue itPalabras;
                        }
                    }
                }
            }
        }
    }

    public int getCantPalValidas() {
        return palabrasValidas.size();
    }

    public uaMap<Character, ullSet<Integer>> getLetras(){
        return letras;
    }

    public int size(){
        return letras.size();
    }

    public String getPalabra(){
        return palabra;
    }
    public String getPalabraAcento(){
        return palabras.get(palabra);
    }

    public boolean palValida(String p){  // Indica si es una palabra valida (contenida en el diccionario)
        return palabras.containsKey(p);
    }

    public String restrToString(){  // Convierte el mapping de restricciones en una string para la pantalla final
        String s = "";

        // Recorremos las letras del alfabeto
        Iterator<Map.Entry<Character, ullSet<Integer>>> itRestr = restricciones.entrySet().iterator();
        while (itRestr.hasNext()){
            Map.Entry<Character, ullSet<Integer>> par = itRestr.next();
            char letra = par.getKey();
            ullSet<Integer> restrLetra = par.getValue();
            if (restrLetra.isEmpty()){
                s += "No pot contenir la '"+Character.toUpperCase(letra)+"', ";
            }
            else{
                s += "Ha de contenir la '"+Character.toUpperCase(letra)+"' ";
            }

            // Recorremos las restricciones de cada letra y buscamos las posiciones donde tiene que estar
            boolean primera = true;
            Iterator<Integer> itRestrLetra = restrLetra.getIterator();
            while (itRestrLetra.hasNext()){
                int res = itRestrLetra.next();
                if (res > 0){
                    if (primera)
                        s += "a la posicio "+res+", ";
                    else
                        s += "i la "+res+", ";
                    primera = false;
                }
            }

            // Recorremos las restricciones de cada letra y buscamos las posiciones donde no puede estar
            primera = true;
            itRestrLetra = restrLetra.getIterator();
            while (itRestrLetra.hasNext()){
                int res = itRestrLetra.next();
                if (res < 0){
                    if (primera)
                        s += "pero no a la posicio "+(-res)+", ";
                    else
                        s += "ni a la "+(-res)+", ";
                    primera = false;
                }
            }
        }
        return s;
    }

    public String palValSetToString(){  // Convierte el conjunto de palabras validas en una string para la pantalla final
        TreeSet<String> palValOrdenadas = new TreeSet<>();

        // Añadimos las palabras del conjunto desrdenado al treeset para ordenarlas (seran relativamente pocas)
        Iterator<String> itPalValSet = palabrasValidas.iterator();
        while (itPalValSet.hasNext()){
            palValOrdenadas.add(itPalValSet.next());
        }

        String s = "";
        itPalValSet = palValOrdenadas.iterator();
        while (itPalValSet.hasNext()){
            s += palabras.get(itPalValSet.next()); // Añadimos la palabra con acento
            s += ", ";
        }

        return s;
    }
}
