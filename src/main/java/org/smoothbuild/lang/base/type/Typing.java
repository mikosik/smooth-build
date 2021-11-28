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
            || functionType.params().stream().anyMatch(t -> contains((T) t, inner));
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

  public boolean inequal(Type typeA, Type that, Side<T> side) {
    return inequalImpl(typeA, that, side, this::inequal);
  }

  public boolean inequalParam(Type typeA, Type that, Side<T> side) {
    return (typeA instanceof Variable)
        || inequalImpl(typeA, that, side, this::inequalParam);
  }

  private boolean inequalImpl(Type typeA, Type that, Side<T> side,
      InequalFunction<T> inequalityFunction) {
    return inequalByEdgeCases(typeA, that, side)
        || inequalByConstruction(typeA, that, side, inequalityFunction);
  }

  private boolean inequalByEdgeCases(Type typeA, Type that, Side<T> side) {
    return that.equals(side.edge())
        || typeA.equals(side.reversed().edge());
  }

  private boolean inequalByConstruction(Type typeA, Type that, Side<T> side,
      InequalFunction<T> isInequal) {
    if (typeA instanceof ArrayType arrayA) {
      if (that instanceof ArrayType arrayB) {
        return isInequal.apply(arrayA.element(), arrayB.element(), side);
      }
    } else if (typeA instanceof FunctionType functionA) {
      if (that instanceof FunctionType functionB) {
        return isInequal.apply(functionA.result(), functionB.result(), side)
            && allMatch(
                functionA.params(),
                functionB.params(),
                (a, b) -> isInequal.apply(a, b, side.reversed()));
      }
    } else {
      return typeA.equals(that);
    }
    return false;
  }

  public static interface InequalFunction<T> {
    public boolean apply(Type typeA, Type typeB, Side<T> side);
  }

  private boolean areConsistent(BoundsMap<T> boundsMap) {
    return boundsMap.map().values().stream()
        .allMatch(b -> isAssignable(b.bounds().upper(), b.bounds().lower()));
  }

  public BoundsMap<T> inferVariableBoundsInCall(
      List<? extends T> paramTypes, List<? extends T> argumentTypes) {
    var result = new HashMap<Variable, Bounded<T>>();
    inferVariableBounds(paramTypes, argumentTypes, factory.lower(), result);
    return new BoundsMap<>(ImmutableMap.copyOf(result));
  }

  public BoundsMap<T> inferVariableBounds(List<? extends T> typesA, List<? extends T> typesB,
      Side<T> side) {
    var result = new HashMap<Variable, Bounded<T>>();
    inferVariableBounds(typesA, typesB, side, result);
    return new BoundsMap<>(ImmutableMap.copyOf(result));
  }

  private void inferVariableBounds(List<? extends T> typesA, List<? extends T> typesB,
      Side<T> side, Map<Variable, Bounded<T>> result) {
    checkArgument(typesA.size() == typesB.size());
    for (int i = 0; i < typesA.size(); i++) {
      inferImpl(typesA.get(i), typesB.get(i), side, result);
    }
  }

  public BoundsMap<T> inferVariableBounds(T typeA, T typeB, Side<T> side) {
    var result = new HashMap<Variable, Bounded<T>>();
    inferImpl(typeA, typeB, side, result);
    return new BoundsMap<>(ImmutableMap.copyOf(result));
  }

  private void inferImpl(T typeA, T typeB, Side<T> side, Map<Variable, Bounded<T>> result) {
    if (typeA instanceof Variable variable) {
      var bounded = new Bounded<>(variable, factory.oneSideBound(side, typeB));
      result.merge(variable, bounded, this::merge);
    } else if (typeA instanceof ArrayType arrayA) {
      if (typeB.equals(side.edge())) {
        inferImpl((T) arrayA.element(), side.edge(), side, result);
      } else if (typeB instanceof ArrayType arrayB) {
        inferImpl((T) arrayA.element(), (T) arrayB.element(), side, result);
      }
    } else if (typeA instanceof FunctionType functionA) {
      if (typeB.equals(side.edge())) {
        var reversed = side.reversed();
        inferImpl((T) functionA.result(), side.edge(), side, result);
        functionA.params().forEach(t -> inferImpl((T) t, reversed.edge(), reversed, result));
      } else if (typeB instanceof FunctionType functionB
          && functionA.params().size() == functionB.params().size()) {
        var reversed = side.reversed();
        inferImpl((T) functionA.result(), (T) functionB.result(), side, result);
        for (int i = 0; i < functionA.params().size(); i++) {
          Type thisParamType = functionA.params().get(i);
          Type thatParamType = functionB.params().get(i);
          inferImpl((T) thisParamType, (T) thatParamType, reversed, result);
        }
      }
    }
  }

  public T mapVariables(T type, BoundsMap<T> boundsMap, Side<T> side) {
    if (type.isPolytype()) {
      if (type instanceof Variable variable) {
        Bounded<T> bounded = boundsMap.map().get(variable);
        if (bounded == null) {
          return type;
        } else {
          return bounded.bounds().get(side);
        }
      } else if (type instanceof ArrayType arrayType) {
        T elemTypeM = mapVariables((T) arrayType.element(), boundsMap, side);
        return (T) createArrayType(arrayType, elemTypeM);
      } else if (type instanceof FunctionType functionType){
        var resultTypeM = mapVariables((T) functionType.result(), boundsMap, side);
        ImmutableList<T> paramsM = map(
            functionType.params(),
            p -> mapVariables((T) p, boundsMap, side.reversed()));
        return (T) createFunctionType(functionType, resultTypeM, paramsM);
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

  public T merge(T typeA, T typeB, Side<T> direction) {
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
        if (functionA.params().size() == functionB.params().size()) {
          var resultM = merge((T) functionA.result(), (T) functionB.result(), direction);
          var paramsM = zip(functionA.params(), functionB.params(),
              (a, b) -> merge((T) a, (T) b, direction.reversed()));
          if (isFunctionTypeEqual(functionA, resultM, paramsM)) {
            return typeA;
          } else if (isFunctionTypeEqual(functionB, resultM, paramsM)){
            return typeB;
          } else {
            return (T) factory.function(resultM, paramsM);
          }
        }
      }
    }
    return direction.edge();
  }

  public Bounded<T> merge(Bounded<T> a, Bounded<T> b) {
    return new Bounded<>(a.variable(), merge(a.bounds(), b.bounds()));
  }

  public Bounds<T> merge(Bounds<T> boundsA, Bounds<T> boundsB) {
    return new Bounds<>(
        merge(boundsA.lower(), boundsB.lower(), factory.upper()),
        merge(boundsA.upper(), boundsB.upper(), factory.lower()));
  }

  private ArrayType createArrayType(ArrayType type, T elemType) {
    if (type.element() == elemType) {
      return type;
    } else {
      return factory.array(elemType);
    }
  }

  private FunctionType createFunctionType(
      FunctionType type, T resultType, ImmutableList<T> params) {
    if (isFunctionTypeEqual(type, resultType, params)) {
      return type;
    }
    return factory.function(resultType, params);
  }

  private boolean isFunctionTypeEqual(
      FunctionType type, Type result, ImmutableList<T> params) {
    return type.result() == result && type.params().equals(params);
  }

  public TypeFactory<T> factory() {
    return factory;
  }
}
