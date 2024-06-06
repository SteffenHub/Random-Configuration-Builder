import AutoBauer.*;
import Tools.KonsistenzPruefung;
import Tools.Operation;
import Tools.TxtReaderWriter;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class Main {

    /**
     * die Anzahl an Variablen Nummern im Regelwerk
     */
    private static int anzahlVariablen;

    public static void main(String[] args) {

        //stoppuhr starten
        Instant start = Instant.now();

        //Konstanten
        final String cnfDateiName = args[2];
        final String ebrDateiName = args[3];
        final String welcherAutoBauer = args[0];
        final int anzahlZuBauendeModelle = Integer.parseInt(args[1]);
        int dtcount = 0;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-dtcount")){
                dtcount = Integer.parseInt(args[i+1]);
            }
        }

        //das RegelWerk einlesen
        ArrayList<int[]> cnfInt = getRegelwerk(cnfDateiName);
        //die Einbauraten einlesen
        double[] ebr = getEbr(ebrDateiName);

        switch (welcherAutoBauer) {
            case "zufallsKonflikteMitNNF" -> new ZufallsKonflikteMitNNF(anzahlZuBauendeModelle, cnfInt, anzahlVariablen, ebr, cnfDateiName,dtcount);
            case "allesZufaellig" -> new AllesZufaellig(anzahlZuBauendeModelle, cnfInt, anzahlVariablen, ebr, cnfDateiName);
            case "allesZufaelligMitEbr01Raus" -> new alleZufaelligMitEbrWahlen01(anzahlZuBauendeModelle, cnfInt, anzahlVariablen, ebr, cnfDateiName);
            case "konsistenzPruefung" -> new KonsistenzPruefung(cnfInt, ebr);
            case "untenNachOben" -> new untenNachOben(anzahlZuBauendeModelle, cnfInt, anzahlVariablen, ebr, cnfDateiName,dtcount);
            case "EbrProzentualNaehern" -> new EbrProzentualNaehern(anzahlZuBauendeModelle, cnfInt, anzahlVariablen, ebr, cnfDateiName);
            default -> System.err.println("Es ist nicht klar was der Autobauer tun soll. Waehle zwischen: zufallsKonflikteMitNNF, allesZufaellig, allesZufaelligMitEbr01Raus, konsistenzPruefung");
        }

        //Stoppuhr Zeit ausgeben
        Instant end = Instant.now();
        Duration interval = Duration.between(start, end);
        System.out.println("Execution time in seconds: " + interval.getSeconds());
    }

    /**
     * liest das RegelWerk ein und formt es zu einer Liste aus int-Arrays um
     *
     * @param cnfDateiName der Name der Txt-Datei indem die cnf ist
     * @return das RegelWerk als Liste aus int-Arrays
     */
    public static ArrayList<int[]> getRegelwerk(String cnfDateiName) {
        //cnf einlesen
        ArrayList<String> cnfStr = TxtReaderWriter.getTxtFromSamePath(cnfDateiName);

        String[] cnfStrSplit = cnfStr.get(0).split(" ");
        String anzahlVarsStr = cnfStrSplit[cnfStrSplit.length - 2];
        anzahlVariablen = Integer.parseInt(anzahlVarsStr);

        //cnf zu int Arrays umformen
        return Operation.stringListZuListAusIntArrays(cnfStr);
    }

    /**
     * liest die Einbauraten ein
     *
     * @param ebrDateiName name der Datei, indem die Einbauraten stehen
     * @return ein Array aus allen Einbauraten
     */
    public static double[] getEbr(String ebrDateiName) {
        return TxtReaderWriter.getEbr(ebrDateiName);
    }
}