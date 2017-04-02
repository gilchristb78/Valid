package org.combinators.solitaire.freecell

import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.{FieldDeclaration, MethodDeclaration, BodyDeclaration}
import com.github.javaparser.ast.expr.{Expression, NameExpr}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.solitaire.shared.GameTemplate
import org.combinators.solitaire.shared.Score52

// domain
import domain._
import domain.freeCell.HomePile
import domain.freeCell.FreePile

// this now becomes available
class GameDomain(val solitaire:Solitaire) extends GameTemplate with Score52 {


	// Be sure to avoid 'var' within the exterior of a combinator.
	@combinator object NumHomePiles {
		def apply: Expression = {
			val f = Solitaire.getInstance().getFoundation()
					Java("" + f.size()).expression()
	}
	val semanticType: Type = 'NumHomePiles
	}

	@combinator object NumFreePiles {
		def apply: Expression = {
			val r = Solitaire.getInstance().getReserve()
					Java("" + r.size()).expression()
	}
	val semanticType: Type = 'NumFreePiles
	}

	@combinator object NumColumns {
		def apply: Expression = {
			val t = Solitaire.getInstance().getTableau()
					Java("" + t.size()).expression()
	}

	val semanticType: Type = 'NumColumns
	}

	// Mapping into Java is a concern for this scala trait.

	@combinator object RootPackage {
		def apply: NameExpr = {
			Java("org.combinators.solitaire.freecell").nameExpression
	}
	val semanticType: Type = 'RootPackage
	}

	@combinator object NameOfTheGame {
		def apply: NameExpr = {
			Java("FreeCell").nameExpression
	}
	val semanticType: Type = 'NameOfTheGame
	}

	// @(NameOfTheGame: String, NumColumns:Expression, NumHomePiles: Expression, NumFreePiles: Expression)


	//  @combinator object Initialization {
	//    def apply(nameOfTheGame: NameExpr, numColumns:Expression, numHomePiles:Expression, numFreePiles: Expression): Seq[Statement] = {
	//      
	//      java.Initialization.render(nameOfTheGame.toString(), numColumns, numHomePiles, numFreePiles).statements()
	//    }
	//    val semanticType: Type = 'NameOfTheGame =>: 'NumColumns =>: 'NumHomePiles =>: 'NumFreePiles =>: 'Initialization :&: 'NonEmptySeq
	//  }

	// FreeCell model derived from the domain model
	@combinator object FreeCellInitModel {

		// note: we could avoid passing in these parameters and just solely
		// visit the domain model. That is an alternative worth considering.

		def apply(NumColumns:Expression, NumHomePiles:Expression, NumFreePiles:Expression): Seq[Statement] = {

				Java(s"""/* construct model elements */
						for (int i = 0; i < $NumColumns; i++) {
						fieldColumns[i] = new Column(ColumnsPrefix + (i+1));
						addModelElement (fieldColumns[i]);		
						fieldColumnViews[i] = new ColumnView(fieldColumns[i]);
						}

						for (int i = 0; i < $NumFreePiles; i++) {
						fieldFreePiles[i] = new Pile (FreePilesPrefix + (i+1));
						addModelElement (fieldFreePiles[i]);
						fieldFreePileViews[i] = new PileView (fieldFreePiles[i]);
						}

						for (int i = 0; i < $NumHomePiles; i++) {
						fieldHomePiles[i] = new Pile (HomePilesPrefix + (i+1));
						addModelElement (fieldHomePiles[i]); 
						fieldHomePileViews[i] = new PileView(fieldHomePiles[i]);
						}
						""").statements
		}

		val semanticType: Type = 'NumColumns =>: 'NumHomePiles =>: 'NumFreePiles =>:
			'Init('Model)
	}

	@combinator object FreeCellInitView {
		def apply(NumColumns:Expression, NumHomePiles:Expression, NumFreePiles:Expression): Seq[Statement] = {

				val found = Solitaire.getInstance().getFoundation()
				val tableau = Solitaire.getInstance().getTableau()
				val free = Solitaire.getInstance().getReserve()
				val lay = Solitaire.getInstance().getLayout()
				val rectFound = lay.get(Layout.Foundation)
				val rectFree = lay.get(Layout.Reserve)
				val rectTableau = lay.get(Layout.Tableau)

				// (a) do the computations natively in scala to generate java code
				// (b) delegation to Layout class, but then needs to pull back into
				//     scala anyway
				//
				// card is 73 x 97

				// HACK! How to create empty sequence to start loop with?
				var stmts = Java("System.out.println(\"Place Foundation and FreeCell Views\");").statements()

				// This could be a whole lot simpler! This places cards within Foundation rectangle
				// with card width of 97 cards each. Gap is fixed and determined by this function
				// Missing: Something that *maps* the domain model to 'fieldHomePileViews' and construction
				val it = lay.placements(Layout.Foundation, found, 97)
				var idx = 0
				while (it.hasNext()) {
				  val r = it.next()
				  
          val s = Java(s"""

									fieldHomePileViews[$idx].setBounds(${r.x}, ${r.y}, cw, ch);
									addViewWidget(fieldHomePileViews[$idx]);
									
									""").statements()

					idx = idx + 1
					stmts = Java(stmts.mkString("\n") + "\n" + s.mkString("\n")).statements()
				}
				
				// would be useful to have Scala utility for appending statements to single body.
				idx = 0
				while (idx < found.size()) {
					val xfree = rectFree.x + 15*idx + idx*73
					val s = Java(s"""

							fieldFreePileViews[$idx].setBounds($xfree, 20, cw, ch);
							addViewWidget(fieldFreePileViews[$idx]);

							""").statements()

					idx = idx + 1
					stmts = Java(stmts.mkString("\n") + "\n" + s.mkString("\n")).statements()
				}

				// now column placement
				idx = 0
				while (idx < tableau.size()) {
					val xtabl = rectTableau.x + 15*idx + idx*73
					val s = Java(s"""

							fieldColumnViews[$idx].setBounds($xtabl, 40 + ch, cw, 13*ch);
							addViewWidget(fieldColumnViews[$idx]);

							""").statements()

					idx = idx + 1
					stmts = Java(stmts.mkString("\n") + "\n" + s.mkString("\n")).statements()
				}
				
				stmts
		}
		
		val semanticType: Type = 'NumColumns =>: 'NumHomePiles =>: 'NumFreePiles =>: 'Init('View)
	}

	@combinator object FreeCellInitControl {
		def apply(NumColumns:Expression, NumHomePiles:Expression, NumFreePiles:Expression, NameOfGame:NameExpr): Seq[Statement] = {

				val nc = NumColumns.toString()
						val np = NumHomePiles.toString()
						val nf = NumFreePiles.toString()
						val name = NameOfGame.toString()

						Java(s"""
								// setup controllers
								for (int i = 0; i < $nc; i++) {
  								fieldColumnViews[i].setMouseMotionAdapter (new SolitaireMouseMotionAdapter (this));
  								fieldColumnViews[i].setUndoAdapter (new SolitaireUndoAdapter (this));
    								fieldColumnViews[i].setMouseAdapter (new ${name}ColumnController (this, fieldColumnViews[i]));
    						}
    
    						for (int i = 0; i < $np; i++) {
      						fieldHomePileViews[i].setMouseMotionAdapter (new SolitaireMouseMotionAdapter (this));
      						fieldHomePileViews[i].setUndoAdapter (new SolitaireUndoAdapter (this));
      						fieldHomePileViews[i].setMouseAdapter (new HomePileController (this, fieldHomePileViews[i]));
    						}
    
    						for (int i = 0; i < $nf; i++) {
      						fieldFreePileViews[i].setMouseMotionAdapter (new SolitaireMouseMotionAdapter (this));
      						fieldFreePileViews[i].setUndoAdapter (new SolitaireUndoAdapter (this));
      						fieldFreePileViews[i].setMouseAdapter (new FreeCellPileController (this, fieldFreePileViews[i]));
    						}

								""").statements()

		}

		val semanticType: Type = 'NumColumns =>: 'NumHomePiles =>: 'NumFreePiles =>: 'NameOfTheGame =>: 'Init('Control)
	}

	// generic deal cards from deck into the tableau
	@combinator object FreeCellInitLayout {
		def apply(NumColumns:Expression): Seq[Statement] = {
      val tableau = Solitaire.getInstance().getTableau()

      // HACK! How to create empty sequence to start loop with?
			var stmts = Java("System.out.println(\"Complete initial deal.\");").statements()

			// standard logic to deal to all tableau cards
			var numColumns = tableau.size() 
			val s = Java(s"""
					int col = 0;
					while (!deck.empty()) {
  					fieldColumns[col++].add(deck.get());
  					if (col >= $numColumns) {
  					  col = 0;
  					}
					}
					""").statements()

					stmts = Java(stmts.mkString("\n") + "\n" + s.mkString("\n")).statements()

					stmts
		}

		val semanticType: Type = 'NumColumns =>: 'Init('Layout)
	}

	// create three separate blocks based on the domain model.
	@combinator object Initialization {
		def apply(minit:Seq[Statement], vinit:Seq[Statement], cinit:Seq[Statement], layout:Seq[Statement]): Seq[Statement] = {

				// @(ModelInit: Seq[Statement], ViewInit: Seq[Statement], ControlInit : Seq[Statement], SetupInitialState : Seq[Statement])
				java.DomainInit.render(minit, vinit, cinit, layout).statements
		}
		val semanticType: Type = 'Init('Model) =>: 'Init('View) =>: 'Init('Control) =>: 'Init('Layout) =>: 'Initialization :&: 'NonEmptySeq
	}

	// vagaries of java imports means these must be defined as well.
	@combinator object ExtraImports {
		def apply(nameExpr:NameExpr): Seq[ImportDeclaration] = {
				Seq(Java("import " + nameExpr.toString() + ".controller.*;").importDeclaration(),
						Java("import " + nameExpr.toString() + ".model.*;").importDeclaration()
						)
		}
		val semanticType: Type = 'RootPackage =>: 'ExtraImports
	}

	@combinator object ExtraMethods {
		def apply(numFreePiles:Expression, numColumns: Expression): Seq[MethodDeclaration] = {

				java.ExtraMethods.render(numFreePiles, numColumns).classBodyDeclarations().map(_.asInstanceOf[MethodDeclaration])
		}
		val semanticType: Type = 'NumFreePiles =>: 'NumColumns =>: 'ExtraMethods :&: 'Column('FreeCellColumn, 'AutoMovesAvailable) 
	}


	@combinator object EmptyExtraMethods {
		def apply(): Seq[MethodDeclaration] = Seq.empty
				val semanticType: Type = 'ExtraMethodsBad
	}


	// @(NumHomePiles: Expression, NumFreePiles: Expression, NumColumns:Expression)
	@combinator object ExtraFields {
		def apply(numHomePiles:Expression, numFreePiles:Expression, numColumns: Expression): Seq[FieldDeclaration] = {
				java.ExtraFields
				.render(numHomePiles, numFreePiles, numColumns)
				.classBodyDeclarations()
				.map(_.asInstanceOf[FieldDeclaration])
		}
		val semanticType: Type = 'NumHomePiles =>: 'NumFreePiles =>: 'NumColumns=>: 'ExtraFields
	}
}
