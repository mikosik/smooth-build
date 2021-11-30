package org.smoothbuild.db.hashed.exc;

import org.smoothbuild.db.hashed.Hash;

public class NoSuchDataExc extends HashedDbExc {
  public NoSuchDataExc(Hash hash) {
    super("No data at " + hash + ".");
  }
}
