package org.smoothbuild.vm.bytecode.expr.value;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.vm.bytecode.expr.ExprDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.type.value.MapFuncCB;

/**
 * 'Map' function.
 * This class is thread-safe.
 */
public final class MapFuncB extends FuncB {
  public MapFuncB(MerkleRoot merkleRoot, ExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.category() instanceof MapFuncCB);
  }

  @Override
  public String exprToString() {
    return "MapFunc(" + type().name() + ")";
  }
}
