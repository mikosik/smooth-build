package org.smoothbuild.compilerfrontend.compile.ast.define;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.NamedFunc;
import org.smoothbuild.compilerfrontend.lang.name.NList;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;

public sealed class PNamedFunc extends PNamedEvaluable implements NamedFunc, PFunc
    permits PConstructor {
  private final PType resultType;
  private final NList<PItem> params;
  private SFuncType sType;
  private SFuncSchema sFuncSchema;

  public PNamedFunc(
      PType resultType,
      String nameText,
      PTypeParams typeParams,
      NList<PItem> params,
      Maybe<PExpr> body,
      Maybe<PAnnotation> annotation,
      Location location) {
    super(nameText, typeParams, body, annotation, location);
    this.resultType = resultType;
    this.params = params;
  }

  @Override
  public PType resultType() {
    return resultType;
  }

  @Override
  public NList<PItem> params() {
    return params;
  }

  @Override
  public PType evaluationType() {
    return resultType();
  }

  @Override
  public SFuncType sType() {
    return sType;
  }

  @Override
  public void setSType(SFuncType type) {
    this.sType = type;
  }

  @Override
  public SFuncSchema schema() {
    return sFuncSchema;
  }

  @Override
  public void setSchema(SFuncSchema sFuncSchema) {
    this.sFuncSchema = sFuncSchema;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof PNamedFunc that
        && Objects.equals(this.resultType, that.resultType)
        && Objects.equals(this.fqn(), that.fqn())
        && Objects.equals(this.params, that.params)
        && Objects.equals(this.body(), that.body())
        && Objects.equals(this.annotation(), that.annotation())
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(resultType, this.fqn(), params, body(), annotation(), location());
  }

  @Override
  public String toString() {
    return new ToStringBuilder("PNamedFuncP")
        .addField("resulT", resultType)
        .addField("fqn", fqn())
        .addListField("params", params().list())
        .addField("body", body())
        .addField("annotation", annotation())
        .addField("location", location())
        .toString();
  }
}
