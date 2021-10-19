package org.smoothbuild.lang.base.type.help;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.util.Comparator.comparing;

import java.util.Collection;

import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.Variable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;

public class StructTypeImplHelper {
  public static ImmutableSet<Variable> calculateVariables(ImmutableList<? extends Type> fields) {
    return fields.stream()
        .map(Type::variables)
        .flatMap(Collection::stream)
        .sorted(comparing(Type::name))
        .collect(toImmutableSet());
  }

  public static ImmutableMap<String, Integer> fieldsMap(ImmutableList<String> names) {
    Builder<String, Integer> builder = ImmutableMap.builder();
    for (int i = 0; i < names.size(); i++) {
      String name = names.get(i);
      if (!name.isEmpty()) {
        builder.put(name, i);
      }
    }
    return builder.build();
  }
}
