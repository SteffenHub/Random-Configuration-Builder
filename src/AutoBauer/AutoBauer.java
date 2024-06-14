package AutoBauer;

import SatSolver.SatSolver;
import Tools.Operation;
import Tools.TxtReaderWriter;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;

/**
 * Die Superklasse aller Autobauer.
 */
public abstract class AutoBauer implements IAutoBauer {

    /**
     * So viele zufalls Modelle sollen erzeugt werden
     */
    protected int anzahlZuErzeugendeModelle;
    /**
     * Der Zufallsgenerator
     */
    protected Random zufallsGenerator;
    /**
     * Die eingelesene cnf
     */
    protected ArrayList<int[]> cnfInt;
    /**
     * Die eingelesenen Einbauraten
     */
    protected double[] ebr;
    /**
     * Alle Variablen, eine einfache Liste die alle Zahlen zwischen 1 und n gespeichert hat
     */
    protected ArrayList<Integer> alleVars;
    /**
     * 2-Dim Array, wo die fertigen gebauten Modelle stehen
     */
    protected boolean[][] modelleBool;
    /**
     * Die Anzahl Variablen
     */
    protected int anzahlVariablen;
    /**
     * Der DateiName der cnf
     */
    protected String cnfDateiName;
    /**
     * Der SatSolver
     */
    protected SatSolver satSolver;
    /**
     * StopUhr
     */
    private final Instant stopUhr;

    protected double[] prozentualeEbr;

    protected int modelleLauf;

    private String iRFileName;

    /**
     * Konstruktor aller Autobauer
     * Der Konstruktor fuehrt alle weiteren Berechnungen durch und speichert das Ergebnis als Txt-Datei.
     *
     * @param anzahlZuErzeugendeModelle wie viel Modelle sollen gebaut werden
     * @param cnfInt                    das zuvor eingelesene RegelWerk in Dimacs Form
     * @param anzahlVariablen           in cnf angegebene anzahl an Variablen
     * @param ebr                       die zuvor eingelesenen Einbauraten
     * @param cnfDateiName              der name der Txt-Datei mit Endung(z.B. 'CNF.txt')
     * @param seed_set                  Has the seed been set? if not then a random seed will be generated
     * @param seed                      The seed for the random generator. null if no seed set.
     */
    public AutoBauer(int anzahlZuErzeugendeModelle, ArrayList<int[]> cnfInt, int anzahlVariablen, double[] ebr, String cnfDateiName, boolean seed_set, long seed, String iRFileName) {
        // save current time for calculation time
        Instant start = Instant.now();
        this.iRFileName = iRFileName;
        this.cnfDateiName = cnfDateiName;
        this.anzahlVariablen = anzahlVariablen;
        this.cnfInt = cnfInt;
        this.ebr = ebr;
        this.stopUhr = Instant.now();
        this.anzahlZuErzeugendeModelle = anzahlZuErzeugendeModelle;
        if (seed_set) {
            this.zufallsGenerator = new Random(seed);
        }else{
            this.zufallsGenerator = new Random();
            seed = this.zufallsGenerator.nextLong();
        }
        this.modelleLauf = 0;
        init();
        zusaetzlicheInit();
        run();

        // print calculation time
        Duration interval = Duration.between(start, Instant.now());
        long executionTime = interval.getSeconds();
        System.out.println("Execution time in seconds: " + interval.getSeconds());
        //In textdatei schreiben
        TxtReaderWriter.writeModelleBool("ausgabe.txt", this.modelleBool, seed, executionTime, this.anzahlVariablen, this.anzahlZuErzeugendeModelle, this.cnfDateiName, this.iRFileName, this.getClass().getSimpleName());
    }

    /**
     * Hier werden die start-Initialisierungen unternommen.
     * Das Hinzufuegen von allne Variablen in einer Liste und das reservieren des speichers fuer das Ergebnis finden hier statt.
     */
    public void init() {
        //Alle Vars in eine Liste
        this.alleVars = Operation.getListOfVars1ToN(anzahlVariablen);

        //Speicher reservieren fuer das Ergebnis
        this.modelleBool = new boolean[anzahlZuErzeugendeModelle][anzahlVariablen];
    }

    /**
     * Falls ein Auto Bauer zusaetzliche Initialisierungen braucht kann er diese Methode ueberschreiben.
     */
    public void zusaetzlicheInit() {
    }

    /**
     * Die Run Methode.
     * Hier wird die Schleife ueber alle zu erzeugende Modelle durchgelaufen und nacheinander ein baubares Model erzeugt und gespeichert.
     */
    public void run() {
        while(modelleLauf < this.anzahlZuErzeugendeModelle){
            berechneEinBaubaresModel();
            speichereModel(modelleLauf);
            ++modelleLauf;
        }
    }

    /**
     * speichert das erbaute Model.
     * dieses wird aus dem SatSolver entnommen, wenn alles richtig lief sollte hier nur das eine moegliche Model ausgegeben werden
     * gespeichert wird es in der modelleBool.
     * Ausserdem gibt diese Methode den status aus.
     *
     * @param modelleLauf bei wie viel gebauten Modellen wir uns gerade befinden
     */
    public void speichereModel(int modelleLauf) {
        //Modell aus dem SatSolver holen
        int[] model = satSolver.getModel();

        //Wahlen speichern
        for (int wahl : model) {
            if (wahl > 0) {
                modelleBool[modelleLauf][wahl - 1] = true;
            }
        }
        //Status ausgeben
        if ((modelleLauf + 1) % 10000 == 0) {
            System.out.println("-----------------Status update-----------------");
            System.out.println("Wir haben wieder 10K Modelle zusammengebaut!");
            System.out.println(modelleLauf + 1 + " von " + this.anzahlZuErzeugendeModelle);
            Duration interval = Duration.between(this.stopUhr, Instant.now());
            System.out.println("Vergangene Zeit: " + interval.getSeconds() / 60 / 60 + "Std. = " + interval.getSeconds() / 60 + "min = " + interval.getSeconds() + "sec");
            System.out.println("Geschaetzte Zeit bis Ende: " + berechneZeit(interval.getSeconds(), modelleLauf + 1, "std") + " Std. = " + berechneZeit(interval.getSeconds(), modelleLauf + 1, "min") + " min = " + berechneZeit(interval.getSeconds(), modelleLauf + 1, "sec") + " sec");
            System.out.println("Verbleibende zu bauende Modelle: " + (this.anzahlZuErzeugendeModelle - (modelleLauf + 1)));
            System.out.println("-----------------------------------------------");
        } else if ((modelleLauf + 1) % 10 == 0) {
            System.out.println((modelleLauf + 1) + "/" + this.anzahlZuErzeugendeModelle);
        }
    }

    /**
     * berechnet die geschaetzte Zeit bis Ende der Berechnung
     *
     * @param sekundenBisher       die sekunden, die bisher vergangen sind
     * @param bishergebauteModelle die anzahl an gebauten Modellen bisher
     * @param welcheZeit           in welchem format willst du? "sec","min" oder "std"
     * @return die geschaetzte restliche benoetigte Zeit bis Ende
     */
    private double berechneZeit(long sekundenBisher, int bishergebauteModelle, String welcheZeit) {
        double zeitProModel = ((double) sekundenBisher) / (bishergebauteModelle);
        double zeit = (zeitProModel * (this.anzahlZuErzeugendeModelle - bishergebauteModelle));

        if ("min".equals(welcheZeit))
            zeit = zeit / 60;
        else if ("std".equals(welcheZeit))
            zeit = zeit / 60 / 60;

        zeit = zeit * 100;
        zeit = (int) zeit;
        zeit = zeit / 100;
        return zeit;
    }
}