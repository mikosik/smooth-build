package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.compilerfrontend.lang.name.Bindings.bindings;

import org.smoothbuild.common.collect.Map;
import org.smoothbuild.compilerfrontend.lang.base.Referenceable;
import org.smoothbuild.compilerfrontend.lang.base.TypeDefinition;
import org.smoothbuild.compilerfrontend.lang.name.Bindings;
import org.smoothbuild.compilerfrontend.lang.name.Name;

public record PScope(
    Bindings<? extends Referenceable> referenceables, Bindings<? extends TypeDefinition> types) {
  public static PScope emptyScope() {
    return new PScope(bindings(), bindings());
  }

  public PScope newInnerScope(
      Map<Name, ? extends Referenceable> innerReferenceables,
      Map<Name, ? extends PTypeDefinition> innerTypes) {
    return new PScope(bindings(referenceables, innerReferenceables), bindings(types, innerTypes));
  }
}
