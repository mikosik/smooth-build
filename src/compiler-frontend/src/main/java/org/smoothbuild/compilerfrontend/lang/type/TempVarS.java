package org.smoothbuild.compilerfrontend.lang.type;

public final class TempVarS extends VarS {
  public TempVarS(String name) {
    super(name);
  }

  public boolean isOlderThan(TempVarS tempVarS) {
    return name().compareTo(tempVarS.name()) < 0;
  }
}
