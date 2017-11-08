package expression.types;

/**
 * Given an existing field name, return its dynamic type
 */
public class DynamicType implements TypeInformation {

    public final String name;

    public DynamicType(String name) {
        this.name = name;
    }
}
