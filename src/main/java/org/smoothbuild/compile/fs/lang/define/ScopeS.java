package org.smoothbuild.compile.fs.lang.define;

import org.smoothbuild.util.bindings.Bindings;
import org.smoothbuild.util.bindings.ImmutableBindings;

public record ScopeS(
  ImmutableBindings<TypeDefinitionS> types,
  ImmutableBindings<NamedEvaluableS> evaluables) {

  public static ScopeS override(ScopeS overriding, ScopeS overriden) {
    return new ScopeS(
        Bindings.override(overriding.types(), overriden.types()),
        Bindings.override(overriding.evaluables(), overriden.evaluables())
    );
  }
}
