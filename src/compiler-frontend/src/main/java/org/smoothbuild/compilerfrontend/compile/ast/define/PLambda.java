package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.some;

import java.util.Objects;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.collect.NList;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;

public final class PLambda extends PPolymorphic implements PFunc {
  private final PImplicitType resultT;
  private final NList<PItem> params;
  private final PExpr body;
  private final String name;
  private SFuncType typeS;
  private SFuncSchema schemaS;
  private PScope scope;

  public PLambda(String name, NList<PItem> params, PExpr body, Location location) {
    super(location);
    this.resultT = new PImplicitType(location);
    this.name = name;
    this.params = params;
    this.body = body;
  }

  @Override
  public PType resultT() {
    return resultT;
  }

  @Override
  public String name() {
    return name;
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
  public SFuncType typeS() {
    return typeS;
  }

  @Override
  public void setTypeS(SFuncType type) {
    this.typeS = type;
  }

  @Override
  public SFuncSchema schemaS() {
    return schemaS;
  }

  @Override
  public void setSchemaS(SFuncSchema schemaS) {
    this.schemaS = schemaS;
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
            "name = " + name,
            "params = [",
            indent(params().list().toString("\n")),
            "]",
            "body = " + body,
            "location = " + location())
        .toString("\n");
    return "LambdaP(\n" + indent(fields) + "\n)";
  }
}