package org.smoothbuild.vm.execute;

import org.smoothbuild.bytecode.hashed.Hash;

public record TraceB (Hash call, Hash called, TraceB tail){
  public TraceB(Hash enclosing, Hash called) {
    this(enclosing, called, null);
  }

  @Override
  public String toString() {
    var line = call.toString() + " " + called.toString();
    if (tail == null) {
      return line;
    } else {
      return line + "\n" + tail;
    }
  }
}
