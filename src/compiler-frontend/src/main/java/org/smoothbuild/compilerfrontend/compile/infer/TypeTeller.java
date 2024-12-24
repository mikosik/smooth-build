package org.smoothbuild.compilerfrontend.compile.infer;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.compilerfrontend.lang.name.TokenNames.isTypeVarName;
import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;

import org.smoothbuild.compilerfrontend.compile.ast.define.PArrayType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PFuncType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PIdType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PImplicitType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PItem;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedEvaluable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PScope;
import org.smoothbuild.compilerfrontend.compile.ast.define.PType;
import org.smoothbuild.compilerfrontend.lang.define.SScope;
import org.smoothbuild.compilerfrontend.lang.name.Id;
import org.smoothbuild.compilerfrontend.lang.type.SArrayType;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.SVar;

public class TypeTeller {
  private final SScope imported;
  private final PScope currentScope;

  public TypeTeller(SScope imported, PScope currentScope) {
    this.imported = imported;
    this.currentScope = currentScope;
  }

  public TypeTeller withScope(PScope pScope) {
    return new TypeTeller(imported, pScope);
  }

  public SSchema schemaFor(Id id) {
    var idString = id.toString();
    var sSchema = currentScope.referencables().getMaybe(idString).map(r -> switch (r) {
      case PNamedEvaluable pNamedEvaluable -> pNamedEvaluable.sSchema();
      case PItem pItem -> new SSchema(varSetS(), requireNonNull(pItem.sType()));
    });
    return sSchema.getOrGet(() -> importedSchemaFor(id));
  }

  private SSchema importedSchemaFor(Id id) {
    var sNamedEvaluable = imported.evaluables().find(id);
    if (sNamedEvaluable.isRight()) {
      return sNamedEvaluable.right().schema();
    } else {
      throw new RuntimeException("Internal error: " + sNamedEvaluable.left());
    }
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
    var structP = currentScope.types().find(id);
    if (structP.isRight()) {
      return requireNonNull(structP.right().sType());
    } else {
      var sTypeDefinition = imported.types().find(id);
      if (sTypeDefinition.isRight()) {
        return sTypeDefinition.right().type();
      } else {
        throw new RuntimeException("Internal error: " + sTypeDefinition.left());
      }
    }
  }
}
