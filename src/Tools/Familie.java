package Tools;

import java.util.ArrayList;
import java.util.List;

public class Familie {


    /**
     * Optimierung:
     * <p>
     * Binaere suche bei wahl von Variable nach wahrscheinlichkeiten durch eine summierte liste
     * <p>
     * Es gibt familien die nicht eine loesbare Variable haben => waehlbareVariableNummern ist null oder leer
     * <p>
     * <p>
     * 0,3 0,3 0,3
     * 0,3 0,6 0,9
     */

    // Die Variablen Nummern, die in dieser Familie sind
    private final Variable[] variableNummern;
    // Der Name dieser Familie
    private final String famName;
    //NUR PUBLIC FUER DEN TEST
    public final Variable[] waehlbareVariableNummern;
    public final double[] waehlbareVariableNummerEinbauraten;
    //private double[] waehlbareVariableEinbauratenSummiert;


    /**
     * Konstruktor fuer eine Familie
     *
     * @param famName   der Name dieser Familie
     * @param variableNummern die Variablen Nummern, die zu dieser Familie gehoeren
     */
    public Familie(String famName, Variable[] variableNummern) {
        this.famName = famName;
        this.variableNummern = variableNummern;
        List<Variable> waehlbareVariable = new ArrayList<>();
        double zuVerteildendeEinbauraten = 0.0;
        for (Variable variable : this.variableNummern) {
            if (variable.istWaehlbar()) {
                waehlbareVariable.add(variable);
            } else {
                zuVerteildendeEinbauraten += variable.getInstallationRate();
            }
        }
        this.waehlbareVariableNummern = waehlbareVariable.toArray(new Variable[0]);
        this.waehlbareVariableNummerEinbauraten = new double[this.waehlbareVariableNummern.length];
        double verteilendeEinbauratenProVar = zuVerteildendeEinbauraten / this.waehlbareVariableNummern.length;
        for (int i = 0; i < this.waehlbareVariableNummerEinbauraten.length; i++) {
            this.waehlbareVariableNummerEinbauraten[i] = this.waehlbareVariableNummern[i].getInstallationRate() + verteilendeEinbauratenProVar;
        }

        // pruefen, ob die Einbauraten insgesamt 1 ergeben
        double wahrscheinlichkeitZsm = 0.0;
        for (Variable variable : variableNummern) {
            wahrscheinlichkeitZsm += variable.getInstallationRate();
        }
        System.out.println(this.famName);
        while ((wahrscheinlichkeitZsm < 0.999 || wahrscheinlichkeitZsm > 1.01) && !this.famName.equals("EIL") && !this.famName.equals("KDT") && !this.famName.equals("SNH")) {
            System.out.println("Die Einbauraten der Variablen Nummer einer Familie ergeben nicht 1.0 zusammen");
        }

        //Familien mit einbaurate insgesamt < 1 die einbauraten anders verteilen
        if ((wahrscheinlichkeitZsm < 1.0 || wahrscheinlichkeitZsm > 1.0)) {
            verteileEinbauraten();
        }
    }

    public void verteileEinbauraten() {
        double wahrscheinlichkeitZsm = 0.0;
        for (Variable variable : variableNummern) {
            wahrscheinlichkeitZsm += variable.getInstallationRate();
        }
        wahrscheinlichkeitZsm = 1 - wahrscheinlichkeitZsm;
        int anzahlZusammen = 0;
        for (Variable variable : this.waehlbareVariableNummern) {
            anzahlZusammen += variable.getFrequencyInOrders();
        }
        for (int i = 0; i < this.waehlbareVariableNummerEinbauraten.length; i++) {
            this.waehlbareVariableNummerEinbauraten[i] = this.waehlbareVariableNummerEinbauraten[i] + (((double) this.waehlbareVariableNummern[i].getFrequencyInOrders() / anzahlZusammen) * wahrscheinlichkeitZsm);
            //this.waehlbareVariableNummern[i].setEinbaurate(this.waehlbareVariableNummerEinbauraten[i]);
        }
    }

    /**
     * TODO error handling
     * Waehlt nach den Einbauraten der einzelnen Variablen Nummern eine Variablen Nummer zufaellig aus
     *
     * @return eine zufaellige Variablen Nummer aus dieser Familie
     */
    public Variable waehleZufaelligEineVariableNachEinbauraten(double zufallsZahl) {
        double wahrscheinlichkeitBisher = 0;
        for (int i = 0; i < this.waehlbareVariableNummern.length; i++) {
            wahrscheinlichkeitBisher += this.waehlbareVariableNummerEinbauraten[i];
            if (wahrscheinlichkeitBisher >= zufallsZahl) {
                return this.waehlbareVariableNummern[i];
            }
        }
        return null;
    }

    /**
     * Getter fuer den Familien Namen
     *
     * @return der Familien Name
     */
    public String getFamName() {
        return this.famName;
    }
}