package org.smoothbuild.virtualmachine.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.virtualmachine.bytecode.type.value.BTypeNames.tupleTypeName;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BTuple;

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
  public BTuple newExpr(MerkleRoot merkleRoot, ExprDb exprDb) {
    checkArgument(merkleRoot.category() instanceof BTupleType);
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
    return tupleTypeName(elementTypes);
  }
}
