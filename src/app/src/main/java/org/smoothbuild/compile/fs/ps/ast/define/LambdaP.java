package org.smoothbuild.compile.fs.ps.ast.define;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Iterables.joinToString;

import java.util.Objects;
import java.util.Optional;

import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.type.FuncSchemaS;
import org.smoothbuild.compile.fs.lang.type.FuncTS;
import org.smoothbuild.util.collect.NList;

public final class LambdaP extends PolymorphicP implements FuncP {
  private final ImplicitTP resultT;
  private final NList<ItemP> params;
  private final ExprP body;
  private final String name;
  private FuncTS typeS;
  private FuncSchemaS schemaS;
  private ScopeP scope;

  public LambdaP(String name, NList<ItemP> params, ExprP body, Location location) {
    super(location);
    this.resultT = new ImplicitTP(location);
    this.name = name;
    this.params = params;
    this.body = body;
  }

  @Override
  public TypeP resultT() {
    return resultT;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public NList<ItemP> params() {
    return params;
  }

  @Override
  public Optional<ExprP> body() {
    return Optional.of(body);
  }

  public ExprP bodyGet() {
    return body;
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
    return schemaS;
  }

  @Override
  public void setSchemaS(FuncSchemaS schemaS) {
    this.schemaS = schemaS;
  }

  @Override
  public ScopeP scope() {
    return scope;
  }

  @Override
  public void setScope(ScopeP scope) {
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
    return object instanceof LambdaP that
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
    var fields = joinToString("\n",
        "name = " + name,
        "params = [",
        indent(joinToString(params(), "\n")),
        "]",
        "body = " + body,
        "location = " + location()
    );
    return "LambdaP(\n" + indent(fields) + "\n)";
  }
}
