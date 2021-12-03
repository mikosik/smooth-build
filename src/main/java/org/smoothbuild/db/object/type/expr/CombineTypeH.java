package org.smoothbuild.db.object.type.expr;

import static org.smoothbuild.db.object.type.base.SpecKindH.COMBINE;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.CombineH;
import org.smoothbuild.db.object.type.base.ExprSpecH;
import org.smoothbuild.db.object.type.val.TupleTypeH;

/**
 * This class is immutable.
 */
public class CombineTypeH extends ExprSpecH {
  public CombineTypeH(Hash hash, TupleTypeH evalType) {
    super("Combine", hash, COMBINE, evalType);
  }

  @Override
  public TupleTypeH evalType() {
    return (TupleTypeH) super.evalType();
  }

  @Override
  public CombineH newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    return (CombineH) super.newObj(merkleRoot, objDb);
  }
}
