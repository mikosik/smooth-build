package org.smoothbuild.vm.bytecode.hashed.exc;

import org.smoothbuild.vm.bytecode.hashed.Hash;

public class NoSuchDataExc extends HashedDbExc {
  public NoSuchDataExc(Hash hash) {
    super("No data at " + hash + ".");
  }
}
