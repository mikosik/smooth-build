package org.smoothbuild.compilerfrontend.lang.define;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.common.collect.Maybe.some;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.PolyEvaluable;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;
import org.smoothbuild.compilerfrontend.lang.type.STypeScheme;
import org.smoothbuild.compilerfrontend.lang.type.STypeVar;

/**
 * Polymorphic evaluable.
 */
public final class SPolyEvaluable implements PolyEvaluable, SReferenceable {
  private final List<STypeVar> typeParams;
  private final SNamedEvaluable evaluable;

  public SPolyEvaluable(List<STypeVar> typeParams, SNamedEvaluable evaluable) {
    this.typeParams = requireNonNull(typeParams);
    this.evaluable = requireNonNull(evaluable);
  }

  @Override
  public List<STypeVar> typeParams() {
    return typeParams;
  }

  @Override
  public SNamedEvaluable evaluable() {
    return evaluable;
  }

  public String toSourceCode() {
    return evaluable.toSourceCode(some(typeParams));
  }

  @Override
  public Fqn fqn() {
    return evaluable.fqn();
  }

  @Override
  public Location location() {
    return evaluable.location();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof SPolyEvaluable that
        && Objects.equals(this.typeParams, that.typeParams)
        && Objects.equals(this.evaluable, that.evaluable);
  }

  @Override
  public int hashCode() {
    return Objects.hash(typeParams, evaluable);
  }

  @Override
  public String toString() {
    return new ToStringBuilder("sPolyEvaluable")
        .addField("typeParams", typeParams)
        .addField("evaluable", evaluable)
        .toString();
  }

  @Override
  public STypeScheme typeScheme() {
    return new STypeScheme(typeParams, evaluable.type());
  }
}
