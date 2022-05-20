package org.smoothbuild.lang.type;

import java.util.List;
import java.util.function.Function;

import org.smoothbuild.util.collect.Named;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * Type (either polimorphic or monomorphic) in smooth language.
 */
public sealed interface TypeS extends Named
    permits FuncTS, MonoTS, PolyTS {

  public MonoTS mapFreeVars(Function<VarS, VarS> varMapper);

  public static ImmutableList<MonoTS> prefixFreeVarsWithIndex(List<? extends TypeS> types) {
    Builder<MonoTS> builder = ImmutableList.builder();
    for (int i = 0; i < types.size(); i++) {
      var type = types.get(i);
      var fullPrefix = Integer.toString(i);
      var prefixed = type.mapFreeVars(v -> v.prefixed(fullPrefix));
      builder.add(prefixed);
    }
    return builder.build();
  }
}
