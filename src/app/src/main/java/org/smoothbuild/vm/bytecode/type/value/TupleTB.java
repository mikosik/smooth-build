package org.smoothbuild.vm.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.TUPLE;
import static org.smoothbuild.vm.bytecode.type.value.TypeNamesB.tupleTypeName;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.hashed.Hash;

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
  public TupleB newExpr(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    checkArgument(merkleRoot.category() instanceof TupleTB);
    return new TupleB(merkleRoot, bytecodeDb);
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
