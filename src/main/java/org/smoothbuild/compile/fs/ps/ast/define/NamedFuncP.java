package org.smoothbuild.compile.fs.ps.ast.define;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Iterables.joinToString;

import java.util.Objects;
import java.util.Optional;

import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.type.FuncSchemaS;
import org.smoothbuild.compile.fs.lang.type.FuncTS;
import org.smoothbuild.util.collect.NList;

public sealed class NamedFuncP
    extends NamedEvaluableP
    implements FuncP
    permits ConstructorP {
  private final TypeP resT;
  private final NList<ItemP> params;
  private FuncTS typeS;
  private FuncSchemaS funcSchemaS;

  public NamedFuncP(
      TypeP resT,
      String fullName,
      String simpleName,
      NList<ItemP> params,
      Optional<ExprP> body,
      Optional<AnnotationP> annotation,
      Location location) {
    super(fullName, simpleName, body, annotation, location);
    this.resT = resT;
    this.params = params;
  }

  @Override
  public TypeP resT() {
    return resT;
  }

  @Override
  public NList<ItemP> params() {
    return params;
  }

  @Override
  public TypeP evalT() {
    return resT();
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
        && Objects.equals(this.resT, that.resT)
        && Objects.equals(this.name(), that.name())
        && Objects.equals(this.params, that.params)
        && Objects.equals(this.body(), that.body())
        && Objects.equals(this.annotation(), that.annotation())
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(resT, name(), params, body(), annotation(), location());
  }

  @Override
  public String toString() {
    var fields = joinToString("\n",
        "resT = " + resT,
        "name = " + name(),
        "params = [",
        indent(joinToString(params(), "\n")),
        "]",
        "body = " + body(),
        "annotation = " + annotation(),
        "location = " + location()
    );
    return "NamedFuncP(\n" + indent(fields) + "\n)";
  }
}
