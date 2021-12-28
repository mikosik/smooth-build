package org.smoothbuild.db.bytecode.type.val;

import static org.smoothbuild.db.bytecode.type.base.CatKindB.TUPLE;
import static org.smoothbuild.lang.base.type.api.TypeNames.tupleTypeName;

import org.smoothbuild.db.bytecode.obj.ByteDbImpl;
import org.smoothbuild.db.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.db.bytecode.obj.val.TupleB;
import org.smoothbuild.db.bytecode.type.base.TypeB;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.type.api.TupleT;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class TupleTB extends TypeB implements TupleT {
  private final ImmutableList<TypeB> itemTs;

  public TupleTB(Hash hash, ImmutableList<TypeB> itemTs) {
    super(calculateName(itemTs), hash, TUPLE, calculateVars(itemTs));
    this.itemTs = ImmutableList.copyOf(itemTs);
  }

  @Override
  public TupleB newObj(MerkleRoot merkleRoot, ByteDbImpl byteDb) {
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
}
