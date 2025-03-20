package org.smoothbuild.compilerfrontend.lang.type;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import org.smoothbuild.common.base.Strings;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.collect.Set;
import org.smoothbuild.compilerfrontend.lang.define.SItemSig;

/**
 * Monomorphic type.
 * This class and all its subclasses are immutable.
 */
public abstract sealed class SType
    permits SBaseType, SArrayType, SFuncType, SInterfaceType, STupleType, STypeVar {
  private final Set<STypeVar> typeVars;

  protected SType(Set<STypeVar> typeVars) {
    this.typeVars = typeVars;
  }

  public Set<STypeVar> typeVars() {
    return typeVars;
  }

  public boolean isFlexibleTypeVar() {
    return false;
  }

  public void forEachFlexibleTypeVar(Consumer<STypeVar> consumer) {
    forEachFlexibleTypeVar(this, consumer);
  }

  private static void forEachFlexibleTypeVar(SType sType, Consumer<STypeVar> consumer) {
    switch (sType) {
      case SArrayType sArrayType -> forEachFlexibleTypeVar(sArrayType.elem(), consumer);
      case SInterfaceType sFieldSetType ->
        sFieldSetType.fieldSet().values().forEach(f -> forEachFlexibleTypeVar(f.type(), consumer));
      case SFuncType sFuncType -> {
        forEachFlexibleTypeVar(sFuncType.params(), consumer);
        forEachFlexibleTypeVar(sFuncType.result(), consumer);
      }
      case STupleType sTupleType ->
        sTupleType.elements().forEach(t -> forEachFlexibleTypeVar(t, consumer));
      case STypeVar sTypeVar -> {
        if (sTypeVar.isFlexibleTypeVar()) {
          consumer.accept(sTypeVar);
        }
      }
      default -> {}
    }
  }

  public SType mapFlexibleTypeVars(Function<SType, SType> map) {
    return mapTypeVars(t -> t.isFlexibleTypeVar() ? map.apply(t) : t);
  }

  public SType mapTypeVars(Map<STypeVar, SType> map) {
    return mapTypeVars(v -> map.getOrDefault(v, v));
  }

  public SType mapTypeVars(Function<? super STypeVar, SType> map) {
    if (typeVars().isEmpty()) {
      return this;
    } else {
      return switch (this) {
        case SArrayType a -> mapTypeVarsInArray(a, map);
        case SFuncType f -> mapTypeVarsInFunc(f, map);
        case SStructType s -> mapTypeVarsInStruct(s, map);
        case SInterfaceType i -> mapTypeVarsInInterface(i, map);
        case STupleType t -> mapTypeVarsInTuple(t, map);
        case STypeVar v -> map.apply(v);
        default -> this;
      };
    }
  }

  private static SArrayType mapTypeVarsInArray(
      SArrayType sArrayType, Function<? super STypeVar, SType> map) {
    var elem = sArrayType.elem().mapTypeVars(map);
    return new SArrayType(elem);
  }

  private static SFuncType mapTypeVarsInFunc(
      SFuncType sFuncType, Function<? super STypeVar, SType> map) {
    var params = (STupleType) sFuncType.params().mapTypeVars(map);
    var result = sFuncType.result().mapTypeVars(map);
    return new SFuncType(params, result);
  }

  private static SInterfaceType mapTypeVarsInInterface(
      SInterfaceType sInterfaceType, Function<? super STypeVar, SType> map) {
    var fields = sInterfaceType.fieldSet().mapValues(f -> mapItemSigComponents(f, map));
    return new SInterfaceType(fields);
  }

  private static SStructType mapTypeVarsInStruct(
      SStructType sStructType, Function<? super STypeVar, SType> map) {
    var fields = sStructType.fields().map(f -> mapItemSigComponents(f, map));
    return new SStructType(sStructType.fqn(), fields);
  }

  private static SItemSig mapItemSigComponents(SItemSig f, Function<? super STypeVar, SType> map) {
    return new SItemSig(f.type().mapTypeVars(map), f.name());
  }

  private static STupleType mapTypeVarsInTuple(
      STupleType sTupleType, Function<? super STypeVar, SType> map) {
    var items = sTupleType.elements().map(type -> type.mapTypeVars(map));
    return new STupleType(items);
  }

  /**
   * Exact smooth source code that is a reference or specification of this type
   * so it can be used to specify type of function parameter, its result type, value type, etc.
   */
  public abstract String specifier();

  public String q() {
    return Strings.q(specifier());
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof SType that
        && getClass().equals(object.getClass())
        && this.specifier().equals(that.specifier());
  }

  @Override
  public int hashCode() {
    return Objects.hash(specifier());
  }

  @Override
  public String toString() {
    return specifier();
  }
}
