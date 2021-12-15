package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.CatKindH.TUPLE;
import static org.smoothbuild.lang.base.type.api.TypeNames.tupleTypeName;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.lang.base.type.api.TupleT;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class TupleTH extends TypeH implements TupleT {
  private final ImmutableList<TypeH> itemTs;

  public TupleTH(Hash hash, ImmutableList<TypeH> itemTs) {
    super(calculateName(itemTs), hash, TUPLE, calculateVars(itemTs));
    this.itemTs = ImmutableList.copyOf(itemTs);
  }

  @Override
  public TupleH newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    return (TupleH) super.newObj(merkleRoot, objDb);
  }

  @Override
  public ImmutableList<TypeH> items() {
    return itemTs;
  }

  private static String calculateName(Iterable<? extends TypeH> itemTs) {
    return tupleTypeName(itemTs);
  }
}
