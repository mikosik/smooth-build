package org.smoothbuild.virtualmachine.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryKinds.TUPLE;
import static org.smoothbuild.virtualmachine.bytecode.type.value.TypeNamesB.tupleTypeName;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;

/**
 * This class is immutable.
 */
public final class TupleTB extends TypeB {
  private final List<TypeB> elements;

  public TupleTB(Hash hash, List<TypeB> elements) {
    super(hash, calculateName(elements), TUPLE);
    this.elements = elements;
  }

  @Override
  public TupleB newExpr(MerkleRoot merkleRoot, ExprDb exprDb) {
    checkArgument(merkleRoot.category() instanceof TupleTB);
    return new TupleB(merkleRoot, exprDb);
  }

  public TypeB get(int i) {
    return elements.get(i);
  }

  public int size() {
    return elements().size();
  }

  public List<TypeB> elements() {
    return elements;
  }

  private static String calculateName(List<? extends TypeB> elementTypes) {
    return tupleTypeName(elementTypes);
  }
}
