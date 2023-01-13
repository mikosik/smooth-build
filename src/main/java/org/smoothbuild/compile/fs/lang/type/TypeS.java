package org.smoothbuild.compile.fs.lang.type;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.compile.fs.lang.type.VarSetS.varSetS;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Maps.mapValues;

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

  public TypeS mapTemps(Function<TypeS, TypeS> map) {
    return switch (this) {
      // @formatter:off
      case ArrayTS     a -> new ArrayTS(a.elem().mapTemps(map));
      case FuncTS      f -> new FuncTS((TupleTS) f.params().mapTemps(map), f.res().mapTemps(map));
      case InterfaceTS i -> new InterfaceTS(mapValues(i.fieldSet(), f -> f.mapType(map)));
      case StructTS    s -> new StructTS(name(), s.fields().map(f -> f.mapType(map)));
      case TupleTS     t -> new TupleTS(map(t.items(), type -> type.mapTemps(map)));
      case TempVarS    v -> map.apply(v);
      default            -> this;
      // @formatter:on
    };
  }

  public TypeS mapVars(Map<VarS, VarS> map) {
    return mapVars(v -> map.getOrDefault(v, v));
  }

  public TypeS mapVars(Function<VarS, TypeS> map) {
    if (vars().isEmpty()) {
      return this;
    } else {
      return switch (this) {
        // @formatter:off
        case ArrayTS     a -> new ArrayTS(a.elem().mapVars(map));
        case FuncTS      f -> new FuncTS((TupleTS) f.params().mapVars(map), f.res().mapVars(map));
        case InterfaceTS i -> new InterfaceTS(mapValues(i.fieldSet(), f -> f.mapVarsInType(map)));
        case StructTS    s -> new StructTS(name(), s.fields().map(f -> f.mapVarsInType(map)));
        case TupleTS     t -> new TupleTS(map(t.items(), type -> type.mapVars(map)));
        case VarS        v -> map.apply(v);
        default            -> this;
        // @formatter:on
      };
    }
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
