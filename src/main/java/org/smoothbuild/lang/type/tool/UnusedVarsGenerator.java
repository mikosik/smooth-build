package org.smoothbuild.lang.type.tool;

import org.smoothbuild.lang.type.VarS;
import org.smoothbuild.lang.type.VarSetS;

public class UnusedVarsGenerator {
  private final AlphabeticalVarsGenerator iterator = new AlphabeticalVarsGenerator();
  private final VarSetS usedVars;

  public UnusedVarsGenerator(VarSetS usedVars) {
    this.usedVars = usedVars;
  }

  public VarS next() {
    VarS var;
    do {
      var = iterator.next();
    } while (usedVars.contains(var));
    return var;
  }
}
