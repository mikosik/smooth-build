package org.smoothbuild.db.bytecode.type.expr;

import static org.smoothbuild.db.bytecode.type.base.CatKindB.COMBINE;

import org.smoothbuild.db.bytecode.obj.ByteDbImpl;
import org.smoothbuild.db.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.db.bytecode.obj.expr.CombineB;
import org.smoothbuild.db.bytecode.type.base.ExprCatB;
import org.smoothbuild.db.bytecode.type.val.TupleTB;
import org.smoothbuild.db.hashed.Hash;

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
  public CombineB newObj(MerkleRoot merkleRoot, ByteDbImpl byteDb) {
    return (CombineB) super.newObj(merkleRoot, byteDb);
  }
}
