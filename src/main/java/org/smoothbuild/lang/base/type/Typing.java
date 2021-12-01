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
import org.smoothbuild.lang.base.type.api.Var;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class Typing<T extends Type> {
  private final TypeFactory<T> factory;

  public Typing(TypeFactory<T> factory) {
    this.factory = factory;
  }

  public boolean contains(T type, T inner) {
    if (type.equals(inner)) {
      return true;
    }
    return switch (type) {
      case ArrayType arrayType -> contains((T) arrayType.elem(), inner);
      case FuncType funcType ->  contains((T) funcType.res(), inner)
          || funcType.params().stream().anyMatch(t -> contains((T) t, inner));
      default -> false;
    };
  }

  public boolean isAssignable(T target, T source) {
    return inequal(target, source, factory.lower());
  }

  public boolean isParamAssignable(T target, T source) {
    return inequalParam(target, source, factory.lower())
        && areConsistent(inferVarBounds(target, source, factory.lower()));
  }

  public boolean inequal(Type typeA, Type that, Side<T> side) {
    return inequalImpl(typeA, that, side, this::inequal);
  }

  public boolean inequalParam(Type typeA, Type that, Side<T> side) {
    return (typeA instanceof Var)
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

  private boolean inequalByConstruction(Type t1, Type t2, Side<T> side,
      InequalFunc<T> isInequal) {
    return switch (t1) {
      case ArrayType a1 -> t2 instanceof ArrayType a2 && isInequal.apply(a1.elem(), a2.elem(), side);
      case FuncType f1 -> t2 instanceof FuncType f2
          && isInequal.apply(f1.res(), f2.res(), side)
          && allMatch(f1.params(), f2.params(), (a, b) -> isInequal.apply(a, b, side.reversed()));
      default -> t1.equals(t2);
    };
  }

  public static interface InequalFunc<T> {
    public boolean apply(Type typeA, Type typeB, Side<T> side);
  }

  private boolean areConsistent(BoundsMap<T> boundsMap) {
    return boundsMap.map().values().stream()
        .allMatch(b -> isAssignable(b.bounds().upper(), b.bounds().lower()));
  }

  public BoundsMap<T> inferVarBoundsInCall(
      List<? extends T> paramTypes, List<? extends T> argTypes) {
    var result = new HashMap<Var, Bounded<T>>();
    inferVarBounds(paramTypes, argTypes, factory.lower(), result);
    return new BoundsMap<>(ImmutableMap.copyOf(result));
  }

  public BoundsMap<T> inferVarBounds(List<? extends T> typesA, List<? extends T> typesB,
      Side<T> side) {
    var result = new HashMap<Var, Bounded<T>>();
    inferVarBounds(typesA, typesB, side, result);
    return new BoundsMap<>(ImmutableMap.copyOf(result));
  }

  private void inferVarBounds(List<? extends T> typesA, List<? extends T> typesB,
      Side<T> side, Map<Var, Bounded<T>> result) {
    checkArgument(typesA.size() == typesB.size());
    for (int i = 0; i < typesA.size(); i++) {
      inferImpl(typesA.get(i), typesB.get(i), side, result);
    }
  }

  public BoundsMap<T> inferVarBounds(T typeA, T typeB, Side<T> side) {
    var result = new HashMap<Var, Bounded<T>>();
    inferImpl(typeA, typeB, side, result);
    return new BoundsMap<>(ImmutableMap.copyOf(result));
  }

  private void inferImpl(T t1, T t2, Side<T> side, Map<Var, Bounded<T>> result) {
    switch (t1) {
      case Var v -> result.merge(v, new Bounded<>(v, factory.oneSideBound(side, t2)), this::merge);
      case ArrayType arrayA -> {
        if (t2.equals(side.edge())) {
          inferImpl((T) arrayA.elem(), side.edge(), side, result);
        } else if (t2 instanceof ArrayType arrayB) {
          inferImpl((T) arrayA.elem(), (T) arrayB.elem(), side, result);
        }
      }
      case FuncType f1 -> {
        if (t2.equals(side.edge())) {
          var reversed = side.reversed();
          inferImpl((T) f1.res(), side.edge(), side, result);
          f1.params().forEach(t -> inferImpl((T) t, reversed.edge(), reversed, result));
        } else if (t2 instanceof FuncType f2 && f1.params().size() == f2.params().size()) {
          var reversed = side.reversed();
          inferImpl((T) f1.res(), (T) f2.res(), side, result);
          for (int i = 0; i < f1.params().size(); i++) {
            Type paramType1 = f1.params().get(i);
            Type paramType2 = f2.params().get(i);
            inferImpl((T) paramType1, (T) paramType2, reversed, result);
          }
        }
      }
      default -> {}
    }
  }

  public T mapVars(T type, BoundsMap<T> boundsMap, Side<T> side) {
    if (type.isPolytype()) {
      return switch (type) {
        case Var var -> {
          Bounded<T> bounded = boundsMap.map().get(var);
          if (bounded == null) {
            yield type;
          } else {
            yield bounded.bounds().get(side);
          }
        }
        case ArrayType arrayType -> {
          T elemTypeM = mapVars((T) arrayType.elem(), boundsMap, side);
          yield  (T) createArrayType(arrayType, elemTypeM);
        }
        case FuncType funcType -> {
          var resultTypeM = mapVars((T) funcType.res(), boundsMap, side);
          ImmutableList<T> paramsM = map(
              funcType.params(),
              p -> mapVars((T) p, boundsMap, side.reversed()));
          yield  (T) createFuncType(funcType, resultTypeM, paramsM);
        }
        default -> type;
      };
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
          var resultM = merge((T) funcA.res(), (T) funcB.res(), direction);
          var paramsM = zip(funcA.params(), funcB.params(),
              (a, b) -> merge((T) a, (T) b, direction.reversed()));
          if (isFuncTypeEqual(funcA, resultM, paramsM)) {
            return typeA;
          } else if (isFuncTypeEqual(funcB, resultM, paramsM)){
            return typeB;
          } else {
            return (T) factory.func(resultM, paramsM);
          }
        }
      }
    }
    return direction.edge();
  }

  public Bounded<T> merge(Bounded<T> a, Bounded<T> b) {
    return new Bounded<>(a.var(), merge(a.bounds(), b.bounds()));
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
    return factory.func(resultType, params);
  }

  private boolean isFuncTypeEqual(
      FuncType type, Type result, ImmutableList<T> params) {
    return type.res() == result && type.params().equals(params);
  }

  public TypeFactory<T> factory() {
    return factory;
  }
}
