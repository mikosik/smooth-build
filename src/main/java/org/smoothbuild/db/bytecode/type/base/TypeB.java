package org.smoothbuild.db.bytecode.type.base;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.util.Comparator.comparing;

import java.util.Collection;

import org.smoothbuild.db.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.db.bytecode.obj.exc.DecodeObjIllegalPolymorphicTypeExc;
import org.smoothbuild.db.bytecode.type.val.VarB;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.type.api.Type;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public abstract class TypeB extends CatB {
  protected TypeB(String name, Hash hash, CatKindB kind) {
    super(name, hash, kind, ImmutableSet.of());
  }

  protected TypeB(String name, Hash hash, CatKindB kind, ImmutableSet<VarB> vars) {
    super(name, hash, kind, vars);
  }

  @Override
  public ImmutableSet<VarB> vars() {
    return (ImmutableSet<VarB>) super.vars();
  }

  @Override
  public String toString() {
    return "TypeB(`" + name() + "`)";
  }

  public static ImmutableSet<VarB> calculateVars(ImmutableList<TypeB> concat) {
    return concat.stream()
        .map(TypeB::vars)
        .flatMap(Collection::stream)
        .sorted(comparing(Type::name))
        .collect(toImmutableSet());
  }

  protected static void validateNotPolymorphic(MerkleRoot merkleRoot) {
    if (merkleRoot.cat().isPolytype()) {
      throw new DecodeObjIllegalPolymorphicTypeExc(merkleRoot.hash(), merkleRoot.cat());
    }
  }
}
