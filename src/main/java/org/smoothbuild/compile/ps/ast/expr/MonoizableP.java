package org.smoothbuild.compile.ps.ast.expr;

import static java.util.Objects.requireNonNullElse;

import java.util.function.Function;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarS;

import com.google.common.collect.ImmutableMap;

/**
 * Operation that can be monomorphized.
 */
public abstract sealed class MonoizableP extends OperP
    permits DefaultArgP, RefP {
  private ImmutableMap<VarS, ? extends TypeS> monoizationMapping;

  public MonoizableP(Loc loc) {
    super(loc);
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
