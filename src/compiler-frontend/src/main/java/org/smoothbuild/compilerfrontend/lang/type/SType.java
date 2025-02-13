package org.smoothbuild.compilerfrontend.lang.type;

import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.sVarSet;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import org.smoothbuild.common.base.Strings;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.compilerfrontend.lang.define.SItemSig;

/**
 * Monomorphic type.
 * This class and all its subclasses are immutable.
 */
public abstract sealed class SType
    permits SBaseType, SArrayType, SFuncType, SInterfaceType, STupleType, STypeVar {
  private final SVarSet vars;

  protected SType(SVarSet vars) {
    this.vars = vars;
  }

  public SVarSet vars() {
    return vars;
  }

  public boolean isFlexibleTypeVar() {
    return false;
  }

  public void forEachFlexibleVar(Consumer<STypeVar> consumer) {
    forEachFlexibleVar(this, consumer);
  }

  private static void forEachFlexibleVar(SType sType, Consumer<STypeVar> consumer) {
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
      case STypeVar sTypeVar -> {
        if (sTypeVar.isFlexibleTypeVar()) {
          consumer.accept(sTypeVar);
        }
      }
      default -> {}
    }
  }

  public SType mapFlexibleVars(Function<SType, SType> map) {
    return mapVars(t -> t.isFlexibleTypeVar() ? map.apply(t) : t);
  }

  public SType mapVars(Map<STypeVar, SType> map) {
    return mapVars(v -> map.getOrDefault(v, v));
  }

  public SType mapVars(Function<? super STypeVar, SType> map) {
    if (vars().isEmpty()) {
      return this;
    } else {
      return switch (this) {
        case SArrayType a -> mapVarsInArray(a, map);
        case SFuncType f -> mapVarsInFunc(f, map);
        case SStructType s -> mapVarsInStruct(s, map);
        case SInterfaceType i -> mapVarsInInterface(i, map);
        case STupleType t -> mapVarsInTuple(t, map);
        case STypeVar v -> map.apply(v);
        default -> this;
      };
    }
  }

  private static SArrayType mapVarsInArray(
      SArrayType sArrayType, Function<? super STypeVar, SType> map) {
    var elem = sArrayType.elem().mapVars(map);
    return new SArrayType(elem);
  }

  private static SFuncType mapVarsInFunc(
      SFuncType sFuncType, Function<? super STypeVar, SType> map) {
    var params = (STupleType) sFuncType.params().mapVars(map);
    var result = sFuncType.result().mapVars(map);
    return new SFuncType(params, result);
  }

  private static SInterfaceType mapVarsInInterface(
      SInterfaceType sInterfaceType, Function<? super STypeVar, SType> map) {
    var fields = sInterfaceType.fieldSet().mapValues(f -> mapItemSigComponents(f, map));
    return new SInterfaceType(fields);
  }

  private static SStructType mapVarsInStruct(
      SStructType sStructType, Function<? super STypeVar, SType> map) {
    var fields = sStructType.fields().map(f -> mapItemSigComponents(f, map));
    return new SStructType(sStructType.fqn(), fields);
  }

  private static SItemSig mapItemSigComponents(SItemSig f, Function<? super STypeVar, SType> map) {
    return new SItemSig(f.type().mapVars(map), f.name());
  }

  private static STupleType mapVarsInTuple(
      STupleType sTupleType, Function<? super STypeVar, SType> map) {
    var items = sTupleType.elements().map(type -> type.mapVars(map));
    return new STupleType(items);
  }

  /**
   * Exact smooth source code that is a reference or specification of this type
   * so it can be used to specify type of function parameter, its result type, value type, etc.
   */
  public String specifier() {
    return specifier(sVarSet());
  }

  public abstract String specifier(SVarSet localVars);

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
