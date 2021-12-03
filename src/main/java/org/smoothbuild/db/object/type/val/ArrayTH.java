package org.smoothbuild.db.object.type.val;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.db.object.type.base.CatKindH.ARRAY;
import static org.smoothbuild.lang.base.type.api.TypeNames.arrayTypeName;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.lang.base.type.api.ArrayT;

/**
 * This class is immutable.
 */
public class ArrayTH extends TypeH implements ArrayT {
  private final TypeH elem;

  public ArrayTH(Hash hash, TypeH elem) {
    super(arrayTypeName(elem), hash, ARRAY, elem.vars());
    this.elem = requireNonNull(elem);
  }

  @Override
  public TypeH elem() {
    return elem;
  }

  @Override
  public ArrayH newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    return (ArrayH) super.newObj(merkleRoot, objDb);
  }
}
