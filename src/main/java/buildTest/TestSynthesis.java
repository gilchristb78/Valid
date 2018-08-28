package buildTest;

import java.util.*;
import java.net.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * With new dynamic routes, we lose the information in the file.
 *
 * Register here individually.
 */
public class TestSynthesis {

    /** Any target that ends with "Controller" is a sub-variation. */
    public static final String CONTROLLER = "Controller";

    public static final String resources = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "routes";

    /** All synthesized files are stored in demo/solitaire folder. */
    public static final String destination = "demo" + File.separator + "solitaire";

    /** KombatSolitaire stand alone JAR file, needed for compilation and execution. */
    public static final String standAlone =  "demo" + File.separator + "standAlone.jar";

    /** Timing helpers. */
    static long start_timeStamp;
    static void startTime() {
        start_timeStamp = System.currentTimeMillis();
    }
    static float endTime() {
        float seconds = (1.0f*(System.currentTimeMillis() - start_timeStamp) / 1000);
        return seconds;
    }

    /**
     * Use git clone to retrieve the source files.
     *
     * @param url     variation name -- package.
     * @return true on success. false otherwise
     */
    static boolean gitRetrieve(String url) {
        File dir = new File (destination);
        if (!dir.exists() && !dir.mkdir()) {
            System.err.println ("  unable to make directory:" + destination);
            return false;
        }
        // git clone -n variation_0 http://localhost:9000/freecell/doublefreecell/doublefreecell.git
        String command = "git clone -b variation_0 " + url;
        try {
            Process proc = Runtime.getRuntime().exec(command, new String[0], dir);
            System.out.println ("  Errors (if any):"); System.out.flush();
            Stream<String> err = new BufferedReader(new InputStreamReader(proc.getErrorStream())).lines();
            err.forEach(System.err::println); System.err.flush();
            System.out.println ("  Output (if any):"); System.out.flush();
            Stream<String> out = new BufferedReader(new InputStreamReader(proc.getInputStream())).lines();
            out.forEach(System.out::println);
            System.out.println ("  ----"); System.out.flush();
            proc.waitFor();
            return true;
        } catch (Exception e) {
            System.err.println ("  Unable to exec:" + command);
            return false;
        }
    }

    /**
     * Compile the classes as found in the given pkg by compiling the main class.
     *
     * @param mainClass   Main class name
     *
     * @return true on success; false otherwise
     */
    static boolean compile(String mainClass, String originalClass) {
        File here = new File (".");
        String jarFile = here.getAbsoluteFile() + File.separator + standAlone;

        File dir = new File (destination, mainClass);
        if (!dir.exists()) {
            System.err.println ("  unable to locate destination directory:" + destination + File.separator + mainClass);
            return false;
        }
        dir = new File (dir, "src");
        dir = new File (dir, "main");
        dir = new File (dir, "java");

        // javac -cp standAlone.jar:./bigforty/src/main/java klondike/src/main/java/org/combinators/solitaire/bigforty/BigForty.java
        //
        //run-bigforty: bigforty
        //        java -cp standAlone.jar:./bigforty/src/main/java org/combinators/solitaire/bigforty/BigForty
        String fs = File.separator;
        String[] args = new String[] { "javac",  "-cp",
                jarFile + File.pathSeparator + ".",
                //"-Xlint:unchecked",
                "org" + fs + "combinators" + fs + "solitaire" + fs +  mainClass + fs + originalClass + ".java"};

        try {
            Process proc = Runtime.getRuntime().exec(args, new String[0], dir);

            System.out.println ("  Errors (if any):"); System.out.flush();
            Stream<String> err = new BufferedReader(new InputStreamReader(proc.getErrorStream())).lines();
            err.forEach(System.err::println); System.err.flush();
            System.out.println ("  Output (if any):"); System.out.flush();
            Stream<String> out = new BufferedReader(new InputStreamReader(proc.getInputStream())).lines();
            out.forEach(System.out::println);
            System.out.println ("  ----"); System.out.flush();
            proc.waitFor();
            return true;
        } catch (Exception e) {
            System.err.println ("  Unable to exec:" + Arrays.toString(args));
            return false;
        }
    }

    /**
     * The git files are only produced after invoking 'prepare'.
     *
     * @param urls        base URL for variation
     * @param variation   variation name -- package.
     */
    static void prepare (String urls, String variation) {
        try {
            System.out.print ("  Attempting to prepare " + variation + " [");
            startTime();
            URL url = new URL(urls + "/prepare?number=0");
            System.out.println ("    Attempting URL " + url);
            BufferedReader br = new BufferedReader (new InputStreamReader (url.openStream()));
            Stream<String> input =  br.lines();
            br.close();
            System.out.println (endTime() + " secs]");
        } catch (Exception e) {
            System.err.println ("  unable to prepare:" + variation + "(" + e.getMessage() + ")");
        }
    }

    /**
     * @param variation    variation name "package:mainClass"
     */
    static void synthesize (String variation) {
        try {
            System.out.print ("  Attempting to synthesize " + variation + " [");
            startTime();

            String urlString;
            String[] vars = variation.split(":");
            String originalClass;
            if (variation.endsWith(CONTROLLER)) {
                originalClass = vars[1].substring(0, vars[1].length()-CONTROLLER.length());
                urlString = "http://localhost:9000/" + vars[0] + "/" + originalClass.toLowerCase();
            } else {
                originalClass = vars[1];
                urlString = "http://localhost:9000/" + originalClass.toLowerCase();
            }

            System.out.println ("  Accessing url " + urlString );
           BufferedReader br = new BufferedReader (new InputStreamReader (new URL(urlString).openStream()));
            Stream<String> input =  br.lines();
            System.out.println (endTime() + " secs]");

            if (input.count() > 0) {
                prepare(urlString, variation);

                startTime();
                gitRetrieve(urlString + "/" + originalClass.toLowerCase() + ".git");
                System.out.println ("  Computed and retrieved git files [" + endTime() + "]");

                startTime();
                compile(originalClass.toLowerCase(), originalClass);
                System.out.println ("  Compiled files [" + endTime() + "]");
            }
        } catch (Exception e) {
            System.err.println ("  unable to synthesize:" + variation + "(" + e.getMessage() + ")");
        }
    }

    /**
     * Launch everything!
     *
     * All code is stored in nextgen-solitaire/demo/solitaire and can be deleted at any time
     * since the generated code is not part of the git repository.
     */
    public static void main (String args[]) throws Exception {

        File f = new File(resources);
        if (!f.exists()) {
            System.err.println ("  Cannot find routes file:" + resources);
            System.exit(-1);
        }

        System.out.println ("Extracting all solitaire variations to:" + destination);

        // Grab all Kombat-Solitaire based variations.
        ArrayList<String> variations = new ArrayList<>();
        Scanner sc = new Scanner(f);
        while (sc.hasNextLine()) {
            String s = sc.nextLine();

            // ->    /                              org.combinators.solitaire.castle.Castle
            // ->    /                              org.combinators.solitaire.freecell.FreeCellController

            Pattern regex = Pattern.compile("->\\s+/\\s+org\\.combinators\\.solitaire\\.(\\w+)\\.(\\w+)");
            Matcher match = regex.matcher(s);

            if (match.find()) {
                String name = match.group(1);
                String mainClassName = match.group(2);
                System.out.println ("  found:" + mainClassName);
                variations.add(name + ":" + mainClassName);
            }
        }

        Collections.shuffle(variations);

        // Perform each one in random order, so we can run multiple trials
        for (String var : variations) {
            System.out.println ("Variation:" + var);
            synthesize(var);
        }
    }
}
