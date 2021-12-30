package org.smoothbuild.bytecode.type.expr;

import static org.smoothbuild.bytecode.type.base.CatKindB.IF;

import org.smoothbuild.bytecode.obj.ByteDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.expr.IfB;
import org.smoothbuild.bytecode.type.base.ExprCatB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.db.Hash;

public class IfCB extends ExprCatB {
  public IfCB(Hash hash, TypeB evalT) {
    super("If", hash, IF, evalT);
  }

  @Override
  public IfB newObj(MerkleRoot merkleRoot, ByteDbImpl byteDb) {
    return (IfB) super.newObj(merkleRoot, byteDb);
  }
}
