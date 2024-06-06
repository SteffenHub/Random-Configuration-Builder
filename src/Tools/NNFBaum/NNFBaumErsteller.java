package Tools.NNFBaum;

import Tools.Operation;
import java.util.ArrayList;

/**
 * Erstellt einen NNF-Baum als Objekt.
 * Dafuer wird der c2d benutzt um die .nnf zu erstellen.
 * Anschliessend wird diese eingelesen und in einen Baum umgeformt, den man rekursiv durchlaufen kann
 */
public class NNFBaumErsteller {

    /**
     * Die eingelesene .nnf-Datei
     */
    private final ArrayList<String> nnf;
    /**
     * Der resultierende NNF-Baum als Objekt
     */
    private final INNFBaum baum;

    /**
     * Konstruktor.
     * Mit dem Aufruf wird die Berechnung von der nnf mit dem c2d angestossen, sowie umformung in ein Baum Objekt.
     *
     * @param cnfDateiName der Datei Name der cnf Datei mit dem Regelwerk
     * @param dtcount wie oft soll der c2d einen Baum erstellen, bevor einer ausgegeben wird
     */
    public NNFBaumErsteller(String cnfDateiName, int dtcount){
        //Die .nnf erstellen lassen vom c2d und einlesen
        this.nnf = Operation.nnfEinlesen(cnfDateiName,dtcount);
        //Die erste Zeile aus der nnf raus nehmen
        if (nnf.get(0).split(" ")[0].equals("nnf"))
            nnf.remove(0);
        this.baum = baumErstellen(nnf.get(nnf.size()-1));
    }

    /**
     * Erstellt den NNF-Baum aus der vom c2d generierten .nnf-Datei
     *
     * @param zeile von welcher Zeile sollen wir anfangen den Baum aufzubauen(normaler weise die letzte zeile in der .nnf-Datei)
     * @return den NNF-Baum, der mit der .nnf-Datei erstellt wurde
     */
    private INNFBaum baumErstellen(String zeile){

        String[] zeileTrim = zeile.split(" ");

        if (zeileTrim[0].equals("A")){
            ArrayList<INNFBaum> abzweigungen = new ArrayList<>();
            for (int i = 2; i < zeileTrim.length; i++) {
                abzweigungen.add(baumErstellen(this.nnf.get(Integer.parseInt(zeileTrim[i]))));
            }
            return new Und(abzweigungen);
        }else if (zeileTrim[0].equals("O")){
            return new Oder(baumErstellen(this.nnf.get(Integer.parseInt(zeileTrim[3]))),baumErstellen(this.nnf.get(Integer.parseInt(zeileTrim[4]))),Integer.parseInt(zeileTrim[1]));
        }else{
            return new Blatt(Integer.parseInt(zeileTrim[1]));
        }
    }

    /**
     * getter fuer den im Konstruktor erstellten NNFBaum
     *
     * @return NNFBaum
     */
    public INNFBaum getBaum(){
        return this.baum;
    }
}