package Tools;

import java.util.ArrayList;
import java.util.List;

public class Variable {

    /**
     * Optimiereung:
     * bei bedingte ebr suche nach dem ersten durchlauf nur noch die ansehen, in dennen diese Variable gewaehlt ist
     * Die ganzen suchen dynamisch machen, sodass nicht alles am anfang gesucht werden muss sondern erst wenn man die daten braucht
     */

    // Die Einbaurate dieser Variable
    private double einbaurate;

    // wie oft kommt diese Variable in den Auftraegen vor
    private final int hauefigkeitInAuftraegen;
    // welche variablen Nummer hat diese Variable im SatSolver(1-Indexiert)
    private final int variablenNummer;
    // Die Einbauraten bedingt von dieser Variable
    private double[] einbauratenBedingt;

    private boolean[][] AuftraegeMitDieserVar;

    private boolean istWaehlbar;

    /**
     * Konstruktor fuer eine Variable
     *
     * @param variablenNummer   die variablen Nummer im SatSolver(1-Indexiert)
     * @param auftraege die historischen Auftraege
     * @param familien
     */
    public Variable(int variablenNummer, int[][] anzahlZweiZusammen, boolean[][] auftraege, boolean istWaehlbar, ArrayList<int[]> familien) {
        this.istWaehlbar = istWaehlbar;
        this.variablenNummer = variablenNummer;
        this.AuftraegeMitDieserVar = this.getAuftraegeMitDieserVariable(auftraege);
        this.hauefigkeitInAuftraegen = this.AuftraegeMitDieserVar.length;
        this.einbaurate = (double) this.hauefigkeitInAuftraegen / auftraege.length;
        // darf erst nach der berechnung von hauefigkeitInAuftraegen aufgerufen werden
        this.einbauratenBedingt = holeBedingteEinbauraten(anzahlZweiZusammen);
    }

    private double[] verteileEinbauraten(double[] einbauratenBedingt, ArrayList<int[]> familien, int[][] anzahlZweiZusammen) {
        for (int i = 0; i < familien.size(); i++) {
            double wZsm = 0.0;
            for (int j = 0; j < familien.get(i).length; j++) {
                wZsm += einbauratenBedingt[familien.get(i)[j] - 1];
            }
            if (wZsm < 0.99999 || wZsm > 1.000001) {
                wZsm = 1 - wZsm;
                int anzahlZusammen = 0;
                for (int j = 0; j < familien.get(i).length; j++) {
                    anzahlZusammen += anzahlZweiZusammen[this.variablenNummer - 1][j];
                }
                for (int j = 0; j < familien.get(i).length; j++) {
                    einbauratenBedingt[familien.get(i)[j] - 1] = einbauratenBedingt[familien.get(i)[j] - 1] + (((double) anzahlZweiZusammen[this.variablenNummer - 1][j] / anzahlZusammen) * wZsm);
                }
            }
        }
        return einbauratenBedingt;
    }

    public double[] holeBedingteEinbauraten(int[][] anzahlZweiZusammen) {
        for (int i = 0; i < anzahlZweiZusammen.length; i++) {
            if (anzahlZweiZusammen[this.variablenNummer - 1][i] < 0 || anzahlZweiZusammen[i][this.variablenNummer - 1] < 0) {
                int anzahlZsm = this.getAnzahlZsmInAuftraegen(i + 1);
                anzahlZweiZusammen[this.variablenNummer - 1][i] = anzahlZsm;
                anzahlZweiZusammen[i][this.variablenNummer - 1] = anzahlZsm;
            }
        }
        double[] einbauratenZweiZusammen = new double[anzahlZweiZusammen.length];
        for (int i = 0; i < einbauratenZweiZusammen.length; i++) {
            einbauratenZweiZusammen[i] = (double) anzahlZweiZusammen[this.variablenNummer - 1][i] / this.hauefigkeitInAuftraegen;
        }
        return einbauratenZweiZusammen;
    }

    /**
     * Geht die Auftraege durch und guckt wie oft diese Variable vorkommt
     * und gibt die Anzahl aus
     *
     * @param auftraege die Historischen Auftraege
     * @return die Hauefigkeit dieser Variable in den Historischen Auftraegen
     */
    private boolean[][] getAuftraegeMitDieserVariable(boolean[][] auftraege) {
        List<boolean[]> auftraegeMitDieserVar = new ArrayList<>();
        for (boolean[] auftrag : auftraege)
            if (auftrag[this.variablenNummer - 1])
                auftraegeMitDieserVar.add(auftrag);
        return auftraegeMitDieserVar.toArray(new boolean[0][0]);
    }

    /**
     * Geht die Auftraege oft durch und stellt die Einbauraten bedingt von dieser Variable zusammen
     *
     * @param auftraege die historischen Auftraege
     * @return die bedingte Einbauraten bedingt von dieser Variable
     */
    private double[] getBedingteEinbauratenAusAuftraegen(boolean[][] auftraege) {
        double[] bedingteEbr = new double[auftraege[0].length];
        for (int i = 1; i <= auftraege[0].length; i++)
            bedingteEbr[i - 1] = (double) this.getAnzahlZsmInAuftraegen(i) / this.hauefigkeitInAuftraegen;
        return bedingteEbr;
    }

    /**
     * Geht alle Auftraege durch und guckt in welchen diese und die zweite Variable zusammen vorkommen
     *
     * @param zweiteVar die Variable mit der diese Variable zusammen vorkommen soll(1-Indexiert)
     * @return wie oft kommen diese beiden Variablen zusammen in den historischen Auftraegen vor
     */
    private int getAnzahlZsmInAuftraegen(int zweiteVar) {
        int anzahlZsm = 0;
        for (boolean[] auftrag : this.AuftraegeMitDieserVar)
            if (auftrag[this.variablenNummer - 1] && auftrag[zweiteVar - 1])
                ++anzahlZsm;
        return anzahlZsm;
    }

    /**
     * Getter fuer die Einbaurate dieser Variable
     *
     * @return Einbaurate dieser Variable
     */
    public double getEinbaurate() {
        return this.einbaurate;
    }

    /**
     * Getter fuer die Einbauraten bedingt von dieser Variable
     *
     * @return die bedingten Einbauraten
     */
    public double[] getEinbauratenBedingt() {
        return this.einbauratenBedingt;
    }

    /**
     * Getter fuer die Variablen Nummer (1-Indexiert)
     *
     * @return diese Variablen Nummer
     */
    public int getVariablenNummer() {
        return this.variablenNummer;
    }

    public boolean istWaehlbar() {
        return this.istWaehlbar;
    }

    public int getHauefigkeitInAuftraegen() {
        return hauefigkeitInAuftraegen;
    }

    public void setEinbaurate(double einbaurate) {
        this.einbaurate = einbaurate;
    }

}