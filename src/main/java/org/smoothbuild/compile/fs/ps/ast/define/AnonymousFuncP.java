package org.smoothbuild.compile.fs.ps.ast.define;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Iterables.joinToString;

import java.util.Objects;
import java.util.Optional;

import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.type.FuncSchemaS;
import org.smoothbuild.compile.fs.lang.type.FuncTS;
import org.smoothbuild.util.collect.NList;

public final class AnonymousFuncP extends MonoizableP implements FuncP {
  private final NList<ItemP> params;
  private final ExprP body;
  private FuncTS typeS;
  private FuncSchemaS schemaS;
  private ScopeP scope;

  public AnonymousFuncP(NList<ItemP> params, ExprP body, Location location) {
    super(location);
    this.params = params;
    this.body = body;
  }

  @Override
  public Optional<TypeP> resT() {
    return Optional.empty();
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
    return "anonymous function";
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof AnonymousFuncP that
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
        "params = [",
        indent(joinToString(params(), "\n")),
        "]",
        "body = " + body,
        "location = " + location()
    );
    return "AnonymousFuncP(\n" + indent(fields) + "\n)";
  }
}
