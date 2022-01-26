package org.smoothbuild.bytecode.type.expr;

import static org.smoothbuild.bytecode.type.base.CatKindB.INVOKE;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.expr.InvokeB;
import org.smoothbuild.bytecode.type.base.ExprCatB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.db.Hash;

public class InvokeCB extends ExprCatB {
  public InvokeCB(Hash hash, TypeB evalT) {
    super(hash, "Invoke", INVOKE, evalT);
  }

  @Override
  public InvokeB newObj(MerkleRoot merkleRoot, ObjDbImpl objDb) {
    return (InvokeB) super.newObj(merkleRoot, objDb);
  }
}
