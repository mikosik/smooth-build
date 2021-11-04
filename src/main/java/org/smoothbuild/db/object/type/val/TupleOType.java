package org.smoothbuild.db.object.type.val;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.type.base.ObjKind.TUPLE;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.Tuple;
import org.smoothbuild.db.object.type.base.ObjType;
import org.smoothbuild.db.object.type.base.ValType;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class TupleOType extends ValType {
  private final ImmutableList<ValType> itemTypes;

  public TupleOType(Hash hash, Iterable<? extends ValType> itemTypes) {
    super(calculateName(itemTypes), hash, TUPLE);
    this.itemTypes = ImmutableList.copyOf(itemTypes);
  }

  @Override
  public Tuple newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    checkArgument(this.equals(merkleRoot.type()));
    return new Tuple(merkleRoot, objDb);
  }

  public ImmutableList<ValType> items() {
    return itemTypes;
  }

  private static String calculateName(Iterable<? extends ValType> itemTypes) {
    return "{" + toCommaSeparatedString(itemTypes, ObjType::name) + "}";
  }
}
