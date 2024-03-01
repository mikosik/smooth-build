package org.smoothbuild.virtualmachine.bytecode.expr.value;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.type.value.MapFuncCB;

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