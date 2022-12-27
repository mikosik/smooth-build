package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.bindings.Bindings.immutableBindings;
import static org.smoothbuild.util.collect.Maps.concat;

import org.smoothbuild.util.bindings.SingleScopeBindings;
import org.smoothbuild.util.collect.Named;

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
      SingleScopeBindings<E> outer,
      SingleScopeBindings<E> inner) {
    return immutableBindings(concat(inner.toMap(), outer.toMap()));
  }
}
