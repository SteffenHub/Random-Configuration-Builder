package Tools;

import SatSolver.SatSolver;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Prueft die CNF und die Einbauraten auf Konsistenz.
 * Es werden Variablen gefunden, die in jedem Model Aequivalent sind und anschliessend geprueft, ob ihre Einbauraten gleich sind.
 * Es werden Variablen gefunden, deren Wahrheitswert von anfang an feststeht, bei diesen wird geprueft ob sie eine passende Einbaurate von 0% oder 100% haben.
 */
public class KonsistenzPruefung {

    /**
     * Die SatSolver Schnittstelle zum Sat4J
     */
    private final SatSolver satSolver;
    //private final ArrayList<int[]> cnfInt;
    /**
     * Die zuvor eingelesenen Einbauraten
     */
    private final double[] ebr;
    /**
     * Wert, ob jede Ueberpruefung erfolgreich war
     */
    private boolean allesInordung = true;

    private ArrayList<String[]> aequivalnzAusgabe;
    private ArrayList<String[]> festehendeWahrheitswerteAusgabe;


    /**
     * @param cnfInt die zuvor eingelesene CNF als liste aus int-Arrays
     * @param ebr    die zuvor eingelesenen Einbauraten
     */
    public KonsistenzPruefung(ArrayList<int[]> cnfInt, double[] ebr) {
        //this.cnfInt = cnfInt;
        this.satSolver = new SatSolver(cnfInt);
        this.ebr = ebr;
        this.aequivalnzAusgabe = new ArrayList<>();
        this.festehendeWahrheitswerteAusgabe = new ArrayList<>();
        run();
        //ergebnis speichern
        TxtReaderWriter.writeListOfStringArrays("aequivalenz.txt", this.aequivalnzAusgabe);
        TxtReaderWriter.writeListOfStringArrays("stehtFest.txt",this.festehendeWahrheitswerteAusgabe);
    }

    /**
     * die Haupt-Methode
     */
    private void run() {
        ArrayList<int[]> equal = this.satSolver.findEquals();
        System.out.println("--------------------Aequivalenz Ergebnis--------------------");
        System.out.println();
        System.out.println("Es wird auf 3 stellen nach dem Komma geprueft");
        System.out.println();
        System.out.println(" Aequivalent   | Einbauraten                                | Einbaurate ist gleich");
        for (int[] oneEqual : equal) {

            //zeile initialisieren
            String[] zeileInAusgabe = new String[3];

            //erste Spalte(die aequivalenz)
            System.out.print(Arrays.toString(oneEqual));
            zeileInAusgabe[0] = Arrays.toString(oneEqual);

            //fuer Tabelle in der Konsole
            for (int i = 0; i < 15 - Arrays.toString(oneEqual).length(); i++) {
                System.out.print(" ");
            }
            System.out.print("| ");

            //die zugehoerigen einbauraten
            double ebr1 = this.ebr[oneEqual[0] - 1];
            double ebr2 = this.ebr[oneEqual[1] - 1];
            double[] einbaurate = new double[]{ebr1, ebr2};
            System.out.print(Arrays.toString(einbaurate));
            zeileInAusgabe[1] = Arrays.toString(einbaurate);

            //fuer Tabelle in der Konsole
            System.out.print("   ");
            for (int i = 0; i < 40 - (Arrays.toString(einbaurate)).length(); i++) {
                System.out.print(" ");
            }
            System.out.print("| ");

            //bewertung
            String bewertung;
            ebr1 = (int) (ebr1 * 1000);
            ebr2 = (int) (ebr2 * 1000);
            if (Math.abs(ebr1 - ebr2) != 0) {
                bewertung = "!!!NICHT GLEICH!!!";
                this.allesInordung = false;
            } else {
                bewertung = "Gut";
            }
            System.out.println(bewertung);
            zeileInAusgabe[2] = bewertung;

            //fuer Tabelle in der Konsole
            System.out.println();
            System.out.println("---------------|--------------------------------------------|------------");

            //Die Zeile speichern
            this.aequivalnzAusgabe.add(zeileInAusgabe);
        }
        System.out.println();
        System.out.println("-----------------Aequivalenz Ergebnis Ende------------------");
        System.out.println();


        System.out.println();
        System.out.println("-------Variablen deren Wahrheitswert schon feststeht-------");
        System.out.println();

        System.out.println("Spalte 'Regeln die den Wahrheitswert bestimmen' wird erstellt, indem jede Regel einmal rausgenommen, geprueft ob ohne diese Regel loesbar und wieder hinzugefuegt wird.\n" +
                "Wenn 'kombination aus Regeln erzwingen das waehlen' ausgegeben wird muessen mind. 2 Regeln gleichzeitig rausgenommen werden, um das Model loesbar zu machen.");
        System.out.println();

        ArrayList<Integer> variablenDieSchonFeststehen = satSolver.getVariablenDieSchonFeststehen();

        //fuer Tabelle in der Konsole
        System.out.println("Var  | Einbaurate          | Ebr im Vergleich           | Regeln die den Wahrheitswert bestimmen");

        for (int var : variablenDieSchonFeststehen) {

            String[] festeVarszeilefuerAusgabe = new String[4];

            //die Variable
            System.out.print(var);
            festeVarszeilefuerAusgabe[0] = String.valueOf(var);

            //fuer Tabelle in der Konsole
            for (int i = 0; i < 5 - String.valueOf(var).length(); i++) {
                System.out.print(" ");
            }
            System.out.print("| ");

            //die zugehoerige Einbaurate
            System.out.print(this.ebr[Math.abs(var) - 1]);
            festeVarszeilefuerAusgabe[1] = String.valueOf(this.ebr[Math.abs(var) - 1]);

            //fuer Tabelle in der Konsole
            for (int i = 0; i < 20 - String.valueOf(this.ebr[Math.abs(var) - 1]).length(); i++) {
                System.out.print(" ");
            }
            System.out.print("| ");

            //die bewertung
            String bewertung;
            if (var < 0) {
                if (this.ebr[Math.abs(var) - 1] > 0.0) {
                    bewertung = "!!!EINBAURATE ZU HOCH!!!";
                    this.allesInordung = false;
                } else {
                    bewertung = "Gut";
                }
            } else {
                if (this.ebr[Math.abs(var) - 1] == 0.0) {
                    bewertung = "!!!EINBAURATE ZU NIEDRIG!!!";
                    this.allesInordung = false;
                } else {
                    bewertung = "Gut";
                }
            }
            System.out.print(bewertung);
            festeVarszeilefuerAusgabe[2] = bewertung;

            //fuer Tabelle in der Konsole
            for (int i = 0; i < 27 - bewertung.length(); i++) {
                System.out.print(" ");
            }
            System.out.print("| ");

            //verantwortliche Regeln
            ArrayList<int[]> regeln = satSolver.getRegelnDieLoesbarkeitVerhindern(var * -1);
            String regelnStr = "";
            if (regeln.size() == 0)
                regelnStr = "kombination aus Regeln erzwingen das waehlen";
            for (int[] regel : regeln) {
                regelnStr += Arrays.toString(regel);
            }
            System.out.print(regelnStr);
            festeVarszeilefuerAusgabe[3] = regelnStr;

            //fuer Tabelle in der Konsole
            System.out.println();
            System.out.println("-----|---------------------|----------------------------|------------");

            this.festehendeWahrheitswerteAusgabe.add(festeVarszeilefuerAusgabe);
        }
        System.out.println();
        System.out.println("-----Variablen deren Wahrheitswert schon feststeht ENDE-----");


        System.out.println();
        System.out.println("------------------Gesamt Ergebnis------------------");
        System.out.println();
        if (this.allesInordung) {
            System.out.println("Die Konsistenzpruefung ist abgeschlossen. Alles in Ordnung :)");
        } else {
            System.out.println("Die Konsistenzpruefung ist abgeschlossen. Es wurden Fehler gefunden, in den oben ausgegeben Listen sind diese stellen markiert. :(");
        }
        System.out.println();
        System.out.println("---------------------------------------------------");
    }
}