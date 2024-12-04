package org.smoothbuild.compilerfrontend.compile.infer;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;

import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.compilerfrontend.compile.ast.define.PArrayType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PFuncType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PItem;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedEvaluable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PScope;
import org.smoothbuild.compilerfrontend.compile.ast.define.PStruct;
import org.smoothbuild.compilerfrontend.compile.ast.define.PType;
import org.smoothbuild.compilerfrontend.lang.base.STypeNames;
import org.smoothbuild.compilerfrontend.lang.define.SScope;
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

  public SSchema schemaFor(String name) {
    return currentScope
        .referencables()
        .getMaybe(name)
        .map(r -> switch (r) {
          case PNamedEvaluable pNamedEvaluable -> pNamedEvaluable.sSchema();
          case PItem pItem -> new SSchema(varSetS(), requireNonNull(pItem.sType()));
        })
        .getOrGet(() -> imported.evaluables().get(name).schema());
  }

  public SType translate(PType type) {
    if (STypeNames.isVarName(type.name())) {
      return new SVar(type.name());
    }
    return switch (type) {
      case PArrayType a -> new SArrayType(translate(a.elemT()));
      case PFuncType f -> new SFuncType(f.params().map(this::translate), translate(f.result()));
      default -> typeWithName(type.name());
    };
  }

  private SType typeWithName(String typeName) {
    Maybe<PStruct> structP = currentScope.types().getMaybe(typeName);
    if (structP.isSome()) {
      return requireNonNull(structP.get().sType());
    } else {
      return imported.types().get(typeName).type();
    }
  }
}
