package org.smoothbuild.virtualmachine.bytecode.kind.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.BTypeNames.tupleTypeName;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;

/**
 * This class is immutable.
 */
public final class BTupleType extends BType {
  private final List<? extends BType> elements;

  public BTupleType(Hash hash, List<? extends BType> elements) {
    super(hash, tupleTypeName(elements), BTuple.class);
    this.elements = elements;
  }

  @Override
  public BTuple newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BTupleType);
    return new BTuple(merkleRoot, exprDb);
  }

  public int size() {
    return elements().size();
  }

  public List<? extends BType> elements() {
    return elements;
  }
}
