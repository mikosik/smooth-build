package org.smoothbuild.bytecode.type.expr;

import static org.smoothbuild.bytecode.type.base.CatKindB.COMBINE;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.expr.CombineB;
import org.smoothbuild.bytecode.type.base.ExprCatB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.db.Hash;

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
  public CombineB newObj(MerkleRoot merkleRoot, ObjDbImpl byteDb) {
    return (CombineB) super.newObj(merkleRoot, byteDb);
  }
}
