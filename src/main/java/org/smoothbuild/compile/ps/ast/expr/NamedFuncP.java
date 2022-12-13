package org.smoothbuild.compile.ps.ast.expr;

import java.util.Optional;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.FuncSchemaS;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.ps.ast.type.TypeP;
import org.smoothbuild.util.collect.NList;

public final class NamedFuncP extends NamedEvaluableP implements FuncP {
  private final Optional<TypeP> resT;
  private final NList<ItemP> params;
  private FuncTS type;
  private FuncSchemaS funcSchemaS;

  public NamedFuncP(
      Optional<TypeP> resT,
      String name,
      NList<ItemP> params,
      Optional<ExprP> body,
      Optional<AnnotationP> annotation,
      Loc loc) {
    super(name, body, annotation, loc);
    this.resT = resT;
    this.params = params;
  }

  @Override
  public Optional<TypeP> resT() {
    return resT;
  }

  @Override
  public NList<ItemP> params() {
    return params;
  }

  @Override
  public Optional<TypeP> evalT() {
    return resT();
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
    return funcSchemaS;
  }

  @Override
  public void setSchemaS(FuncSchemaS funcSchemaS) {
    this.funcSchemaS = funcSchemaS;
  }
}
