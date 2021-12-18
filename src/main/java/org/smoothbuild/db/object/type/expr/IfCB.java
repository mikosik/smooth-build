package org.smoothbuild.db.object.type.expr;

import static org.smoothbuild.db.object.type.base.CatKindB.IF;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ByteDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.IfB;
import org.smoothbuild.db.object.type.base.ExprCatB;
import org.smoothbuild.db.object.type.base.TypeB;

public class IfCB extends ExprCatB {
  public IfCB(Hash hash, TypeB evalT) {
    super("If", hash, IF, evalT);
  }

  @Override
  public IfB newObj(MerkleRoot merkleRoot, ByteDb byteDb) {
    return (IfB) super.newObj(merkleRoot, byteDb);
  }
}
