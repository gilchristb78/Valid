package domain;

/**
 
  When an instance in the domain has specialized concepts, these
  are created as internal classes. If the concepts are worthwhile
  enough to be exposed as a new concept, then they are pushed
  out into the domain package.

 */


public class FreeCell extends Solitaire {

    class FreePile extends Pile {

    }

    class HomePile extends Pile {

    }

    public FreeCell() {

    }
}