package org.smoothbuild.bytecode.type.expr;

import static org.smoothbuild.bytecode.type.base.CatKindB.SELECT;

import org.smoothbuild.bytecode.obj.ByteDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.expr.SelectB;
import org.smoothbuild.bytecode.type.base.ExprCatB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.db.Hash;

/**
 * This class is immutable.
 */
public class SelectCB extends ExprCatB {
  public SelectCB(Hash hash, TypeB evalT) {
    super("Select", hash, SELECT, evalT);
  }

  @Override
  public SelectB newObj(MerkleRoot merkleRoot, ByteDbImpl byteDb) {
    return (SelectB) super.newObj(merkleRoot, byteDb);
  }
}
