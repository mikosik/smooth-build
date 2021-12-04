package org.smoothbuild.db.object.type.expr;

import static org.smoothbuild.db.object.type.base.CatKindH.COMBINE;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.CombineH;
import org.smoothbuild.db.object.type.base.ExprCatH;
import org.smoothbuild.db.object.type.val.TupleTH;

/**
 * This class is immutable.
 */
public class CombineCH extends ExprCatH {
  public CombineCH(Hash hash, TupleTH evalT) {
    super("Combine", hash, COMBINE, evalT);
  }

  @Override
  public TupleTH evalT() {
    return (TupleTH) super.evalT();
  }

  @Override
  public CombineH newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    return (CombineH) super.newObj(merkleRoot, objDb);
  }
}
