package org.smoothbuild.compile.ps.ast.expr;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Lists.joinToString;

import java.util.Optional;

import org.smoothbuild.compile.lang.base.location.Location;
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
      Location location) {
    super(name, body, annotation, location);
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

  @Override
  public String toString() {
    var fields = joinToString("\n",
        "resT = " + resT,
        "name = " + name(),
        "params = [",
        indent(joinToString(params(), "\n")),
        "]",
        "body = " + body(),
        "annotation = " + annotation(),
        "location = " + location()
    );
    return "NamedFuncP(\n" + indent(fields) + "\n)";
  }
}
