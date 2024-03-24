package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import java.util.Objects;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.collect.NList;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;

public sealed class NamedFuncP extends NamedEvaluableP implements FuncP permits ConstructorP {
  private final TypeP resultT;
  private final NList<ItemP> params;
  private SFuncType typeS;
  private SFuncSchema sFuncSchema;

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
  public TypeP evaluationType() {
    return resultT();
  }

  @Override
  public SFuncType typeS() {
    return typeS;
  }

  @Override
  public void setTypeS(SFuncType type) {
    this.typeS = type;
  }

  @Override
  public SFuncSchema schemaS() {
    return sFuncSchema;
  }

  @Override
  public void setSchemaS(SFuncSchema sFuncSchema) {
    this.sFuncSchema = sFuncSchema;
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
    var paramsString = params().list().toString("\n");
    var fields = list(
            "resulT = " + resultT,
            "name = " + name(),
            "params = [",
            indent(paramsString),
            "]",
            "body = " + body(),
            "annotation = " + annotation(),
            "location = " + location())
        .toString("\n");
    return "NamedFuncP(\n" + indent(fields) + "\n)";
  }
}
