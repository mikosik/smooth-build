package org.smoothbuild.compile.ps.ast.refable;

import java.util.Optional;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.SchemaS;
import org.smoothbuild.compile.ps.ast.AnnP;
import org.smoothbuild.compile.ps.ast.expr.ExprP;
import org.smoothbuild.compile.ps.ast.type.TypeP;

public final class NamedValueP extends NamedEvaluableP {
  private final Optional<TypeP> type;
  private SchemaS schemaS;

  public NamedValueP(Optional<TypeP> type, String name, Optional<ExprP> body,
      Optional<AnnP> annotation, Loc loc) {
    super(name, body, annotation, loc);
    this.type = type;
  }

  public Optional<TypeP> type() {
    return type;
  }

  @Override
  public Optional<TypeP> evalT() {
    return type();
  }

  public void setSchemaS(SchemaS schemaS) {
    this.schemaS = schemaS;
  }

  @Override
  public SchemaS schemaS() {
    return schemaS;
  }
}
