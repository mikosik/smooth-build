package org.smoothbuild.vm.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.CLOSURE;

import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.value.ClosureB;
import org.smoothbuild.vm.bytecode.hashed.Hash;

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
