package org.smoothbuild.lang.base.define;

import static java.util.stream.Collectors.joining;

import java.util.Objects;

import org.smoothbuild.lang.base.type.impl.FunctionTypeS;
import org.smoothbuild.lang.expr.Annotation;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class NativeFunction extends Function implements NativeEvaluable {
  private final Annotation annotation;

  public NativeFunction(FunctionTypeS type, ModulePath modulePath, String name,
      ImmutableList<Item> parameters, Annotation annotation, Location location) {
    super(type, modulePath, name, parameters, location);
    this.annotation = annotation;
  }

  @Override
  public Annotation annotation() {
    return annotation;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof NativeFunction that
        && this.resultType().equals(that.resultType())
        && this.modulePath().equals(that.modulePath())
        && this.name().equals(that.name())
        && this.parameters().equals(that.parameters())
        && this.annotation.equals(that.annotation)
        && this.location().equals(that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(resultType(), modulePath(), name(), parameters(), annotation, location());
  }

  @Override
  public String toString() {
    return annotation.toString() + " Function(`" + resultType() + "(" + parametersToString() + ")";
  }

  private String parametersToString() {
    return parameters()
        .stream()
        .map(Object::toString)
        .collect(joining(", "));
  }
}
