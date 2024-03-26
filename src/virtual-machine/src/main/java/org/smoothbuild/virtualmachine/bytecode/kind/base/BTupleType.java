package org.smoothbuild.virtualmachine.bytecode.kind.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;

/**
 * This class is immutable.
 */
public final class BTupleType extends BType {
  private final List<BType> elements;

  public BTupleType(Hash hash, List<BType> elements) {
    super(hash, calculateName(elements), BTuple.class);
    this.elements = elements;
  }

  @Override
  public BTuple newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BTupleType);
    return new BTuple(merkleRoot, exprDb);
  }

  public BType get(int i) {
    return elements.get(i);
  }

  public int size() {
    return elements().size();
  }

  public List<BType> elements() {
    return elements;
  }

  private static String calculateName(List<? extends BType> elementTypes) {
    return BTypeNames.tupleTypeName(elementTypes);
  }
}
