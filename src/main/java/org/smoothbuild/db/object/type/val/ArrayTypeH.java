package org.smoothbuild.db.object.type.val;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.db.object.type.base.TypeKindH.ARRAY;
import static org.smoothbuild.lang.base.type.api.TypeNames.arrayTypeName;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.lang.base.type.api.ArrayType;

/**
 * This class is immutable.
 */
public class ArrayTypeH extends TypeHV implements ArrayType {
  private final TypeHV element;

  public ArrayTypeH(Hash hash, TypeHV element) {
    super(arrayTypeName(element), hash, ARRAY, element.variables());
    this.element = requireNonNull(element);
  }

  @Override
  public TypeHV element() {
    return element;
  }

  @Override
  public ArrayH newObj(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    return (ArrayH) super.newObj(merkleRoot, objectHDb);
  }
}
