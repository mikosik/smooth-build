package org.smoothbuild.db.object.exc;

import static org.smoothbuild.db.object.spec.base.SpecKind.STRUCT;

import org.smoothbuild.db.hashed.Hash;

public class DecodeStructSpecWrongNamesSizeException extends DecodeSpecException {
  public DecodeStructSpecWrongNamesSizeException(Hash hash, int itemsSize, int namesSize) {
    super(hash, "It is " + STRUCT + " with items size = " + itemsSize + " and names size = "
        + namesSize + ".");
  }
}
