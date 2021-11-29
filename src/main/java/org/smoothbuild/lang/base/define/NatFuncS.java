package org.smoothbuild.lang.base.define;

import java.util.Objects;

import org.smoothbuild.lang.base.type.impl.FuncTypeS;
import org.smoothbuild.lang.expr.Annotation;
import org.smoothbuild.util.collect.NList;

/**
 * This class is immutable.
 */
public final class NatFuncS extends FuncS {
  private final Annotation annotation;

  public NatFuncS(FuncTypeS type, ModulePath modulePath, String name,
      NList<Item> params, Annotation annotation, Location location) {
    super(type, modulePath, name, params, location);
    this.annotation = annotation;
  }

  public Annotation annotation() {
    return annotation;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof NatFuncS that
        && this.resultType().equals(that.resultType())
        && this.modulePath().equals(that.modulePath())
        && this.name().equals(that.name())
        && this.params().equals(that.params())
        && this.annotation.equals(that.annotation)
        && this.location().equals(that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(resultType(), modulePath(), name(), params(), annotation, location());
  }

  @Override
  public String toString() {
    return "NatFunc(`" + code() + "`)";
  }

  private String code() {
    return annotation + " " + signature();
  }
}
