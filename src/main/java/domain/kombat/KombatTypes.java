package domain.kombat;

import domain.ContainerType;

/**
 * These are the containers (score and numLeft) which are isolated to kombatSolitaire.
 */
public enum KombatTypes implements ContainerType {


    Score("score"),
    NumberCardsLeft("numLeft");

    public final String name;

    KombatTypes(String name) {
        this.name = name;
    }

    public String getName() { return name; }
}
