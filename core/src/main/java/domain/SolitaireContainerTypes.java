package domain;

/**
 * These are the default types.
 */
public enum SolitaireContainerTypes implements ContainerType {
    Foundation("foundation"),
    Tableau("tableau"),
    Reserve("reserve"),
    Waste("waste"),
    Stock("stock");

    /**
     * Each container type has a unique name. Make sure of this
     * when doing extensions.
     */
    public final String name;

    SolitaireContainerTypes(String name) {
        this.name = name;
    }

    /** To conform to interface. */
    public String getName() { return name; }
}
