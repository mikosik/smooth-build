package org.smoothbuild.compilerfrontend.compile.infer;

import static org.smoothbuild.compilerfrontend.lang.name.TokenNames.isTypeVarName;

import org.smoothbuild.compilerfrontend.compile.ast.define.PArrayType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PFuncType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PIdType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PImplicitType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PItem;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedEvaluable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PScope;
import org.smoothbuild.compilerfrontend.compile.ast.define.PType;
import org.smoothbuild.compilerfrontend.lang.base.Referenceable;
import org.smoothbuild.compilerfrontend.lang.base.TypeDefinition;
import org.smoothbuild.compilerfrontend.lang.define.SReferenceable;
import org.smoothbuild.compilerfrontend.lang.name.Id;
import org.smoothbuild.compilerfrontend.lang.type.SArrayType;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.SVar;

public class TypeFinder {
  private final PScope scope;

  public TypeFinder(PScope scope) {
    this.scope = scope;
  }

  public SSchema schemaFor(Id id) {
    return scope
        .referencables()
        .find(id)
        .mapRight(TypeFinder::schemaFor)
        .rightOrThrow(e -> new RuntimeException("Internal error: " + e));
  }

  private static SSchema schemaFor(Referenceable referenceable) {
    return switch (referenceable) {
      case PNamedEvaluable pNamedEvaluable -> pNamedEvaluable.schema();
      case PItem pItem -> pItem.schema();
      case SReferenceable sReferenceable -> sReferenceable.schema();
        // TODO this won't be needed once we introduce java modules and make Referenceable class
        // sealed
      case Referenceable p -> throw new RuntimeException();
    };
  }

  public SType translate(PType type) {
    if (isTypeVarName(type.nameText())) {
      return new SVar(type.nameText());
    }
    return switch (type) {
      case PArrayType a -> new SArrayType(translate(a.elemT()));
      case PFuncType f -> new SFuncType(f.params().map(this::translate), translate(f.result()));
      case PIdType i -> typeWithId(i.id());
      case PImplicitType im -> throw new RuntimeException(
          "Internal error: Did not expect implicit type.");
    };
  }

  private SType typeWithId(Id id) {
    return scope
        .types()
        .find(id)
        .mapRight(TypeDefinition::type)
        .rightOrThrow(e -> new RuntimeException("Internal error: " + e));
  }
}
