package org.smoothbuild.parse.ast.expr;

import static java.util.Objects.requireNonNullElse;

import java.util.function.Function;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.WithLocImpl;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.lang.type.VarS;
import org.smoothbuild.util.Strings;

import com.google.common.collect.ImmutableMap;

public final class RefP extends WithLocImpl implements ExprP {
  private final String name;
  private ImmutableMap<VarS, ? extends TypeS> monoizationMapping;

  public RefP(String name, Loc loc) {
    super(loc);
    this.name = name;
  }

  public String name() {
    return name;
  }

  public String q() {
    return Strings.q(name);
  }

  @Override
  public String toString() {
    return "RefP(`" + name + "`)";
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
