package org.smoothbuild.db.object.type.expr;

import static org.smoothbuild.db.object.type.base.SpecKindH.CALL;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.CallH;
import org.smoothbuild.db.object.type.base.ExprSpecH;
import org.smoothbuild.db.object.type.base.TypeH;

/**
 * This class is immutable.
 */
public class CallTypeH extends ExprSpecH {
  public CallTypeH(Hash hash, TypeH evalType) {
    super("Call", hash, CALL, evalType);
  }

  @Override
  public CallH newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    return (CallH) super.newObj(merkleRoot, objDb);
  }
}
