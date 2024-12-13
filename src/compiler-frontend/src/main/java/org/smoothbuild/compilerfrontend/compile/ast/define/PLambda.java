package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.some;

import java.util.Objects;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.NList;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;

public final class PLambda extends PPolymorphic implements PFunc {
  private final PImplicitType resultT;
  private final NList<PItem> params;
  private final PExpr body;
  private SFuncType typeS;
  private SFuncSchema sSchema;
  private PScope scope;

  public PLambda(String nameText, NList<PItem> params, PExpr body, Location location) {
    super(nameText, location);
    this.resultT = new PImplicitType(location);
    this.params = params;
    this.body = body;
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
  public Maybe<PExpr> body() {
    return some(body);
  }

  public PExpr bodyGet() {
    return body;
  }

  @Override
  public SFuncType sType() {
    return typeS;
  }

  @Override
  public void setSType(SFuncType type) {
    this.typeS = type;
  }

  @Override
  public SFuncSchema sSchema() {
    return sSchema;
  }

  @Override
  public void setSSchema(SFuncSchema sSchema) {
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
    var fields = list(
            "name = " + nameText(),
            "params = [",
            indent(params().list().toString("\n")),
            "]",
            "body = " + body,
            "location = " + location())
        .toString("\n");
    return "PLambda(\n" + indent(fields) + "\n)";
  }
}
