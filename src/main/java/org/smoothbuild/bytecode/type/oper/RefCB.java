package org.smoothbuild.bytecode.type.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.bytecode.type.CategoryKinds.REF;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.oper.RefB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.value.TypeB;

public class RefCB extends OperCB {
  public RefCB(Hash hash, TypeB evalT) {
    super(hash, REF, evalT);
  }

  @Override
  public RefB newExpr(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    checkArgument(merkleRoot.category() instanceof RefCB);
    return new RefB(merkleRoot, bytecodeDb);
  }
}
