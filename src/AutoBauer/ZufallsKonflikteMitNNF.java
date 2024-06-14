package AutoBauer;

import SatSolver.SatSolver;
import Tools.Operation;

import java.util.ArrayList;

/**
 * Hier wird mit dem c2d die nnf erstellt, die Konflikte(O-Zeilen) rausgelesen und die dazu gehoehrigen Konflikte gespeichert.
 * Nun wird in der Berechnung nur noch die Konflikte betrachtet nicht mehr alle Variablen.
 * Sonstiges vorgehen genau so wie beim AllesZufaellig-AutoBauer
 */
public class ZufallsKonflikteMitNNF extends AutoBauer {

    /**
     * die Liste aller konflikte aus der nnf
     */
    private ArrayList<Integer> alleKonflikte;
    /**
     * Fuer den c2d. Wie oft ein Baum erstellt werden soll bevor einer ausgegeben wird
     */
    private final int dtcount;

    /**
     * @param anzahlZuErzeugendeModelle wie viel Modelle sollen gebaut werden
     * @param cnfInt                    das zuvor eingelesene RegelWerk in Dimacs Form
     * @param anzahlVariablen           in cnf angegebene anzahl an Variablen
     * @param ebr                       die zuvor eingelesenen Einbauraten
     * @param cnfDateiName              der name der Txt-Datei mit Endung(z.B. 'CNF.txt')
     * @param dtcount                   Fuer den c2d. Wie oft ein Baum erstellt werden soll bevor einer ausgegeben wird
     * @param seed_set                  Has the seed been set? if not then a random seed will be generated
     * @param seed                      The seed for the random generator. null if no seed set.
     */
    public ZufallsKonflikteMitNNF(int anzahlZuErzeugendeModelle, ArrayList<int[]> cnfInt, int anzahlVariablen, double[] ebr, String cnfDateiName, int dtcount, boolean seed_set, long seed) {
        super(anzahlZuErzeugendeModelle, cnfInt, anzahlVariablen, ebr, cnfDateiName, seed_set, seed);
        this.dtcount = dtcount;
    }

    /**
     * Die zusaetzliche initialisierung ist das einlesen aller konflikte aus der nnf(Alle O-Zeilen)
     */
    @Override
    public void zusaetzlicheInit() {
        //nnf einlesen
        ArrayList<String> nnf = Operation.nnfEinlesen(cnfDateiName,dtcount);

        //alle Konflikte rauslesen
        this.alleKonflikte = Operation.getAlleKonflikte(nnf);
        //Statistiken der nnf ausgeben
        printStatsKonflikte(this.alleKonflikte);
    }

    /**
     * Berechnet ein baubares Model, dabei wird zufaellig eine Variablen Nummer aus der konflikt liste genommen und dann per
     * zufall entschieden, ob wir sie waehlen oder nicht. Dann wird geprueft, ob ein Auto mit dieser entscheidung baubar ist, wenn ja nehmen wir sie, wenn nicht
     * dann folgt der wahrheitswert(Wahl) dieses konflikts aus einer vorher getroffenen entscheidung.
     */
    public void berechneEinBaubaresModel() {
        //Sat Solver neu erstellen und mit Regeln fuellen
        this.satSolver = new SatSolver(this.cnfInt);
        //alle Konflikte Liste kopieren
        ArrayList<Integer> alleKonflikteModified = new ArrayList<>(this.alleKonflikte);
        //solange es noch nicht betrachtete konflikte gibt
        while (alleKonflikteModified.size() > 0) {
            //zufaellige Zeile in konflikt liste erzeugen
            int zufallsZeile = this.zufallsGenerator.nextInt(alleKonflikteModified.size());
            //Konflikt an der zufallszeile rauslesen
            int konflikt = alleKonflikteModified.get(zufallsZeile);

            //entscheiden ob waehlen oder nicht
            if (this.ebr[konflikt - 1] < this.zufallsGenerator.nextDouble())
                konflikt = konflikt * -1;

            //wenn loesbar mit der entscheidung dann hinzufuegen
            if (this.satSolver.istLoesbarMit(konflikt))
                this.satSolver.addVariable(konflikt);

            //den betrachteten Konflikt rausnehmen
            alleKonflikteModified.remove(zufallsZeile);
        }
    }

    /**
     * Gibt Infos ueber die Konflikte aus.
     * Alle Konflikte nebeneinander und Anzahl aller Konflikte
     *
     * @param alleKonflikte zuvor erstellt Liste alle Konflikte
     */
    private void printStatsKonflikte(ArrayList<Integer> alleKonflikte) {
        System.out.print("Alle Konflikte: ");
        //Gibt alle Konflikte nebeneinander aus
        for (int a : alleKonflikte) {
            System.out.print(a + " ");
        }
        System.out.println();
        //Gibt anzahl aller konflikte aus
        System.out.println("Anzahl Konflikte: " + alleKonflikte.size());
    }
}