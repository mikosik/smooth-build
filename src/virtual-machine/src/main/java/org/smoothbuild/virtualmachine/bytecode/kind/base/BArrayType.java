package org.smoothbuild.virtualmachine.bytecode.kind.base;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;

/**
 * This class is immutable.
 */
public final class BArrayType extends BType {
  private final BType element;

  public BArrayType(Hash hash, BType element) {
    super(hash, BTypeNames.arrayTypeName(element), BArray.class);
    this.element = requireNonNull(element);
  }

  public BType element() {
    return element;
  }

  @Override
  public BArray newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BArrayType);
    return new BArray(merkleRoot, exprDb);
  }
}
