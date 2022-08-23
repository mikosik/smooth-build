package org.smoothbuild.lang.type;

public class UnusedVarsGenerator {
  private final AlphabeticalVarsIterator iterator = new AlphabeticalVarsIterator();
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
