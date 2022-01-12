package org.smoothbuild.bytecode.type.val;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.bytecode.type.base.CatKindB.ARRAY;
import static org.smoothbuild.lang.base.type.api.TypeNames.arrayTypeName;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.db.Hash;
import org.smoothbuild.lang.base.type.api.ArrayT;
import org.smoothbuild.lang.base.type.api.Type;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class ArrayTB extends TypeB implements ArrayT {
  private final TypeB elem;

  public ArrayTB(Hash hash, TypeB elem) {
    super(arrayTypeName(elem), hash, ARRAY, elem.hasOpenVars(), elem.hasClosedVars());
    this.elem = requireNonNull(elem);
  }

  @Override
  public TypeB elem() {
    return elem;
  }

  @Override
  public ArrayB newObj(MerkleRoot merkleRoot, ObjDbImpl byteDb) {
    validateNotPolymorphic(merkleRoot);
    return (ArrayB) super.newObj(merkleRoot, byteDb);
  }

  @Override
  public ImmutableList<Type> covars() {
    return ImmutableList.of(elem);
  }

  @Override
  public ImmutableList<Type> contravars() {
    return ImmutableList.of();
  }
}
