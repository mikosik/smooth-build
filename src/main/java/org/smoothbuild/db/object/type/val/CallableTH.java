package org.smoothbuild.db.object.type.val;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.util.Comparator.comparing;
import static org.smoothbuild.util.collect.Lists.concat;

import java.util.Collection;

import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.lang.base.type.api.Type;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public interface CallableTH {
  public TypeH res();

  public ImmutableList<TypeH> params();

  public TupleTH paramsTuple();

  static ImmutableSet<VarH> calculateVars(TypeH resT, ImmutableList<TypeH> params) {
    return concat(resT, params).stream()
        .map(TypeH::vars)
        .flatMap(Collection::stream)
        .sorted(comparing(Type::name))
        .collect(toImmutableSet());
  }
}
