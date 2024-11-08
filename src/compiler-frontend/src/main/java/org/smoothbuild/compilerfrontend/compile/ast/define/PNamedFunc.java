package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import java.util.Objects;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.collect.NList;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;

public sealed class PNamedFunc extends PNamedEvaluable implements PFunc permits PConstructor {
  private final PType resultT;
  private final NList<PItem> params;
  private SFuncType typeS;
  private SFuncSchema sFuncSchema;

  public PNamedFunc(
      PType resultT,
      String fullName,
      String shortName,
      NList<PItem> params,
      Maybe<PExpr> body,
      Maybe<PAnnotation> annotation,
      Location location) {
    super(fullName, shortName, body, annotation, location);
    this.resultT = resultT;
    this.params = params;
  }

  @Override
  public PType resultT() {
    return resultT;
  }

  @Override
  public NList<PItem> params() {
    return params;
  }

  @Override
  public PType evaluationType() {
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
    return object instanceof PNamedFunc that
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
