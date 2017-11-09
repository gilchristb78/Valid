package expression;

import expression.types.TypeInformation;

/**
 * This is the high-level representation of a desired operation.
 */
public abstract class Operation {

    /** Name of operation. */
    public final String name;

    /** Resulting type of operation. */
    public final TypeInformation type;

    public Operation(String name, TypeInformation type) {
        this.name = name;
        this.type = type;
    }
}
