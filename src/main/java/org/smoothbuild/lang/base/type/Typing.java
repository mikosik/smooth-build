package org.smoothbuild.lang.base.type;

import static com.google.common.collect.Iterables.concat;
import static org.smoothbuild.lang.base.type.Bounds.oneSideBound;
import static org.smoothbuild.lang.base.type.Bounds.unbounded;
import static org.smoothbuild.lang.base.type.BoundsMap.boundsMap;
import static org.smoothbuild.lang.base.type.BoundsMap.merge;
import static org.smoothbuild.lang.base.type.ItemSignature.itemSignature;
import static org.smoothbuild.lang.base.type.Side.LOWER;
import static org.smoothbuild.util.Lists.allMatch;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.Lists.zip;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.db.object.db.SpecDb;
import org.smoothbuild.util.Sets;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

@Singleton
public class Typing {
  private final SpecDb specDb;

  @Inject
  public Typing(SpecDb specDb) {
    this.specDb = specDb;
  }

  public ImmutableSet<BaseType> baseTypes() {
    return Types.BASE_TYPES;
  }

  public ImmutableSet<BaseType> inferableBaseTypes() {
    return Types.INFERABLE_BASE_TYPES;
  }

  public Variable variable(String name) {
    return Types.variable(name);
  }

  public AnyType anyT() {
    return Types.anyT();
  }

  public ArrayType arrayT(Type elemType) {
    return Types.arrayT(elemType);
  }

  public BlobType blobT() {
    return Types.blobT();
  }

  public BoolType boolT() {
    return Types.boolT();
  }

  public IntType intT() {
    return Types.intT();
  }

  public NothingType nothingT() {
    return Types.nothingT();
  }

  public StringType stringT() {
    return Types.stringT();
  }

  public StructType structT(String name, Iterable<ItemSignature> fields) {
    return Types.structT(name, fields);
  }

  public FunctionType functionT(Type resultType, Iterable<ItemSignature> parameters) {
    return Types.functionT(resultType, parameters);
  }

  public Type strip(Type type) {
    // TODO in java 17 use pattern matching switch
    if (type instanceof ArrayType arrayType) {
      return stripArrayType(arrayType);
    } else if (type instanceof FunctionType functionType) {
      return stripFunctionType(functionType);
    } else {
      return type;
    }
  }

  private Type stripArrayType(ArrayType arrayType) {
    Type elemType = arrayType.elemType();
    Type newElemType = strip(elemType);
    if (elemType == newElemType) {
      return arrayType;
    } else {
      return newArrayType(newElemType);
    }
  }

  private Type stripFunctionType(FunctionType functionType) {
    var oldResultType = functionType.resultType();
    var newResultType = strip(oldResultType);
    var oldParameters = functionType.parameters();
    var newParameters = map(oldParameters, i -> itemSignature(strip(i.type())));
    if (oldResultType == newResultType && oldParameters.equals(newParameters)) {
      return functionType;
    }
    return newFunctionType(newResultType, newParameters);
  }

  private static ArrayType newArrayType(Type elemType) {
    return Types.arrayT(elemType);
  }

  private static FunctionType newFunctionType(
      Type result, ImmutableList<ItemSignature> parameters) {
    return Types.functionT(result, parameters);
  }

  public boolean isAssignable(Type target, Type source) {
    return inequal(target, source, LOWER);
  }

  public boolean isParamAssignable(Type target, Type source) {
    return inequalParam(target, source, LOWER)
        && inferVariableBounds(target, source, LOWER).areConsistent();
  }

  private boolean inequal(Type typeA, Type typeB, Side side) {
    return inequalImpl(typeA, typeB, side, (a, b) -> s -> inequal(a, b, s));
  }

  private boolean inequalParam(Type TypeA, Type typeB, Side side) {
    return (TypeA instanceof Variable)
        || inequalImpl(TypeA, typeB, side, (a, b) -> s -> inequalParam(a, b, s));
  }

  private boolean inequalImpl(Type typeA, Type typeB, Side side,
      BiFunction<Type, Type, Function<Side, Boolean>> inequalityFunction) {
    return inequalByEdgeCases(typeA, typeB, side)
        || inequalByConstruction(typeA, typeB, side, inequalityFunction);
  }

  private boolean inequalByEdgeCases(Type typeA, Type typeB, Side side) {
    return typeB.equals(side.edge())
        || typeA.equals(side.reversed().edge());
  }

  private boolean inequalByConstruction(Type typeA, Type typeB, Side s,
      BiFunction<Type, Type, Function<Side, Boolean>> f) {
    return typeA.typeConstructor().equals(typeB.typeConstructor())
        && allMatch(
            typeA.covariants(),
            typeB.covariants(),
            (a, b) -> f.apply(a, b).apply(s))
        && allMatch(
            typeA.contravariants(),
            typeB.contravariants(),
            (a, b) -> f.apply(a, b).apply(s.reversed()));
  }

  public BoundsMap inferVariableBounds(List<Type> typesA, List<Type> typesB, Side side) {
    return BoundsMap.merge(zip(typesA, typesB, inferFunction(side)));
  }

  public BoundsMap inferVariableBounds(Type typeA, Type typeB, Side side) {
    if (typeA instanceof Variable variable) {
      return boundsMap(new Bounded(variable, oneSideBound(side, typeB)));
    } else if (typeB.equals(side.edge())) {
      return inferVariableBoundFromEdge(typeA, side);
    } else if (typeA.typeConstructor().equals(typeB.typeConstructor())) {
      return merge(concat(
          zip(typeA.covariants(), typeB.covariants(), inferFunction(side)),
          zip(typeA.contravariants(), typeB.contravariants(), inferFunction(side.reversed()))));
    } else {
      return boundsMap();
    }
  }

  private BiFunction<Type, Type, BoundsMap> inferFunction(Side side) {
    return (Type a, Type b) -> inferVariableBounds(a, b, side);
  }

  private BoundsMap inferVariableBoundFromEdge(Type type, Side side) {
    Side reversed = side.reversed();
    return merge(concat(
        map(type.covariants(), t -> inferVariableBounds(t, side.edge(), side)),
        map(type.contravariants(), t -> inferVariableBounds(t, reversed.edge(), reversed))));
  }

  public Type inferResultType(FunctionType functionType, List<Type> argumentTypes) {
    var boundedVariables = inferVariableBoundsInCall(functionType.resultType(),
        functionType.parameterTypes(), argumentTypes);
    return functionType.resultType().mapVariables(boundedVariables, LOWER);
  }

  public BoundsMap inferVariableBoundsInCall(Type resultTypes,
      List<Type> parameterTypes, List<Type> argumentTypes) {
    var boundedVariables = inferVariableBounds(parameterTypes, argumentTypes, LOWER);
    var resultVariables = Sets.map(resultTypes.variables(), v -> new Bounded(v, unbounded()));
    return boundedVariables.mergeWith(resultVariables);
  }
}
