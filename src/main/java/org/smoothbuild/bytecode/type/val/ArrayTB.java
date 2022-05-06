package org.smoothbuild.bytecode.type.val;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.bytecode.type.CatKindB.ARRAY;
import static org.smoothbuild.bytecode.type.val.TypeNamesB.arrayTypeName;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.db.Hash;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public final class ArrayTB extends TypeB implements ComposedTB {
  private final TypeB elem;

  public ArrayTB(Hash hash, TypeB elem) {
    super(hash, arrayTypeName(elem), ARRAY, elem.vars());
    this.elem = requireNonNull(elem);
  }

  public TypeB elem() {
    return elem;
  }

  @Override
  public ArrayB newObj(MerkleRoot merkleRoot, ObjDbImpl objDb) {
    validateNotPolymorphic(merkleRoot);
    return (ArrayB) super.newObj(merkleRoot, objDb);
  }

  @Override
  public ImmutableList<TypeB> covars() {
    return ImmutableList.of(elem);
  }

  @Override
  public ImmutableList<TypeB> contravars() {
    return ImmutableList.of();
  }
}
