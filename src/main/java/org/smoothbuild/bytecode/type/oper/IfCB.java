package org.smoothbuild.bytecode.type.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.bytecode.type.CatKindB.IF;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.oper.IfB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.val.TypeB;

public class IfCB extends OperCatB {
  public IfCB(Hash hash, TypeB evalT) {
    super(hash, "If", IF, evalT);
  }

  @Override
  public IfB newObj(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    checkArgument(merkleRoot.cat() instanceof IfCB);
    return new IfB(merkleRoot, bytecodeDb);
  }
}
