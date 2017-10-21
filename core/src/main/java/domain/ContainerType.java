package domain;

import domain.constraints.MoveInformation;

/**
 * Default types in the domain of solitaire.
 *
 * To add new kinds of containers, just have an enum extend this interface
 *
 * Reason for having ContainerType extend MoveInformation is to enable individual elements
 * to also be used within the description of valid moves.
 */
public interface ContainerType extends MoveInformation {
     /** Every entity must be capable of returning a (unique) name. */
     String getName();
}
