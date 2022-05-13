package org.smoothbuild.lang.type;

public class ConstrDecomposeExc extends Exception {
  public ConstrDecomposeExc(ConstrS constr) {
    super("Unsolvable constraint: " + constr);
  }
}
