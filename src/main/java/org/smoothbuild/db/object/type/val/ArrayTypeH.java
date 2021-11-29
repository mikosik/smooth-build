package org.smoothbuild.db.object.type.val;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.db.object.type.base.SpecKindH.ARRAY;
import static org.smoothbuild.lang.base.type.api.TypeNames.arrayTypeName;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.lang.base.type.api.ArrayType;

/**
 * This class is immutable.
 */
public class ArrayTypeH extends TypeH implements ArrayType {
  private final TypeH elem;

  public ArrayTypeH(Hash hash, TypeH elem) {
    super(arrayTypeName(elem), hash, ARRAY, elem.vars());
    this.elem = requireNonNull(elem);
  }

  @Override
  public TypeH elem() {
    return elem;
  }

  @Override
  public ArrayH newObj(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    return (ArrayH) super.newObj(merkleRoot, objectHDb);
  }
}
