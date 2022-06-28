package org.smoothbuild.bytecode.type.cnst;

import static org.smoothbuild.bytecode.type.CatKindB.TUPLE;
import static org.smoothbuild.bytecode.type.cnst.TNamesB.tupleTypeName;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.cnst.TupleB;
import org.smoothbuild.db.Hash;

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
  public TupleB newObj(MerkleRoot merkleRoot, ObjDbImpl objDb) {
    return (TupleB) super.newObj(merkleRoot, objDb);
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
