import AutoBauer.*;
import Tools.KonsistenzPruefung;
import Tools.Operation;
import Tools.TxtReaderWriter;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class Main {

    /**
     * Number of variables in ruleset
     */
    private static int numberOfVariables;

    public static void main(String[] args) throws Exception {

        // save current time for calculation time
        Instant start = Instant.now();

        // Input constants
        final String cnfFileName = args[2];
        final String iRFileName = args[3];
        final String procedure = args[0];
        final int numberModelsToBuild = Integer.parseInt(args[1]);
        int dtcount = 0;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-dtcount")){
                dtcount = Integer.parseInt(args[i+1]);
            }
        }
        boolean seed_set = false;
        Long seed = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-seed")){
                seed = Long.parseLong(args[i+1]);
                seed_set = true;
            }
        }

        // Read the rule set
        ArrayList<int[]> cnfInt = getRuleSet(cnfFileName);
        // Read the installation rates
        double[] ebr = TxtReaderWriter.getEbr(iRFileName);

        switch (procedure) {
            case "zufallsKonflikteMitNNF" -> new ZufallsKonflikteMitNNF(numberModelsToBuild, cnfInt, numberOfVariables, ebr, cnfFileName, dtcount, seed_set, seed);
            case "allesZufaellig" -> new AllesZufaellig(numberModelsToBuild, cnfInt, numberOfVariables, ebr, cnfFileName, seed_set, seed);
            case "allesZufaelligMitEbr01Raus" -> new alleZufaelligMitEbrWahlen01(numberModelsToBuild, cnfInt, numberOfVariables, ebr, cnfFileName, seed_set, seed);
            case "konsistenzPruefung" -> new KonsistenzPruefung(cnfInt, ebr);
            case "untenNachOben" -> new untenNachOben(numberModelsToBuild, cnfInt, numberOfVariables, ebr, cnfFileName,dtcount, seed_set, seed);
            case "EbrProzentualNaehern" -> new EbrProzentualNaehern(numberModelsToBuild, cnfInt, numberOfVariables, ebr, cnfFileName, seed_set, seed);
            default -> System.err.println("Es ist nicht klar was der Autobauer tun soll. Waehle zwischen: zufallsKonflikteMitNNF, allesZufaellig, allesZufaelligMitEbr01Raus, konsistenzPruefung");
        }

        // print calculation time
        Instant end = Instant.now();
        Duration interval = Duration.between(start, end);
        System.out.println("Execution time in seconds: " + interval.getSeconds());
    }

    /**
     * Reads the rule set and transforms it into a list of int arrays
     *
     * @param cnfFileName The name of the txt file with the CNF
     * @return The rule set as a list of in arrays
     */
    public static ArrayList<int[]> getRuleSet(String cnfFileName) throws Exception {
        // read cnf
        ArrayList<String> cnfStr = TxtReaderWriter.getTxtFromSamePath(cnfFileName);

        boolean foundNumberVars = false;
        for (String line : cnfStr) {
            if (line.charAt(0) == 'p') {
                String[] cnfStrSplit = line.split(" ");
                String numberVarsStr = cnfStrSplit[cnfStrSplit.length - 2];
                numberOfVariables = Integer.parseInt(numberVarsStr);
                foundNumberVars = true;
                break;
            }
        }
        if (!foundNumberVars){
            throw new Exception("Can't found a line in cnf file starting with p");
        }
        cnfStr.removeIf(line -> line.charAt(0) == 'c');
        // cnf to int arrays
        return Operation.stringListZuListAusIntArrays(cnfStr);
    }
}