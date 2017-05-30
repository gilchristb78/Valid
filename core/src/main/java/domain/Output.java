package domain;
import java.io.*;

public class Output {

  public static PrintWriter create(String name) {

    try {
	    return new PrintWriter (name);
    } catch (Exception e) {
	e.printStackTrace();
	return null;
    }
  }
}

