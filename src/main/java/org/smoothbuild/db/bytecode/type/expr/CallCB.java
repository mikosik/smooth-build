package org.smoothbuild.db.bytecode.type.expr;

import static org.smoothbuild.db.bytecode.type.base.CatKindB.CALL;

import org.smoothbuild.db.bytecode.obj.ByteDb;
import org.smoothbuild.db.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.db.bytecode.obj.expr.CallB;
import org.smoothbuild.db.bytecode.type.base.ExprCatB;
import org.smoothbuild.db.bytecode.type.base.TypeB;
import org.smoothbuild.db.hashed.Hash;

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
