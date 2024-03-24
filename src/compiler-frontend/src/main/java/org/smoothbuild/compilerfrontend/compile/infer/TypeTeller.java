package org.smoothbuild.compilerfrontend.compile.infer;

import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.List.pullUpMaybe;
import static org.smoothbuild.common.collect.Maybe.maybe;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;

import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.compilerfrontend.compile.ast.define.ArrayTP;
import org.smoothbuild.compilerfrontend.compile.ast.define.FuncTP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ItemP;
import org.smoothbuild.compilerfrontend.compile.ast.define.NamedEvaluableP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ScopeP;
import org.smoothbuild.compilerfrontend.compile.ast.define.StructP;
import org.smoothbuild.compilerfrontend.compile.ast.define.TypeP;
import org.smoothbuild.compilerfrontend.lang.base.TypeNamesS;
import org.smoothbuild.compilerfrontend.lang.define.ScopeS;
import org.smoothbuild.compilerfrontend.lang.type.SArrayType;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.SVar;
import org.smoothbuild.compilerfrontend.lang.type.SchemaS;

public class TypeTeller {
  private final ScopeS imported;
  private final ScopeP scopeP;

  public TypeTeller(ScopeS imported, ScopeP scopeP) {
    this.imported = imported;
    this.scopeP = scopeP;
  }

  public TypeTeller withScope(ScopeP scopeP) {
    return new TypeTeller(imported, scopeP);
  }

  public Maybe<SchemaS> schemaFor(String name) {
    return scopeP
        .referencables()
        .getMaybe(name)
        .map(r -> switch (r) {
          case NamedEvaluableP namedEvaluableP -> maybe(namedEvaluableP.schemaS());
          case ItemP itemP -> maybe(itemP.typeS()).map(t -> new SchemaS(varSetS(), t));
        })
        .getOrGet(() -> some(imported.evaluables().get(name).schema()));
  }

  public Maybe<SType> translate(TypeP type) {
    if (TypeNamesS.isVarName(type.name())) {
      return some(new SVar(type.name()));
    }
    return switch (type) {
      case ArrayTP array -> translate(array.elemT()).map(SArrayType::new);
      case FuncTP func -> {
        var resultOpt = translate(func.result());
        var paramsOpt = pullUpMaybe(func.params().map(this::translate));
        yield resultOpt.mapWith(paramsOpt, (r, p) -> new SFuncType(listOfAll(p), r));
      }
      default -> typeWithName(type);
    };
  }

  private Maybe<SType> typeWithName(TypeP type) {
    Maybe<StructP> structP = scopeP.types().getMaybe(type.name());
    if (structP.isSome()) {
      return maybe(structP.get().typeS());
    } else {
      return some(imported.types().get(type.name()).type());
    }
  }
}
