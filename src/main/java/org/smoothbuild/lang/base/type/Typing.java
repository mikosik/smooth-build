package org.smoothbuild.lang.base.type;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Iterables.concat;
import static org.smoothbuild.lang.base.type.BoundsMap.boundsMap;
import static org.smoothbuild.lang.base.type.TypeNames.isVariableName;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.Lists.zip;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.lang.base.type.Sides.Side;
import org.smoothbuild.util.Sets;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

@Singleton
public class Typing {
  /**
   * Base types that are legal in smooth language.
   */
  private final ImmutableSet<BaseType> baseTypes;

  /**
   * Inferable base types are types that can be inferred but `Any` type is not legal in smooth
   * language.
   */
  private final ImmutableSet<BaseType> inferableBaseTypes;

  private final TypeFactory typeFactory;

  @Inject
  public Typing(TypeFactory typeFactory) {
    this.typeFactory = typeFactory;
    this.baseTypes = ImmutableSet.of(
        typeFactory.blob(),
        typeFactory.bool(),
        typeFactory.int_(),
        typeFactory.nothing(),
        typeFactory.string()
    );
    this.inferableBaseTypes = ImmutableSet.<BaseType>builder()
            .addAll(baseTypes)
            .add(typeFactory.any())
            .build();
  }

  public ImmutableSet<BaseType> baseTypes() {
    return baseTypes;
  }

  public ImmutableSet<BaseType> inferableBaseTypes() {
    return inferableBaseTypes;
  }

  public Variable variable(String name) {
    checkArgument(isVariableName(name), "Illegal type variable name '%s'", name);
    return new Variable(name);
  }

  public AnyType anyT() {
    return typeFactory.any();
  }

  public ArrayType arrayT(Type elemType) {
    return typeFactory.array(elemType);
  }

  public BlobType blobT() {
    return typeFactory.blob();
  }

  public BoolType boolT() {
    return typeFactory.bool();
  }

  public IntType intT() {
    return typeFactory.int_();
  }

  public NothingType nothingT() {
    return typeFactory.nothing();
  }

  public StringType stringT() {
    return typeFactory.string();
  }

  public StructType structT(String name, ImmutableList<ItemSignature> fields) {
    return typeFactory.struct(name, fields);
  }

  public FunctionType functionT(Type resultType, Iterable<ItemSignature> parameters) {
    return typeFactory.function(resultType, parameters);
  }

  public Sides.Side upper() {
    return typeFactory.upper();
  }

  public Sides.Side lower() {
    return typeFactory.lower();
  }

  public Bounds unbounded() {
    return typeFactory.unbounded();
  }

  public Bounds oneSideBound(Side side, Type type) {
    return typeFactory.oneSideBound(side, type);
  }

  public Type strip(Type type) {
    return type.strip(typeFactory);
  }

  public boolean isAssignable(Type target, Type source) {
    return target.inequal(source, lower());
  }

  public boolean isParamAssignable(Type target, Type source) {
    return target.inequalParam(source, lower())
        && areConsistent(inferVariableBounds(target, source, lower()));
  }

  private boolean areConsistent(BoundsMap boundsMap) {
    return boundsMap.map().values().stream()
        .allMatch(b -> isAssignable(b.bounds().upper(), b.bounds().lower()));
  }

  public BoundsMap inferVariableBounds(List<Type> typesA, List<Type> typesB, Side side) {
    return merge(zip(typesA, typesB, inferFunction(side)));
  }

  public BoundsMap inferVariableBounds(Type typeA, Type typeB, Side side) {
    if (typeA instanceof Variable variable) {
      return boundsMap(new Bounded(variable, typeFactory.oneSideBound(side, typeB)));
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
    var resultVariables = Sets.map(
        resultTypes.variables(), v -> new Bounded(v, typeFactory.unbounded()));
    return mergeWith(boundedVariables, resultVariables);
  }

  public Type mapVariables(Type type, BoundsMap boundsMap, Side side) {
    return type.mapVariables(boundsMap, side, typeFactory);
  }

  public Type mergeUp(Type typeA, Type typeB) {
    return merge(typeA, typeB, upper());
  }

  public Type mergeDown(Type typeA, Type typeB) {
    return merge(typeA, typeB, lower());
  }

  public Type merge(Type typeA, Type typeB, Side direction) {
    return typeA.merge(typeB, direction, typeFactory);
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
    return typeFactory.merge(boundsA, boundsB);
  }
}
