package buildTest;

import java.util.*;
import java.net.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Provides a rudimentary way to automatically synthesize all known solitaire variations (on a PC), compile the code, and
 * ultimately validate that the synthesized Java code compils cleanly.
 *
 * Note: this will be more challenging to do with generated Python code.
 */
public class TestSynthesis {

    public static final String resources = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "routes";

    // new directory each time, in demo directory
    public static final String destination = "demo" + File.separator + "solitaire";

    public static final String standAlone =  "demo" + File.separator + "standAlone.jar";

    static long start_timeStamp;
    static void startTime() {
        start_timeStamp = System.currentTimeMillis();
    }
    static float endTime() {
        float seconds = (1.0f*(System.currentTimeMillis() - start_timeStamp) / 1000);
        return seconds;
    }
    static boolean gitRetrieve(String variation) {
        File dir = new File (destination);
        if (!dir.exists() && !dir.mkdir()) {
            System.err.println ("  unable to make directory:" + destination);
            return false;
        }

        String command = "git clone -b variation_0 http://localhost:9000/" + variation + "/" + variation + ".git";
        try {
            Process proc = Runtime.getRuntime().exec(command, new String[0], dir);
            proc.waitFor();
            return true;
        } catch (Exception e) {
            System.err.println ("  Unable to exec:" + command);
            return false;
        }
    }

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

    static void synthesize (String pkgPlusVar) {
        try {
            int idx = pkgPlusVar.indexOf('.');
            String variation = pkgPlusVar.substring(0, idx);
            String mainClass = pkgPlusVar.substring(idx+1);
            System.out.print ("  Attempting to synthesize " + variation + " [");
            startTime();
            URL url = new URL("http://localhost:9000/" + variation + "/prepare?number=0");
            BufferedReader br = new BufferedReader (new InputStreamReader (url.openStream()));
            Stream<String> input =  br.lines();
            System.out.println (endTime() + " secs]");

            if (input.count() > 0) {
                startTime();
                gitRetrieve(variation);
                System.out.println ("  Computed and retrieved git files [" + endTime() + "]");

                startTime();
                compile(variation, mainClass);
                System.out.println ("  Compiled files [" + endTime() + "]");
            }
        } catch (Exception e) {
            System.err.println ("  unable to synthesize:" + pkgPlusVar + "(" + e.getMessage() + ")");
        }
    }

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

            // GET   /freecell/prepare              org.combinators.solitaire.freecell.FreeCell.prepare(number: Long)

            Pattern regex = Pattern.compile("GET\\s+/(\\w+)/prepare\\s+org\\.combinators\\.solitaire\\.(\\w+)\\.(\\w+)\\.prepare");
            Matcher match = regex.matcher(s);
            if (match.find()) {
                String name = match.group(1);
                String mainClassName = match.group(3);
                System.out.println ("  found:" + name);
                variations.add(name + "." + mainClassName);
            }
        }

        for (String var : variations) {
            System.out.println ("Variation:" + var);
            synthesize(var);
        }
    }
}
