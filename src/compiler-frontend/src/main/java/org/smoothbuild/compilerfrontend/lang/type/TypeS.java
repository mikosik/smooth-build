package org.smoothbuild.compilerfrontend.lang.type;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import org.smoothbuild.common.base.Strings;
import org.smoothbuild.compilerfrontend.lang.define.ItemSigS;

/**
 * Monomorphic type.
 * This class and all its subclasses are immutable.
 */
public abstract sealed class TypeS
    permits ArrayTS, BlobTS, BoolTS, FieldSetTS, FuncTS, IntTS, StringTS, TupleTS, VarS {
  private final VarSetS vars;
  private final String name;

  protected TypeS(String name) {
    this(name, VarSetS.varSetS());
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

  public void forEachTempVar(Consumer<TempVarS> consumer) {
    forEachTempVar(this, consumer);
  }

  private static void forEachTempVar(TypeS typeS, Consumer<TempVarS> consumer) {
    switch (typeS) {
      case ArrayTS arrayTS -> forEachTempVar(arrayTS.elem(), consumer);
      case FieldSetTS fieldSetTS -> fieldSetTS
          .fieldSet()
          .values()
          .forEach(f -> forEachTempVar(f.type(), consumer));
      case FuncTS funcTS -> {
        forEachTempVar(funcTS.params(), consumer);
        forEachTempVar(funcTS.result(), consumer);
      }
      case TupleTS tupleTS -> tupleTS.elements().forEach(t -> forEachTempVar(t, consumer));
      case TempVarS temp -> consumer.accept(temp);
      default -> {}
    }
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
        case ArrayTS a -> mapVarsInArray(a, map);
        case FuncTS f -> mapVarsInFunc(f, map);
        case InterfaceTS i -> mapVarsInInterface(i, map);
        case StructTS s -> mapVarsInStruct(s, map);
        case TupleTS t -> mapVarsInTuple(t, map);
        case VarS v -> map.apply(v);
        default -> this;
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
    var fields = interfaceTS.fieldSet().mapValues(f -> mapItemSigComponents(f, map));
    return new InterfaceTS(fields);
  }

  private static StructTS mapVarsInStruct(StructTS structTS, Function<? super VarS, TypeS> map) {
    var fields = structTS.fields().map(f -> mapItemSigComponents(f, map));
    return new StructTS(structTS.name(), fields);
  }

  private static ItemSigS mapItemSigComponents(ItemSigS f, Function<? super VarS, TypeS> map) {
    return new ItemSigS(f.type().mapVars(map), f.name());
  }

  private static TupleTS mapVarsInTuple(TupleTS tupleTS, Function<? super VarS, TypeS> map) {
    var items = tupleTS.elements().map(type -> type.mapVars(map));
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
