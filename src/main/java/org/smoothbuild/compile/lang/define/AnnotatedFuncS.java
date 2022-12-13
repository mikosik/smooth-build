package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.Strings.indent;

import java.util.Objects;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.FuncSchemaS;
import org.smoothbuild.util.collect.NList;

/**
 * Annotated function that has no defined body.
 * This class is immutable.
 */
public final class AnnotatedFuncS extends NamedFuncS {
  private final AnnotationS annotation;

  public AnnotatedFuncS(
      AnnotationS annotation,
      FuncSchemaS schema,
      String name,
      NList<ItemS> params,
      Loc loc) {
    super(schema, name, params, loc);
    this.annotation = annotation;
  }

  public AnnotationS annotation() {
    return annotation;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof AnnotatedFuncS that
        && this.annotation.equals(that.annotation)
        && this.schema().equals(that.schema())
        && this.name().equals(that.name())
        && this.params().equals(that.params())
        && this.loc().equals(that.loc());
  }

  @Override
  public int hashCode() {
    return Objects.hash(annotation, schema(), name(), params(), loc());
  }

  @Override
  public String toString() {
    var fields = annotation.toString() + "\n" + fieldsToString();
    return "AnnotatedFuncS(\n" + indent(fields) + "\n)";
  }
}
