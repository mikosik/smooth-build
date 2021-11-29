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
import org.smoothbuild.lang.base.type.api.FuncType;
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
      return contains((T) arrayType.elem(), inner);
    } else if (type instanceof FuncType funcType) {
        return contains((T) funcType.result(), inner)
            || funcType.params().stream().anyMatch(t -> contains((T) t, inner));
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
      InequalFunc<T> inequalityFunc) {
    return inequalByEdgeCases(typeA, that, side)
        || inequalByConstruction(typeA, that, side, inequalityFunc);
  }

  private boolean inequalByEdgeCases(Type typeA, Type that, Side<T> side) {
    return that.equals(side.edge())
        || typeA.equals(side.reversed().edge());
  }

  private boolean inequalByConstruction(Type typeA, Type that, Side<T> side,
      InequalFunc<T> isInequal) {
    if (typeA instanceof ArrayType arrayA) {
      if (that instanceof ArrayType arrayB) {
        return isInequal.apply(arrayA.elem(), arrayB.elem(), side);
      }
    } else if (typeA instanceof FuncType funcA) {
      if (that instanceof FuncType funcB) {
        return isInequal.apply(funcA.result(), funcB.result(), side)
            && allMatch(
                funcA.params(),
                funcB.params(),
                (a, b) -> isInequal.apply(a, b, side.reversed()));
      }
    } else {
      return typeA.equals(that);
    }
    return false;
  }

  public static interface InequalFunc<T> {
    public boolean apply(Type typeA, Type typeB, Side<T> side);
  }

  private boolean areConsistent(BoundsMap<T> boundsMap) {
    return boundsMap.map().values().stream()
        .allMatch(b -> isAssignable(b.bounds().upper(), b.bounds().lower()));
  }

  public BoundsMap<T> inferVariableBoundsInCall(
      List<? extends T> paramTypes, List<? extends T> argTypes) {
    var result = new HashMap<Variable, Bounded<T>>();
    inferVariableBounds(paramTypes, argTypes, factory.lower(), result);
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
        inferImpl((T) arrayA.elem(), side.edge(), side, result);
      } else if (typeB instanceof ArrayType arrayB) {
        inferImpl((T) arrayA.elem(), (T) arrayB.elem(), side, result);
      }
    } else if (typeA instanceof FuncType funcA) {
      if (typeB.equals(side.edge())) {
        var reversed = side.reversed();
        inferImpl((T) funcA.result(), side.edge(), side, result);
        funcA.params().forEach(t -> inferImpl((T) t, reversed.edge(), reversed, result));
      } else if (typeB instanceof FuncType funcB
          && funcA.params().size() == funcB.params().size()) {
        var reversed = side.reversed();
        inferImpl((T) funcA.result(), (T) funcB.result(), side, result);
        for (int i = 0; i < funcA.params().size(); i++) {
          Type thisParamType = funcA.params().get(i);
          Type thatParamType = funcB.params().get(i);
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
        T elemTypeM = mapVariables((T) arrayType.elem(), boundsMap, side);
        return (T) createArrayType(arrayType, elemTypeM);
      } else if (type instanceof FuncType funcType){
        var resultTypeM = mapVariables((T) funcType.result(), boundsMap, side);
        ImmutableList<T> paramsM = map(
            funcType.params(),
            p -> mapVariables((T) p, boundsMap, side.reversed()));
        return (T) createFuncType(funcType, resultTypeM, paramsM);
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
        var elemA = (T) arrayA.elem();
        var elemB = (T) arrayB.elem();
        var elemM = merge(elemA, elemB, direction);
        if (elemA == elemM) {
          return typeA;
        } else if (elemB == elemM) {
          return typeB;
        } else {
          return (T) factory.array(elemM);
        }
      }
    } else if (typeA instanceof FuncType funcA) {
      if (typeB instanceof FuncType funcB) {
        if (funcA.params().size() == funcB.params().size()) {
          var resultM = merge((T) funcA.result(), (T) funcB.result(), direction);
          var paramsM = zip(funcA.params(), funcB.params(),
              (a, b) -> merge((T) a, (T) b, direction.reversed()));
          if (isFuncTypeEqual(funcA, resultM, paramsM)) {
            return typeA;
          } else if (isFuncTypeEqual(funcB, resultM, paramsM)){
            return typeB;
          } else {
            return (T) factory.abstFunc(resultM, paramsM);
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
    if (type.elem() == elemType) {
      return type;
    } else {
      return factory.array(elemType);
    }
  }

  private FuncType createFuncType(
      FuncType type, T resultType, ImmutableList<T> params) {
    if (isFuncTypeEqual(type, resultType, params)) {
      return type;
    }
    return factory.abstFunc(resultType, params);
  }

  private boolean isFuncTypeEqual(
      FuncType type, Type result, ImmutableList<T> params) {
    return type.result() == result && type.params().equals(params);
  }

  public TypeFactory<T> factory() {
    return factory;
  }
}
