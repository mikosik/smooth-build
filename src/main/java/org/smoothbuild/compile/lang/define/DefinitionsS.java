package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.bindings.Bindings.immutableBindings;

import org.smoothbuild.util.bindings.SingleScopeBindings;
import org.smoothbuild.util.collect.Named;

import com.google.common.collect.ImmutableMap;

public record DefinitionsS(
    SingleScopeBindings<TypeDefinitionS> types,
    SingleScopeBindings<NamedEvaluableS> evaluables) {

  public static DefinitionsS empty() {
    return new DefinitionsS(immutableBindings(), immutableBindings());
  }

  public DefinitionsS withModule(ModuleS module) {
    return new DefinitionsS(
        merge(types, module.types()),
        merge(evaluables, module.evaluables())
    );
  }

  public <E extends Named> SingleScopeBindings<E> merge(
      SingleScopeBindings<? extends E> outer,
      SingleScopeBindings<? extends E> inner) {
    var builder = ImmutableMap.<String, E>builder();
    builder.putAll(outer.toMap());
    builder.putAll(inner.toMap());
    return immutableBindings(builder.build());
  }
}
