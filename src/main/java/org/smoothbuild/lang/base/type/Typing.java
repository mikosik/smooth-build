package org.smoothbuild.lang.base.type;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.collect.Lists.allMatch;
import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Lists.zip;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.smoothbuild.lang.base.type.api.ArrayT;
import org.smoothbuild.lang.base.type.api.Bounded;
import org.smoothbuild.lang.base.type.api.Bounds;
import org.smoothbuild.lang.base.type.api.FuncT;
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.lang.base.type.api.TupleT;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.TypeFactory;
import org.smoothbuild.lang.base.type.api.Var;
import org.smoothbuild.lang.base.type.api.VarBounds;

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
      case ArrayT arrayT -> contains((T) arrayT.elem(), inner);
      case FuncT funcT ->  contains((T) funcT.res(), inner)
          || funcT.params().stream().anyMatch(t -> contains((T) t, inner));
      case TupleT tupleT -> tupleT.items().stream().anyMatch(t -> contains((T) t, inner));
      default -> false;
    };
  }

  public T inferCallResT(FuncT callableT, ImmutableList<T> argTs,
      Supplier<RuntimeException> illegalArgsExcThrower) {
    ImmutableList<T> paramTs = (ImmutableList<T>) callableT.params();
    allMatchOtherwise(
        paramTs,
        argTs,
        this::isParamAssignable,
        (expectedSize, actualSize) -> { throw illegalArgsExcThrower.get(); },
        i -> { throw illegalArgsExcThrower.get(); }
    );
    var varBounds = inferVarBoundsLower(paramTs, argTs);
    T res = (T) callableT.res();
    return mapVarsLower(res, varBounds);
  }

  public boolean isAssignable(T target, T source) {
    return inequal(target, source, factory.lower());
  }

  public boolean isParamAssignable(T target, T source) {
    return inequalParam(target, source, factory.lower())
        && areConsistent(inferVarBoundsLower(target, source));
  }

  public boolean inequal(Type type1, Type type2, Side<T> side) {
    return inequalImpl(type1, type2, side, this::inequal);
  }

  public boolean inequalParam(Type type1, Type type2, Side<T> side) {
    return (type1 instanceof Var)
        || inequalImpl(type1, type2, side, this::inequalParam);
  }

  private boolean inequalImpl(Type type1, Type type2, Side<T> side,
      InequalFunc<T> inequalityFunc) {
    return inequalByEdgeCases(type1, type2, side)
        || inequalByConstruction(type1, type2, side, inequalityFunc);
  }

  private boolean inequalByEdgeCases(Type type1, Type type2, Side<T> side) {
    return type2.equals(side.edge())
        || type1.equals(side.reversed().edge());
  }

  private boolean inequalByConstruction(Type t1, Type t2, Side<T> side,
      InequalFunc<T> isInequal) {
    return switch (t1) {
      case ArrayT a1 -> t2 instanceof ArrayT a2 && isInequal.apply(a1.elem(), a2.elem(), side);
      case FuncT f1 -> t2 instanceof FuncT f2
          && isInequal.apply(f1.res(), f2.res(), side)
          && allMatch(f1.params(), f2.params(), (a, b) -> isInequal.apply(a, b, side.reversed()));
      case TupleT tup1 -> t2 instanceof TupleT tup2
          && allMatch(tup1.items(), tup2.items(), (a, b) -> isInequal.apply(a, b, side));
      default -> t1.equals(t2);
    };
  }

  public static interface InequalFunc<T> {
    public boolean apply(Type type1, Type type2, Side<T> side);
  }

  private boolean areConsistent(VarBounds<T> varBounds) {
    return varBounds.map().values().stream()
        .allMatch(b -> isAssignable(b.bounds().upper(), b.bounds().lower()));
  }

  public VarBounds<T> inferVarBoundsLower(List<? extends T> types1, List<? extends T> types2) {
    return inferVarBounds(types1, types2, factory.lower());
  }

  public VarBounds<T> inferVarBounds(
      List<? extends T> types1, List<? extends T> types2, Side<T> side) {
    checkArgument(types1.size() == types2.size());
    var result = new HashMap<Var, Bounded<T>>();
    for (int i = 0; i < types1.size(); i++) {
      inferImpl(types1.get(i), types2.get(i), side, result);
    }
    return new VarBounds<>(ImmutableMap.copyOf(result));
  }

  public VarBounds<T> inferVarBoundsLower(T type1, T type2) {
    return inferVarBounds(type1, type2, factory().lower());
  }

  public VarBounds<T> inferVarBounds(T type1, T type2, Side<T> side) {
    var result = new HashMap<Var, Bounded<T>>();
    inferImpl(type1, type2, side, result);
    return new VarBounds<>(ImmutableMap.copyOf(result));
  }

  private void inferImpl(T t1, T t2, Side<T> side, Map<Var, Bounded<T>> result) {
    switch (t1) {
      case Var v -> result.merge(v, new Bounded<>(v, factory.oneSideBound(side, t2)), this::merge);
      case ArrayT arrayA -> {
        if (t2.equals(side.edge())) {
          inferImpl((T) arrayA.elem(), side.edge(), side, result);
        } else if (t2 instanceof ArrayT arrayB) {
          inferImpl((T) arrayA.elem(), (T) arrayB.elem(), side, result);
        }
      }
      case FuncT f1 -> {
        if (t2.equals(side.edge())) {
          var reversed = side.reversed();
          inferImpl((T) f1.res(), side.edge(), side, result);
          f1.params().forEach(t -> inferImpl((T) t, reversed.edge(), reversed, result));
        } else if (t2 instanceof FuncT f2 && f1.params().size() == f2.params().size()) {
          var reversed = side.reversed();
          inferImpl((T) f1.res(), (T) f2.res(), side, result);
          for (int i = 0; i < f1.params().size(); i++) {
            Type paramT1 = f1.params().get(i);
            Type paramT2 = f2.params().get(i);
            inferImpl((T) paramT1, (T) paramT2, reversed, result);
          }
        }
      }
      case TupleT tup1 -> {
        if (t2.equals(side.edge())) {
          tup1.items().forEach(t -> inferImpl((T) t, side.edge(), side, result));
        } else if (t2 instanceof TupleT tup2 && tup1.items().size() == tup2.items().size()) {
          for (int i = 0; i < tup1.items().size(); i++) {
            Type itemT1 = tup1.items().get(i);
            Type itemT2 = tup2.items().get(i);
            inferImpl((T) itemT1, (T) itemT2, side, result);
          }
        }
      }
      default -> {}
    }
  }

  public T mapVarsLower(T type, VarBounds<T> varBounds) {
    return mapVars(type, varBounds, factory.lower());
  }

  public T mapVars(T type, VarBounds<T> varBounds, Side<T> side) {
    if (type.isPolytype()) {
      return switch (type) {
        case Var var -> {
          Bounded<T> bounded = varBounds.map().get(var);
          if (bounded == null) {
            yield type;
          } else {
            yield bounded.bounds().get(side);
          }
        }
        case ArrayT arrayT -> {
          T elemTM = mapVars((T) arrayT.elem(), varBounds, side);
          yield  (T) createArrayT(arrayT, elemTM);
        }
        case FuncT funcT -> {
          var resultTM = mapVars((T) funcT.res(), varBounds, side);
          var paramsTM = map(
              funcT.params(),
              p -> mapVars((T) p, varBounds, side.reversed()));
          yield  (T) createFuncT(funcT, resultTM, paramsTM);
        }
        case TupleT tupleT -> {
          var itemsTM = map(tupleT.items(), p -> mapVars((T) p, varBounds, side));
          yield  (T) createTupleT(tupleT, itemsTM);
        }
        default -> type;
      };
    }
    return type;
  }

  public T mergeUp(T type1, T type2) {
    return merge(type1, type2, factory.upper());
  }

  public T mergeDown(T type1, T type2) {
    return merge(type1, type2, factory.lower());
  }

  public T merge(T type1, T type2, Side<T> direction) {
    Type reversedEdge = direction.reversed().edge();
    if (reversedEdge.equals(type2)) {
      return type1;
    } else if (reversedEdge.equals(type1)) {
      return type2;
    } else if (type1.equals(type2)) {
      return type1;
    } else if (type1 instanceof ArrayT arrayA) {
      if (type2 instanceof ArrayT arrayB) {
        var elemA = (T) arrayA.elem();
        var elemB = (T) arrayB.elem();
        var elemM = merge(elemA, elemB, direction);
        if (elemA == elemM) {
          return type1;
        } else if (elemB == elemM) {
          return type2;
        } else {
          return (T) factory.array(elemM);
        }
      }
    } else if (type1 instanceof FuncT funcA) {
      if (type2 instanceof FuncT funcB) {
        if (funcA.params().size() == funcB.params().size()) {
          var resultM = merge((T) funcA.res(), (T) funcB.res(), direction);
          var paramsM = zip(funcA.params(), funcB.params(),
              (a, b) -> merge((T) a, (T) b, direction.reversed()));
          if (isFuncTEqual(funcA, resultM, paramsM)) {
            return type1;
          } else if (isFuncTEqual(funcB, resultM, paramsM)){
            return type2;
          } else {
            return (T) factory.func(resultM, paramsM);
          }
        }
      }
    } else if (type1 instanceof TupleT tupleA) {
      if (type2 instanceof TupleT tupleB) {
        if (tupleA.items().size() == tupleB.items().size()) {
          var itemsTM = zip(tupleA.items(), tupleB.items(),
              (a, b) -> merge((T) a, (T) b, direction));
          if (isTupleTEqual(tupleA, itemsTM)) {
            return type1;
          } else if (isTupleTEqual(tupleB, itemsTM)){
            return type2;
          } else {
            return (T) factory.tuple(itemsTM);
          }
        }
      }
    }
    return direction.edge();
  }

  public Bounded<T> merge(Bounded<T> a, Bounded<T> b) {
    return new Bounded<>(a.var(), merge(a.bounds(), b.bounds()));
  }

  public Bounds<T> merge(Bounds<T> bounds1, Bounds<T> bounds2) {
    return new Bounds<>(
        merge(bounds1.lower(), bounds2.lower(), factory.upper()),
        merge(bounds1.upper(), bounds2.upper(), factory.lower()));
  }

  private ArrayT createArrayT(ArrayT type, T elemT) {
    if (type.elem() == elemT) {
      return type;
    } else {
      return factory.array(elemT);
    }
  }

  private FuncT createFuncT(FuncT type, T resT, ImmutableList<T> paramTs) {
    if (isFuncTEqual(type, resT, paramTs)) {
      return type;
    }
    return factory.func(resT, paramTs);
  }

  private boolean isFuncTEqual(FuncT type, Type result, ImmutableList<T> params) {
    return type.res() == result && type.params().equals(params);
  }

  private TupleT createTupleT(TupleT type, ImmutableList<T> items) {
    if (isTupleTEqual(type, items)) {
      return type;
    } else {
      return factory.tuple(items);
    }
  }

  private boolean isTupleTEqual(TupleT type, ImmutableList<T> items) {
    return type.items().equals(items);
  }

  public TypeFactory<T> factory() {
    return factory;
  }
}
