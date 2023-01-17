package org.smoothbuild.compile.fs.lang.type;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.compile.fs.lang.type.VarSetS.varSetS;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Maps.mapValues;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.smoothbuild.compile.fs.lang.define.ItemSigS;
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
    return mapVars(t -> t instanceof TempVarS ? map.apply(t) : t);
  }

  public TypeS mapVars(Map<VarS, TypeS> map) {
    return mapVars(v -> map.getOrDefault(v, v));
  }

  public TypeS mapVars(Function<? super VarS, TypeS> map) {
    if (vars().isEmpty()) {
      return this;
    } else {
      return switch (this) {
        // @formatter:off
        case ArrayTS     a -> mapVarsInArray(a, map);
        case FuncTS      f -> mapVarsInFunc(f, map);
        case InterfaceTS i -> mapVarsInInterface(i, map);
        case StructTS    s -> mapVarsInStruct(s, map);
        case TupleTS     t -> mapVarsInTuple(t, map);
        case VarS        v -> map.apply(v);
        default            -> this;
        // @formatter:on
      };
    }
  }

  private static ArrayTS mapVarsInArray(ArrayTS arrayTS, Function<? super VarS, TypeS> map) {
    var elem = arrayTS.elem().mapVars(map);
    return new ArrayTS(elem);
  }

  private static FuncTS mapVarsInFunc(FuncTS funcTS, Function<? super VarS, TypeS> map) {
    var params = (TupleTS) funcTS.params().mapVars(map);
    var result = funcTS.result().mapVars(map);
    return new FuncTS(params, result);
  }

  private static InterfaceTS mapVarsInInterface(
      InterfaceTS interfaceTS, Function<? super VarS, TypeS> map) {
    var fields = mapValues(interfaceTS.fieldSet(), f -> mapItemSigComponents(f, map));
    return new InterfaceTS(fields);
  }

  private static StructTS mapVarsInStruct(
      StructTS structTS, Function<? super VarS, TypeS> map) {
    var fields = structTS.fields().map(f -> mapItemSigComponents(f, map));
    return new StructTS(structTS.name(), fields);
  }

  private static ItemSigS mapItemSigComponents(ItemSigS f, Function<? super VarS, TypeS> map) {
    return new ItemSigS(f.type().mapVars(map), f.name());
  }

  private static TupleTS mapVarsInTuple(TupleTS tupleTS, Function<? super VarS, TypeS> map) {
    var items = map(tupleTS.elements(), type -> type.mapVars(map));
    return new TupleTS(items);
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
