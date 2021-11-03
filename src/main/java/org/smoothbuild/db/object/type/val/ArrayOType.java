package org.smoothbuild.db.object.type.val;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static org.smoothbuild.db.object.type.base.ObjKind.ARRAY;
import static org.smoothbuild.lang.base.type.api.TypeNames.arrayTypeName;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.type.base.ValType;
import org.smoothbuild.lang.base.type.api.ArrayType;

/**
 * This class is immutable.
 */
public class ArrayOType extends ValType implements ArrayType {
  private final ValType element;

  public ArrayOType(Hash hash, ValType element) {
    super(arrayTypeName(element), hash, ARRAY, element.variables());
    this.element = requireNonNull(element);
  }

  @Override
  public ValType element() {
    return element;
  }

  @Override
  public Array newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    checkArgument(this.equals(merkleRoot.type()));
    return new Array(merkleRoot, objectDb);
  }
}
