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

trait Game extends GameTemplate with Score52 {

  //lazy val alpha = Variable("alpha")

  // extend existing kinding
  lazy val newKinding = Kinding(tableauType)
                        .addOption('FourColumnTableau)
                        .addOption('EightColumnTableau)

  // Free cell is an example solitaire game that uses Foundation, Reserve, and Tableau.
  @combinator object FreeCellConstruction {
    def apply(s:Solitaire, f:Foundation, r:Reserve, t:Tableau): Solitaire = {
       s.setFoundation(f)
       s.setReserve(r)
       s.setTableau(t)
       
       Solitaire.setInstance(s)
       s
    }
    
    val semanticType:Type =  
      'Solitaire('Tableau('None)) :&: 'Solitaire('Foundation('None)) :&: 'Solitaire('Reserve('None)) =>:
      'Foundation('Valid :&: 'HomePile) =>:
      'Reserve('Valid :&: 'FreePile) =>:
      'Tableau('Valid :&: 'Column) =>: 
      'FreeCellVariation
  }                      
                        
  // 4-HomePile Foundation
  @combinator object FourHomePileFoundation {
    def apply(): Foundation = {
       val f = new Foundation()

       f.add (new HomePile())    // put into for-loop soon.
       f.add (new HomePile())
       f.add (new HomePile())
       f.add (new HomePile())

       println("setting four HomePile Foundation")

       f
    }
    
    val semanticType:Type = 'Foundation('Valid :&: 'Four :&: 'HomePile)
  }
  
  // in FreeCell we need a valid foundation.
  @combinator object AddFourPileFoundation {
    def apply(s:Solitaire, f:Foundation): Solitaire = {
      s.setFoundation(f)
      println("setting four-pile foundation.")
      s
    }

    val semanticType: Type =
      'Solitaire('Foundation('None)) =>: 'Foundation('Valid :&: 'HomePile) =>: 
          'Solitaire('Foundation('Valid :&: 'HomePile))
  }
  
  // 4-HomePile Foundation
  @combinator object FourHomePileReserve {
    def apply(): Reserve = {
       val r = new Reserve()

       r.add (new FreePile())    // put into for-loop soon.
       r.add (new FreePile())
       r.add (new FreePile())
       r.add (new FreePile())

       println("setting four FreePile Reserve")

       r
    }
    
    val semanticType:Type = 'Reserve('Valid :&: 'Four :&: 'FreePile)
  }
  
  // in FreeCell we need a valid reserve
  @combinator object AddFourPileReserve {
    def apply(s:Solitaire, r:Reserve): Solitaire = {
      s.setReserve(r)
      println("setting four-pile reserve.")
      s
    }

    val semanticType: Type =
      'Solitaire('Reserve('None)) =>: 'Reserve('Valid :&: 'FreePile) =>: 
          'Solitaire('Reserve('Valid :&: 'FreePile))
  }
 
  // in FreeCell we need a valid tableau. Not sure why we have to
  // restrict that here to be 8; could still be searched
  @combinator object AddEightColumnTableau {
    def apply(s:Solitaire, tab:Tableau): Solitaire = {
      s.setTableau(tab)
      println("setting eight-column tableau")
      s
    }

    val semanticType: Type =
      'Solitaire('Tableau('None)) =>: 'Tableau('Valid :&: 'Column) =>:
          'Solitaire('Tableau('Valid :&: 'Column))
  }

}
