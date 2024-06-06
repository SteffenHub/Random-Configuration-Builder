package AutoBauer;

import SatSolver.SatSolver;

import java.util.ArrayList;

/**
 * Baut ein moegliches Model per Zufall zsm.
 * Es wird eine Liste alle Variablen erstellt und zufaellig eines gewaehlt und per Zufall entschieden, ob dieses gewaehlt sein soll oder nicht.
 * Anschliessend wird geprueft, ob unsere entscheidung ueberhaupt moeglich ist falls ja wird unsere entscheidung ins Model uebernommen, falls nicht muss leider das negative unserer Entscheidung hinzugefuegt werden.
 * Ob unsere Entscheidung moeglich ist oder nicht wird davon bestimmt, welche Entscheidungen wir davor schon getroffen haben.
 * Dann wird diese betrachtete zufaellig gewaehlte Variable aus unserer liste entnommen, da iwr fuer diese schon unsere Entscheidung gefaellt haben.
 */
public class AllesZufaellig extends AutoBauer {

    /**
     * Konstruktor.
     *
     * @param anzahlZuErzeugendeModelle wie viel Modelle sollen gebaut werden
     * @param cnfInt                    das zuvor eingelesene RegelWerk in Dimacs Form
     * @param anzahlVariablen           in cnf angegebene anzahl an Variablen
     * @param ebr                       die zuvor eingelesenen Einbauraten
     * @param cnfDateiName              der name der Txt-Datei mit Endung(z.B. 'CNF.txt')
     */
    public AllesZufaellig(int anzahlZuErzeugendeModelle, ArrayList<int[]> cnfInt, int anzahlVariablen, double[] ebr, String cnfDateiName) {
        super(anzahlZuErzeugendeModelle, cnfInt, anzahlVariablen, ebr, cnfDateiName);
    }

    /**
     * berechnet genau ein baubares Model
     */
    public void berechneEinBaubaresModel() {
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