package buildTest;

import java.util.*;
import java.net.*;
import java.io.*;
import java.util.stream.Stream;

/**
 * With new dynamic routes, we lose the information in the file.
 *
 * Register here individually.
 */
public class TestSynthesis {

    /**
     * Each entry has three fields
     *
     * [0] Base URL for invoking the synthesis algorithm. This is matched in 'routes' file
     * [1] The name of the variation (all lowercase)
     * [2] The name of the top-level Java class which is main Variation.
     */
    public static final String[][] targets = new String[][] {
            {"http://localhost:9000/archway",    "archway",     "Archway"},
            {"http://localhost:9000/bigforty",   "bigforty",    "BigForty"},
            {"http://localhost:9000/castle",     "castle",      "Castle"},
            {"http://localhost:9000/freecell",   "freecell",    "FreeCell"},
            {"http://localhost:9000/idiot",      "idiot",       "Idiot"},
            {"http://localhost:9000/narcotic",   "narcotic",    "Narcotic"},

            {"http://localhost:9000/klondike/dealbythree",    "dealbythree",     "DealByThree"},
            {"http://localhost:9000/klondike/eastcliff",      "eastcliff",       "EastCliff"},
            {"http://localhost:9000/klondike/klondike",       "klondike",        "Klondike"},
            {"http://localhost:9000/klondike/smallharp",      "smallharp",       "SmallHarp"},
            {"http://localhost:9000/klondike/thumbandpouch",  "thumbandpouch",   "ThumbAndPouch"},
            {"http://localhost:9000/klondike/whitehead",      "whitehead",       "WhiteHead"},
            {"http://localhost:9000/klondike/easthaven",      "easthaven",       "EastHaven"},
    };

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
     * @param url           original target URL
     * @param variation     variation name -- package.
     * @return true on success. false otherwise
     */
    static boolean gitRetrieve(String url, String variation) {
        File dir = new File (destination);
        if (!dir.exists() && !dir.mkdir()) {
            System.err.println ("  unable to make directory:" + destination);
            return false;
        }

        String command = "git clone -b variation_0 " + url + "/" + variation + ".git";
        try {
            Process proc = Runtime.getRuntime().exec(command, new String[0], dir);
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
     * @param pkg         variation name -- package.
     * @param mainClass   Main class name
     *
     * @return true on success; false otherwise
     */
    static boolean compile(String pkg, String mainClass) {
        File here = new File (".");
        String jarFile = here.getAbsoluteFile() + File.separator + standAlone;

        File dir = new File (destination, pkg);
        if (!dir.exists()) {
            System.err.println ("  unable to locate destination directory:" + destination + File.separator + pkg);
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
                "org" + fs + "combinators" + fs + "solitaire" + fs + pkg + fs + mainClass + ".java"};

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
            BufferedReader br = new BufferedReader (new InputStreamReader (url.openStream()));
            Stream<String> input =  br.lines();
            br.close();
            System.out.println (endTime() + " secs]");
        } catch (Exception e) {
            System.err.println ("  unable to prepare:" + variation + "(" + e.getMessage() + ")");
        }
    }

    /**
     * @param urls         variation URL
     * @param variation    variation name -- package.
     * @param mainClass    Main class name
     */
    static void synthesize (String urls, String variation, String mainClass) {
        try {
            System.out.print ("  Attempting to synthesize " + variation + " [");
            startTime();
            URL url = new URL(urls);
            BufferedReader br = new BufferedReader (new InputStreamReader (url.openStream()));
            Stream<String> input =  br.lines();
            System.out.println (endTime() + " secs]");

            if (input.count() > 0) {
                prepare(urls, variation);

                startTime();
                gitRetrieve(urls, variation);
                System.out.println ("  Computed and retrieved git files [" + endTime() + "]");

                startTime();
                compile(variation, mainClass);
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
    public static void main (String args[]) {
        List<String[]> list = Arrays.asList(targets);
        Collections.shuffle(list);

        // Perform each one in random order, so we can run multiple trials
        for (String[] var : list) {
            System.out.println ("Variation:" + var[1]);
            synthesize(var[0], var[1], var[2]);
        }
    }
}
