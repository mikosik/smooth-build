package org.smoothbuild.lang.base.define;

import java.util.Objects;

import org.smoothbuild.lang.base.type.impl.FunctionTypeS;
import org.smoothbuild.lang.expr.Annotation;
import org.smoothbuild.util.collect.NList;

/**
 * This class is immutable.
 */
public final class NativeFunctionS extends FunctionS implements NativeEvaluableS {
  private final Annotation annotation;

  public NativeFunctionS(FunctionTypeS type, ModulePath modulePath, String name,
      NList<Item> parameters, Annotation annotation, Location location) {
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
    return object instanceof NativeFunctionS that
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
    return "NativeFunction(`" + code() + "`)";
  }

  private String code() {
    return annotation + " " + signature();
  }
}
