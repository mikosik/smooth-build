package org.smoothbuild.compilerfrontend.lang.type;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import org.smoothbuild.common.base.Strings;
import org.smoothbuild.compilerfrontend.lang.define.SItemSig;

/**
 * Monomorphic type.
 * This class and all its subclasses are immutable.
 */
public abstract sealed class SType
    permits SBaseType, SArrayType, SFuncType, SInterfaceType, STupleType, SVar {
  private final SVarSet vars;
  private final String name;

  protected SType(String name) {
    this(name, varSetS());
  }

  protected SType(String name, SVarSet vars) {
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

  public SVarSet vars() {
    return vars;
  }

  public boolean isFlexibleVar() {
    return false;
  }

  public void forEachFlexibleVar(Consumer<SVar> consumer) {
    forEachFlexibleVar(this, consumer);
  }

  private static void forEachFlexibleVar(SType sType, Consumer<SVar> consumer) {
    switch (sType) {
      case SArrayType sArrayType -> forEachFlexibleVar(sArrayType.elem(), consumer);
      case SInterfaceType sFieldSetType -> sFieldSetType
          .fieldSet()
          .values()
          .forEach(f -> forEachFlexibleVar(f.type(), consumer));
      case SFuncType sFuncType -> {
        forEachFlexibleVar(sFuncType.params(), consumer);
        forEachFlexibleVar(sFuncType.result(), consumer);
      }
      case STupleType sTupleType -> sTupleType
          .elements()
          .forEach(t -> forEachFlexibleVar(t, consumer));
      case SVar sVar -> {
        if (sVar.isFlexibleVar()) {
          consumer.accept(sVar);
        }
      }
      default -> {}
    }
  }

  public SType mapFlexibleVars(Function<SType, SType> map) {
    return mapVars(t -> t.isFlexibleVar() ? map.apply(t) : t);
  }

  public SType mapVars(Map<SVar, SType> map) {
    return mapVars(v -> map.getOrDefault(v, v));
  }

  public SType mapVars(Function<? super SVar, SType> map) {
    if (vars().isEmpty()) {
      return this;
    } else {
      return switch (this) {
        case SArrayType a -> mapVarsInArray(a, map);
        case SFuncType f -> mapVarsInFunc(f, map);
        case SStructType s -> mapVarsInStruct(s, map);
        case SInterfaceType i -> mapVarsInInterface(i, map);
        case STupleType t -> mapVarsInTuple(t, map);
        case SVar v -> map.apply(v);
        default -> this;
      };
    }
  }

  private static SArrayType mapVarsInArray(
      SArrayType sArrayType, Function<? super SVar, SType> map) {
    var elem = sArrayType.elem().mapVars(map);
    return new SArrayType(elem);
  }

  private static SFuncType mapVarsInFunc(SFuncType sFuncType, Function<? super SVar, SType> map) {
    var params = (STupleType) sFuncType.params().mapVars(map);
    var result = sFuncType.result().mapVars(map);
    return new SFuncType(params, result);
  }

  private static SInterfaceType mapVarsInInterface(
      SInterfaceType sInterfaceType, Function<? super SVar, SType> map) {
    var fields = sInterfaceType.fieldSet().mapValues(f -> mapItemSigComponents(f, map));
    return new SInterfaceType(fields);
  }

  private static SStructType mapVarsInStruct(
      SStructType sStructType, Function<? super SVar, SType> map) {
    var fields = sStructType.fields().map(f -> mapItemSigComponents(f, map));
    return new SStructType(sStructType.id(), fields);
  }

  private static SItemSig mapItemSigComponents(SItemSig f, Function<? super SVar, SType> map) {
    return new SItemSig(f.type().mapVars(map), f.name());
  }

  private static STupleType mapVarsInTuple(
      STupleType sTupleType, Function<? super SVar, SType> map) {
    var items = sTupleType.elements().map(type -> type.mapVars(map));
    return new STupleType(items);
  }

  public String toSourceCode() {
    return name();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof SType that
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
