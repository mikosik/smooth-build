package org.smoothbuild.lang.base.type;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.collect.Lists.allMatch;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Lists.zip;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.smoothbuild.lang.base.type.api.ArrayType;
import org.smoothbuild.lang.base.type.api.Bounded;
import org.smoothbuild.lang.base.type.api.Bounds;
import org.smoothbuild.lang.base.type.api.BoundsMap;
import org.smoothbuild.lang.base.type.api.FunctionType;
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.TypeFactory;
import org.smoothbuild.lang.base.type.api.Variable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

// TODO use switch pattern matching on JDK-17
public class Typing<T extends Type> {
  private final TypeFactory<T> factory;

  public Typing(TypeFactory<T> factory) {
    this.factory = factory;
  }

  public boolean contains(T type, T inner) {
    if (type.equals(inner)) {
      return true;
    } else if (type instanceof ArrayType arrayType) {
      return contains((T) arrayType.element(), inner);
    } else if (type instanceof FunctionType functionType) {
        return contains((T) functionType.result(), inner)
            || functionType.parameters().stream().anyMatch(t -> contains((T) t, inner));
    }
    return false;
  }

  public boolean isAssignable(T target, T source) {
    return inequal(target, source, factory.lower());
  }

  public boolean isParamAssignable(T target, T source) {
    return inequalParam(target, source, factory.lower())
        && areConsistent(inferVariableBounds(target, source, factory.lower()));
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
        .allMatch(b -> isAssignable((T) b.bounds().upper(), (T) b.bounds().lower()));
  }

  public BoundsMap inferVariableBoundsInCall(
      T resultTypes, List<? extends T> parameterTypes, List<? extends T> argumentTypes) {
    var result = new HashMap<Variable, Bounded>();
    inferVariableBounds(parameterTypes, argumentTypes, factory.lower(), result);
    resultTypes.variables().forEach(v -> result.merge(
        v, new Bounded(v, factory.unbounded()), this::merge));
    return new BoundsMap(ImmutableMap.copyOf(result));
  }

  public BoundsMap inferVariableBounds(List<? extends T> typesA, List<? extends T> typesB,
      Side side) {
    var result = new HashMap<Variable, Bounded>();
    inferVariableBounds(typesA, typesB, side, result);
    return new BoundsMap(ImmutableMap.copyOf(result));
  }

  private void inferVariableBounds(List<? extends T> typesA, List<? extends T> typesB,
      Side side, Map<Variable, Bounded> result) {
    checkArgument(typesA.size() == typesB.size());
    for (int i = 0; i < typesA.size(); i++) {
      inferImpl(typesA.get(i), typesB.get(i), side, result);
    }
  }

  public BoundsMap inferVariableBounds(T typeA, T typeB, Side side) {
    var result = new HashMap<Variable, Bounded>();
    inferImpl(typeA, typeB, side, result);
    return new BoundsMap(ImmutableMap.copyOf(result));
  }

  private void inferImpl(T typeA, T typeB, Side side, Map<Variable, Bounded> result) {
    if (typeA instanceof Variable variable) {
      Bounded bounded = new Bounded(variable, factory.oneSideBound(side, typeB));
      result.merge(variable, bounded, this::merge);
    } else if (typeA instanceof ArrayType arrayA) {
      if (typeB.equals(side.edge())) {
        inferImpl((T) arrayA.element(), (T) side.edge(), side, result);
      } else if (typeB instanceof ArrayType arrayB) {
        inferImpl((T) arrayA.element(), (T) arrayB.element(), side, result);
      }
    } else if (typeA instanceof FunctionType functionA) {
      if (typeB.equals(side.edge())) {
        Side reversed = side.reversed();
        inferImpl((T) functionA.result(), (T) side.edge(), side, result);
        functionA.parameters().forEach(t -> inferImpl((T) t, (T) reversed.edge(), reversed, result));
      } else if (typeB instanceof FunctionType functionB
          && functionA.parameters().size() == functionB.parameters().size()) {
        Side reversed = side.reversed();
        inferImpl((T) functionA.result(), (T) functionB.result(), side, result);
        for (int i = 0; i < functionA.parameters().size(); i++) {
          Type thisParamType = functionA.parameters().get(i);
          Type thatParamType = functionB.parameters().get(i);
          inferImpl((T) thisParamType, (T) thatParamType, reversed, result);
        }
      }
    }
  }

  public T mapVariables(T type, BoundsMap boundsMap, Side side) {
    if (type.isPolytype()) {
      if (type instanceof Variable variable) {
        return (T) boundsMap.map().get(variable).bounds().get(side);
      } else if (type instanceof ArrayType arrayType) {
        T elemTypeM = mapVariables((T) arrayType.element(), boundsMap, side);
        return (T) createArrayType(arrayType, elemTypeM);
      } else if (type instanceof FunctionType functionType){
        var resultTypeM = mapVariables((T) functionType.result(), boundsMap, side);
        ImmutableList<T> parametersM = map(
            functionType.parameters(),
            p -> mapVariables((T) p, boundsMap, side.reversed()));
        return (T) createFunctionType(functionType, (T) resultTypeM, parametersM);
      }
    }
    return type;
  }

  public T mergeUp(T typeA, T typeB) {
    return merge(typeA, typeB, factory.upper());
  }

  public T mergeDown(T typeA, T typeB) {
    return merge(typeA, typeB, factory.lower());
  }

  public T merge(T typeA, T typeB, Side direction) {
    Type reversedEdge = direction.reversed().edge();
    if (reversedEdge.equals(typeB)) {
      return typeA;
    } else if (reversedEdge.equals(typeA)) {
      return typeB;
    } else if (typeA.equals(typeB)) {
      return typeA;
    } else if (typeA instanceof ArrayType arrayA) {
      if (typeB instanceof ArrayType arrayB) {
        var elemA = (T) arrayA.element();
        var elemB = (T) arrayB.element();
        var elemM = merge(elemA, elemB, direction);
        if (elemA == elemM) {
          return typeA;
        } else if (elemB == elemM) {
          return typeB;
        } else {
          return (T) factory.array(elemM);
        }
      }
    } else if (typeA instanceof FunctionType functionA) {
      if (typeB instanceof FunctionType functionB) {
        if (functionA.parameters().size() == functionB.parameters().size()) {
          var resultM = merge((T) functionA.result(), (T) functionB.result(), direction);
          var parametersM = zip(functionA.parameters(), functionB.parameters(),
              (a, b) -> merge((T) a, (T) b, direction.reversed()));
          if (isFunctionTypeEqual(functionA, resultM, parametersM)) {
            return typeA;
          } else if (isFunctionTypeEqual(functionB, resultM, parametersM)){
            return typeB;
          } else {
            return (T) factory.function(resultM, parametersM);
          }
        }
      }
    }
    return (T) direction.edge();
  }

  public Bounded merge(Bounded a, Bounded b) {
    return new Bounded(a.variable(), merge(a.bounds(), b.bounds()));
  }

  public Bounds merge(Bounds boundsA, Bounds boundsB) {
    return new Bounds(
        merge((T) boundsA.lower(), (T) boundsB.lower(), factory.upper()),
        merge((T) boundsA.upper(), (T) boundsB.upper(), factory.lower()));
  }

  private ArrayType createArrayType(ArrayType type, T elemType) {
    if (type.element() == elemType) {
      return type;
    } else {
      return factory.array(elemType);
    }
  }

  private FunctionType createFunctionType(
      FunctionType type, T resultType, ImmutableList<T> parameters) {
    if (isFunctionTypeEqual(type, resultType, parameters)) {
      return type;
    }
    return factory.function(resultType, parameters);
  }

  private boolean isFunctionTypeEqual(
      FunctionType type, Type result, ImmutableList<T> parameters) {
    return type.result() == result && type.parameters().equals(parameters);
  }
}
