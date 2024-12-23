package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.compilerfrontend.lang.bindings.Bindings.immutableBindings;

import org.smoothbuild.compilerfrontend.lang.bindings.ImmutableBindings;

public record PScope(
    ImmutableBindings<PReferenceable> referencables, ImmutableBindings<PStruct> types) {
  public static PScope emptyScope() {
    return new PScope(immutableBindings(), immutableBindings());
  }

  public PScope newInnerScope(
      ImmutableBindings<PReferenceable> innerReferenceables,
      ImmutableBindings<PStruct> innerTypes) {
    return new PScope(
        immutableBindings(referencables, innerReferenceables),
        immutableBindings(types, innerTypes));
  }
}
