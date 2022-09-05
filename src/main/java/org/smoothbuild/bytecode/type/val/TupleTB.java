package org.smoothbuild.bytecode.type.val;

import static org.smoothbuild.bytecode.type.CatKindB.TUPLE;
import static org.smoothbuild.bytecode.type.val.TNamesB.tupleTypeName;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.hashed.Hash;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public final class TupleTB extends TypeB implements ComposedTB {
  private final ImmutableList<TypeB> itemTs;

  public TupleTB(Hash hash, ImmutableList<TypeB> itemTs) {
    super(hash, calculateName(itemTs), TUPLE);
    this.itemTs = ImmutableList.copyOf(itemTs);
  }

  @Override
  public TupleB newObj(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    return (TupleB) super.newObj(merkleRoot, bytecodeDb);
  }

  public ImmutableList<TypeB> items() {
    return itemTs;
  }

  private static String calculateName(Iterable<? extends TypeB> itemTs) {
    return tupleTypeName(itemTs);
  }

  @Override
  public ImmutableList<TypeB> covars() {
    return items();
  }

  @Override
  public ImmutableList<TypeB> contravars() {
    return ImmutableList.of();
  }
}
