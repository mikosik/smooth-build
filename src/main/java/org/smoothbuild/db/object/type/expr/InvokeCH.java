package org.smoothbuild.db.object.type.expr;

import static org.smoothbuild.db.object.type.base.CatKindH.INVOKE;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.InvokeH;
import org.smoothbuild.db.object.type.base.ExprCatH;
import org.smoothbuild.db.object.type.base.TypeH;

public class InvokeCH extends ExprCatH {
  public InvokeCH(Hash hash, TypeH evalT) {
    super("Invoke", hash, INVOKE, evalT);
  }

  @Override
  public InvokeH newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    return (InvokeH) super.newObj(merkleRoot, objDb);
  }
}
