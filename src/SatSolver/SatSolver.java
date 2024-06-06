package SatSolver;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Die Schnittstelle zum Sat4J SatSolver mit weiteren Methoden, die bei AutoBauern und Tools helfen
 */
public class SatSolver {

    /**
     * Der SatSolver als Sat4J Objekt
     */
    ISolver solver;
    /**
     * Die cnf als liste aus int-Arrays
     */
    ArrayList<int[]> cnfArr;
    /**
     * die anzahl Variablen, die in der CNF angegeben ist
     */
    int anzahlVariablen;

    /**
     * Konstruktor der SatSolver Klasse
     *
     * @param cnfArr die zuvor eingelesene und umgewandelte cnf(Regelwerk)
     */
    public SatSolver(ArrayList<int[]> cnfArr) {
        //solver erzeugen
        this.solver = SolverFactory.newDefault();
        this.cnfArr = new ArrayList<>(cnfArr);
        //klauseln hinzufuegen
        for (int[] klausel : cnfArr) {
            try {
                solver.addClause(new VecInt(klausel));
            } catch (ContradictionException e) {
                e.printStackTrace();
                System.out.println("Diese Regel erzeugt eine Kontradiktion: " + Arrays.toString(klausel));
            }
        }
        this.anzahlVariablen = solver.nVars();
    }

    /**
     * ueberprueft ob das Problem loesbar ist
     *
     * @return boolean isSatisfiable
     */
    public boolean istLoesbar() {
        boolean loesbar = false;
        IProblem problem = solver;
        try {
            if (problem.isSatisfiable()) {
                loesbar = true;
            }
        } catch (TimeoutException e) {
            System.err.println("--------------------------ERROR DESCRIPTION--------------------------");
            System.err.println("Das suchen eines baubaren Modells hat zu lange gedauert");
            System.err.println();
            e.printStackTrace();
            System.err.println("---------------------------DESCRIPTION END---------------------------");
        }
        return loesbar;
    }

    /**
     * Prueft, ob es mit der angegebenen Variable ein Model gibt.
     *
     * @param variable beliebe Variable negativ oder positiv
     * @return es gibt ein Model mit dieser Variable
     */
    public boolean istLoesbarMit(int variable) {
        boolean loesbar = false;
        IProblem problem = this.solver;
        try {
            if (problem.isSatisfiable(new VecInt(new int[]{variable}))) {
                loesbar = true;
            }
        } catch (TimeoutException e) {
            System.err.println("--------------------------ERROR DESCRIPTION--------------------------");
            System.err.println("Das suchen eines baubaren Modells hat zu lange gedauert");
            System.err.println();
            e.printStackTrace();
            System.err.println("---------------------------DESCRIPTION END---------------------------");
        }
        return loesbar;
    }

    /**
     * Ueberladene Methode, zu istLoesbarMit(int variable)
     * Prueft, ob es mit den angegebenen Variablen ein Model gibt.
     * Die variablen werden jeweils in eine Klausel gesteckt, also verUndet.
     *
     * @param variable1 beliebe Variable negativ oder positiv
     * @param variable2 beliebe Variable negativ oder positiv
     * @return gibt es ein Model mit diesen Variablen
     */
    public boolean istLoesbarMit(int variable1, int variable2) {
        boolean loesbar = false;
        IProblem problem = this.solver;
        try {
            if (problem.isSatisfiable(new VecInt(new int[]{variable1, variable2}))) {
                loesbar = true;
            }
        } catch (TimeoutException e) {
            System.err.println("--------------------------ERROR DESCRIPTION--------------------------");
            System.err.println("Das suchen eines baubaren Modells hat zu lange gedauert");
            System.err.println();
            e.printStackTrace();
            System.err.println("---------------------------DESCRIPTION END---------------------------");
        }
        return loesbar;
    }

    /**
     * Fuegt dem RegelWerk die angegebene Variable als Klausel hinzu.
     *
     * @param variable beliebe Variable negativ oder positiv
     */
    public void addVariable(int variable) {
        try {
            this.solver.addClause(new VecInt(new int[]{variable}));
            this.cnfArr.add(new int[]{variable});
        } catch (ContradictionException e) {
            System.err.println("--------------------------ERROR DESCRIPTION--------------------------");
            System.err.println("Das Einfuegen dieser Variable erzeugt eine Kontradiktion: " + variable);
            System.err.println("Hinzufuegen nicht moeglich!");
            System.err.println();
            e.printStackTrace();
            System.err.println("---------------------------DESCRIPTION END---------------------------");
        }
    }

    /**
     * Sucht ein baubares Model.
     *
     * @return das Model als int Array, falls keins existiert ist dieses Array leer.
     */
    public int[] getModel() {
        IProblem problem = this.solver;
        try {
            if (problem.isSatisfiable()) {
                return problem.model();
            } else {
                System.out.println("--------------------------WARNING DESCRIPTION--------------------------");
                System.out.println("Es existiert kein Baubares Modell");
                System.out.println("----------------------------DESCRIPTION END----------------------------");
            }
        } catch (TimeoutException e) {
            System.err.println("--------------------------ERROR DESCRIPTION--------------------------");
            System.err.println("Das suchen eines baubaren Modells hat zu lange gedauert");
            System.err.println();
            e.printStackTrace();
            System.err.println("---------------------------DESCRIPTION END---------------------------");
        }
        return new int[]{};
    }

    /**
     * Findet mit Hilfe des Regelwerks Variablen, deren Wahrheitswert in jedem Modell Aequivalent sind.
     * Berechnung dauert lange, es werden mind. (n^2)*2 ueberpruefungen auf Loesbarkeit gemacht.
     *
     * @return Liste aus Arrays mit laenge 2. Array[0] ist Aequivalent zu Array[1]
     */
    public ArrayList<int[]> findEquals() {
        System.out.println("Suche nach Aequivalenzen");
        //ArrayList<Integer> stehtFest = getVariablenDieSchonFeststehen();
        ArrayList<Integer> alleVars = new ArrayList<>();
        for (int i = 1; i <= solver.nVars(); i++) {
            alleVars.add(i);
        }
        //aequivalenz finden
        ArrayList<int[]> aequivalenteVars = new ArrayList<>();
        int lauf = 1;
        for (int variable : alleVars) {
            for (int variableToCheck : alleVars) {
                if (variable != variableToCheck && istLoesbarMit(variable, variableToCheck) && istLoesbarMit(-variable, -variableToCheck)) {
                    if (!istLoesbarMit(variable, -variableToCheck) && !istLoesbarMit(-variable, variableToCheck)) {
                        aequivalenteVars.add(new int[]{variable, variableToCheck});
                        System.out.println(variable + " <--> " + variableToCheck);
                    }
                }
            }
            System.out.println("Gepruefte Vars auf Aequivalenz: " + lauf + "/" + solver.nVars());
            ++lauf;
        }
        //aequivalenzen, die doppelt vorkommen loeschen: 5<->3 & 3<->5
        ArrayList<int[]> aequivalenteVarsDoppelte = new ArrayList<>();
        for (int erste = 0; erste < aequivalenteVars.size(); erste++) {
            for (int zweite = erste; zweite < aequivalenteVars.size(); zweite++) {
                if (aequivalenteVars.get(erste)[0] == aequivalenteVars.get(zweite)[1] && aequivalenteVars.get(erste)[1] == aequivalenteVars.get(zweite)[0]) {
                    aequivalenteVarsDoppelte.add(aequivalenteVars.get(zweite));
                }
            }
        }
        for (int[] doppelt : aequivalenteVarsDoppelte) {
            aequivalenteVars.remove(doppelt);
        }
        return aequivalenteVars;
    }

    /**
     * Berechnet Alle Variablen, die man nicht waehlen kann.
     * Der Wahrheitswert dieser Variablen steht schon fest.
     * Ob dieser Wahrheitswert 0 oder 1 ist, ist mit einer Negation markiert.
     *
     * @return liste aller Vars, die nicht waehlbar sind
     */
    public ArrayList<Integer> getVariablenDieSchonFeststehen() {
        int anzahlVars = solver.nVars();
        ArrayList<Integer> stehtFest = new ArrayList<>();
        for (int i = 1; i <= anzahlVars; i++) {
            //Wenn nicht positiv und negativ loesbar
            if (!(istLoesbarMit(i) && istLoesbarMit(-i))) {
                //herausfinden , ob positiv oder negativ loesbar
                if (!istLoesbarMit(i))
                    stehtFest.add(-i);
                else
                    stehtFest.add(i);
            }
        }
        return stehtFest;
    }

    /**
     * Nimmt nacheinander jede Regel einmal raus und schaut, ob nach dem entfernen der einen Regel ein baubares Model existiert
     *
     * @param variable bel. variable positiv oder negativ
     * @return list aus Regeln, wenn man eine dieser Regeln entfernt, dann existiert ein baubares Model mit der angegeben Variable
     */
    public ArrayList<int[]> getRegelnDieLoesbarkeitVerhindern(int variable) {
        ArrayList<int[]> regelnDieVerhindern = new ArrayList<>();
        //pruefen, ob die variable von anfang schon loesbar ist
        if (istLoesbarMit(variable)) {
            System.out.println("Mit der Variable " + variable + " existiert Loesung.");
            return regelnDieVerhindern;
        }
        //Alle Regeln durchgehen
        for (int[] regel : this.cnfArr) {
            //Regeln wiederherstellen
            ArrayList<int[]> cnfArrModified = new ArrayList<>(this.cnfArr);
            //eine Regel entfernen
            cnfArrModified.remove(regel);
            //neuen SatSolver mit der rausgenommenen Regel erzeugen
            SatSolver satSolverTemp = new SatSolver(cnfArrModified);
            if (satSolverTemp.getNVars() == this.anzahlVariablen) {
                //wenn dieser SatSolver ohne die eine Regel und mit der variable loesbar
                if (satSolverTemp.istLoesbarMit(variable))
                    regelnDieVerhindern.add(regel);
            }
        }
        return regelnDieVerhindern;
    }

    /**
     * Gibt aus welche Variablen die Loesbarkeit verhindern.
     * Gegensatz zu getRegelnDieLoesbarkeit verhindern,
     * diese Methode wird mittels solver.unsatExplanation vom Sat4J zu verfuegung gestellt.
     *
     * @param variable bel. var positiv oder negativ, die wahr sein soll
     * @return liste aus Variablen, die die Loesbarkeit verhindern
     */
    public int[] getVariablenDieLoesbarkeitVerhindern(int variable) {
        IProblem problem = this.solver;
        try {
            if (!problem.isSatisfiable(new VecInt(new int[]{variable}))) {
                return solver.unsatExplanation().toArray();
            }
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return new int[]{};
    }

    /**
     * Findet im Regelwerk die Familien.
     * Das funktioniert nur, wenn alle anderen Klauseln nur negative Variablen haben. z.B.:[-3,-5],[-54,-4]...
     *
     * @param cnf die zuvor eingelesene und umgeformte cnf
     * @return Liste aus int-Arrays pro Array steht eine familie. z.B.: [1,2,3],[4,5],[6],[7,8,9,10]
     */
    public ArrayList<int[]> getFamilien(ArrayList<int[]> cnf) {
        ArrayList<int[]> familien = new ArrayList<>();
        ArrayList<int[]> familienRegeln = getFamilienRegel(cnf);
        for (int[] regel : familienRegeln) {
            if (regel[0] > 0)
                familien.add(regel);
        }
        return familien;
    }

    /**
     * Findet in dem Regelwerk(cnf) die Regeln, die die Familien beschreiben. z.B.:[1,2],[-1,-2],
     * Das funktioniert nur, wenn alle anderen Klauseln nur negative Variablen haben. z.B.:[-3,-5],[-54,-4]...
     *
     * @param cnf die zuvor eingelesene CNF (Unveraendert). Modifizierte CNF's koennen eine falsche ausgabe generieren
     * @return Liste aus int-Arrays, jedes Array beinhalten eine Klausel
     */
    public ArrayList<int[]> getFamilienRegel(ArrayList<int[]> cnf) {
        ArrayList<int[]> familienRegel = new ArrayList<>();
        boolean anfangGefunden = false;
        for (int[] regel : cnf) {
            if (regel[0] > 0) {
                anfangGefunden = true;
            }
            if (anfangGefunden) {
                familienRegel.add(regel);
            }
        }
        return familienRegel;
    }

    /**
     * Gibt die anzahl an Variablen aus, die in dem aktuellen Regelwerk existieren.
     *
     * @return anzahl Variablen im Regelwerk
     */
    public int getNVars() {
        return solver.nVars();
    }
}