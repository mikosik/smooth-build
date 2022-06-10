package org.smoothbuild.lang.type;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.type.VarSetS.toVarSetS;
import static org.smoothbuild.lang.type.VarSetS.varSetS;

import java.util.Objects;
import java.util.function.Function;

import com.google.common.collect.ImmutableCollection;

/**
 * Monomorphic type.
 *
 * This class and all its subclasses are immutable.
 */
public abstract sealed class MonoTS implements TypeS
    permits ArrayTS, BaseTS, MonoFuncTS, MergeTS, StructTS, VarS {
  private final VarSetS vars;
  private final String name;

  protected MonoTS(String name) {
    this(name, varSetS());
  }

  protected MonoTS(String name, VarSetS vars) {
    checkArgument(!name.isBlank());
    this.name = name;
    this.vars = vars;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String q() {
    return "`" + name() + "`";
  }

  public static VarSetS calculateVars(ImmutableCollection<MonoTS> types) {
    return types.stream()
        .map(MonoTS::vars)
        .flatMap(VarSetS::stream)
        .collect(toVarSetS());
  }

  public VarSetS vars() {
    return vars;
  }

  public boolean includes(MonoTS type) {
    return this.equals(type);
  }

  public MonoTS mapVars(Function<VarS, VarS> varMapper) {
    return this;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof MonoTS that
        && getClass().equals(object.getClass())
        && this.name().equals(that.name());
  }

  @Override
  public int hashCode() {
    return Objects.hash(name());
  }

  @Override
  public String toString() {
    return name();
  }
}
