package org.smoothbuild.compile.fs.ps.infer;

import static org.smoothbuild.compile.fs.lang.type.VarSetS.varSetS;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Optionals.mapPair;
import static org.smoothbuild.util.collect.Optionals.pullUp;

import java.util.Optional;

import org.smoothbuild.compile.fs.lang.base.TypeNamesS;
import org.smoothbuild.compile.fs.lang.define.ScopeS;
import org.smoothbuild.compile.fs.lang.type.ArrayTS;
import org.smoothbuild.compile.fs.lang.type.FuncTS;
import org.smoothbuild.compile.fs.lang.type.SchemaS;
import org.smoothbuild.compile.fs.lang.type.TypeS;
import org.smoothbuild.compile.fs.lang.type.VarS;
import org.smoothbuild.compile.fs.ps.ast.define.ArrayTP;
import org.smoothbuild.compile.fs.ps.ast.define.FuncTP;
import org.smoothbuild.compile.fs.ps.ast.define.ItemP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedEvaluableP;
import org.smoothbuild.compile.fs.ps.ast.define.ScopeP;
import org.smoothbuild.compile.fs.ps.ast.define.StructP;
import org.smoothbuild.compile.fs.ps.ast.define.TypeP;

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

  public Optional<SchemaS> schemaFor(String name) {
    return scopeP.referencables().getOptional(name)
        .map(r -> switch (r) {
          case NamedEvaluableP namedEvaluableP -> Optional.ofNullable(namedEvaluableP.schemaS());
          case ItemP itemP ->
              Optional.ofNullable(itemP.typeS()).map(t -> new SchemaS(varSetS(), t));
        })
        .orElseGet(() -> Optional.of(imported.evaluables().get(name).schema()));
  }

  public Optional<TypeS> translate(TypeP type) {
    if (TypeNamesS.isVarName(type.name())) {
      return Optional.of(new VarS(type.name()));
    }
    return switch (type) {
      case ArrayTP array -> translate(array.elemT()).map(ArrayTS::new);
      case FuncTP func -> {
        var resultOpt = translate(func.result());
        var paramsOpt = pullUp(map(func.params(), this::translate));
        yield mapPair(resultOpt, paramsOpt, (r, p) -> new FuncTS(p, r));
      }
      default -> typeWithName(type);
    };
  }

  private Optional<TypeS> typeWithName(TypeP type) {
    Optional<StructP> structP = scopeP.types().getOptional(type.name());
    if (structP.isPresent()) {
      return Optional.ofNullable(structP.get().typeS());
    } else {
      return Optional.of(imported.types().get(type.name()).type());
    }
  }
}
