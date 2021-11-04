package org.smoothbuild.db.object.type.val;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static org.smoothbuild.db.object.type.base.ObjKind.ARRAY;
import static org.smoothbuild.lang.base.type.api.TypeNames.arrayTypeName;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.type.base.TypeV;
import org.smoothbuild.lang.base.type.api.ArrayType;

/**
 * This class is immutable.
 */
public class ArrayTypeO extends TypeV implements ArrayType {
  private final TypeV element;

  public ArrayTypeO(Hash hash, TypeV element) {
    super(arrayTypeName(element), hash, ARRAY, element.variables());
    this.element = requireNonNull(element);
  }

  @Override
  public TypeV element() {
    return element;
  }

  @Override
  public Array newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    checkArgument(this.equals(merkleRoot.type()));
    return new Array(merkleRoot, objDb);
  }
}
