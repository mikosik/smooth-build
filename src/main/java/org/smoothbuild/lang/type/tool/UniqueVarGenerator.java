package org.smoothbuild.lang.type.tool;

import org.smoothbuild.lang.type.VarS;

public class UniqueVarGenerator {
  private int counter = 0;
  private final String prefix;

  public UniqueVarGenerator(String prefix) {
    this.prefix = prefix;
  }

  public VarS generate() {
    return new VarS(Integer.toString(counter++)).prefixed(prefix);
  }
}
