package org.smoothbuild.compile.frontend.compile.ast.define;

import static org.smoothbuild.common.Strings.indent;
import static org.smoothbuild.common.collect.Iterables.joinToString;

import java.util.Objects;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.collect.NList;
import org.smoothbuild.compile.frontend.lang.base.location.Location;
import org.smoothbuild.compile.frontend.lang.type.FuncSchemaS;
import org.smoothbuild.compile.frontend.lang.type.FuncTS;

public sealed class NamedFuncP extends NamedEvaluableP implements FuncP permits ConstructorP {
  private final TypeP resultT;
  private final NList<ItemP> params;
  private FuncTS typeS;
  private FuncSchemaS funcSchemaS;

  public NamedFuncP(
      TypeP resultT,
      String fullName,
      String shortName,
      NList<ItemP> params,
      Maybe<ExprP> body,
      Maybe<AnnotationP> annotation,
      Location location) {
    super(fullName, shortName, body, annotation, location);
    this.resultT = resultT;
    this.params = params;
  }

  @Override
  public TypeP resultT() {
    return resultT;
  }

  @Override
  public NList<ItemP> params() {
    return params;
  }

  @Override
  public TypeP evaluationT() {
    return resultT();
  }

  @Override
  public FuncTS typeS() {
    return typeS;
  }

  @Override
  public void setTypeS(FuncTS type) {
    this.typeS = type;
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
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof NamedFuncP that
        && Objects.equals(this.resultT, that.resultT)
        && Objects.equals(this.name(), that.name())
        && Objects.equals(this.params, that.params)
        && Objects.equals(this.body(), that.body())
        && Objects.equals(this.annotation(), that.annotation())
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(resultT, name(), params, body(), annotation(), location());
  }

  @Override
  public String toString() {
    var fields = joinToString(
        "\n",
        "resulT = " + resultT,
        "name = " + name(),
        "params = [",
        indent(joinToString(params(), "\n")),
        "]",
        "body = " + body(),
        "annotation = " + annotation(),
        "location = " + location());
    return "NamedFuncP(\n" + indent(fields) + "\n)";
  }
}
