package org.smoothbuild.db.exc;

import org.smoothbuild.db.Hash;

public class NoSuchDataExc extends HashedDbExc {
  public NoSuchDataExc(Hash hash) {
    super("No data at " + hash + ".");
  }
}
