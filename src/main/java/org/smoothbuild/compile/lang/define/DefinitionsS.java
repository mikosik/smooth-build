package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.bindings.ImmutableBindings.immutableBindings;

import java.util.HashMap;

import org.smoothbuild.util.bindings.Bindings;
import org.smoothbuild.util.bindings.ImmutableBindings;
import org.smoothbuild.util.collect.Named;

import com.google.common.collect.ImmutableMap;

public record DefinitionsS(
    ImmutableBindings<TypeDefinitionS> types,
    ImmutableBindings<NamedEvaluableS> evaluables) {

  public static DefinitionsS empty() {
    return new DefinitionsS(immutableBindings(), immutableBindings());
  }

  public DefinitionsS withModule(ModuleS module) {
    return new DefinitionsS(
        merge(types, module.types()),
        merge(evaluables, module.evaluables())
    );
  }

  public <E extends Named> ImmutableBindings<E> merge(
      Bindings<E> outer, Bindings<? extends E> inner) {
    var map = new HashMap<String, E>();
    map.putAll(outer.asMap());
    map.putAll(inner.asMap());
    return immutableBindings(ImmutableMap.copyOf(map));
  }
}
