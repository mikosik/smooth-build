package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.bindings.Bindings.immutableBindings;

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
    var builder = ImmutableMap.<String, E>builder();
    builder.putAll(outer.toMap());
    builder.putAll(inner.toMap());
    return immutableBindings(builder.build());
  }
}
