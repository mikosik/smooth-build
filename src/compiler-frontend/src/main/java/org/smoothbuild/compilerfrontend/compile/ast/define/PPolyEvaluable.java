package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.collect.Map.zipToMap;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.PolyEvaluable;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.STypeScheme;
import org.smoothbuild.compilerfrontend.lang.type.STypeVar;

public final class PPolyEvaluable implements PReferenceable, PolyEvaluable, PContainer {
  private final PTypeParams typeParams;
  private final PNamedEvaluable evaluable;
  private PScope scope;

  public PPolyEvaluable(PTypeParams typeParams, PNamedEvaluable evaluable) {
    this.typeParams = typeParams;
    this.evaluable = evaluable;
  }

  public PTypeParams pTypeParams() {
    return typeParams;
  }

  @Override
  public List<STypeVar> typeParams() {
    return typeParams.typeVars();
  }

  @Override
  public PNamedEvaluable evaluable() {
    return evaluable;
  }

  @Override
  public SType instantiatedType(List<SType> typeArgs) {
    var map = zipToMap(typeParams().toList(), typeArgs);
    return evaluable.sType().mapTypeVars(v -> map.getOrDefault(v, v));
  }

  @Override
  public STypeScheme typeScheme() {
    return new STypeScheme(typeParams(), evaluable.sType());
  }

  @Override
  public void setScope(PScope scope) {
    this.scope = scope;
  }

  @Override
  public PScope scope() {
    return scope;
  }

  @Override
  public Fqn fqn() {
    return evaluable.fqn();
  }

  @Override
  public void setFqn(Fqn fqn) {
    throw new UnsupportedOperationException();
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
    return object instanceof PPolyEvaluable that
        && Objects.equals(this.typeParams, that.typeParams)
        && Objects.equals(this.evaluable, that.evaluable)
        && Objects.equals(this.scope, that.scope);
  }

  @Override
  public int hashCode() {
    return Objects.hash(typeParams, evaluable, scope);
  }

  @Override
  public String toString() {
    return new ToStringBuilder("PPolyEvaluable")
        .addField("typeParams", typeParams)
        .addField("evaluable", evaluable)
        .addField("scope", scope)
        .toString();
  }
}
