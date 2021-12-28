package org.smoothbuild.db.bytecode.type.expr;

import static org.smoothbuild.db.bytecode.type.base.CatKindB.SELECT;

import org.smoothbuild.db.bytecode.obj.ByteDbImpl;
import org.smoothbuild.db.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.db.bytecode.obj.expr.SelectB;
import org.smoothbuild.db.bytecode.type.base.ExprCatB;
import org.smoothbuild.db.bytecode.type.base.TypeB;
import org.smoothbuild.db.hashed.Hash;

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
