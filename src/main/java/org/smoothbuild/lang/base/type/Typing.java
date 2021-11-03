package org.smoothbuild.lang.base.type;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.collect.Lists.allMatch;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Lists.zip;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.lang.base.type.api.ArrayType;
import org.smoothbuild.lang.base.type.api.Bounded;
import org.smoothbuild.lang.base.type.api.Bounds;
import org.smoothbuild.lang.base.type.api.BoundsMap;
import org.smoothbuild.lang.base.type.api.FunctionType;
import org.smoothbuild.lang.base.type.api.Sides;
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.TypeFactory;
import org.smoothbuild.lang.base.type.api.Variable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

// TODO use switch pattern matching on JDK-17
@Singleton
public class Typing {
  private final TypeFactory typeFactory;
  private final Sides sides;

  @Inject
  public Typing(TypeFactory typeFactory) {
    this.typeFactory = typeFactory;
    this.sides = new Sides(typeFactory.any(), typeFactory.nothing());
  }

  public Side upper() {
    return sides.upper();
  }

  public Side lower() {
    return sides.lower();
  }

  public Bounds unbounded() {
    return new Bounds(typeFactory.nothing(), typeFactory.any());
  }

  public Bounds oneSideBound(Side side, Type type) {
    return side.dispatch(
        () -> new Bounds(type, typeFactory.any()),
        () -> new Bounds(typeFactory.nothing(), type)
    );
  }

  public boolean contains(Type type, Type inner) {
    if (type.equals(inner)) {
      return true;
    } else if (type instanceof ArrayType arrayType) {
      return contains(arrayType.element(), inner);
    } else if (type instanceof FunctionType functionType) {
        return contains(functionType.result(), inner)
            || functionType.parameters().stream().anyMatch(t -> contains(t, inner));
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
        return isInequal.apply(arrayA.element(), arrayB.element(), side);
      }
    } else if (typeA instanceof FunctionType functionA) {
      if (that instanceof FunctionType functionB) {
        return isInequal.apply(functionA.result(), functionB.result(), side)
            && allMatch(
                functionA.parameters(),
                functionB.parameters(),
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

  public BoundsMap inferVariableBoundsInCall(
      Type resultTypes, List<? extends Type> parameterTypes, List<? extends Type> argumentTypes) {
    var result = new HashMap<Variable, Bounded>();
    inferVariableBounds(parameterTypes, argumentTypes, lower(), result);
    resultTypes.variables().forEach(v -> result.merge(
        v, new Bounded(v, unbounded()), this::merge));
    return new BoundsMap(ImmutableMap.copyOf(result));
  }

  public BoundsMap inferVariableBounds(List<? extends Type> typesA, List<? extends Type> typesB,
      Side side) {
    var result = new HashMap<Variable, Bounded>();
    inferVariableBounds(typesA, typesB, side, result);
    return new BoundsMap(ImmutableMap.copyOf(result));
  }

  private void inferVariableBounds(List<? extends Type> typesA, List<? extends Type> typesB,
      Side side, Map<Variable, Bounded> result) {
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
        inferImpl(arrayA.element(), side.edge(), side, result);
      } else if (typeB instanceof ArrayType arrayB) {
        inferImpl(arrayA.element(), arrayB.element(), side, result);
      }
    } else if (typeA instanceof FunctionType functionA) {
      if (typeB.equals(side.edge())) {
        Side reversed = side.reversed();
        inferImpl(functionA.result(), side.edge(), side, result);
        functionA.parameters().forEach(t -> inferImpl(t, reversed.edge(), reversed, result));
      } else if (typeB instanceof FunctionType functionB
          && functionA.parameters().size() == functionB.parameters().size()) {
        Side reversed = side.reversed();
        inferImpl(functionA.result(), functionB.result(), side, result);
        for (int i = 0; i < functionA.parameters().size(); i++) {
          Type thisParamType = functionA.parameters().get(i);
          Type thatParamType = functionB.parameters().get(i);
          inferImpl(thisParamType, thatParamType, reversed, result);
        }
      }
    }
  }

  public Type mapVariables(Type type, BoundsMap boundsMap, Side side) {
    if (type.isPolytype()) {
      if (type instanceof Variable variable) {
        return boundsMap.map().get(variable).bounds().get(side);
      } else if (type instanceof ArrayType arrayType) {
        Type elemTypeM = mapVariables(arrayType.element(), boundsMap, side);
        return createArrayType(arrayType, elemTypeM);
      } else if (type instanceof FunctionType functionType){
        var resultTypeM = mapVariables(functionType.result(), boundsMap, side);
        var parametersM = map(
            functionType.parameters(),
            p -> mapVariables(p, boundsMap, side.reversed()));
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
      return typeA;
    } else if (typeA instanceof ArrayType arrayA) {
      if (typeB instanceof ArrayType arrayB) {
        var elemA = arrayA.element();
        var elemB = arrayB.element();
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
          var resultM = merge(functionA.result(), functionB.result(), direction);
          var parametersM = zip(functionA.parameters(), functionB.parameters(),
              (a, b) -> merge(a, b, direction.reversed()));
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
    if (type.element() == elemType) {
      return type;
    } else {
      return typeFactory.array(elemType);
    }
  }

  private FunctionType createFunctionType(
      FunctionType type, Type resultType, ImmutableList<Type> parameters) {
    if (isFunctionTypeEqual(type, resultType, parameters)) {
      return type;
    }
    return typeFactory.function(resultType, parameters);
  }

  private boolean isFunctionTypeEqual(
      FunctionType type, Type result, ImmutableList<Type> parameters) {
    return type.result() == result && type.parameters().equals(parameters);
  }
}
