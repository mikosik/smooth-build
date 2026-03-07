package org.smoothbuild.virtualmachine.bytecode.kind.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.BTypeNames.variantTypeName;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BVariant;

/**
 * This class is immutable.
 */
public final class BVariantType extends BType {
  private final List<BType> alternatives;

  public BVariantType(Hash hash, List<BType> alternatives) {
    super(hash, variantTypeName(alternatives), BVariant.class);
    this.alternatives = alternatives;
  }

  @Override
  public BVariant newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BVariantType);
    return new BVariant(merkleRoot, exprDb);
  }

  public int size() {
    return alternatives().size();
  }

  public List<BType> alternatives() {
    return alternatives;
  }
}
