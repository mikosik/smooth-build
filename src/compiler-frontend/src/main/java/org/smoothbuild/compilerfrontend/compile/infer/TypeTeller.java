package org.smoothbuild.compilerfrontend.compile.infer;

import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.List.pullUpMaybe;
import static org.smoothbuild.common.collect.Maybe.maybe;
import static org.smoothbuild.common.collect.Maybe.some;
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
  private final PScope pScope;

  public TypeTeller(SScope imported, PScope pScope) {
    this.imported = imported;
    this.pScope = pScope;
  }

  public TypeTeller withScope(PScope pScope) {
    return new TypeTeller(imported, pScope);
  }

  public Maybe<SSchema> schemaFor(String name) {
    return pScope
        .referencables()
        .getMaybe(name)
        .map(r -> switch (r) {
          case PNamedEvaluable pNamedEvaluable -> maybe(pNamedEvaluable.sSchema());
          case PItem pItem -> maybe(pItem.sType()).map(t -> new SSchema(varSetS(), t));
        })
        .getOrGet(() -> some(imported.evaluables().get(name).schema()));
  }

  public Maybe<SType> translate(PType type) {
    if (STypeNames.isVarName(type.name())) {
      return some(new SVar(type.name()));
    }
    return switch (type) {
      case PArrayType array -> translate(array.elemT()).map(SArrayType::new);
      case PFuncType func -> {
        var resultOpt = translate(func.result());
        var paramsOpt = pullUpMaybe(func.params().map(this::translate));
        yield resultOpt.mapWith(paramsOpt, (r, p) -> new SFuncType(listOfAll(p), r));
      }
      default -> typeWithName(type.name());
    };
  }

  private Maybe<SType> typeWithName(String typeName) {
    Maybe<PStruct> structP = pScope.types().getMaybe(typeName);
    if (structP.isSome()) {
      return maybe(structP.get().sType());
    } else {
      return some(imported.types().get(typeName).type());
    }
  }
}
