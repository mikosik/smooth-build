package org.smoothbuild.lang.type;

public class TempVarFS {
  private final TypeSF typeF;
  private int currentId;

  public TempVarFS(TypeSF typeF) {
    this.typeF = typeF;
    this.currentId = 0;
  }

  public VarS newTempVar() {
    return typeF.var("_" + currentId++);
  }
}
