package Tools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class VariableTest {

    /**
     * Eingaben fuer erstellen einer Variable
     */
    Variable variable;
    int variablenNummer;
    int[][] anzahlZweiZusammen;
    boolean[][] auftraege;
    boolean istWaehlbar;
    ArrayList<int[]> familien;

    /**
     * Eingaben fuer verteile Einbauraten
     */
    double[] einbauratenBedingt;
    //ArrayList<int[]> familien;
    //int[][] anzahlZweiZusammen;

    @BeforeEach
    void setUp() {
        this.variablenNummer = 4;
        this.istWaehlbar = true;
        this.familien = new ArrayList<>(){{
            add(new int[] {1,2,3});
            add(new int[] {4});
        }};
        this.auftraege = new boolean[][]{
                new boolean[]{false,true,false,true},
                new boolean[]{false,false,true,true},
                new boolean[]{false,true,false,true},
                new boolean[]{false,true,false,true},
                new boolean[]{false,false,false,true},
                new boolean[]{false,false,false,true}

        };
        this.anzahlZweiZusammen = new int[4][4];
        for (int i = 0; i < anzahlZweiZusammen.length; i++) {
            Arrays.fill(this.anzahlZweiZusammen[i], -1);
        }
        this.variable = new Variable(this.variablenNummer, this.anzahlZweiZusammen, this.auftraege, this.istWaehlbar, this.familien);
        this.einbauratenBedingt = this.variable.getConditionalInstallationRate();
    }

    @Test
    void verteileEinbauraten() {
        // [0,0.5,0.1666666]->[0,75,25]

        // 1-0.6666 = 0.34                      | zu verteilende Einbauraten
        // Var2 = 1/4                            | diesePRAnzahl/waehlbarePRAnzahl
        // Var3 = 3/4
        // 0.34 * 1/4 = 0.085 = Var3             | einbaurate zugeteilt
        // 0.34 * 3/4 = 0.255 = Var2
        // Var2 -> 0.5 + 0.255 = 0.755           | Neue einbauraten berechnet
        // Var3 -> 0.166666 + 0.085 = 0.251666
        assertArrayEquals(new double[]{0.0,0.75,0.25,1.0}, this.variable.getConditionalInstallationRate());
    }
}