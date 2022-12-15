package org.smoothbuild.vm.bytecode.expr.value;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.type.value.MapFuncCB;

/**
 * Map function.
 * This class is thread-safe.
 */
public final class MapFuncB extends FuncB {
  public MapFuncB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
    checkArgument(merkleRoot.category() instanceof MapFuncCB);
  }

  @Override
  public String exprToString() {
    return "MapFunc(" + type().name() + ")";
  }
}
