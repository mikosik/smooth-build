package org.smoothbuild.db.object.type.expr;

import static org.smoothbuild.db.object.type.base.CatKindB.COMBINE;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ByteDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.CombineB;
import org.smoothbuild.db.object.type.base.ExprCatB;
import org.smoothbuild.db.object.type.val.TupleTB;

/**
 * This class is immutable.
 */
public class CombineCB extends ExprCatB {
  public CombineCB(Hash hash, TupleTB evalT) {
    super("Combine", hash, COMBINE, evalT);
  }

  @Override
  public TupleTB evalT() {
    return (TupleTB) super.evalT();
  }

  @Override
  public CombineB newObj(MerkleRoot merkleRoot, ByteDb byteDb) {
    return (CombineB) super.newObj(merkleRoot, byteDb);
  }
}
