package domain.ui;


/**
 * Represents a View that is required above-and-beyond that of Kombat Solitaire.
 *
 * Might not be in the right place, since this is aware of what's missing from SolutionDomain
 */
public class View  {

    public final String name;
    public final String parent;
    public final String model;

    public View (String name, String parent, String model) {
        this.name = name;
        this.parent = parent;
        this.model = model;
    }
}
