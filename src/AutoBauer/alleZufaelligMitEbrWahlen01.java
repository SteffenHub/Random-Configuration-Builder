package AutoBauer;

import SatSolver.SatSolver;
import Tools.Operation;

import java.util.ArrayList;

/**
 * Model zusammenstellung genau so wie der AllesZufaellig Autobauer, nur dass hier die Variablen, die ind den Einbauraten 0% oder 100% haben nicht betrachtet werden.
 */
public class alleZufaelligMitEbrWahlen01 extends AutoBauer {

    /**
     * @param anzahlZuErzeugendeModelle wie viel Modelle sollen gebaut werden
     * @param cnfInt                    das zuvor eingelesene RegelWerk in Dimacs Form
     * @param anzahlVariablen           in cnf angegebene anzahl an Variablen
     * @param ebr                       die zuvor eingelesenen Einbauraten
     * @param cnfDateiName              der name der Txt-Datei mit Endung(z.B. 'CNF.txt')
     * @param seed_set                  Has the seed been set? if not then a random seed will be generated
     * @param seed                      The seed for the random generator. null if no seed set.
     */
    public alleZufaelligMitEbrWahlen01(int anzahlZuErzeugendeModelle, ArrayList<int[]> cnfInt, int anzahlVariablen, double[] ebr, String cnfDateiName, boolean seed_set, long seed) {
        super(anzahlZuErzeugendeModelle, cnfInt, anzahlVariablen, ebr, cnfDateiName, seed_set, seed);
    }

    /**
     * die zusaetzliche Initialisierung ist das hinzufuegen der Einbauraten von Variablen die 0% oder 100% haben dem Regelwerk hinzuzufuegen
     */
    @Override
    public void zusaetzlicheInit() {
        // 0 1 Zeilen aus ebr auslesen
        ArrayList<Integer> zusatzAusEbr = Operation.getEbr01(this.ebr);
        System.out.println("Anzahl der in den einbauraten Zeilen mit 0 oder 1: " + zusatzAusEbr.size());

        //0 1 Ebr der cnf hinzufuegen
        for (int zusatzVar : zusatzAusEbr) {
            this.cnfInt.add(new int[]{zusatzVar});
        }

        removeVarsDieSchonFeststehen(zusatzAusEbr);

        System.out.println("Anzahl noch zu betrachteten Variablen: " + this.alleVars.size());
    }

    /**
     * Entfernt aus der AlleVars Liste die variablen, die nicht mehr betrachtet werden muessen
     *
     * @param zusatzAusEbr eine Liste aus int, die jeweils eineVariablen Nummer repraesentieren
     */
    private void removeVarsDieSchonFeststehen(ArrayList<Integer> zusatzAusEbr) {
        for (int zusatzVar : zusatzAusEbr) {
            for (int a = this.alleVars.size() - 1; a >= 0; a--) {
                if (this.alleVars.get(a) == Math.abs(zusatzVar)) {
                    this.alleVars.remove(a);
                    break;
                }
            }
        }
    }

    /**
     * Berechnet genau ein baubares Model
     */
    public void berechneEinBaubaresModel() {
        //Sat Solver erstellen und mit Regeln fuellen
        this.satSolver = new SatSolver(cnfInt);

        //alle Konflikte Liste kopieren
        ArrayList<Integer> alleVarsModified = new ArrayList<>(this.alleVars);

        while (alleVarsModified.size() > 0) {
            int zufallsZeile = this.zufallsGenerator.nextInt(alleVarsModified.size());
            int varWahl = alleVarsModified.get(zufallsZeile);

            if (this.ebr[varWahl - 1] < this.zufallsGenerator.nextDouble()) {
                varWahl = varWahl * -1;
            }
            if (this.satSolver.istLoesbarMit(varWahl)) {
                this.satSolver.addVariable(varWahl);
            } else {
                this.satSolver.addVariable(varWahl * -1);
            }
            alleVarsModified.remove(zufallsZeile);
        }
    }
}