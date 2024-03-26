package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.type.base.BMapKind;

/**
 * 'Map' function.
 * This class is thread-safe.
 */
public final class BMap extends BFunc {
  public BMap(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BMapKind);
  }

  @Override
  public String exprToString() {
    return "MapFunc(" + type().name() + ")";
  }
}
