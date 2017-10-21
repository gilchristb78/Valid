package domain.archway;

import domain.ContainerType;

public enum ArchwayContainerTypes implements ContainerType {
    // Special Container where cards are built down from kings.
    KingsDown("kings");

    public final String name;

    ArchwayContainerTypes(String name) {
        this.name = name;
    }
}
