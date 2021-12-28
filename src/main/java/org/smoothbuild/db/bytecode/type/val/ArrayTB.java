package org.smoothbuild.db.bytecode.type.val;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.db.bytecode.type.base.CatKindB.ARRAY;
import static org.smoothbuild.lang.base.type.api.TypeNames.arrayTypeName;

import org.smoothbuild.db.bytecode.obj.ByteDbImpl;
import org.smoothbuild.db.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.db.bytecode.obj.val.ArrayB;
import org.smoothbuild.db.bytecode.type.base.TypeB;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.type.api.ArrayT;

/**
 * This class is immutable.
 */
public class ArrayTB extends TypeB implements ArrayT {
  private final TypeB elem;

  public ArrayTB(Hash hash, TypeB elem) {
    super(arrayTypeName(elem), hash, ARRAY, elem.vars());
    this.elem = requireNonNull(elem);
  }

  @Override
  public TypeB elem() {
    return elem;
  }

  @Override
  public ArrayB newObj(MerkleRoot merkleRoot, ByteDbImpl byteDb) {
    validateNotPolymorphic(merkleRoot);
    return (ArrayB) super.newObj(merkleRoot, byteDb);
  }
}
