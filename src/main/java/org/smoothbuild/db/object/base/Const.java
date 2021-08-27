package org.smoothbuild.db.object.base;

import static org.smoothbuild.db.object.db.Helpers.wrapObjectDbExceptionAsDecodingObjectException;

import org.smoothbuild.db.object.db.CannotDecodeObjectException;
import org.smoothbuild.db.object.db.ObjectDb;

/**
 * This class is immutable.
 */
public class Const extends Expr {
  public Const(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
  }

  public Obj value() {
    Obj obj = wrapObjectDbExceptionAsDecodingObjectException(
        hash(), () -> objectDb().get(merkleRoot().dataHash()));
    if (obj instanceof Val val) {
      return val;
    } else {
      throw new CannotDecodeObjectException(
          hash(), "Its data should contain Val but contains " + obj.spec().name() + ".");
    }
  }

  @Override
  public String valueToString() {
    return "Const(???)";
  }
}
