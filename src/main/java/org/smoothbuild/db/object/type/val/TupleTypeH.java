package org.smoothbuild.db.object.type.val;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.type.base.TypeKindH.TUPLE;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.base.TypeHV;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class TupleTypeH extends TypeHV {
  private final ImmutableList<TypeHV> itemTypes;

  public TupleTypeH(Hash hash, Iterable<? extends TypeHV> itemTypes) {
    super(calculateName(itemTypes), hash, TUPLE);
    this.itemTypes = ImmutableList.copyOf(itemTypes);
  }

  @Override
  public TupleH newObj(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    checkArgument(this.equals(merkleRoot.type()));
    return new TupleH(merkleRoot, objectHDb);
  }

  public ImmutableList<TypeHV> items() {
    return itemTypes;
  }

  private static String calculateName(Iterable<? extends TypeHV> itemTypes) {
    return "{" + toCommaSeparatedString(itemTypes, TypeH::name) + "}";
  }
}
