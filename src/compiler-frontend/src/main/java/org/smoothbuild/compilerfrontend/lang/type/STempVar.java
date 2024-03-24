package org.smoothbuild.compilerfrontend.lang.type;

public final class STempVar extends SVar {
  public STempVar(String name) {
    super(name);
  }

  public boolean isOlderThan(STempVar sTempVar) {
    return name().compareTo(sTempVar.name()) < 0;
  }
}
