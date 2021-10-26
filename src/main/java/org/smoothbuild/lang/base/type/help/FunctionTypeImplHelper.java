package org.smoothbuild.lang.base.type.help;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.util.Comparator.comparing;
import static org.smoothbuild.util.collect.Lists.concat;

import java.util.Collection;

import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.Variable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class FunctionTypeImplHelper {
  public static ImmutableSet<Variable> calculateVariables(
      Type resultType, ImmutableList<? extends Type> parameters) {
    return concat(resultType, parameters).stream()
        .map(Type::variables)
        .flatMap(Collection::stream)
        .sorted(comparing(Type::name))
        .collect(toImmutableSet());
  }
}
