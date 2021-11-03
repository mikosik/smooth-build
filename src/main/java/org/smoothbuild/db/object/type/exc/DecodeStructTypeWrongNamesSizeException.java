package org.smoothbuild.db.object.type.exc;

import static org.smoothbuild.db.object.type.base.ObjKind.STRUCT;

import org.smoothbuild.db.hashed.Hash;

public class DecodeStructTypeWrongNamesSizeException extends DecodeTypeException {
  public DecodeStructTypeWrongNamesSizeException(Hash hash, int itemsSize, int namesSize) {
    super(hash, "It is " + STRUCT + " with items size = " + itemsSize + " and names size = "
        + namesSize + ".");
  }
}
