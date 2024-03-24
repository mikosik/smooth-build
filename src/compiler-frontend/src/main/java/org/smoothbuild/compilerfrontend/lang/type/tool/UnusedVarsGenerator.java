package org.smoothbuild.compilerfrontend.lang.type.tool;

import org.smoothbuild.compilerfrontend.lang.type.SVar;
import org.smoothbuild.compilerfrontend.lang.type.SVarSet;

public class UnusedVarsGenerator {
  private final AlphabeticalVarsGenerator iterator = new AlphabeticalVarsGenerator();
  private final SVarSet reservedVars;

  public UnusedVarsGenerator(SVarSet reservedVars) {
    this.reservedVars = reservedVars;
  }

  public SVar next() {
    SVar var;
    do {
      var = iterator.next();
    } while (reservedVars.contains(var));
    return var;
  }
}
