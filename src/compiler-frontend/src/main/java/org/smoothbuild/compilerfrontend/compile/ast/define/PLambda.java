package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.collect.Maybe.some;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;
import org.smoothbuild.compilerfrontend.lang.name.NList;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;

public final class PLambda implements PFunc, PPolymorphic {
  private final String nameText;
  private final Location location;
  private final PImplicitType resultType;
  private final PTypeParams typeParams;
  private final NList<PItem> params;
  private final PExpr body;
  private Fqn fqn;
  private SFuncType sType;
  private SFuncSchema sSchema;
  private PScope scope;

  public PLambda(
      String nameText, PTypeParams typeParams, NList<PItem> params, PExpr body, Location location) {
    this.nameText = nameText;
    this.typeParams = typeParams;
    this.location = location;
    this.resultType = new PImplicitType(location);
    this.params = params;
    this.body = body;
  }

  @Override
  public String nameText() {
    return nameText;
  }

  @Override
  public void setFqn(Fqn fqn) {
    this.fqn = fqn;
  }

  @Override
  public Fqn fqn() {
    return fqn;
  }

  @Override
  public Location location() {
    return location;
  }

  @Override
  public PType resultType() {
    return resultType;
  }

  @Override
  public PTypeParams typeParams() {
    return typeParams;
  }

  @Override
  public NList<PItem> params() {
    return params;
  }

  @Override
  public Maybe<PExpr> body() {
    return some(body);
  }

  public PExpr bodyGet() {
    return body;
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
    return sSchema;
  }

  @Override
  public void setSchema(SFuncSchema sSchema) {
    this.sSchema = sSchema;
  }

  @Override
  public PScope scope() {
    return scope;
  }

  @Override
  public void setScope(PScope scope) {
    this.scope = scope;
  }

  @Override
  public String q() {
    return "lambda";
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof PLambda that
        && Objects.equals(this.params, that.params)
        && Objects.equals(this.body, that.body)
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(params, body, location());
  }

  @Override
  public String toString() {
    return new ToStringBuilder("PLambda")
        .addField("name", nameText())
        .addListField("params", params().list())
        .addField("body", body)
        .addField("location", location())
        .toString();
  }
}
