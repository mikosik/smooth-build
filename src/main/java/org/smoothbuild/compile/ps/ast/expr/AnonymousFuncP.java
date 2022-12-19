package org.smoothbuild.compile.ps.ast.expr;

import java.util.Optional;

import org.smoothbuild.compile.lang.base.location.Location;
import org.smoothbuild.compile.lang.type.FuncSchemaS;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.ps.ast.type.TypeP;
import org.smoothbuild.util.collect.NList;

public final class AnonymousFuncP extends MonoizableP implements FuncP {
  private final NList<ItemP> params;
  private final ExprP body;
  private FuncTS type;
  private FuncSchemaS schemaS;
  private ScopeP scope;

  public AnonymousFuncP(NList<ItemP> params, ExprP body, Location location) {
    super(location);
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

  @Override
  public void setSchemaS(FuncSchemaS schemaS) {
    this.schemaS = schemaS;
  }

  @Override
  public ScopeP scope() {
    return scope;
  }

  @Override
  public void setScope(ScopeP scope) {
    this.scope = scope;
  }

  @Override
  public String q() {
    return "anonymous function";
  }
}
