package org.smoothbuild.bytecode.hashed.exc;

import org.smoothbuild.bytecode.hashed.Hash;

public class NoSuchDataExc extends HashedDbExc {
  public NoSuchDataExc(Hash hash) {
    super("No data at " + hash + ".");
  }
}
