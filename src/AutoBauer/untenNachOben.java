package AutoBauer;

import SatSolver.SatSolver;
import Tools.NNFBaum.*;

import java.util.ArrayList;

/**
 * Baut ein moegliches Model per Zufall zsm.
 * Dafuer wird die NNF erstellt und der Baum im Objekt INNFBaum gespeichert um diesen rekursiv durchlaufen zu koennen.
 * Der Baum wird von unten angefangen durchgegangen und bei einem Oder-Zweig eine Entscheidung getroffen.
 * Dem SatSolver werden dann nur die resultierten Blatt Knoten hinzugefuegt, die restlichen Entscheidungen folgen aus den gewaehlten Blatt-Knoten.
 */
public class untenNachOben extends AutoBauer{

    /**
     * Der NNF Baum als Objekt
     */
    private INNFBaum baum;
    /**
     * Fuer den c2d. Wie oft ein Baum erstellt werden soll bevor einer ausgegeben wird
     */
    private final int dtcount;

    /**
     * Konstruktor.
     *
     * @param anzahlZuErzeugendeModelle wie viel Modelle sollen gebaut werden
     * @param cnfInt                    das zuvor eingelesene RegelWerk in Dimacs Form
     * @param anzahlVariablen           in cnf angegebene anzahl an Variablen
     * @param ebr                       die zuvor eingelesenen Einbauraten
     * @param cnfDateiName              der name der Txt-Datei mit Endung(z.B. 'CNF.txt')
     * @param dtcount                   Fuer den c2d. Wie oft ein Baum erstellt werden soll bevor einer ausgegeben wird
     */
    public untenNachOben(int anzahlZuErzeugendeModelle, ArrayList<int[]> cnfInt, int anzahlVariablen, double[] ebr, String cnfDateiName,int dtcount) {
        super(anzahlZuErzeugendeModelle, cnfInt, anzahlVariablen, ebr, cnfDateiName);
        this.dtcount = dtcount;
    }

    /**
     * die zusaetzliche Initialisierung ist das erstellen vom NNFBaum
     */
    @Override
    public void zusaetzlicheInit() {
        this.baum = new NNFBaumErsteller(this.cnfDateiName,dtcount).getBaum();
    }

    /**
     * berechnet genau ein baubares Model
     */
    @Override
    public void berechneEinBaubaresModel() {
        //Sat Solver neu erstellen und mit Regeln fuellen
        this.satSolver = new SatSolver(this.cnfInt);
        berechne(this.baum);
    }

    /**
     * berechnet genau ein baubares Model
     * getrennte Methode von berechneEinBaubaresModel(), da wir hier ein Uebergabe Parameter(den NNFBaum) brauchen, um diesen rekursiv zu durchlaufen
     *
     * @param baumObjekt der NNF Baum
     */
    private void berechne(INNFBaum baumObjekt){
        if (baumObjekt instanceof Und){
            for (INNFBaum abzweigung : ((Und) baumObjekt).getAbzweigungen())
                berechne(abzweigung);

        }else if (baumObjekt instanceof Oder){
            if (this.ebr[((Oder) baumObjekt).getKonflikt() - 1] < this.zufallsGenerator.nextDouble()) {
                //nicht waehlen
                if (((Oder) baumObjekt).getRichtung())
                    //links
                    berechne(((Oder) baumObjekt).getLinkerZweig());
                else
                    //rechts
                    berechne(((Oder) baumObjekt).getRechterZweig());

            }else{
                //waehlen
                if (((Oder) baumObjekt).getRichtung())
                    //rechts
                    berechne(((Oder) baumObjekt).getRechterZweig());
                else
                    //links
                    berechne(((Oder) baumObjekt).getLinkerZweig());
            }

        }else if (baumObjekt instanceof Blatt)
            this.satSolver.addVariable(((Blatt) baumObjekt).getVariable());
    }
}