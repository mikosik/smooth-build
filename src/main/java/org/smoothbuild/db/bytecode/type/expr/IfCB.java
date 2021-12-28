package org.smoothbuild.db.bytecode.type.expr;

import static org.smoothbuild.db.bytecode.type.base.CatKindB.IF;

import org.smoothbuild.db.bytecode.obj.ByteDbImpl;
import org.smoothbuild.db.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.db.bytecode.obj.expr.IfB;
import org.smoothbuild.db.bytecode.type.base.ExprCatB;
import org.smoothbuild.db.bytecode.type.base.TypeB;
import org.smoothbuild.db.hashed.Hash;

public class IfCB extends ExprCatB {
  public IfCB(Hash hash, TypeB evalT) {
    super("If", hash, IF, evalT);
  }

  @Override
  public IfB newObj(MerkleRoot merkleRoot, ByteDbImpl byteDb) {
    return (IfB) super.newObj(merkleRoot, byteDb);
  }
}
