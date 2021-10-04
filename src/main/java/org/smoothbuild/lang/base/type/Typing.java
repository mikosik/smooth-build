package org.smoothbuild.lang.base.type;

import static com.google.common.collect.Iterables.concat;
import static org.smoothbuild.lang.base.type.BoundsMap.boundsMap;
import static org.smoothbuild.lang.base.type.ItemSignature.itemSignature;
import static org.smoothbuild.util.Lists.allMatch;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.Lists.zip;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.db.object.db.SpecDb;
import org.smoothbuild.lang.base.type.Sides.Side;
import org.smoothbuild.util.Sets;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
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

  public Sides.Side upper() {
    return Types.upper();
  }

  public Sides.Side lower() {
    return Types.lower();
  }

  public Bounds unbounded() {
    return new Bounds(nothingT(), anyT());
  }

  public Bounds oneSideBound(Side side, Type type) {
    return side.dispatch(
        () -> new Bounds(type, anyT()),
        () -> new Bounds(nothingT(), type)
    );
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
    return createArrayType(arrayType, newElemType);
  }

  private Type stripFunctionType(FunctionType functionType) {
    var oldResultType = functionType.resultType();
    var newResultType = strip(oldResultType);
    var oldParameters = functionType.parameters();
    var newParameters = map(oldParameters, i -> itemSignature(strip(i.type())));
    return createFunctionType(functionType, newResultType, newParameters);
  }

  public boolean isAssignable(Type target, Type source) {
    return inequal(target, source, lower());
  }

  public boolean isParamAssignable(Type target, Type source) {
    return inequalParam(target, source, lower())
        && areConsistent(inferVariableBounds(target, source, lower()));
  }

  private boolean areConsistent(BoundsMap boundsMap) {
    return boundsMap.map().values().stream()
        .allMatch(b -> isAssignable(b.bounds().upper(), b.bounds().lower()));
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
    return merge(zip(typesA, typesB, inferFunction(side)));
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
    return mapVariables(functionType.resultType(), boundedVariables, lower());
  }

  public BoundsMap inferVariableBoundsInCall(Type resultTypes,
      List<Type> parameterTypes, List<Type> argumentTypes) {
    var boundedVariables = inferVariableBounds(parameterTypes, argumentTypes, lower());
    var resultVariables = Sets.map(resultTypes.variables(), v -> new Bounded(v, unbounded()));
    return mergeWith(boundedVariables, resultVariables);
  }

  public Type mapVariables(Type type, BoundsMap boundsMap, Side side) {
    if (type.isPolytype()) {
      if (type instanceof Variable) {
        return boundsMap.map().get(type).bounds().get(side);
      } else if (type instanceof ArrayType oldArrayType) {
        Type elemType = oldArrayType.elemType();
        Type newElemType = mapVariables(elemType, boundsMap, side);
        return createArrayType(oldArrayType, newElemType);
      } else if (type instanceof FunctionType functionType) {
        var oldResultType = functionType.resultType();
        var newResultType = mapVariables(oldResultType, boundsMap, side);
        var oldParameters = functionType.parameters();
        var newParameters = map(
            oldParameters,
            i -> itemSignature(mapVariables(i.type(), boundsMap, side.reversed())));
        return createFunctionType(functionType, newResultType, newParameters);
      } else {
        throw new RuntimeException(
            "Unexpected Type subclass: " + type.getClass().getCanonicalName());
      }
    } else {
      return type;
    }
  }

  public Type merge(Type typeA, Type typeB, Side direction) {
    Side reversed = direction.reversed();
    Type reversedEdge = reversed.edge();
    if (reversedEdge.equals(typeB)) {
      return typeA;
    } else if (reversedEdge.equals(typeA)) {
      return typeB;
    } else if (typeA.equals(typeB)) {
      return strip(typeA);
    } else if (typeA instanceof ArrayType arrayA && typeB instanceof ArrayType arrayB) {
      var elemA = arrayA.elemType();
      var elemB = arrayB.elemType();
      var elemM = merge(elemA, elemB, direction);
      if (arrayA.elemType() == elemM) {
        return arrayA;
      } else if (arrayB.elemType() == elemM) {
        return arrayB;
      } else {
        return newArrayType(elemM);
      }
    } else if (typeA instanceof FunctionType functionA && typeB instanceof FunctionType functionB) {
      if (functionA.parameters().size() == functionB.parameters().size()) {
        var resultA = functionA.resultType();
        var resultB = functionB.resultType();
        var resultM = merge(resultA, resultB, direction);
        var parameterTypesA = functionA.parameterTypes();
        var parametersTypesB = functionB.parameterTypes();
        var parametersM = zip(parameterTypesA, parametersTypesB,
            (a, b) -> itemSignature(merge(a, b, reversed)));
        if (isFunctionTypeEqual(functionA, resultM, parametersM)) {
          return functionA;
        } else if (isFunctionTypeEqual(functionB, resultM, parametersM)){
          return functionB;
        } else {
          return newFunctionType(resultM, parametersM);
        }
      } else {
        return direction.edge();
      }
    } else {
      return direction.edge();
    }
  }

  private static FunctionType createFunctionType(FunctionType functionType, Type resultType,
      ImmutableList<ItemSignature> parameters) {
    if (isFunctionTypeEqual(functionType, resultType, parameters)) {
      return functionType;
    }
    return newFunctionType(resultType, parameters);
  }

  private static boolean isFunctionTypeEqual(FunctionType functionType, Type resultType,
      ImmutableList<ItemSignature> parameters) {
    return functionType.resultType() == resultType && functionType.parameters().equals(parameters);
  }

  private static FunctionType newFunctionType(
      Type result, ImmutableList<ItemSignature> parameters) {
    return Types.functionT(result, parameters);
  }

  private ArrayType createArrayType(ArrayType arrayType, Type elemType) {
    if (arrayType.elemType() == elemType) {
      return arrayType;
    } else {
      return newArrayType(elemType);
    }
  }

  private static ArrayType newArrayType(Type elemType) {
    return Types.arrayT(elemType);
  }

  public BoundsMap merge(Iterable<BoundsMap> iterable) {
    var result = new HashMap<Variable, Bounded>();
    for (BoundsMap boundsMap : iterable) {
      mergeToMap(result, boundsMap.map().values());
    }
    return new BoundsMap(ImmutableMap.copyOf(result));
  }

  public BoundsMap mergeWith(BoundsMap boundsMap, Iterable<Bounded> boundeds) {
    var result = new HashMap<>(boundsMap.map());
    mergeToMap(result, boundeds);
    return new BoundsMap(ImmutableMap.copyOf(result));
  }

  private void mergeToMap(Map<Variable, Bounded> map, Iterable<Bounded> boundeds) {
    for (Bounded bounded : boundeds) {
      map.merge(bounded.variable(), bounded,
          (a, b) -> new Bounded(a.variable(), merge(a.bounds(), b.bounds())));
    }
  }

  public Bounds merge(Bounds boundsA, Bounds boundsB) {
    return new Bounds(
        merge(boundsA.lower(), boundsB.lower(), upper()),
        merge(boundsA.upper(), boundsB.upper(), lower()));
  }
}
