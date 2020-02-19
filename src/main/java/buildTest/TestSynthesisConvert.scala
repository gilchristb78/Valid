package buildTest

import java.util._
import java.net._
import java.io._
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.stream.Stream

/**
  * With new dynamic routes, we lose the information in the file.
  *
  * Register here individually.
  */
object TestSynthesisConvert {
  /** Any target that ends with "Controller" is a sub-variation. */
    val CONTROLLER = "Controller"
  val resources = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "routes"
  /** All synthesized files are stored in demo/solitaire folder. */
  val destination = "demo" + File.separator + "solitaire"
  /** KombatSolitaire stand alone JAR file, needed for compilation and execution. */
  val standAlone = "demo" + File.separator + "standAlone.jar"
  /** Timing helpers. */
  private[buildTest] var start_timeStamp = 0L

  private[buildTest] def startTime() = start_timeStamp = System.currentTimeMillis

  private[buildTest] def endTime = {
    val seconds = 1.0f * (System.currentTimeMillis - start_timeStamp) / 1000
    seconds
  }

  /**
    * Use git clone to retrieve the source files.
    *
    * @param url variation name -- package.
    * @return true on success. false otherwise
    */
  private[buildTest] def gitRetrieve(url: String): Boolean = {
    val dir = new File(destination)
    if (!dir.exists && !dir.mkdir) {
      System.err.println("  unable to make directory:" + destination)
      return false
    }
    // git clone -n variation_0 http://localhost:9000/freecell/doublefreecell/doublefreecell.git
    val command = "git clone -b variation_0 " + url
    try {
      val proc = Runtime.getRuntime.exec(command, new Array[String](0), dir)
      System.out.println("  Errors (if any):")
      System.out.flush()
      val err = new BufferedReader(new InputStreamReader(proc.getErrorStream)).lines
      err.forEach(System.err.println)
      System.err.flush()
      System.out.println("  Output (if any):")
      System.out.flush()
      val out = new BufferedReader(new InputStreamReader(proc.getInputStream)).lines
      out.forEach(System.out.println)
      System.out.println("  ----")
      System.out.flush()
      proc.waitFor
      true
    } catch {
      case e: Exception =>
        System.err.println("  Unable to exec:" + command)
        false
    }
  }

  /**
    * Compile the classes as found in the given pkg by compiling the main class.
    *
    * @param mainClass Main class name
    * @return true on success; false otherwise
    */
  private[buildTest] def compile(mainClass: String, originalClass: String): Boolean = {
    val here = new File(".")
    val jarFile = here.getAbsoluteFile + File.separator + standAlone
    var dir = new File(destination, mainClass)
    if (!dir.exists) {
      System.err.println("  unable to locate destination directory:" + destination + File.separator + mainClass)
      return false
    }
    dir = new File(dir, "src")
    dir = new File(dir, "main")
    dir = new File(dir, "java")
    // javac -cp standAlone.jar:./bigforty/src/main/java klondike/src/main/java/org/combinators/solitaire/bigforty/BigForty.java
    //
    //run-bigforty: bigforty
    //        java -cp standAlone.jar:./bigforty/src/main/java org/combinators/solitaire/bigforty/BigForty
    val fs = File.separator
    val args = Array[String]("javac", "-cp", jarFile + File.pathSeparator + ".", //"-Xlint:unchecked",
      "org" + fs + "combinators" + fs + "solitaire" + fs + mainClass + fs + originalClass + ".java")
    try {
      val proc = Runtime.getRuntime.exec(args, new Array[String](0), dir)
      System.out.println("  Errors (if any):")
      System.out.flush()
      val err = new BufferedReader(new InputStreamReader(proc.getErrorStream)).lines
      err.forEach(System.err.println)
      System.err.flush()
      System.out.println("  Output (if any):")
      System.out.flush()
      val out = new BufferedReader(new InputStreamReader(proc.getInputStream)).lines
      out.forEach(System.out.println)
      System.out.println("  ----")
      System.out.flush()
      proc.waitFor
      true
    } catch {
      case e: Exception =>
        System.err.println("  Unable to exec:" + args.toList)
        false
    }
  }

  /**
    * The git files are only produced after invoking 'prepare'.
    *
    * @param urls      base URL for variation
    * @param variation variation name -- package.
    */
  private[buildTest] def prepare(urls: String, variation: String) = try {
    System.out.print("  Attempting to prepare " + variation + " [")
    startTime()
    val url = new URL(urls + "/prepare?number=0")
    System.out.println("    Attempting URL " + url)
    val br = new BufferedReader(new InputStreamReader(url.openStream))
    val input = br.lines
    br.close()
    System.out.println(endTime + " secs]")
  } catch {
    case e: Exception =>
      System.err.println("  unable to prepare:" + variation + "(" + e.getMessage + ")")
  }

  /**
    * @param variation variation name "package:mainClass"
    */
  private[buildTest] def synthesize(variation: String) = try {
    System.out.print("  Attempting to synthesize " + variation + " [")
    startTime()
    var urlString = ""
    val vars = variation.split(":")
    var originalClass = ""
    if (variation.endsWith(CONTROLLER)) {
      originalClass = vars(1).substring(0, vars(1).length - CONTROLLER.length)
      urlString = "http://localhost:9000/" + vars(0) + "/" + originalClass.toLowerCase
    }
    else {
      originalClass = vars(1)
      urlString = "http://localhost:9000/" + originalClass.toLowerCase
    }
    System.out.println("  Accessing url " + urlString)
    val br = new BufferedReader(new InputStreamReader(new URL(urlString).openStream))
    val input = br.lines
    System.out.println(endTime + " secs]")
    if (input.count > 0) {
      prepare(urlString, variation)
      startTime()
      gitRetrieve(urlString + "/" + originalClass.toLowerCase + ".git")
      System.out.println("  Computed and retrieved git files [" + endTime + "]")
      startTime()
      compile(originalClass.toLowerCase, originalClass)
      System.out.println("  Compiled files [" + endTime + "]")
    }
  } catch {
    case e: Exception =>
      System.err.println("  unable to synthesize:" + variation + "(" + e.getMessage + ")")
  }

  /**
    * Launch everything!
    *
    * All code is stored in nextgen-solitaire/demo/solitaire and can be deleted at any time
    * since the generated code is not part of the git repository.
    */
  @throws[Exception]
  def main(args: Array[String]) = {
    val f = new File(resources)
    if (!f.exists) {
      System.err.println("  Cannot find routes file:" + resources)
      System.exit(-1)
    }else{
      System.out.println("RESOURCES FOUND")
    }
    System.out.println("Extracting all solitaire variations to:" + destination)
    // Grab all Kombat-Solitaire based variations.
    val variations = new ArrayList[String]
    val sc = new Scanner(f)
    while ( {
      sc.hasNextLine
    }) {
      val s = sc.nextLine
      // ->    /                              org.combinators.solitaire.castle.Castle
      // ->    /                              org.combinators.solitaire.freecell.FreeCellController
      val regex = Pattern.compile("->\\s+/\\s+org\\.combinators\\.solitaire\\.(\\w+)\\.(\\w+)")
      val `match` = regex.matcher(s)
      if (`match`.find) {
        val name = `match`.group(1)
        val mainClassName = `match`.group(2)
        System.out.println("  found:" + mainClassName)
        variations.add(name + ":" + mainClassName)
      }
    }
    Collections.shuffle(variations)
    // Perform each one in random order, so we can run multiple trials
    import scala.collection.JavaConversions._
    for (vari <- variations) {
      System.out.println("Variation:" + vari)
      synthesize(vari)
    }
  }
}