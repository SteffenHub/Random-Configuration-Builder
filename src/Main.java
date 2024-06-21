import AutoBauer.*;
import Tools.KonsistenzPruefung;
import Tools.Operation;
import Tools.TxtReaderWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    /**
     * Number of variables in ruleset
     */
    private static int numberOfVariables;

    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);

        // Read the rule set
        System.out.println("Which CNF file should be used. Pass the path like: input_data/CNF_10Vars_Variance_1000.txt");
        String cnfFileName = scanner.nextLine();
        ArrayList<int[]> cnfInt = getRuleSet(cnfFileName);
        for (int[] line : cnfInt){
            System.out.println(Arrays.toString(line));
        }

        // Read the installation rates
        System.out.println("Which Installation rate file should be used. Pass the path like: input_data/freq_result_CNF_10Vars_Variance_1000_10Dec.txt");
        String iRFileName = scanner.nextLine();
        double[] iR = TxtReaderWriter.getEbr(iRFileName);
        for (double iRVar : iR){
            System.out.print(iRVar + " - ");
        }
        System.out.println();

        // Read procedure
        String[] procedures = new String[]{"zufallsKonflikteMitNNF", "allesZufaellig", "allesZufaelligMitEbr01Raus", "konsistenzPruefung", "untenNachOben", "EbrProzentualNaehern"};
        System.out.println("Which procedure you want to use. Type in a number. Choose between: ");
        for (int i = 0; i < procedures.length; i++) {
            System.out.println(i+1 + ". " + procedures[i]);
        }
        int procedureNumber = Integer.parseInt(scanner.nextLine());
        String procedure = procedures[procedureNumber -1];

        // Read number of models to build
        System.out.println("How many models should be build. Example: 100000");
        int numberModelsToBuild = Integer.parseInt(scanner.nextLine());

        // Read seed
        System.out.println("Which seed should be used for the random generator. Example: 8902374. Type 'None' if a random seed should be generated.");
        String seed_input = scanner.nextLine();
        boolean seed_set = false;
        long seed = -1;
        if (!seed_input.equals("None")){
            seed_set = true;
            seed = Long.parseLong(seed_input);
        }

        // Input constants
        int dtcount = 0;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-dtcount")){
                dtcount = Integer.parseInt(args[i+1]);
            }
        }

        switch (procedure) {
            case "zufallsKonflikteMitNNF" -> new ZufallsKonflikteMitNNF(numberModelsToBuild, cnfInt, numberOfVariables, iR, cnfFileName, dtcount, seed_set, seed, iRFileName);
            case "allesZufaellig" -> new AllesZufaellig(numberModelsToBuild, cnfInt, numberOfVariables, iR, cnfFileName, seed_set, seed, iRFileName);
            case "allesZufaelligMitEbr01Raus" -> new alleZufaelligMitEbrWahlen01(numberModelsToBuild, cnfInt, numberOfVariables, iR, cnfFileName, seed_set, seed, iRFileName);
            case "konsistenzPruefung" -> new KonsistenzPruefung(cnfInt, iR);
            case "untenNachOben" -> new untenNachOben(numberModelsToBuild, cnfInt, numberOfVariables, iR, cnfFileName,dtcount, seed_set, seed, iRFileName);
            case "EbrProzentualNaehern" -> new EbrProzentualNaehern(numberModelsToBuild, cnfInt, numberOfVariables, iR, cnfFileName, seed_set, seed, iRFileName);
            default -> System.err.println("Es ist nicht klar was der Autobauer tun soll. Waehle zwischen: zufallsKonflikteMitNNF, allesZufaellig, allesZufaelligMitEbr01Raus, konsistenzPruefung");
        }
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