package org.smoothbuild.compilerfrontend.lang.type.tool;

import org.smoothbuild.compilerfrontend.lang.type.TempVarS;

public class TempVarGenerator {
  private int tempVarCounter = 0;

  public TempVarS next() {
    return new TempVarS(Integer.toString(tempVarCounter++));
  }
}
