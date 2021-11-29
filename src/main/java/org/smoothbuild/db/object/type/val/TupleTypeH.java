package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.SpecKindH.TUPLE;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.db.object.type.base.SpecH;
import org.smoothbuild.db.object.type.base.TypeH;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class TupleTypeH extends TypeH {
  private final ImmutableList<TypeH> itemTypes;

  public TupleTypeH(Hash hash, Iterable<? extends TypeH> itemTypes) {
    super(calculateName(itemTypes), hash, TUPLE);
    this.itemTypes = ImmutableList.copyOf(itemTypes);
  }

  @Override
  public TupleH newObj(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    return (TupleH) super.newObj(merkleRoot, objectHDb);
  }

  public ImmutableList<TypeH> items() {
    return itemTypes;
  }

  private static String calculateName(Iterable<? extends TypeH> itemTypes) {
    return "{" + toCommaSeparatedString(itemTypes, SpecH::name) + "}";
  }
}
