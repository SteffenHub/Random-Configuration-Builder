package Tools;

import java.io.IOException;

/**
 * Die Schnittstelle zur Konsole.
 * Hier koennen Befehle in der Konsole ausgefuehrt werden
 */
public class KonsolenSchnittstelle {

    /**
     * Fuehrt den angegeben Befehl in der Konsole aus.
     *
     * @param cmdString der Befehl z.B.:"c2d -in R.cnf"
     */
    public static void konsolenEingabe(String cmdString) {
        try {
            Process proc = Runtime.getRuntime().exec(cmdString);
            proc.waitFor();
        } catch (InterruptedException | IOException eIE) {
            System.err.println("--------------------------ERROR DESCRIPTION--------------------------");
            System.err.println("Der Befehl '" + cmdString + "' konnte nicht ausgefuehrt werden.");
            System.err.println();
            eIE.printStackTrace();
            System.err.println("---------------------------DESCRIPTION END---------------------------");
        }
    }
}