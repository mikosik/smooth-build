package org.smoothbuild.vm.bytecode.expr.value;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.type.value.FuncCB;
import org.smoothbuild.vm.bytecode.type.value.FuncTB;

public abstract sealed class FuncB extends ValueB
    permits LambdaB, IfFuncB, MapFuncB, NativeFuncB {
  public FuncB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
    checkArgument(merkleRoot.category() instanceof FuncCB);
  }

  @Override
  public FuncTB evaluationT() {
    return type();
  }

  @Override
  public FuncTB type() {
    return ((FuncCB) category()).type();
  }
}
