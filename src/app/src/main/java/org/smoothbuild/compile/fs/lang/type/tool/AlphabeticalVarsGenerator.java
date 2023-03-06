package org.smoothbuild.compile.fs.lang.type.tool;

import java.util.Arrays;

import org.smoothbuild.compile.fs.lang.type.VarS;

public class AlphabeticalVarsGenerator {
  private char[] name = new char[] {'A'};

  public VarS next() {
    VarS varS = new VarS(new String(name));
    generateNextName();
    return varS;
  }

  private void generateNextName() {
    if (allZ()) {
      name = new char[name.length + 1];
      Arrays.fill(name, 'A');
    } else {
      boolean increment = true;
      int i = name.length - 1;
      while (increment && 0 <= i) {
        char current = name[i];
        if (current == 'Z') {
          name[i] = 'A';
          i--;
        } else {
          current += 1;
          name[i] = current;
          increment = false;
        }
      }
    }
  }

  private boolean allZ() {
    for (char c : name) {
      if (c != 'Z') {
        return false;
      }
    }
    return true;
  }
}
