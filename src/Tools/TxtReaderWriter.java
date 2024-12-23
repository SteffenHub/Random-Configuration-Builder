package Tools;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Diese Klasse kuemmert sich um das schreiben und lesen von Txt-Dateien
 */
public class TxtReaderWriter {

    /**
     * Liest eine Datei ein und gibt den Inhalt in eine Liste von Strings aus.
     * Bisher nur .txt erprobt moeglicherweise andere Dateitypen moeglich
     *
     * @param nameDerDateiMitEndung voller Name mit DateiEndung z.B.: "Regeln.txt"
     * @return Inhalt der angegeben Datei
     */
    public static ArrayList<String> getTxtFromSamePath(String nameDerDateiMitEndung) {
        ArrayList<String> txtFile = new ArrayList<>();
        try {
            //datei einholen
            File myObj = new File(nameDerDateiMitEndung);
            //Reader drüber legen
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                //Zeilenweise Inhalt speichern
                txtFile.add(myReader.nextLine());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.err.println("--------------------------ERROR DESCRIPTION--------------------------");
            System.err.println("Die Datei '" + nameDerDateiMitEndung + "' konnte nicht gelesen werden.");
            System.err.println();
            e.printStackTrace();
            System.err.println("---------------------------DESCRIPTION END---------------------------");
        }
        return txtFile;
    }

    /**
     * schreibt das Ergebnis des AutoBauers in eine Txt-Datei
     *
     * @param nameDerDatei Name, wie die Ergebnis Datei heissen soll
     * @param modelleBool  das ergebnis des AutoBauers als 2 Dimensionales Bool-Array
     * @param seed
     */
    public static void writeModelleBool(String nameDerDatei, boolean[][] modelleBool, long seed, long executionTime, int numberOfVariables, int generatedModels, String cnfFileName, String iRFileName, String procedure, double averageDeviation) {
        //StopUhr starten
        Instant start1 = Instant.now();

        int lastIndexSlash = iRFileName.lastIndexOf('/');
        if (lastIndexSlash != -1){
            nameDerDatei = iRFileName.substring(lastIndexSlash+1);
        }
        nameDerDatei = "randomConfBuilder_result_" + generatedModels + "_" + nameDerDatei;

        try (FileWriter fw = new FileWriter("./" + nameDerDatei, StandardCharsets.UTF_8);
            BufferedWriter writer = new BufferedWriter(fw)) {
            writer.append("c Used Seed: ").append(String.valueOf(seed));
            writer.newLine();
            writer.append("c Execution time: ").append(String.valueOf(executionTime)).append(" seconds");
            writer.newLine();
            writer.append("c Number of variables: ").append(String.valueOf(numberOfVariables));
            writer.newLine();
            writer.append("c Generated models: ").append(String.valueOf(generatedModels));
            writer.newLine();
            writer.append("c Used CNF file: ").append(String.valueOf(cnfFileName));
            writer.newLine();
            writer.append("c Used Installation rates file: ").append(iRFileName);
            writer.newLine();
            writer.append("c Used procedure: ").append(procedure);
            writer.newLine();
            writer.append("c Average deviation: ").append(String.valueOf(averageDeviation));
            writer.newLine();

            for (boolean[] zeile : modelleBool) {
                String line = Arrays.toString(zeile).replace("true", "1").replace("false", "0").replace("[", "").replace("]", "").replace(" ","").replace(",","");
                writer.append(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Die Datei '" + nameDerDatei + "' wurde im selben Ordner gespeichert");
        Instant end1 = Instant.now();
        Duration interval1 = Duration.between(start1, end1);
        System.out.println("Zeit fuer das Speichern: " + interval1.getSeconds() + "sec");
    }

    public static void writeListOfStringArrays(String nameDerDatei, ArrayList<String[]> liste){
        //StopUhr starten
        Instant start1 = Instant.now();

        try (FileWriter fw = new FileWriter("./" + nameDerDatei, StandardCharsets.UTF_8);
             BufferedWriter writer = new BufferedWriter(fw)) {

            for (String[] zeile : liste){
                String zeileStr = Arrays.toString(zeile);
                writer.append(zeileStr.substring(1,zeileStr.length()-1));
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Die Datei '" + nameDerDatei + "' wurde im selben Ordner gespeichert");
        Instant end1 = Instant.now();
        Duration interval1 = Duration.between(start1, end1);
        System.out.println("Zeit fuer das Speichern: " + interval1.getSeconds() + "sec");
    }

    /**
     * liest die Einbauraten ein
     *
     * @param nameDerDatei name der Datei, indem die Einbauraten sind(z.B. 'ebr.txt')
     * @return eine double-Array mit den Einbauraten
     */
    public static double[] getEbr(String nameDerDatei) {
        ArrayList<String> ebrStr = getTxtFromSamePath(nameDerDatei);
        ebrStr.removeIf(line -> line.charAt(0) == 'c');
        double[] ebr = new double[ebrStr.size()];
        for (int i = 0; i < ebr.length; i++) {
            ebr[i] = Double.parseDouble(ebrStr.get(i));
        }
        return ebr;
    }
}