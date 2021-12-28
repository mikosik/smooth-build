package org.smoothbuild.db.bytecode.type.expr;

import static org.smoothbuild.db.bytecode.type.base.CatKindB.INVOKE;

import org.smoothbuild.db.bytecode.obj.ByteDbImpl;
import org.smoothbuild.db.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.db.bytecode.obj.expr.InvokeB;
import org.smoothbuild.db.bytecode.type.base.ExprCatB;
import org.smoothbuild.db.bytecode.type.base.TypeB;
import org.smoothbuild.db.hashed.Hash;

public class InvokeCB extends ExprCatB {
  public InvokeCB(Hash hash, TypeB evalT) {
    super("Invoke", hash, INVOKE, evalT);
  }

  @Override
  public InvokeB newObj(MerkleRoot merkleRoot, ByteDbImpl byteDb) {
    return (InvokeB) super.newObj(merkleRoot, byteDb);
  }
}
