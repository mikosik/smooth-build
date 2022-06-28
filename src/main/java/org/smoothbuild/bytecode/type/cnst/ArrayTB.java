package org.smoothbuild.bytecode.type.cnst;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.bytecode.type.CatKindB.ARRAY;
import static org.smoothbuild.bytecode.type.cnst.TNamesB.arrayTypeName;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.cnst.ArrayB;
import org.smoothbuild.db.Hash;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public final class ArrayTB extends TypeB implements ComposedTB {
  private final TypeB elem;

  public ArrayTB(Hash hash, TypeB elem) {
    super(hash, arrayTypeName(elem), ARRAY);
    this.elem = requireNonNull(elem);
  }

  public TypeB elem() {
    return elem;
  }

  @Override
  public ArrayB newObj(MerkleRoot merkleRoot, ObjDbImpl objDb) {
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
