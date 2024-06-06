package Tools.NNFBaum;

import java.util.ArrayList;

/**
 * Das Und-Objekt fuer den NNFBaum
 */
public class Und implements INNFBaum{

    /**
     * Alle Abzweigungen, die von diesem Und Knoten abgehen
     */
    private ArrayList<INNFBaum> abzweigungen;

    /**
     * Konstruktor.
     *
     * @param abzweigungen Alle Abzweigungen, die von diesem Und Knoten abgehen
     */
    public Und(ArrayList<INNFBaum> abzweigungen){
        this.abzweigungen = abzweigungen;
    }

    /**
     * getter fuer die Abzweigungen
     *
     * @return Alle Abzweigungen, die von diesem Und Knoten abgehen
     */
    public ArrayList<INNFBaum> getAbzweigungen(){
        return this.abzweigungen;
    }
}