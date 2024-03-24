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
import org.smoothbuild.compilerfrontend.lang.base.TypeNamesS;
import org.smoothbuild.compilerfrontend.lang.define.ScopeS;
import org.smoothbuild.compilerfrontend.lang.type.SArrayType;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.SVar;
import org.smoothbuild.compilerfrontend.lang.type.SchemaS;

public class TypeTeller {
  private final ScopeS imported;
  private final PScope pScope;

  public TypeTeller(ScopeS imported, PScope pScope) {
    this.imported = imported;
    this.pScope = pScope;
  }

  public TypeTeller withScope(PScope pScope) {
    return new TypeTeller(imported, pScope);
  }

  public Maybe<SchemaS> schemaFor(String name) {
    return pScope
        .referencables()
        .getMaybe(name)
        .map(r -> switch (r) {
          case PNamedEvaluable pNamedEvaluable -> maybe(pNamedEvaluable.schemaS());
          case PItem pItem -> maybe(pItem.typeS()).map(t -> new SchemaS(varSetS(), t));
        })
        .getOrGet(() -> some(imported.evaluables().get(name).schema()));
  }

  public Maybe<SType> translate(PType type) {
    if (TypeNamesS.isVarName(type.name())) {
      return some(new SVar(type.name()));
    }
    return switch (type) {
      case PArrayType array -> translate(array.elemT()).map(SArrayType::new);
      case PFuncType func -> {
        var resultOpt = translate(func.result());
        var paramsOpt = pullUpMaybe(func.params().map(this::translate));
        yield resultOpt.mapWith(paramsOpt, (r, p) -> new SFuncType(listOfAll(p), r));
      }
      default -> typeWithName(type);
    };
  }

  private Maybe<SType> typeWithName(PType type) {
    Maybe<PStruct> structP = pScope.types().getMaybe(type.name());
    if (structP.isSome()) {
      return maybe(structP.get().typeS());
    } else {
      return some(imported.types().get(type.name()).type());
    }
  }
}
