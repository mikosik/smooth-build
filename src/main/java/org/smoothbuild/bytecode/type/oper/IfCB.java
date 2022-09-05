package org.smoothbuild.bytecode.type.oper;

import static org.smoothbuild.bytecode.type.CatKindB.IF;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.oper.IfB;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.db.Hash;

public class IfCB extends OperCatB {
  public IfCB(Hash hash, TypeB evalT) {
    super(hash, "If", IF, evalT);
  }

  @Override
  public IfB newObj(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    return (IfB) super.newObj(merkleRoot, bytecodeDb);
  }
}
