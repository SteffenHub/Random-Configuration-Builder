package Tools.NNFBaum;

/**
 * Das Blatt-Objekt fuer den NNFBaum
 */
public class Blatt implements INNFBaum{

    /**
     * welche variable steht in diesem Blatt Knoten
     */
    private int variable;

    /**
     * Konstruktor.
     *
     * @param variable welche variable steht in diesem Blatt Knoten
     */
    public Blatt(int variable){
        this.variable = variable;
    }

    /**
     * getter fuer die variable im Blatt
     *
     * @return welche variable steht in diesem Blatt Knoten
     */
    public int getVariable() {
        return variable;
    }
}