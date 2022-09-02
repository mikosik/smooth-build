package org.smoothbuild.parse.ast.expr;

import static java.util.Objects.requireNonNullElse;

import java.util.function.Function;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.NalImpl;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.lang.type.VarS;

import com.google.common.collect.ImmutableMap;

public final class RefP extends NalImpl implements ExprP {
  private ImmutableMap<VarS, ? extends TypeS> monoizationMapping;

  public RefP(String name, Loc loc) {
    super(name, loc);
  }

  @Override
  public String toString() {
    return "RefP(`" + q() + "`)";
  }

  public void setMonoizationMapping(ImmutableMap<VarS, ? extends TypeS> monoizationMapping) {
    this.monoizationMapping = monoizationMapping;
  }

  public ImmutableMap<VarS, ? extends TypeS> monoizationMapping() {
    return monoizationMapping;
  }

  public Function<VarS, TypeS> monoizationMapper() {
    return key -> requireNonNullElse(monoizationMapping.get(key), key);
  }
}
