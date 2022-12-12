package org.smoothbuild.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.bytecode.type.CategoryKinds.CLOSURE;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.value.ClosureB;
import org.smoothbuild.bytecode.hashed.Hash;

public final class ClosureCB extends FuncCB {
  public ClosureCB(Hash hash, FuncTB funcTB) {
    super(hash, CLOSURE, funcTB);
  }

  @Override
  public ClosureB newExpr(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    checkArgument(merkleRoot.category() instanceof ClosureCB);
    return new ClosureB(merkleRoot, bytecodeDb);
  }
}
