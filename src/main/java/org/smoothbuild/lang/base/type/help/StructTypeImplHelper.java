package org.smoothbuild.lang.base.type.help;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.util.Comparator.comparing;

import java.util.Collection;
import java.util.Optional;

import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.Variable;
import org.smoothbuild.util.collect.Named;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;

public class StructTypeImplHelper {
  public static ImmutableSet<Variable> calculateVariables(
      ImmutableList<? extends Named<? extends Type>> fields) {
    return fields.stream()
        .map(f -> f.object().variables())
        .flatMap(Collection::stream)
        .sorted(comparing(Type::name))
        .collect(toImmutableSet());
  }

  public static ImmutableMap<String, Integer> fieldsMap(ImmutableList<Optional<String>> names) {
    Builder<String, Integer> builder = ImmutableMap.builder();
    for (int i = 0; i < names.size(); i++) {
      int index = i;
      names.get(i).ifPresent(n -> builder.put(n, index));
    }
    return builder.build();
  }
}
