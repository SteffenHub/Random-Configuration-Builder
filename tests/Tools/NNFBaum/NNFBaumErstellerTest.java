package Tools.NNFBaum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NNFBaumErstellerTest {

    private NNFBaumErsteller nnfBaumErsteller;

    @BeforeEach
    void setUp() {
        nnfBaumErsteller = new NNFBaumErsteller("test/testDateien/cnf_test1.txt",0);
    }

    @Test
    void getBaum() {
    }
}