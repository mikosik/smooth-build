package org.smoothbuild.compile.fs.lang.type.tool;

import org.smoothbuild.compile.fs.lang.type.VarS;
import org.smoothbuild.compile.fs.lang.type.VarSetS;

public class UnusedVarsGenerator {
  private final AlphabeticalVarsGenerator iterator = new AlphabeticalVarsGenerator();
  private final VarSetS reservedVars;

  public UnusedVarsGenerator(VarSetS reservedVars) {
    this.reservedVars = reservedVars;
  }

  public VarS next() {
    VarS var;
    do {
      var = iterator.next();
    } while (reservedVars.contains(var));
    return var;
  }
}
