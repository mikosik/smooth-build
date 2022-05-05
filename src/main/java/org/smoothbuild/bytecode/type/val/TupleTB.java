package org.smoothbuild.bytecode.type.val;

import static org.smoothbuild.bytecode.type.base.CatKindB.TUPLE;
import static org.smoothbuild.bytecode.type.val.TypeNamesB.tupleTypeName;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.db.Hash;
import org.smoothbuild.lang.type.api.TupleT;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public final class TupleTB extends TypeB implements TupleT, ComposedTB {
  private final ImmutableList<TypeB> itemTs;

  public TupleTB(Hash hash, ImmutableList<TypeB> itemTs) {
    super(hash, calculateName(itemTs), TUPLE, calculateVars(itemTs));
    this.itemTs = ImmutableList.copyOf(itemTs);
  }

  @Override
  public TupleB newObj(MerkleRoot merkleRoot, ObjDbImpl objDb) {
    validateNotPolymorphic(merkleRoot);
    return (TupleB) super.newObj(merkleRoot, objDb);
  }

  @Override
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
