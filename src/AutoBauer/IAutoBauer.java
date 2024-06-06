package AutoBauer;

/**
 * Interface der AutoBauer.
 * Hier stehen Methoden, die jeder AutoBauer implementieren muss.
 */
public interface IAutoBauer {

    /**
     * hier werden alle Variablen in eine Liste gesteckt und speicher fuer das Ergebnis reserviert
     */
    void init();

    /**
     * Falls ein Autobauer zusaetzliche Initialisierung braucht kann er diese Methode ueberschreiben
     */
    void zusaetzlicheInit();

    /**
     * berechnet genau ein baubares Model
     */
    void berechneEinBaubaresModel();

    /**
     * startet die Haupt for-Schleife
     */
    void run();
}