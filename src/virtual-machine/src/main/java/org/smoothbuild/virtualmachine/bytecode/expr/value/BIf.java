package org.smoothbuild.virtualmachine.bytecode.expr.value;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.type.value.BIfKind;

/**
 * 'If' function.
 * This class is thread-safe.
 */
public final class BIf extends BFunc {
  public BIf(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BIfKind);
  }

  @Override
  public String exprToString() {
    return "IfFunc(" + type().name() + ")";
  }
}
