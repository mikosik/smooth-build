package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.CatKindH.TUPLE;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.db.object.type.base.CatH;
import org.smoothbuild.db.object.type.base.TypeH;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class TupleTH extends TypeH {
  private final ImmutableList<TypeH> itemTs;

  public TupleTH(Hash hash, Iterable<? extends TypeH> itemTs) {
    super(calculateName(itemTs), hash, TUPLE);
    this.itemTs = ImmutableList.copyOf(itemTs);
  }

  @Override
  public TupleH newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    return (TupleH) super.newObj(merkleRoot, objDb);
  }

  public ImmutableList<TypeH> items() {
    return itemTs;
  }

  private static String calculateName(Iterable<? extends TypeH> itemTs) {
    return "{" + toCommaSeparatedString(itemTs, CatH::name) + "}";
  }
}
