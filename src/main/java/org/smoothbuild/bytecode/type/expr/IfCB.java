package org.smoothbuild.bytecode.type.expr;

import static org.smoothbuild.bytecode.type.CatKindB.IF;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.expr.IfB;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.db.Hash;

public class IfCB extends ExprCatB {
  public IfCB(Hash hash, TypeB evalT) {
    super(hash, "If", IF, evalT);
  }

  @Override
  public IfB newObj(MerkleRoot merkleRoot, ObjDbImpl objDb) {
    return (IfB) super.newObj(merkleRoot, objDb);
  }
}
