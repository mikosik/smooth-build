package org.smoothbuild.db.object.type.expr;

import static org.smoothbuild.db.object.type.base.CatKindH.INVOKE;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.InvokeH;
import org.smoothbuild.db.object.type.base.ExprCatH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.val.TupleTH;

import com.google.common.collect.ImmutableList;

public class InvokeCH extends ExprCatH {
  private final TypeH res;
  private final TupleTH params;

  public InvokeCH(Hash hash, TypeH res, TupleTH params) {
    super("Invoke", hash, INVOKE, res);
    this.res = res;
    this.params = params;
  }

  public TypeH res() {
    return res;
  }

  public ImmutableList<TypeH> params() {
    return params.items();
  }

  public TupleTH paramsTuple() {
    return params;
  }

  @Override
  public InvokeH newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    return (InvokeH) super.newObj(merkleRoot, objDb);
  }
}
