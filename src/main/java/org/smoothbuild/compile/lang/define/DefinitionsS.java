package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.bindings.ImmutableBindings.immutableBindings;

import java.util.HashMap;

import org.smoothbuild.util.bindings.FlatBindings;
import org.smoothbuild.util.collect.Named;

import com.google.common.collect.ImmutableMap;

public record DefinitionsS(
    FlatBindings<TypeDefinitionS> types,
    FlatBindings<NamedEvaluableS> evaluables) {

  public static DefinitionsS empty() {
    return new DefinitionsS(immutableBindings(), immutableBindings());
  }

  public DefinitionsS withModule(ModuleS module) {
    return new DefinitionsS(
        merge(types, module.types()),
        merge(evaluables, module.evaluables())
    );
  }

  public <E extends Named> FlatBindings<E> merge(
      FlatBindings<E> outer, FlatBindings<? extends E> inner) {
    var map = new HashMap<String, E>();
    map.putAll(outer.toMap());
    map.putAll(inner.toMap());
    return immutableBindings(ImmutableMap.copyOf(map));
  }
}
