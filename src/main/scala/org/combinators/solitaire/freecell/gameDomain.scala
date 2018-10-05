package org.combinators.solitaire.freecell

import com.github.javaparser.ast.body.{BodyDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.{Expression, Name}
import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.stmt.Statement
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types._
import org.combinators.cls.types.syntax._
import org.combinators.templating.twirl.Java
import domain.freeCell.{Full, SufficientFree}
import org.combinators.solitaire.domain._
import org.combinators.solitaire.shared._
import org.combinators.solitaire.shared.compilation.{CodeGeneratorRegistry, generateHelper}

// Looks awkward how solitaire val is defined, but I think I need to do this
// to get the code to compile 
class gameDomain(override val solitaire:Solitaire) extends SolitaireDomain(solitaire) with Controller with GameTemplate  {

  object freecellCodeGenerator {
    val generators:CodeGeneratorRegistry[Expression] = CodeGeneratorRegistry.merge[Expression](

      CodeGeneratorRegistry[Expression, SufficientFree] {
        case (registry:CodeGeneratorRegistry[Expression], c:SufficientFree) =>
          val destination = registry(c.destination).get
          val src = registry(c.src).get
          val column = registry(c.column).get
          val reserve = registry(c.reserve).get
          val tableau = registry(c.tableau).get
          Java(s"""ConstraintHelper.sufficientFree($column, $src, $destination, $reserve, $tableau)""").expression()
      },

      CodeGeneratorRegistry[Expression, Full] {
        case (registry:CodeGeneratorRegistry[Expression], f:Full) =>
          val src = registry(f.src).get
          Java(s"""ConstraintHelper.isFull($src)""").expression()
      },


    ).merge(constraintCodeGenerators.generators)
  }

  /**
    * Freecell requires specialized extensions for constraints to work.
    */
  @combinator object FreeCellGenerator {
    def apply: CodeGeneratorRegistry[Expression] = freecellCodeGenerator.generators
    val semanticType: Type = constraints(constraints.generator)
  }

  /** Each Solitaire variation must provide default do generation. */
  @combinator object DefaultDoGenerator {
    def apply: CodeGeneratorRegistry[Seq[Statement]] = constraintCodeGenerators.doGenerators

    val semanticType: Type = constraints(constraints.do_generator)
  }

  /** Each Solitaire variation must provide default conversion for moves. */
  @combinator object DefaultUndoGenerator {
    def apply: CodeGeneratorRegistry[Seq[Statement]] = constraintCodeGenerators.undoGenerators

    val semanticType: Type = constraints(constraints.undo_generator)
  }

  /**
    * Deal may require additional generators.
    */
  @combinator object DefaultDealGenerator {
    def apply: CodeGeneratorRegistry[Expression] = constraintCodeGenerators.mapGenerators
    val semanticType: Type = constraints(constraints.map)
  }


  // vagaries of java imports means these must be defined as well.
  @combinator object ExtraImports {
    def apply(nameExpr: Name): Seq[ImportDeclaration] = {
      Seq(
        Java(s"import $nameExpr.controller.*;").importDeclaration(),
        Java(s"import $nameExpr.model.*;").importDeclaration()
      )
    }
    val semanticType: Type = packageName =>: game(game.imports)
  }

  /**
    * Specialized methods to help out in processing constraints. Specifically,
    * these are meant to be generic, things like getTableua, getReserve()
    */
  @combinator object HelperMethodsFreeCell {
    def apply(): Seq[BodyDeclaration[_]] = {
      generateHelper.helpers(solitaire) ++
        Java(
          s"""
             |/** A Foundation stack is full at 13 cards. */
             |public static boolean isFull(Stack src) {
             |  return (src.count() == 13);
             |}
             |
             |public static boolean sufficientFree (Column column, Stack src, Stack destination, Stack[] reserve, Stack[] tableau) {
             |	int numEmpty = 0;
             |	for (Stack s : tableau) {
             |		if (s.empty() && s != destination) numEmpty++;
             |	}
             |
             | 	// now count columns
             |	for (Stack r : reserve) {
             |		if (r.empty() && r != destination) numEmpty++;
             |	}
             |
           |	return column.count() <= 1 + numEmpty;
             |}""".stripMargin).classBodyDeclarations()
    }

    val semanticType: Type = constraints(constraints.methods)
  }

  @combinator object ExtraMethods {
    def apply(): Seq[MethodDeclaration] = {

//      val reserve = solitaire.containers.get(SolitaireContainerTypes.Reserve).size()
//      val tableau = solitaire.containers.get(SolitaireContainerTypes.Tableau).size()
//      val numFreePiles: IntegerLiteralExpr = Java(s"$reserve").expression()
//      val numColumns: IntegerLiteralExpr = Java(s"$tableau").expression()
//      val methods = java.ExtraMethods.render(numFreePiles, numColumns).classBodyDeclarations().map(_.asInstanceOf[MethodDeclaration])

      val methods = Java(
        s"""
           |/**
           | * A card is unneeded when no lower-rank cards of the opposite color remain
           | * in the playing area.
           | * <p>
           | * Returns TRUE if cards of (rank-1) and opposite colored suit have both
           | * already been played to the foundation.
           | * <p>
           | * Note that true is returned if an ACE is passed in.
           | */
           |protected boolean unneeded(int rank, int suit) {
           |	// error situation.
           |	if (rank == Card.ACE) return true;
           |
           |	// see if cards of next lower rank and opposite color are both played
           |	// in the foundation.
           |	int countOppositeColorLowerRank = 0;
           |	for (int b = 0; b < foundation.length; b++) {
           |		if (foundation[b].empty()) continue;
           |
           |		Card bc = foundation[b].peek();
           |		if (bc.oppositeColor (suit) && bc.getRank() >= rank-1) {
           |			countOppositeColorLowerRank++;
           |		}
           |	}
           |
           |	// determine validity
           |	return (countOppositeColorLowerRank == 2);
           |}
           |
           |// should be encapsulated out elswhere since this is standard logic...
           |public void tryAutoMoves() {
           |	Move m;
           |	do {
           |		m = autoMoveAvailable();
           |		if (m!= null) {
           |			if (m.doMove (this)) {
           |				pushMove (m);
           |				refreshWidgets();
           |			} else {
           |				// ERROR. Break now!
           |				break;
           |			}
           |		}
           |	} while (m != null);
           |}
           |
           |
           |/** For now, no automoves just yet... */
           |public Move autoMoveAvailable() {
           |	// opportunity for L2 inspection of elements to generate move codes...
           |	// NOTE: Here we embed 'field' because this is used as a parameter in the bindings.
           |	// 1. First see if any columnBaseMove allowed.
           |	for (int c = 0; c < tableau.length; c++) {
           |		if (tableau[c].empty()) continue;
           |
           |		if (tableau[c].rank() == Card.ACE) {
           |
           |			// find the empty destination pile
           |			Pile emptyDest = null;
           |			for (int i = 0; i < foundation.length; i++) {
           |				if (foundation[i].empty()) {
           |					emptyDest = foundation[i];
           |				}
           |			}
           |
           |			// SANITY CHECK.
           |			if (emptyDest == null) {
           |				throw new IllegalStateException ("ACE is available to play but no open destination piles.");
           |			}
           |
           |			return new PotentialBuildColumn (tableau[c], emptyDest);
           |		}
           |
           |		Card cc = tableau[c].peek();
           |
           |		// try to find a destination it goes to.
           |		Move theMove = null;
           |		boolean foundMove = false;
           |		for (int b = 0; b<foundation.length; b++) {
           |			theMove = new PotentialBuildColumn (tableau[c], foundation[b]);
           |			if (theMove.valid (this)) {
           |				foundMove = true;
           |				break;
           |			}
           |		}
           |
           |		// see if cards of next lower rank and opposite color are both played
           |		// in the foundation; we have to do this *two* levels since we need
           |		// to know that all four Suits are taken care of. Consider the decision
           |		// to place a 4H into a base pile; we need to know that both 3C and 3S
           |		// have been placed. We also need to know that the 2D has been played
           |		// (note: for a valid move we know that the 2H has been played).
           |		if (foundMove) {
           |			if (unneeded (cc.getRank(), cc.getSuit())) {
           |				int otherSuit = cc.getSuit();
           |				if ((otherSuit == Card.CLUBS) || (otherSuit == Card.SPADES)) {
           |					otherSuit = Card.HEARTS;  // arbitrary RED
           |				} else {
           |					otherSuit = Card.CLUBS; // arbitrary BLACK
           |				}
           |
           |				// now go down one more level
           |				if (unneeded (cc.getRank()-1, otherSuit)) {
           |					return theMove;
           |				}
           |			}
           |		}
           |	}
           |
           |	// 2. Second see if any FreeCellBaseMove allowed.
           |	Move theMove = null;
           |	boolean foundMove = false;
           |	Card bc = null;
           |	for (int f = 0; f < reserve.length; f++) {
           |		if (reserve[f].empty()) continue;
           |
           |		// try to find a destination it goes to.
           |		for (int b = 0; b<foundation.length; b++) {
           |			theMove = new PotentialBuildFreePileCard (reserve[f], foundation[b]);
           |			if (theMove.valid (this)) {
           |				bc = reserve[f].peek();
           |				foundMove = true;
           |				break;
           |			}
           |		}
           |
           |		if (foundMove) {
           |			if (unneeded(bc.getRank(), bc.getSuit())) {
           |				int otherSuit = bc.getSuit();
           |				if ((otherSuit == Card.CLUBS)
           |						|| (otherSuit == Card.SPADES)) {
           |					otherSuit = Card.HEARTS; // arbitrary RED
           |				} else {
           |					otherSuit = Card.CLUBS; // arbitrary BLACK
           |				}
           |
           |				// ACEs can be moved immediately...
           |				if (bc.getRank() == Card.ACE) {
           |					return theMove;
           |				}
           |
           |				// now go down one more level
           |				if (unneeded(bc.getRank() - 1, otherSuit)) {
           |					return theMove;
           |				}
           |			}
           |
           |			// no move allowed.
           |			return null;
           |		}
           |	}
           |
           |	// if nothing found, stop
           |	if (!foundMove) {
           |		theMove = null;
           |	}
           |
           |	return theMove;
           |}
         """.stripMargin).methodDeclarations()

      val solvableMoves = Java(
        s"""
           |public boolean validColumn(Column column) {
           |		return column.alternatingColors() && column.descending();
           |}
           |
           |public java.util.Enumeration<Move> availableMoves() {
           |			java.util.Vector<Move> v = new java.util.Vector<Move>();
           |
           |        // try to build card to foundation
           |        for (Column c : tableau) {
           |            for (Pile p : foundation) {
           |                PotentialBuildColumn pbc = new PotentialBuildColumn(c, p);
           |                if (pbc.valid(this)) {
           |                    v.add(pbc);
           |                }
           |            }
           |        }
           |        // try to move cards from free cell to foundation
           |        for (Pile s : reserve) {
           |            for (Pile d : foundation) {
           |                PotentialBuildFreePileCard pbfpc = new PotentialBuildFreePileCard(s, d);
           |                if (pbfpc.valid(this)) {
           |                    v.add(pbfpc);
           |                }
           |            }
           |        }
           |        // try to move any column of any size (from greatest to smallest), but
           |        // to avoid infinite cycles, only move if remaining column is smaller
           |        // than the destination.
           |        for (Column s : tableau) {
           |            for (Column d : tableau) {
           |                if (s != d) {
           |                    for (int i = s.count(); i > 0; i--) {
           |                        PotentialMoveColumn pmc = new PotentialMoveColumn(s, d, i);
           |                        if (pmc.valid(this)) {
           |                            if (s.count() - i < d.count()) {
           |                                v.add(pmc);
           |                            }
           |                        }
           |                    }
           |                }
           |            }
           |        }
           |        // move smallest facing up column card to a free pile
           |        Column lowest = null;
           |        for (Column s : tableau) {
           |            if (s.count() > 0) {
           |                if (lowest == null) {
           |                    lowest = s;
           |                } else if (s.rank() < lowest.rank()) {
           |                    lowest = s;
           |                }
           |            }
           |        }
           |        if (lowest != null) {
           |	        for (Pile p : reserve) {
           |	            if (p.count() == 0) {
           |	                PotentialPlaceColumn ppc = new PotentialPlaceColumn(lowest, p);
           |	                v.add(ppc);
           |	                break;
           |	            }
           |	        }
           |        }
           |
           |        return v.elements();
           |}""".stripMargin).methodDeclarations()


      methods ++ solvableMoves

    }
    val semanticType: Type = game(game.methods :&: game.availableMoves)
  }



//   @combinator object MakeHomePile extends ExtendModel("Pile", "HomePile", 'HomePileClass)
//   @combinator object MakeFreePile extends ExtendModel("Pile", "FreePile", 'FreePileClass)
//   @combinator object MakeHomePileView extends ExtendView("View", "HomePileView", "HomePile", 'HomePileViewClass)
//   @combinator object MakeFreePileView extends ExtendView("View", "FreePileView", "FreePile", 'FreePileViewClass)

}
