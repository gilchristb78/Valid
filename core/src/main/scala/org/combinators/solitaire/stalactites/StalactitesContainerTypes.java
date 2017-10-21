package org.combinators.solitaire.stalactites;

import domain.ContainerType;

public enum StalactitesContainerTypes implements ContainerType {
        // Cards at top
        Base("base");

        public final String name;

        StalactitesContainerTypes(String name) {
                this.name = name;
        }

}
