package Tools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class FamilieTest {

    private Familie fam;
    private int[][] anzahlZweiZsm;
    private boolean[][] auftraege;
    private Variable[] variableNummern;

    @BeforeEach
    void setUp() {
        this.auftraege = new boolean[][]{
                new boolean[]{false,true,false},
                new boolean[]{false,false,true},
                new boolean[]{false,true,false},
                new boolean[]{false,true,false},
                new boolean[]{false,false,false},
                new boolean[]{false,false,false}

        };
        this.anzahlZweiZsm = new int[3][3];
        for (int i = 0; i < anzahlZweiZsm.length; i++) {
            Arrays.fill(this.anzahlZweiZsm[i], -1);
        }
        this.variableNummern = new Variable[]{
                //new Variable(1, this.anzahlZweiZsm, this.auftraege, true),
                //new Variable(2, this.anzahlZweiZsm, this.auftraege, true),
                //new Variable(3, this.anzahlZweiZsm, this.auftraege, true)

        };

        this.fam = new Familie("Fam1", this.variableNummern);
    }

    @Test
    void verteileEinbauraten() {

        // [0,0.5,0.1666666]->[0,75,25]

        // 1-0.6666 = 0.34                      | zu verteilende Einbauraten
        // Var2 = 1/4                            | dieseVarAnzahl/waehlbareVarAnzahl
        // Var3 = 3/4
        // 0.34 * 1/4 = 0.085 = Var3             | einbaurate zugeteilt
        // 0.34 * 3/4 = 0.255 = Var2
        // Var2 -> 0.5 + 0.255 = 0.755           | Neue einbauraten berechnet
        // Var3 -> 0.166666 + 0.085 = 0.251666
        assertArrayEquals(new double[]{0.0,0.75,0.25}, this.fam.waehlbareVariableNummerEinbauraten);
    }

    @Test
    void waehleZufaelligEineVarNachEinbauraten() {
    }

    @Test
    void getFamName() {
    }
}