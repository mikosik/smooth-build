package org.smoothbuild.compile.fs.ps.infer2;

import static org.smoothbuild.compile.fs.lang.type.VarSetS.varSetS;
import static org.smoothbuild.util.collect.Optionals.pullUp;

import java.util.Optional;

import org.smoothbuild.compile.fs.lang.define.ScopeS;
import org.smoothbuild.compile.fs.lang.type.SchemaS;
import org.smoothbuild.compile.fs.lang.type.TypeS;
import org.smoothbuild.compile.fs.ps.ast.define.ItemP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedEvaluableP;
import org.smoothbuild.compile.fs.ps.ast.define.ScopeP;
import org.smoothbuild.compile.fs.ps.ast.define.StructP;

public class TypeTeller2 {
  private final ScopeS imported;
  private final ScopeP scopeP;

  public TypeTeller2(ScopeS imported, ScopeP scopeP) {
    this.imported = imported;
    this.scopeP = scopeP;
  }

  public TypeTeller2 withScope(ScopeP scopeP) {
    return new TypeTeller2(imported, scopeP);
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

  public TypeS typeWithName(String name) {
    Optional<StructP> structP = scopeP.types().getOptional(name);
    if (structP.isPresent()) {
      return structP.get().unifierType();
    } else {
      return imported.types().get(name).type();
    }
  }
}
