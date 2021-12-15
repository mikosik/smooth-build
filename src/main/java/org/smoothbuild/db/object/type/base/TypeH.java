package org.smoothbuild.db.object.type.base;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.util.Comparator.comparing;

import java.util.Collection;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.val.VarH;
import org.smoothbuild.lang.base.type.api.Type;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public abstract class TypeH extends CatH {
  protected TypeH(String name, Hash hash, CatKindH kind) {
    super(name, hash, kind, ImmutableSet.of());
  }

  protected TypeH(String name, Hash hash, CatKindH kind, ImmutableSet<VarH> vars) {
    super(name, hash, kind, vars);
  }

  @Override
  public ImmutableSet<VarH> vars() {
    return (ImmutableSet<VarH>) super.vars();
  }

  @Override
  public String toString() {
    return "TypeH(`" + name() + "`)";
  }

  public static ImmutableSet<VarH> calculateVars(ImmutableList<TypeH> concat) {
    return concat.stream()
        .map(TypeH::vars)
        .flatMap(Collection::stream)
        .sorted(comparing(Type::name))
        .collect(toImmutableSet());
  }
}
