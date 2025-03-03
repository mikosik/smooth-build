package org.smoothbuild.compilerfrontend.lang.define;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;
import org.smoothbuild.compilerfrontend.lang.name.NList;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.STypeVar;

/**
 * Lambda.
 *
 * This class is immutable.
 */
public final class SLambda implements SExprFunc, SExpr {
  private final SFuncType funcType;
  private final Fqn fqn;
  private final NList<SItem> params;
  private final SExpr body;
  private final Location location;

  public SLambda(SType resultType, Fqn fqn, NList<SItem> params, SExpr body, Location location) {
    this.funcType = new SFuncType(params.list().map(SItem::type), resultType);
    this.fqn = fqn;
    this.params = params;
    this.body = body;
    this.location = location;
  }

  @Override
  public SFuncType type() {
    return funcType;
  }

  @Override
  public Fqn fqn() {
    return fqn;
  }

  @Override
  public NList<SItem> params() {
    return params;
  }

  @Override
  public SExpr body() {
    return body;
  }

  @Override
  public Location location() {
    return location;
  }

  @Override
  public SFuncType evaluationType() {
    return funcType;
  }

  @Override
  public String toSourceCode() {
    return params().list().map(SItem::toSourceCode).toString("(", ", ", ")") + " -> "
        + body().toSourceCode();
  }

  @Override
  public String toSourceCode(Maybe<List<STypeVar>> typeParams) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof SLambda that
        && this.funcType.equals(that.funcType)
        && this.params.equals(that.params)
        && this.body.equals(that.body)
        && this.location.equals(that.location);
  }

  @Override
  public int hashCode() {
    return Objects.hash(funcType, params, body, location);
  }

  @Override
  public String toString() {
    return new ToStringBuilder("SLambda")
        .addField("fqn", fqn)
        .addField("type", funcType)
        .addListField("params", params().list())
        .addField("location", location)
        .addField("body", body)
        .toString();
  }
}
