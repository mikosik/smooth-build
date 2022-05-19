package org.smoothbuild.lang.type.solver;

import org.smoothbuild.lang.type.ConstrS;

public class ConstrDecomposeExc extends Exception {
  public ConstrDecomposeExc(ConstrS constr) {
    super("Unsolvable constraint: " + constr);
  }
}
