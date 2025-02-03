package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.compilerfrontend.lang.bindings.Bindings.immutableBindings;
import static org.smoothbuild.compilerfrontend.lang.name.TokenNames.isTypeVarName;

import org.smoothbuild.compilerfrontend.lang.base.Referenceable;
import org.smoothbuild.compilerfrontend.lang.base.TypeDefinition;
import org.smoothbuild.compilerfrontend.lang.bindings.ImmutableBindings;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;
import org.smoothbuild.compilerfrontend.lang.name.Id;
import org.smoothbuild.compilerfrontend.lang.type.SArrayType;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.SVar;

public record PScope(
    ImmutableBindings<? extends Referenceable> referencables,
    ImmutableBindings<? extends TypeDefinition> types) {
  public static PScope emptyScope() {
    return new PScope(immutableBindings(), immutableBindings());
  }

  public PScope newInnerScope(
      ImmutableBindings<? extends Referenceable> innerReferenceables,
      ImmutableBindings<PStruct> innerTypes) {
    return new PScope(
        immutableBindings(referencables, innerReferenceables),
        immutableBindings(types, innerTypes));
  }

  public SSchema schemaFor(Id id) {
    return referencables
        .find(id)
        .mapOk(Referenceable::schema)
        .okOrThrow(e -> new RuntimeException("Internal error: " + e));
  }

  public SType translate(PType type) {
    return switch (type) {
      case PArrayType a -> new SArrayType(translate(a.elemT()));
      case PFuncType f -> new SFuncType(f.params().map(this::translate), translate(f.result()));
      case PTypeReference r -> typeByReference(r.fqn());
      case PImplicitType i -> throw new RuntimeException(
          "Internal error: Did not expect implicit type.");
    };
  }

  private SType typeByReference(Fqn fqn) {
    if (isTypeVarName(fqn.parts().getLast().toString())) {
      return new SVar(fqn);
    }
    return types
        .find(fqn)
        .mapOk(TypeDefinition::type)
        .okOrThrow(e -> new RuntimeException("Internal error: " + e));
  }
}
