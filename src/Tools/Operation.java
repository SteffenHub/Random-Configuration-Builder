package Tools;

import java.util.ArrayList;

/**
 * Hier werden paar Rechenoperationen ausgelagert, die in jedem AutoBauer gebraucht werden koennten.
 */
public class Operation {

    /**
     * Sucht in einer nnf Alle Konflikte raus.
     * Also holt alle Konflikt Variablen aus den O-Zeilen.
     *
     * @param nnf Die zuvor mit dem c2d erstellte und eingelesene nnf
     * @return Liste mit allen Konflikt Variablen
     */
    public static ArrayList<Integer> getAlleKonflikte(ArrayList<String> nnf) {
        ArrayList<Integer> alleKonflikte = new ArrayList<>();
        //nnf durchgehen
        for (String zeile : nnf) {
            //Wenn Oder-Zeile gefunden
            if (zeile.charAt(0) == 'O') {
                //Zeile bei leerzeichen spalten
                String[] aufgeteilt = zeile.split(" ");
                boolean existiertBereits = false;
                //Pruefen, ob konflikt schon vorkam
                for (int konflikt : alleKonflikte) {
                    //konflikt Variable steht an zweiter stelle "O 3 ..."
                    if (Integer.parseInt(aufgeteilt[1]) == konflikt) {
                        existiertBereits = true;
                    }
                }
                // Wenn dieser Konflikt nicht schonmal vorkam
                if (!existiertBereits) {
                    alleKonflikte.add(Integer.parseInt(aufgeteilt[1]));
                }
            }
        }
        return alleKonflikte;
    }

    /**
     * Formt eine cnf von Strings zu int Arrays um.
     * Dabei wird die erste Zeile ausgelassen, wenn dort die CNF Beschreibung steht z.B.: "p cnf 234 32"
     * Zusaetzlich wird das letzte Element jeder Zeile ausgelassen, um die 0-len am ende nicht mitzunehmen,
     * z.B.: "-50 3 0" wird zu new int[] {-50,3}
     *
     * @param cnfStr die zuvor eingelesene cnf als Liste aus Strings
     * @return die CNF als Liste aus int Arrays, ohne erste Zeile und 0-len in jeder Zeile
     */
    public static ArrayList<int[]> stringListZuListAusIntArrays(ArrayList<String> cnfStr) {
        ArrayList<int[]> cnfArr = new ArrayList<>();
        int start = 0;
        // Wenn in erster Zeile noch cnf beschreibung steht
        if (cnfStr.get(0).charAt(0) == 'p') {
            //dann diese Zeile ueberspringen
            start = 1;
        }
        //cnf durchgehen
        for (int i = start; i < cnfStr.size(); i++) {
            //Zeile bei leerzeichen spalten
            String[] aufgeteilt = cnfStr.get(i).split(" ");
            //speicher reservieren. Um 1 verkleiner, um die 0 am Zeilenende zu ignorieren
            int[] klausel = new int[aufgeteilt.length - 1];
            for (int x = 0; x < klausel.length; x++) {
                klausel[x] = Integer.parseInt(aufgeteilt[x]);
            }
            cnfArr.add(klausel);
        }
        return cnfArr;
    }

    /**
     * Sucht in den Ebr alle Einbauraten, die 0 oder 1 sind.
     *
     * @param ebr die zuvor eingelesenen Einbauraten
     * @return Liste mit Vars(Variable), deren Ebr=0 or 1. Ebr=0 sind mit einem minus(negative Zahl) markiert
     */
    public static ArrayList<Integer> getEbr01(double[] ebr) {
        ArrayList<Integer> zusatzAusEbr = new ArrayList<>();
        //ebr durchgehen
        for (int i = 0; i < ebr.length; i++) {
            //Wenn ebr = 0
            if (ebr[i] == 0.0) {
                zusatzAusEbr.add((i + 1) * -1);
            }
            //wenn ebr = 1
            if (ebr[i] == 1.0) {
                zusatzAusEbr.add((i + 1));
            }
        }
        return zusatzAusEbr;
    }

    /**
     * Stellt eine Liste zsm, indem alle Integer-Werte von 1 bis nVars vertreten sind.
     *
     * @param nVars anzahl an Variablen, die es in dem Regelwerk gibt(die wir in der Liste haben wollen)
     * @return eine Liste aus Integer, indem alle Variablen drin sind
     */
    public static ArrayList<Integer> getListOfVars1ToN(int nVars) {
        ArrayList<Integer> alleVars = new ArrayList<>();
        for (int i = 1; i <= nVars; i++) {
            alleVars.add(i);
        }
        return alleVars;
    }

    /**
     * Erstellt eine .nnf-Datei mit dem c2d und liest diese ein.
     *
     * @param cnfDateiName Fuer den c2d. Der cnf-Dateiname mit dem Regelwerk
     * @param dt_count Fuer den c2d. Wie oft ein Baum erstellt werden soll bevor einer ausgegeben wird
     * @return Die .nnf-Datei
     */
    public static ArrayList<String> nnfEinlesen(String cnfDateiName, int dt_count){
        System.out.println("Erstelle nnf mit dem c2d...");
        //dateiendung rausnehmen
        String dateiNameOhneEndung = cnfDateiName.substring(0, cnfDateiName.length() - 4);
        //txt zu cnf umbenennen
        KonsolenSchnittstelle.konsolenEingabe("cmd /c ren " + cnfDateiName + " " + dateiNameOhneEndung + ".cnf");
        //c2d drueber laufen lassen und nnf erstellen
        if (dt_count > 0)
            KonsolenSchnittstelle.konsolenEingabe("c2d -in " + dateiNameOhneEndung + ".cnf" + " -dt_count " + dt_count);
        else
            KonsolenSchnittstelle.konsolenEingabe("c2d -in " + dateiNameOhneEndung + ".cnf");
        //nnf einlesen
        ArrayList<String> nnf = TxtReaderWriter.getTxtFromSamePath(dateiNameOhneEndung + ".cnf.nnf");
        //wieder zurueck zu einer txt umbenennen
        KonsolenSchnittstelle.konsolenEingabe("cmd /c ren " + dateiNameOhneEndung + ".cnf " + cnfDateiName);
        //die nnf loeschen
        KonsolenSchnittstelle.konsolenEingabe("cmd /c del " + dateiNameOhneEndung + ".cnf.nnf");
        System.out.println("nnf size: " + nnf.size());

        return nnf;
    }
}