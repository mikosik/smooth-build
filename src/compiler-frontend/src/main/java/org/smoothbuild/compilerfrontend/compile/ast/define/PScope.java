package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.bindings.Bindings.immutableBindings;

import org.smoothbuild.common.bindings.ImmutableBindings;

public record PScope(
    String name,
    ImmutableBindings<PReferenceable> referencables,
    ImmutableBindings<PStruct> types) {
  public static PScope emptyScope() {
    return new PScope("", immutableBindings(), immutableBindings());
  }

  @Override
  public String name() {
    return name;
  }

  public PScope newInnerScope(
      String name,
      ImmutableBindings<PReferenceable> innerReferenceables,
      ImmutableBindings<PStruct> innerTypes) {
    return new PScope(
        name,
        immutableBindings(referencables, innerReferenceables),
        immutableBindings(types, innerTypes));
  }
}
