package org.smoothbuild.vm.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.TUPLE;
import static org.smoothbuild.vm.bytecode.type.value.TypeNamesB.tupleTypeName;

import io.vavr.collection.Array;
import io.vavr.collection.Traversable;
import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.hashed.Hash;

/**
 * This class is immutable.
 */
public final class TupleTB extends TypeB {
  private final Array<TypeB> elements;

  public TupleTB(Hash hash, Array<TypeB> elements) {
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

  public Array<TypeB> elements() {
    return elements;
  }

  private static String calculateName(Traversable<? extends TypeB> elementTypes) {
    return tupleTypeName(elementTypes);
  }
}
