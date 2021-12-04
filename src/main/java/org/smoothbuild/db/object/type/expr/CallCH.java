package org.smoothbuild.db.object.type.expr;

import static org.smoothbuild.db.object.type.base.CatKindH.CALL;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.CallH;
import org.smoothbuild.db.object.type.base.ExprCatH;
import org.smoothbuild.db.object.type.base.TypeH;

/**
 * This class is immutable.
 */
public class CallCH extends ExprCatH {
  public CallCH(Hash hash, TypeH evalT) {
    super("Call", hash, CALL, evalT);
  }

  @Override
  public CallH newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    return (CallH) super.newObj(merkleRoot, objDb);
  }
}
