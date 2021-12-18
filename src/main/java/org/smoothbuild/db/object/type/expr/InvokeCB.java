package org.smoothbuild.db.object.type.expr;

import static org.smoothbuild.db.object.type.base.CatKindB.INVOKE;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ByteDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.InvokeB;
import org.smoothbuild.db.object.type.base.ExprCatB;
import org.smoothbuild.db.object.type.base.TypeB;

public class InvokeCB extends ExprCatB {
  public InvokeCB(Hash hash, TypeB evalT) {
    super("Invoke", hash, INVOKE, evalT);
  }

  @Override
  public InvokeB newObj(MerkleRoot merkleRoot, ByteDb byteDb) {
    return (InvokeB) super.newObj(merkleRoot, byteDb);
  }
}
