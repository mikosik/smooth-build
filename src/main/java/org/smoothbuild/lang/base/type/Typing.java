package org.smoothbuild.lang.base.type;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.base.type.api.ItemSignature.itemSignature;
import static org.smoothbuild.util.Lists.allMatch;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.Lists.zip;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.lang.base.type.api.AnyType;
import org.smoothbuild.lang.base.type.api.ArrayType;
import org.smoothbuild.lang.base.type.api.BaseType;
import org.smoothbuild.lang.base.type.api.BlobType;
import org.smoothbuild.lang.base.type.api.BoolType;
import org.smoothbuild.lang.base.type.api.Bounded;
import org.smoothbuild.lang.base.type.api.Bounds;
import org.smoothbuild.lang.base.type.api.BoundsMap;
import org.smoothbuild.lang.base.type.api.FunctionType;
import org.smoothbuild.lang.base.type.api.IntType;
import org.smoothbuild.lang.base.type.api.ItemSignature;
import org.smoothbuild.lang.base.type.api.NothingType;
import org.smoothbuild.lang.base.type.api.Sides;
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.lang.base.type.api.StringType;
import org.smoothbuild.lang.base.type.api.StructType;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.TypeFactory;
import org.smoothbuild.lang.base.type.api.Variable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

// TODO use switch pattern matching on JDK-17
@Singleton
public class Typing {
  private final TypeFactory typeFactory;
  private final Sides sides;

  @Inject
  public Typing(TypeFactory typeFactory) {
    this.typeFactory = typeFactory;
    this.sides = new Sides(any(), nothing());
  }

  /**
   * Inferable base types are types that can be inferred but `Any` type is not legal in smooth
   * language.
   */
  public ImmutableSet<BaseType> inferableBaseTypes() {
    return ImmutableSet.<BaseType>builder()
        .addAll(baseTypes())
        .add(typeFactory.any())
        .build();
  }

  /**
   * Base types that are legal in smooth language.
   */
  public ImmutableSet<BaseType> baseTypes() {
    return ImmutableSet.of(
        typeFactory.blob(),
        typeFactory.bool(),
        typeFactory.int_(),
        typeFactory.nothing(),
        typeFactory.string()
    );
  }

  public Variable variable(String name) {
    return typeFactory.variable(name);
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

  public Side upper() {
    return sides.upper();
  }

  public Side lower() {
    return sides.lower();
  }

  public Bounds unbounded() {
    return new Bounds(nothing(), any());
  }

  public Bounds oneSideBound(Side side, Type type) {
    return side.dispatch(
        () -> new Bounds(type, any()),
        () -> new Bounds(nothing(), type)
    );
  }

  public boolean contains(Type type, Type inner) {
    if (type.equals(inner)) {
      return true;
    } else if (type instanceof ArrayType arrayType) {
      return contains(arrayType.elemType(), inner);
    } else if (type instanceof FunctionType functionType) {
        return contains(functionType.resultType(), inner)
            || functionType.parameters().stream().anyMatch(p -> contains(p.type(), inner));
    }
    return false;
  }

  public boolean isAssignable(Type target, Type source) {
    return inequal(target, source, lower());
  }

  public boolean isParamAssignable(Type target, Type source) {
    return inequalParam(target, source, lower())
        && areConsistent(inferVariableBounds(target, source, lower()));
  }

  public boolean inequal(Type typeA, Type that, Side side) {
    return inequalImpl(typeA, that, side, this::inequal);
  }

  public boolean inequalParam(Type typeA, Type that, Side side) {
    return (typeA instanceof Variable)
        || inequalImpl(typeA, that, side, this::inequalParam);
  }

  private boolean inequalImpl(Type typeA, Type that, Side side,
      InequalFunction inequalityFunction) {
    return inequalByEdgeCases(typeA, that, side)
        || inequalByConstruction(typeA, that, side, inequalityFunction);
  }

  private boolean inequalByEdgeCases(Type typeA, Type that, Side side) {
    return that.equals(side.edge())
        || typeA.equals(side.reversed().edge());
  }

  private boolean inequalByConstruction(Type typeA, Type that, Side side,
      InequalFunction isInequal) {
    if (typeA instanceof ArrayType arrayA) {
      if (that instanceof ArrayType arrayB) {
        return isInequal.apply(arrayA.elemType(), arrayB.elemType(), side);
      }
    } else if (typeA instanceof FunctionType functionA) {
      if (that instanceof FunctionType functionB) {
        return isInequal.apply(functionA.resultType(), functionB.resultType(), side)
            && allMatch(
                functionA.parameterTypes(),
                functionB.parameterTypes(),
                (a, b) -> isInequal.apply(a, b, side.reversed()));
      }
    } else {
      return typeA.equals(that);
    }
    return false;
  }

  public static interface InequalFunction {
    public boolean apply(Type typeA, Type typeB, Side side);
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
        v, new Bounded(v, unbounded()), this::merge));
    return new BoundsMap(ImmutableMap.copyOf(result));
  }

  public BoundsMap inferVariableBounds(List<Type> typesA, List<Type> typesB, Side side) {
    var result = new HashMap<Variable, Bounded>();
    inferVariableBounds(typesA, typesB, side, result);
    return new BoundsMap(ImmutableMap.copyOf(result));
  }

  private void inferVariableBounds(List<Type> typesA, List<Type> typesB, Side side,
      Map<Variable, Bounded> result) {
    checkArgument(typesA.size() == typesB.size());
    for (int i = 0; i < typesA.size(); i++) {
      inferImpl(typesA.get(i), typesB.get(i), side, result);
    }
  }

  public BoundsMap inferVariableBounds(Type typeA, Type typeB, Side side) {
    var result = new HashMap<Variable, Bounded>();
    inferImpl(typeA, typeB, side, result);
    return new BoundsMap(ImmutableMap.copyOf(result));
  }

  private void inferImpl(Type typeA, Type typeB, Side side, Map<Variable, Bounded> result) {
    if (typeA instanceof Variable variable) {
      Bounded bounded = new Bounded(variable, oneSideBound(side, typeB));
      result.merge(variable, bounded, this::merge);
    } else if (typeA instanceof ArrayType arrayA) {
      if (typeB.equals(side.edge())) {
        inferImpl(arrayA.elemType(), side.edge(), side, result);
      } else if (typeB instanceof ArrayType arrayB) {
        inferImpl(arrayA.elemType(), arrayB.elemType(), side, result);
      }
    } else if (typeA instanceof FunctionType functionA) {
      if (typeB.equals(side.edge())) {
        Side reversed = side.reversed();
        inferImpl(functionA.resultType(), side.edge(), side, result);
        functionA.parameters().forEach(p -> inferImpl(p.type(), reversed.edge(), reversed, result));
      } else if (typeB instanceof FunctionType functionB
          && functionA.parameters().size() == functionB.parameters().size()) {
        Side reversed = side.reversed();
        inferImpl(functionA.resultType(), functionB.resultType(), side, result);
        for (int i = 0; i < functionA.parameters().size(); i++) {
          Type thisParamType = functionA.parameters().get(i).type();
          Type thatParamType = functionB.parameters().get(i).type();
          inferImpl(thisParamType, thatParamType, reversed, result);
        }
      }
    }
  }

  public Type strip(Type type) {
    if (type instanceof ArrayType arrayType) {
      var elemS = strip(arrayType.elemType());
      return createArrayType(arrayType, elemS);
    } else if (type instanceof FunctionType functionType) {
      var resultS = strip(functionType.resultType());
      var parametersS = map(functionType.parameters(), p -> itemSignature(strip(p.type())));
      return createFunctionType(functionType, resultS, parametersS);
    }
    return type;
  }

  public Type mapVariables(Type type, BoundsMap boundsMap, Side side) {
    if (type.isPolytype()) {
      if (type instanceof Variable variable) {
        return boundsMap.map().get(variable).bounds().get(side);
      } else if (type instanceof ArrayType arrayType) {
        Type elemTypeM = mapVariables(arrayType.elemType(), boundsMap, side);
        return createArrayType(arrayType, elemTypeM);
      } else if (type instanceof FunctionType functionType){
        var resultTypeM = mapVariables(functionType.resultType(), boundsMap, side);
        var parametersM = map(functionType.parameters(),
            p -> itemSignature(mapVariables(p.type(), boundsMap, side.reversed())));
        return createFunctionType(functionType, resultTypeM, parametersM);
      }
    }
    return type;
  }

  public Type mergeUp(Type typeA, Type typeB) {
    return merge(typeA, typeB, upper());
  }

  public Type mergeDown(Type typeA, Type typeB) {
    return merge(typeA, typeB, lower());
  }

  public Type merge(Type typeA, Type typeB, Side direction) {
    Type reversedEdge = direction.reversed().edge();
    if (reversedEdge.equals(typeB)) {
      return typeA;
    } else if (reversedEdge.equals(typeA)) {
      return typeB;
    } else if (typeA.equals(typeB)) {
      return strip(typeA);
    } else if (typeA instanceof ArrayType arrayA) {
      if (typeB instanceof ArrayType arrayB) {
        var elemA = arrayA.elemType();
        var elemB = arrayB.elemType();
        var elemM = merge(elemA, elemB, direction);
        if (elemA == elemM) {
          return arrayA;
        } else if (elemB == elemM) {
          return arrayB;
        } else {
          return typeFactory.array(elemM);
        }
      }
    } else if (typeA instanceof FunctionType functionA) {
      if (typeB instanceof FunctionType functionB) {
        if (functionA.parameters().size() == functionB.parameters().size()) {
          var resultA = functionA.resultType();
          var resultB = functionB.resultType();
          var resultM = merge(resultA, resultB, direction);
          var parameterTypesA = functionA.parameters();
          var parametersTypesB = functionB.parameters();
          var parametersM = zip(parameterTypesA, parametersTypesB,
              (a, b) -> itemSignature(merge(a.type(), b.type(), direction.reversed())));
          if (isFunctionTypeEqual(functionA, resultM, parametersM)) {
            return functionA;
          } else if (isFunctionTypeEqual(functionB, resultM, parametersM)){
            return functionB;
          } else {
            return typeFactory.function(resultM, parametersM);
          }
        }
      }
    }
    return direction.edge();
  }

  public Bounded merge(Bounded a, Bounded b) {
    return new Bounded(a.variable(), merge(a.bounds(), b.bounds()));
  }

  public Bounds merge(Bounds boundsA, Bounds boundsB) {
    return new Bounds(
        merge(boundsA.lower(), boundsB.lower(), this.upper()),
        merge(boundsA.upper(), boundsB.upper(), this.lower()));
  }

  private ArrayType createArrayType(ArrayType type, Type elemType) {
    if (type.elemType() == elemType) {
      return type;
    } else {
      return typeFactory.array(elemType);
    }
  }

  private FunctionType createFunctionType(FunctionType type, Type resultType,
      ImmutableList<ItemSignature> parameters) {
    if (isFunctionTypeEqual(type, resultType, parameters)) {
      return type;
    }
    return typeFactory.function(resultType, parameters);
  }

  private boolean isFunctionTypeEqual(FunctionType type,
      Type resultType, ImmutableList<ItemSignature> parameters) {
    return type.resultType() == resultType && type.parameters().equals(parameters);
  }
}
