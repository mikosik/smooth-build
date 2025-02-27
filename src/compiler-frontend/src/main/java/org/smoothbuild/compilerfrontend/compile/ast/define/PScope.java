package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.compilerfrontend.lang.name.Bindings.bindings;

import org.smoothbuild.common.collect.Map;
import org.smoothbuild.compilerfrontend.lang.base.Referenceable;
import org.smoothbuild.compilerfrontend.lang.base.TypeDefinition;
import org.smoothbuild.compilerfrontend.lang.name.Bindings;
import org.smoothbuild.compilerfrontend.lang.name.Name;
import org.smoothbuild.compilerfrontend.lang.type.SArrayType;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;
import org.smoothbuild.compilerfrontend.lang.type.SType;

public record PScope(
    Bindings<? extends Referenceable> referencables, Bindings<? extends TypeDefinition> types) {
  public static PScope emptyScope() {
    return new PScope(bindings(), bindings());
  }

  public PScope newInnerScope(
      Map<Name, ? extends Referenceable> innerReferenceables,
      Map<Name, ? extends PTypeDefinition> innerTypes) {
    return new PScope(bindings(referencables, innerReferenceables), bindings(types, innerTypes));
  }

  public SType translate(PType type) {
    return switch (type) {
      case PArrayType a -> new SArrayType(translate(a.elemT()));
      case PFuncType f -> new SFuncType(f.params().map(this::translate), translate(f.result()));
      case PTypeReference r -> r.referenced().type();
      case PImplicitType i -> throw new RuntimeException(
          "Internal error: Did not expect implicit type.");
    };
  }
}
