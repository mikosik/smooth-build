package org.smoothbuild.compile.fs.lang.type;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.compile.fs.lang.type.VarSetS.varSetS;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.smoothbuild.util.Strings;

/**
 * Monomorphic type.
 * This class and all its subclasses are immutable.
 */
public abstract sealed class TypeS
    permits ArrayTS, BlobTS, BoolTS, FieldSetTS, FuncTS, IntTS, StringTS, TupleTS, VarS {
  private final VarSetS vars;
  private final String name;

  protected TypeS(String name) {
    this(name, varSetS());
  }

  protected TypeS(String name, VarSetS vars) {
    checkArgument(!name.isBlank());
    this.name = name;
    this.vars = vars;
  }

  public String name() {
    return name;
  }

  public String q() {
    return Strings.q(name);
  }

  public VarSetS vars() {
    return vars;
  }

  public TypeS mapComponents(Function<TypeS, TypeS> mapper) {
    return this;
  }

  public TypeS mapVars(Map<VarS, VarS> map) {
    return mapVars(v -> map.getOrDefault(v, v));
  }

  public TypeS mapVars(Function<VarS, TypeS> varMapper) {
    return this;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof TypeS that
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
