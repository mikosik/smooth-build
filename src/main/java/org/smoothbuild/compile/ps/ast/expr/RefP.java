package org.smoothbuild.compile.ps.ast.expr;

import static java.util.Objects.requireNonNullElse;
import static org.smoothbuild.util.Strings.q;

import java.util.function.Function;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarS;

import com.google.common.collect.ImmutableMap;

public final class RefP extends OperP {
  private final String name;
  private ImmutableMap<VarS, ? extends TypeS> monoizationMapping;

  public RefP(String name, Loc loc) {
    super(loc);
    this.name = name;
  }

  public String name() {
    return name;
  }

  @Override
  public String toString() {
    return "RefP(`" + q(name) + "`)";
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
