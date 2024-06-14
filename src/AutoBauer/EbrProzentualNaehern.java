package AutoBauer;

import SatSolver.SatSolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class EbrProzentualNaehern extends AutoBauer {

    /**
     * Konstruktor.
     *
     * @param anzahlZuErzeugendeModelle wie viel Modelle sollen gebaut werden
     * @param cnfInt                    das zuvor eingelesene RegelWerk in Dimacs Form
     * @param anzahlVariablen           in cnf angegebene anzahl an Variablen
     * @param ebr                       die zuvor eingelesenen Einbauraten
     * @param cnfDateiName              der name der Txt-Datei mit Endung(z.B. 'CNF.txt')
     * @param seed_set                  Has the seed been set? if not then a random seed will be generated
     * @param seed                      The seed for the random generator. null if no seed set.
     */
    public EbrProzentualNaehern(int anzahlZuErzeugendeModelle, ArrayList<int[]> cnfInt, int anzahlVariablen, double[] ebr, String cnfDateiName, boolean seed_set, long seed, String iRFileName) {
        super(anzahlZuErzeugendeModelle, cnfInt, anzahlVariablen, ebr, cnfDateiName, seed_set, seed, iRFileName);
    }

    @Override
    public void zusaetzlicheInit() {
        this.prozentualeEbr = new double[10];

        for (double einbaurate : ebr) {
            int stelle = (int) (einbaurate * 10);
            if (stelle == 10) {
                stelle = 9;
            }
            ++prozentualeEbr[stelle];
        }
        for (int i = 0; i < prozentualeEbr.length; i++) {
            prozentualeEbr[i] = prozentualeEbr[i]/ ebr.length;
        }

        int[] anzahlEbr = new int[10];
        int anzahlGesamt = 0;
        for (int i = 0; i < 10; i++) {
            int anzahlFuerDiesenBereich = (int) (this.prozentualeEbr[i] * this.anzahlVariablen);
            anzahlEbr[i] = anzahlFuerDiesenBereich;
            anzahlGesamt += anzahlFuerDiesenBereich;
        }
        if (anzahlGesamt != this.anzahlVariablen){
            --anzahlEbr[9];
            --anzahlGesamt;
            if (anzahlGesamt != this.anzahlVariablen) {
                while (true){System.out.println("Was ist hier los?");}
            }
        }

        //Ebr aktuallisieren
        int[] anzahlEbrCopy = new int[anzahlEbr.length];
        System.arraycopy(anzahlEbr, 0, anzahlEbrCopy, 0, anzahlEbr.length);
        this.ebr = new double[this.anzahlVariablen];
        for (int i = 0; i < this.anzahlVariablen; i++) {
            // suche zufaellig eine Ebr Klasse aus in die wir diese Variable stecken
            boolean gefunden = false;
            while (!gefunden){
                int klasse = new Random().nextInt(10);
                if (anzahlEbr[klasse] > 0){
                    this.ebr[i] = (klasse * 0.1) * new Random().nextDouble() * 0.1;
                    --anzahlEbr[klasse];
                    gefunden = true;
                }
            }
        }

        // überprüfen, ob alle ebr klassen auch genutzt und zugeteilt wurden
        if (this.ebr.length != this.anzahlVariablen){
            while (true){System.out.println("Was ist hier los?");}
        }
        for (double einbaurate : this.ebr) {
            int stelle = (int) (einbaurate * 10);
            if (stelle == 10) {
                stelle = 9;
            }
            --anzahlEbrCopy[stelle];
        }
        int stelleGesamt = 0;
        for (int stelle : anzahlEbrCopy) {
            stelleGesamt += stelle;
        }
        if (stelleGesamt != 0){
            while (true){System.out.println("Was ist hier los?");}
        }
    }


    /**
     * berechnet genau ein baubares Model
     */
    public void berechneEinBaubaresModel() {
        /*
        // ebr von den bereits erstellten modelle berechnen
        double[] ebrJetzt = new double[this.alleVars.size()];
        for (int i = 0; i < this.modelleLauf; i++) {
            for (int j = 0; j < this.modelleBool[i].length; j++) {
                if (this.modelleBool[i][j]) {
                    ++ebrJetzt[j];
                }
            }
        }

        List<List<Integer>> ebrZugehoerigkeit = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ebrZugehoerigkeit.add(new ArrayList<>());
        }
        for (int i = 0; i < ebrJetzt.length; i++) {
            int stelle = (int) (ebrJetzt[i] * 10);
            if (stelle == 10) {
                stelle = 9;
            }
            List<Integer> ebrStelle = ebrZugehoerigkeit.get(stelle);
        }
         */

        //Sat Solver erstellen und mit Regeln fuellen
        this.satSolver = new SatSolver(this.cnfInt);

        //alle Variablen Liste kopieren
        ArrayList<Integer> alleVarsModified = new ArrayList<>(this.alleVars);

        //solange es noch nicht betrachtete Variablen gibt
        while (!alleVarsModified.isEmpty()) {
            //zufaellige zeile generieren
            int zufallsZeile = this.zufallsGenerator.nextInt(alleVarsModified.size());
            // Variable aus dieser zufalls Zeile holen
            int varWahl = alleVarsModified.get(zufallsZeile);

            // Wenn Einbaurate dieser Variable kleiner einer zufaelligen Zahl
            if (this.ebr[varWahl - 1] < this.zufallsGenerator.nextDouble()) {
                varWahl = varWahl * -1;
            }

            //Wenn es ueberhaupt moeglich ist die Wahl hinzuzufuegen
            if (this.satSolver.istLoesbarMit(varWahl)) {
                this.satSolver.addVariable(varWahl);
            } else {
                this.satSolver.addVariable(varWahl * -1);
            }

            //betrachtete Variable rausnehmen
            alleVarsModified.remove(zufallsZeile);
        }
    }
}