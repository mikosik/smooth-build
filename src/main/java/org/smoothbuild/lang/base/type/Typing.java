package org.smoothbuild.lang.base.type;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.base.type.TypeNames.isVariableName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.lang.base.type.Sides.Side;

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

  public AnyType any() {
    return typeFactory.any();
  }

  public ArrayType array(Type elemType) {
    return typeFactory.array(elemType);
  }

  public BlobType blob() {
    return typeFactory.blob();
  }

  public BoolType bool() {
    return typeFactory.bool();
  }

  public IntType int_() {
    return typeFactory.int_();
  }

  public NothingType nothing() {
    return typeFactory.nothing();
  }

  public StringType string() {
    return typeFactory.string();
  }

  public StructType struct(String name, ImmutableList<ItemSignature> fields) {
    return typeFactory.struct(name, fields);
  }

  public FunctionType function(Type resultType, Iterable<ItemSignature> parameters) {
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

  public Type inferResultType(FunctionType functionType, List<Type> argumentTypes) {
    var boundedVariables = inferVariableBoundsInCall(functionType.resultType(),
        functionType.parameterTypes(), argumentTypes);
    return mapVariables(functionType.resultType(), boundedVariables, lower());
  }

  public BoundsMap inferVariableBoundsInCall(
      Type resultTypes, List<Type> parameterTypes, List<Type> argumentTypes) {
    var result = new HashMap<Variable, Bounded>();
    inferVariableBounds(parameterTypes, argumentTypes, lower(), result);
    resultTypes.variables().forEach(v -> result.merge(
        v, new Bounded(v, typeFactory.unbounded()), typeFactory::merge));
    return new BoundsMap(ImmutableMap.copyOf(result));
  }

  public BoundsMap inferVariableBounds(List<Type> typesA, List<Type> typesB, Side side) {
    var result = new HashMap<Variable, Bounded>();
    inferVariableBounds(typesA, typesB, side, result);
    return new BoundsMap(ImmutableMap.copyOf(result));
  }

  private void inferVariableBounds(List<Type> typesA, List<Type> typesB, Side side,
      HashMap<Variable, Bounded> result) {
    checkArgument(typesA.size() == typesB.size());
    for (int i = 0; i < typesA.size(); i++) {
      typesA.get(i).inferVariableBounds(typesB.get(i), side, typeFactory, result);
    }
  }

  public BoundsMap inferVariableBounds(Type typeA, Type typeB, Side side) {
    var result = new HashMap<Variable, Bounded>();
    typeA.inferVariableBounds(typeB, side, typeFactory, result);
    return new BoundsMap(ImmutableMap.copyOf(result));
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

  public Bounds merge(Bounds boundsA, Bounds boundsB) {
    return typeFactory.merge(boundsA, boundsB);
  }
}
