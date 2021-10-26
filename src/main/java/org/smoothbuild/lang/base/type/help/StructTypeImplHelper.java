package org.smoothbuild.lang.base.type.help;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.util.Comparator.comparing;

import java.util.Collection;

import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.Variable;
import org.smoothbuild.util.collect.NamedList;

import com.google.common.collect.ImmutableSet;

public class StructTypeImplHelper {
  public static ImmutableSet<Variable> calculateVariables(NamedList<? extends Type> fields) {
    return fields.list().stream()
        .map(f -> f.object().variables())
        .flatMap(Collection::stream)
        .sorted(comparing(Type::name))
        .collect(toImmutableSet());
  }
}
