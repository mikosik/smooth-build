package org.smoothbuild.bytecode.type.expr;

import static org.smoothbuild.bytecode.type.base.CatKindB.CALL;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.expr.CallB;
import org.smoothbuild.bytecode.type.base.ExprCatB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.db.Hash;

/**
 * This class is immutable.
 */
public class CallCB extends ExprCatB {
  public CallCB(Hash hash, TypeB evalT) {
    super("Call", hash, CALL, evalT);
  }

  @Override
  public CallB newObj(MerkleRoot merkleRoot, ObjDbImpl byteDb) {
    return (CallB) super.newObj(merkleRoot, byteDb);
  }
}
