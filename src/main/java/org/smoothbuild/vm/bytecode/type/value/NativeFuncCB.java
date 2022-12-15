package org.smoothbuild.vm.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.NATIVE_FUNC;

import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.value.NativeFuncB;
import org.smoothbuild.vm.bytecode.hashed.Hash;

public final class NativeFuncCB extends FuncCB {
  public NativeFuncCB(Hash hash, FuncTB funcTB) {
    super(hash, NATIVE_FUNC, funcTB);
  }

  @Override
  public NativeFuncB newExpr(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    checkArgument(merkleRoot.category() instanceof NativeFuncCB);
    return new NativeFuncB(merkleRoot, bytecodeDb);
  }
}
