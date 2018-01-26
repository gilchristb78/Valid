package cls;

import domain.Container;
import domain.Element;
import domain.Move;
import domain.Solitaire;

import java.util.*;

/**
 * Produces the targets for a given domain.
 */
public class TargetFile {

    public static final String primaryTarget = "Gamma.InhabitationBatchJob[CompilationUnit](game(complete))";

    static List<String> targets = new ArrayList<>();

    static void addTarget(String s) {
        targets.add("[CompilationUnit]" + s);
    }

    static String generateString() {
        String response = primaryTarget + "\n";

        for (String tgt : targets) {
            response += "    .addJob" + tgt + "\n";
        }

        return response;
    }

    // awkward. Must map to SemanticTypes; annoying lower case, which could be fixed by just using the same
    public static String map(String element) {
        if (element.equals("BuildablePile")) { return "buildablePile"; }
        if (element.equals("Card")) { return "card"; }
        if (element.equals("Column")) { return "column"; }
        if (element.equals("Deck")) { return "deck"; }
        if (element.equals("Pile")) { return "pile"; }

        return "'" + element;
    }

    static void processMoves(Iterator<Move> it, boolean doPotentials) {
        while (it.hasNext()) {
            Move m = it.next();

            addTarget("(move('" + m.getName() + " :&: move.generic, complete))");

            if (doPotentials) {
                if (m.isSingleCardMove()) {
                    addTarget("(move('" + m.getName() + " :&: move.potential, complete))");
                } else {
                    addTarget("(move('" + m.getName() + " :&: move.potentialMultipleMove, complete))");
                }
            }
        }
    }

    public static String process(Solitaire domain) {
        addTarget("(constraints(complete))");

        // one for each controllers (from containers)
        for (Container c: domain.structure.values()) {
            if (domain.isVisible(c)) {
                for (Iterator<String> it = c.types(); it.hasNext(); ) {
                    String element = map(it.next());
                    addTarget("(controller(" + element + ", complete))");
                }
            }
        }

        // special classes
        for (Iterator<Element> it = domain.domainElements(); it.hasNext(); ) {
            Element e = it.next();
            addTarget("('" + e.getClass().getSimpleName() + "Class)");
            addTarget("('" + e.getClass().getSimpleName() + "ViewClass)");
        }

        // all move classes
        processMoves(domain.getRules().presses(), false);
        processMoves(domain.getRules().clicks(), false);
        processMoves(domain.getRules().drags(), true);

        return generateString();
    }

    public static void main (String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter name of solitaire domain class:");
        String s = sc.nextLine();
        try {
            Solitaire d = (Solitaire) Class.forName(s).newInstance();

            System.out.println (process(d));
        } catch (Exception e) {
            System.out.println ("Unable to procceed with:" + s);
            e.printStackTrace();
        }
        sc.close();
    }
}
