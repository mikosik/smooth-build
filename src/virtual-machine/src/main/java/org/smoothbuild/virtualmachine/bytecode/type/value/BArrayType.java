package org.smoothbuild.virtualmachine.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BArray;

/**
 * This class is immutable.
 */
public final class BArrayType extends BType {
  private final BType elem;

  public BArrayType(Hash hash, BType elem) {
    super(hash, BTypeNames.arrayTypeName(elem), BArray.class);
    this.elem = requireNonNull(elem);
  }

  public BType elem() {
    return elem;
  }

  @Override
  public BArray newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BArrayType);
    return new BArray(merkleRoot, exprDb);
  }
}
