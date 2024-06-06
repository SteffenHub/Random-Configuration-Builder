package Tools.NNFBaum;

/**
 * Das Oder-Objekt fuer den NNFBaum
 */
public class Oder implements INNFBaum{

    /**
     * Der linke Zweig des Oder Knotens
     */
    private INNFBaum linkerZweig;
    /**
     * Der rechte Zweig des Oder Knotens
     */
    private INNFBaum rechterZweig;
    /**
     * Der Konflikt, der gelost werden muss, um eine richtung zu entscheiden
     */
    private int konflikt;
    /**
     * die richtung, in die man gehen muss, um die Konflikt variable zu waehlen(true fuer nach rechts um zu waehlen, false fuer nach links gehen um zu waehlen)
     */
    private boolean richtung;
    /**
     * ist bekannt, in welche richtung man gehen muss um den Konflikt zu waehlen, sonst muss berechnet werden
     */
    private boolean richtungBekannt;

    /**
     * Konstruktor.
     *
     * @param linkerZweig die linke Abzweigung des Oder-Objektes
     * @param rechterZweig die rechte Abzweigung des Oder-Objektes
     * @param konflikt der Konflikt(Variable), die geloest werden muss um weine richtung zu entscheiden
     */
    public Oder(INNFBaum linkerZweig, INNFBaum rechterZweig, int konflikt){
        this.linkerZweig = linkerZweig;
        this.rechterZweig = rechterZweig;
        this.konflikt = konflikt;
        this.richtungBekannt = false;
    }

    /**
     * getter fuer die linke Abzweigung
     *
     * @return die Linke Abzweigung des Oder-Objektes
     */
    public INNFBaum getLinkerZweig() {
        return linkerZweig;
    }

    /**
     * getter fuer die rechte Abzweigung
     *
     * @return die rechte Abzweigung des Oder-Objektes
     */
    public INNFBaum getRechterZweig() {
        return rechterZweig;
    }

    /**
     * getter fuer den Konflikt
     *
     * @return der Konflikt(Variable), die geloest werden muss, um eine Richtung zu entscheiden
     */
    public int getKonflikt() {
        return konflikt;
    }

    /**
     * getter fuer die Richtung, in die man gehen muss, um die Konflikt Variable zu waehlen
     *
     * @return die richtung, in die man gehen muss, um die Konflikt variable zu waehlen(true fuer nach rechts um zu waehlen, false fuer nach links gehen um zu waehlen)
     */
    public boolean getRichtung(){
        if (!this.richtungBekannt){
            findeRichtung(this.getLinkerZweig());
            this.richtungBekannt = true;
        }
        return this.richtung;
    }

    /**
     * Sucht im Baum die Richtung, in die man gehen muss, um die Konflikt Variable zu waehlen.
     * Dabei wird der Baum komplett durchgegangen ab diesem Oder Knotens, bis die Konflikt-Variable gefunden wird(egal ob positiv oder negativ).
     * Je nach Wahrheitswert des gefundenen Blattes und die Richtung, in die wir gegangen sind wird this.richtung entschieden
     *
     * @param baumObjekt dieser Oder Knoten
     */
    private void findeRichtung(INNFBaum baumObjekt){
        if (baumObjekt instanceof Und){
            for (INNFBaum abzweigung : ((Und) baumObjekt).getAbzweigungen()){
                findeRichtung(abzweigung);
            }
        }else if (baumObjekt instanceof Oder){
            findeRichtung(((Oder) baumObjekt).getLinkerZweig());
            findeRichtung(((Oder) baumObjekt).getRechterZweig());
        }else if (baumObjekt instanceof Blatt){
            if (this.konflikt == ((Blatt) baumObjekt).getVariable()){
                this.richtung = false;
            }
            if (-this.konflikt == ((Blatt) baumObjekt).getVariable()){
                this.richtung = true;
            }
        }
    }
}