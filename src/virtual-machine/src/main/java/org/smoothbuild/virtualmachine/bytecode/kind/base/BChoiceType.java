package org.smoothbuild.virtualmachine.bytecode.kind.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.BTypeNames.choiceTypeName;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BChoice;

/**
 * This class is immutable.
 */
public final class BChoiceType extends BType {
  private final List<BType> alternatives;

  public BChoiceType(Hash hash, List<BType> alternatives) {
    super(hash, choiceTypeName(alternatives), BChoice.class);
    this.alternatives = alternatives;
  }

  @Override
  public BChoice newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BChoiceType);
    return new BChoice(merkleRoot, exprDb);
  }

  public int size() {
    return alternatives().size();
  }

  public List<BType> alternatives() {
    return alternatives;
  }
}
