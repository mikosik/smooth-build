package org.smoothbuild.compile.ps.ast.expr;

import java.util.Optional;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.FuncSchemaS;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.ps.ast.refable.FuncP;
import org.smoothbuild.compile.ps.ast.refable.ItemP;
import org.smoothbuild.compile.ps.ast.type.TypeP;
import org.smoothbuild.util.collect.NList;

public final class AnonFuncP extends MonoizableP implements FuncP {
  private final NList<ItemP> params;
  private final ExprP body;
  private FuncTS type;
  private FuncSchemaS schemaS;

  public AnonFuncP(NList<ItemP> params, ExprP body, Loc loc) {
    super(loc);
    this.params = params;
    this.body = body;
  }

  @Override
  public Optional<TypeP> resT() {
    return Optional.empty();
  }

  @Override
  public NList<ItemP> params() {
    return params;
  }

  @Override
  public Optional<ExprP> body() {
    return Optional.of(body);
  }

  public ExprP bodyGet() {
    return body;
  }

  @Override
  public FuncTS typeS() {
    return type;
  }

  @Override
  public void setTypeS(FuncTS type) {
    this.type = type;
  }

  @Override
  public FuncSchemaS schemaS() {
    return schemaS;
  }

  public void setSchemaS(FuncSchemaS schemaS) {
    this.schemaS = schemaS;
  }

  @Override
  public String q() {
    return "anonymous function";
  }
}
