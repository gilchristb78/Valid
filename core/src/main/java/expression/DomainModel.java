package expression;

import java.util.*;

/**
 * Represents the desired features.
 *
 * Note that 'Exp' is not in the domain of data types, only sub-types are.
 */
public class DomainModel {

    /** Desired data types. */
    public List<Exp> data = new ArrayList<>();

    /** Desired operations. */
    public List<Operation> ops = new ArrayList<>();

    public DomainModel() {
        super();
    }

}
