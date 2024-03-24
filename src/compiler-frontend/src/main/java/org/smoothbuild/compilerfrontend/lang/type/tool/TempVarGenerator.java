package org.smoothbuild.compilerfrontend.lang.type.tool;

import org.smoothbuild.compilerfrontend.lang.type.STempVar;

public class TempVarGenerator {
  private int tempVarCounter = 0;

  public STempVar next() {
    return new STempVar(Integer.toString(tempVarCounter++));
  }
}
