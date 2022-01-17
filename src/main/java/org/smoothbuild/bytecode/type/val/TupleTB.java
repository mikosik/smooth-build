package org.smoothbuild.bytecode.type.val;

import static org.smoothbuild.bytecode.type.base.CatKindB.TUPLE;
import static org.smoothbuild.lang.base.type.api.TypeNames.tupleTypeName;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.db.Hash;
import org.smoothbuild.lang.base.type.api.TupleT;
import org.smoothbuild.lang.base.type.api.Type;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class TupleTB extends TypeB implements TupleT {
  private final ImmutableList<TypeB> itemTs;

  public TupleTB(Hash hash, ImmutableList<TypeB> itemTs) {
    super(hash, calculateName(itemTs), TUPLE,
        itemTs.stream().anyMatch(Type::hasOpenVars),
        itemTs.stream().anyMatch(Type::hasClosedVars));
    this.itemTs = ImmutableList.copyOf(itemTs);
  }

  @Override
  public TupleB newObj(MerkleRoot merkleRoot, ObjDbImpl byteDb) {
    validateNotPolymorphic(merkleRoot);
    return (TupleB) super.newObj(merkleRoot, byteDb);
  }

  @Override
  public ImmutableList<TypeB> items() {
    return itemTs;
  }

  private static String calculateName(Iterable<? extends TypeB> itemTs) {
    return tupleTypeName(itemTs);
  }

  @Override
  public ImmutableList<Type> covars() {
    return (ImmutableList<Type>)(Object) items();
  }

  @Override
  public ImmutableList<Type> contravars() {
    return ImmutableList.of();
  }
}
