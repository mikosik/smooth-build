package org.smoothbuild.bytecode.type.val;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.bytecode.type.CatKinds.TUPLE;
import static org.smoothbuild.bytecode.type.val.TNamesB.tupleTypeName;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.hashed.Hash;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public final class TupleTB extends TypeB {
  private final ImmutableList<TypeB> items;

  public TupleTB(Hash hash, ImmutableList<TypeB> items) {
    super(hash, calculateName(items), TUPLE);
    this.items = ImmutableList.copyOf(items);
  }

  @Override
  public TupleB newExpr(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    checkArgument(merkleRoot.cat() instanceof TupleTB);
    return new TupleB(merkleRoot, bytecodeDb);
  }

  public TypeB get(int i) {
    return items.get(i);
  }

  public int size() {
    return items().size();
  }

  public ImmutableList<TypeB> items() {
    return items;
  }

  private static String calculateName(Iterable<? extends TypeB> itemTs) {
    return tupleTypeName(itemTs);
  }
}
