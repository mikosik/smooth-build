package org.smoothbuild.vm.execute;

import org.smoothbuild.bytecode.hashed.Hash;

public record TraceB (Hash enclosing, Hash called, TraceB tail){
  public TraceB(Hash enclosing, Hash called) {
    this(enclosing, called, null);
  }

  @Override
  public String toString() {
    var line = enclosing.toString() + " " + called.toString();
    if (tail == null) {
      return line;
    } else {
      return line + "\n" + tail;
    }
  }
}
