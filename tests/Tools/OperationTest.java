package Tools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OperationTest {

    ArrayList<String> nnf;
    ArrayList<String> cnf;
    double[] ebr;

    @BeforeEach
    void setUp() {
        this.nnf = new ArrayList<>();
        this.nnf.add("nnf 14 14 5");
        this.nnf.add("L 1");
        this.nnf.add("O 3 2 4");
        this.nnf.add("L 5");
        this.nnf.add("O 2 0 3");
        this.nnf.add("A 3 0 1 2");

        this.cnf = new ArrayList<>();
        this.cnf.add("p cnf 4 235");
        this.cnf.add("2 13 0");
        this.cnf.add("-4 5 0");
        this.cnf.add("3 -3 0");

        this.ebr = new double[6];
        this.ebr[0] = 0.234;    //1
        this.ebr[1] = 1.0;      //2
        this.ebr[2] = 1.0;      //3
        this.ebr[3] = 0.23523;  //4
        this.ebr[4] = 0.0;      //5
        this.ebr[5] = 0.0;      //6
    }

    @Test
    void getAlleKonflikte() {
        assertEquals(2, Operation.getAlleKonflikte(this.nnf).size());
        assertEquals(3, Operation.getAlleKonflikte(this.nnf).get(0));
        assertEquals(2, Operation.getAlleKonflikte(this.nnf).get(1));
    }

    @Test
    void stringListZuListAusIntArrays() {
        assertEquals(3, Operation.stringListZuListAusIntArrays(this.cnf).size());
        assertArrayEquals(new int[]{2, 13}, Operation.stringListZuListAusIntArrays(this.cnf).get(0));
        assertArrayEquals(new int[]{-4, 5}, Operation.stringListZuListAusIntArrays(this.cnf).get(1));
        assertArrayEquals(new int[]{3, -3}, Operation.stringListZuListAusIntArrays(this.cnf).get(2));
    }

    @Test
    void printStatsKonflikte() {
        //No tests, Just printing
    }

    @Test
    void getEbr01() {
        assertEquals(4, Operation.getEbr01(this.ebr).size());
        assertEquals(2, Operation.getEbr01(this.ebr).get(0));
        assertEquals(3, Operation.getEbr01(this.ebr).get(1));
        assertEquals(-5, Operation.getEbr01(this.ebr).get(2));
        assertEquals(-6, Operation.getEbr01(this.ebr).get(3));
    }
}