package org.smoothbuild.db.object.type.expr;

import static org.smoothbuild.db.object.type.base.CatKindB.CALL;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ByteDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.CallB;
import org.smoothbuild.db.object.type.base.ExprCatB;
import org.smoothbuild.db.object.type.base.TypeB;

/**
 * This class is immutable.
 */
public class CallCB extends ExprCatB {
  public CallCB(Hash hash, TypeB evalT) {
    super("Call", hash, CALL, evalT);
  }

  @Override
  public CallB newObj(MerkleRoot merkleRoot, ByteDb byteDb) {
    return (CallB) super.newObj(merkleRoot, byteDb);
  }
}
