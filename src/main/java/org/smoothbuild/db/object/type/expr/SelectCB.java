package org.smoothbuild.db.object.type.expr;

import static org.smoothbuild.db.object.type.base.CatKindB.SELECT;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ByteDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.SelectB;
import org.smoothbuild.db.object.type.base.ExprCatB;
import org.smoothbuild.db.object.type.base.TypeB;

/**
 * This class is immutable.
 */
public class SelectCB extends ExprCatB {
  public SelectCB(Hash hash, TypeB evalT) {
    super("Select", hash, SELECT, evalT);
  }

  @Override
  public SelectB newObj(MerkleRoot merkleRoot, ByteDb byteDb) {
    return (SelectB) super.newObj(merkleRoot, byteDb);
  }
}
