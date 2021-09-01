package org.smoothbuild.db.object.obj.expr;

import static org.smoothbuild.db.object.db.Helpers.wrapObjectDbExceptionAsDecodeObjException;

import org.smoothbuild.db.object.db.DecodeObjException;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.obj.base.Val;

/**
 * This class is immutable.
 */
public class Const extends Expr {
  public Const(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
  }

  public Obj value() {
    Obj obj = wrapObjectDbExceptionAsDecodeObjException(
        hash(), () -> objectDb().get(merkleRoot().dataHash()));
    if (obj instanceof Val val) {
      return val;
    } else {
      throw new DecodeObjException(
          hash(), "Its data should contain Val but contains " + obj.spec().name() + ".");
    }
  }

  @Override
  public String valueToString() {
    return "Const(???)";
  }
}
