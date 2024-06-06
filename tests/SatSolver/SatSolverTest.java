package SatSolver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * Die Testklasse des SatSolver
 */
class SatSolverTest {

    /**
     * Der SatSolver
     */
    SatSolver satSolver;
    /**
     * die CNF als liste aus int-Arrays
     */
    ArrayList<int[]> cnf;

    @BeforeEach
    void setUp() {
        this.cnf = new ArrayList<>();
        this.cnf.add(new int[]{1, 3});
        this.cnf.add(new int[]{2});
        this.cnf.add(new int[]{4, -1});
        this.satSolver = new SatSolver(cnf);
        /*
        1 | 2 | 3 | 4 | Model
        ------------------
        0 	0 	0 	0 	0
        0 	0 	0 	1 	0
        0 	0 	1 	0 	0
        0 	0 	1 	1 	0
        0 	1 	0 	0 	0
        0 	1 	0 	1 	0
        0 	1 	1 	0 	1
        0 	1 	1 	1 	1
        1 	0 	0 	0 	0
        1 	0 	0 	1 	0
        1 	0 	1 	0 	0
        1 	0 	1 	1 	0
        1 	1 	0 	0 	0
        1 	1 	0 	1 	1
        1 	1 	1 	0 	0
        1 	1 	1 	1 	1
        */
    }

    @Test
    void istLoesbar() {
        assertTrue(satSolver.istLoesbar());

        this.cnf.add(new int[]{3});
        this.satSolver = new SatSolver(this.cnf);
        assertTrue(satSolver.istLoesbar());

        this.cnf.add(new int[]{4});
        this.satSolver = new SatSolver(this.cnf);
        assertTrue(satSolver.istLoesbar());

        this.cnf.add(new int[]{-1});
        this.satSolver = new SatSolver(this.cnf);
        assertTrue(satSolver.istLoesbar());
    }

    @Test
    void istLoesbarMit() {
        assertTrue(satSolver.istLoesbarMit(1));
        assertTrue(satSolver.istLoesbarMit(2));
        assertTrue(satSolver.istLoesbarMit(3));
        assertTrue(satSolver.istLoesbarMit(4));
        assertFalse(satSolver.istLoesbarMit(-2));
    }

    @Test
    void testIstLoesbarMit() {
        assertTrue(satSolver.istLoesbarMit(-4, 2));
        assertTrue(satSolver.istLoesbarMit(3, -4));
        assertTrue(satSolver.istLoesbarMit(-1, -4));
        assertFalse(satSolver.istLoesbarMit(1, -4));
        assertFalse(satSolver.istLoesbarMit(-2, 4));
        assertFalse(satSolver.istLoesbarMit(-2, 3));
        assertFalse(satSolver.istLoesbarMit(-3, -4));
    }

    @Test
    void addVariable() {
        satSolver.addVariable(5);
        assertEquals(5, satSolver.solver.nVars());
        assertFalse(satSolver.istLoesbarMit(-5));
    }

    @Test
    void getModel() {
        assertTrue(Arrays.equals(satSolver.getModel(), new int[]{-1, 2, 3, -4}) ||
                Arrays.equals(satSolver.getModel(), new int[]{-1, 2, 3, 4}) ||
                Arrays.equals(satSolver.getModel(), new int[]{1, 2, -3, 4}) ||
                Arrays.equals(satSolver.getModel(), new int[]{1, 2, 3, 4}));

        satSolver.addVariable(1);
        assertTrue(Arrays.equals(satSolver.getModel(), new int[]{1, 2, -3, 4}) ||
                Arrays.equals(satSolver.getModel(), new int[]{1, 2, 3, 4}));
    }

    @Test
    void findEquals() {
        assertEquals(0, satSolver.findEquals().size());

        ArrayList<int[]> newCNF = new ArrayList<>();
        newCNF.add(new int[]{-1, 2});
        newCNF.add(new int[]{1, -2});
        this.satSolver = new SatSolver(newCNF);
        assertEquals(1, satSolver.findEquals().size());
        assertArrayEquals(new int[]{1, 2}, satSolver.findEquals().get(0));

        newCNF = new ArrayList<>();
        newCNF.add(new int[]{-1, 2});
        newCNF.add(new int[]{1, -2});
        newCNF.add(new int[]{3, -4});
        newCNF.add(new int[]{4, -3});
        this.satSolver = new SatSolver(newCNF);
        assertEquals(2, satSolver.findEquals().size());
        assertArrayEquals(new int[]{1, 2}, satSolver.findEquals().get(0));
        assertArrayEquals(new int[]{3, 4}, satSolver.findEquals().get(1));

        newCNF = new ArrayList<>();
        newCNF.add(new int[]{-1, 2});
        newCNF.add(new int[]{1, -2});
        newCNF.add(new int[]{3, -4});
        newCNF.add(new int[]{4, -3});
        newCNF.add(new int[]{1, -4});
        newCNF.add(new int[]{4, -1});
        this.satSolver = new SatSolver(newCNF);
        assertEquals(6, satSolver.findEquals().size());
        assertArrayEquals(new int[]{1, 2}, satSolver.findEquals().get(0));
        assertArrayEquals(new int[]{1, 3}, satSolver.findEquals().get(1));
        assertArrayEquals(new int[]{1, 4}, satSolver.findEquals().get(2));
        assertArrayEquals(new int[]{2, 3}, satSolver.findEquals().get(3));
        assertArrayEquals(new int[]{2, 4}, satSolver.findEquals().get(4));
        assertArrayEquals(new int[]{3, 4}, satSolver.findEquals().get(5));
    }

    @Test
    void getVariablenDieSchonFeststehen() {
        assertEquals(1, satSolver.getVariablenDieSchonFeststehen().size());
        assertEquals(2, satSolver.getVariablenDieSchonFeststehen().get(0));

        satSolver.addVariable(1);
        this.cnf.add(new int[]{1});
        assertEquals(3, satSolver.getVariablenDieSchonFeststehen().size());
        assertEquals(1, satSolver.getVariablenDieSchonFeststehen().get(0));
        assertEquals(2, satSolver.getVariablenDieSchonFeststehen().get(1));
        assertEquals(4, satSolver.getVariablenDieSchonFeststehen().get(2));

        //[2] rausnehmen
        this.cnf.remove(1);
        //[1] rausnehmen
        this.cnf.remove(2);
        this.satSolver = new SatSolver(this.cnf);
        assertEquals(0, satSolver.getVariablenDieSchonFeststehen().size());
    }

    @Test
    void getRegelnDieLoesbarkeitVerhindern() {
        assertEquals(1, satSolver.getRegelnDieLoesbarkeitVerhindern(-2).size());
        assertArrayEquals(new int[]{2}, satSolver.getRegelnDieLoesbarkeitVerhindern(-2).get(0));

        this.cnf.add(new int[]{-2, 3});
        this.satSolver = new SatSolver(this.cnf);
        assertEquals(2, satSolver.getRegelnDieLoesbarkeitVerhindern(-3).size());
        assertArrayEquals(new int[]{2}, satSolver.getRegelnDieLoesbarkeitVerhindern(-3).get(0));
        assertArrayEquals(new int[]{-2, 3}, satSolver.getRegelnDieLoesbarkeitVerhindern(-3).get(1));
    }

    @Test
    void getFamilien() {
        this.cnf = new ArrayList<>();
        cnf.add(new int[]{-1, -2});
        cnf.add(new int[]{-2, -3});
        cnf.add(new int[]{-5, -7});
        cnf.add(new int[]{1, 2});
        cnf.add(new int[]{-1, -2});
        cnf.add(new int[]{3, 4, 5});
        cnf.add(new int[]{-3, -4});
        cnf.add(new int[]{-3, -5});
        cnf.add(new int[]{-4, -5});
        cnf.add(new int[]{6, 7});
        cnf.add(new int[]{-6, -7});

        assertEquals(3, satSolver.getFamilien(this.cnf).size());
        assertArrayEquals(new int[]{1, 2}, satSolver.getFamilien(this.cnf).get(0));
        assertArrayEquals(new int[]{3, 4, 5}, satSolver.getFamilien(this.cnf).get(1));
        assertArrayEquals(new int[]{6, 7}, satSolver.getFamilien(this.cnf).get(2));
    }

    @Test
    void getFamilienRegel() {
        this.cnf = new ArrayList<>();
        cnf.add(new int[]{-1, -2});
        cnf.add(new int[]{-2, -3});
        cnf.add(new int[]{-5, -7});
        cnf.add(new int[]{1, 2});
        cnf.add(new int[]{-1, -2});
        cnf.add(new int[]{3, 4, 5});
        cnf.add(new int[]{-3, -4});
        cnf.add(new int[]{-3, -5});
        cnf.add(new int[]{-4, -5});
        cnf.add(new int[]{6, 7});
        cnf.add(new int[]{-6, -7});

        assertEquals(8, satSolver.getFamilienRegel(this.cnf).size());
        assertArrayEquals(new int[]{1, 2}, satSolver.getFamilienRegel(this.cnf).get(0));
        assertArrayEquals(new int[]{-1, -2}, satSolver.getFamilienRegel(this.cnf).get(1));
        assertArrayEquals(new int[]{3, 4, 5}, satSolver.getFamilienRegel(this.cnf).get(2));
        assertArrayEquals(new int[]{-3, -4}, satSolver.getFamilienRegel(this.cnf).get(3));
        assertArrayEquals(new int[]{-3, -5}, satSolver.getFamilienRegel(this.cnf).get(4));
        assertArrayEquals(new int[]{-4, -5}, satSolver.getFamilienRegel(this.cnf).get(5));
        assertArrayEquals(new int[]{6, 7}, satSolver.getFamilienRegel(this.cnf).get(6));
        assertArrayEquals(new int[]{-6, -7}, satSolver.getFamilienRegel(this.cnf).get(7));
    }

    @Test
    void getVariablenDieLoesbarkeitVerhindern() {
    }

    @Test
    void getNVars() {
    }
}