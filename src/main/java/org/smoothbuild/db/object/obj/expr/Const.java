package org.smoothbuild.db.object.obj.expr;

import org.smoothbuild.db.object.db.Helpers;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.exc.UnexpectedNodeException;
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

  public Val value() {
    Obj obj = Helpers.wrapObjectDbExceptionAsDecodeObjNodeException(
        hash(), spec(), DATA_PATH, () -> objectDb().get(dataHash()));
    if (obj instanceof Val val) {
      return val;
    } else {
      throw new UnexpectedNodeException(hash(), spec(), DATA_PATH, Val.class, obj.getClass());
    }
  }

  @Override
  public String valueToString() {
    return "Const(" + value().valueToString() + ")";
  }
}
