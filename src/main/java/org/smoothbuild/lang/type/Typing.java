package org.smoothbuild.lang.type;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static org.smoothbuild.util.Throwables.unexpectedCaseExc;
import static org.smoothbuild.util.collect.Lists.allMatch;
import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Lists.zip;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.smoothbuild.lang.type.api.ArrayT;
import org.smoothbuild.lang.type.api.Bounded;
import org.smoothbuild.lang.type.api.Bounds;
import org.smoothbuild.lang.type.api.ClosedVarT;
import org.smoothbuild.lang.type.api.ComposedT;
import org.smoothbuild.lang.type.api.FuncT;
import org.smoothbuild.lang.type.api.OpenVarT;
import org.smoothbuild.lang.type.api.Sides.Side;
import org.smoothbuild.lang.type.api.TupleT;
import org.smoothbuild.lang.type.api.Type;
import org.smoothbuild.lang.type.api.TypeF;
import org.smoothbuild.lang.type.api.VarBounds;
import org.smoothbuild.lang.type.api.VarT;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class Typing<T extends Type> {
  private final TypeF<T> typeF;

  public Typing(TypeF<T> typeF) {
    this.typeF = typeF;
  }

  public boolean contains(T type, T inner) {
    if (type.equals(inner)) {
      return true;
    }
    if (type instanceof ComposedT composedT) {
      return composedT.covars().stream().anyMatch(t -> contains((T) t, inner))
          || composedT.contravars().stream().anyMatch(t -> contains((T) t, inner));
    } else {
      return false;
    }
  }

  public T inferCallResT(FuncT funcT, ImmutableList<T> argTs,
      Supplier<RuntimeException> illegalArgsExcThrower) {
    ImmutableList<T> paramTs = (ImmutableList<T>) funcT.params();
    allMatchOtherwise(
        paramTs,
        argTs,
        this::isParamAssignable,
        (expectedSize, actualSize) -> { throw illegalArgsExcThrower.get(); },
        i -> { throw illegalArgsExcThrower.get(); }
    );
    var varBounds = inferVarBoundsLower(paramTs, argTs);
    T res = (T) funcT.res();
    return mapVarsLower(res, varBounds);
  }

  public boolean isAssignable(T target, T source) {
    return inequal(target, source, typeF.lower());
  }

  public boolean isParamAssignable(T target, T source) {
    return inequalParam(target, source, typeF.lower())
        && areConsistent(inferVarBoundsLower(target, source));
  }

  public boolean inequal(Type type1, Type type2, Side<T> side) {
    return inequalImpl(type1, type2, side, this::inequal);
  }

  public boolean inequalParam(Type type1, Type type2, Side<T> side) {
    return (type1 instanceof VarT)
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
    if (t1 instanceof ComposedT c1) {
      if (t1.getClass().equals(t2.getClass())) {
        var c2 = (ComposedT) t2;
        return allMatch(c1.covars(), c2.covars(), (a, b) -> isInequal.apply(a, b, side))
            && allMatch(c1.contravars(), c2.contravars(), (a, b) -> isInequal.apply(a, b, side.reversed()));
      } else {
        return false;
      }
    } else {
      return t1.equals(t2);
    }
  }

  public static interface InequalFunc<T> {
    public boolean apply(Type type1, Type type2, Side<T> side);
  }

  private boolean areConsistent(VarBounds<T> varBounds) {
    return varBounds.map().values().stream()
        .allMatch(b -> isAssignable(b.bounds().upper(), b.bounds().lower()));
  }

  public VarBounds<T> inferVarBoundsLower(List<? extends T> types1, List<? extends T> types2) {
    return inferVarBounds(types1, types2, typeF.lower());
  }

  public VarBounds<T> inferVarBounds(
      List<? extends T> types1, List<? extends T> types2, Side<T> side) {
    checkArgument(types1.size() == types2.size());
    var result = new HashMap<VarT, Bounded<T>>();
    for (int i = 0; i < types1.size(); i++) {
      inferImpl(types1.get(i), types2.get(i), side, result);
    }
    return new VarBounds<>(ImmutableMap.copyOf(result));
  }

  public VarBounds<T> inferVarBoundsLower(T type1, T type2) {
    return inferVarBounds(type1, type2, typeF().lower());
  }

  public VarBounds<T> inferVarBounds(T type1, T type2, Side<T> side) {
    var result = new HashMap<VarT, Bounded<T>>();
    inferImpl(type1, type2, side, result);
    return new VarBounds<>(ImmutableMap.copyOf(result));
  }

  private void inferImpl(T t1, T t2, Side<T> side, Map<VarT, Bounded<T>> result) {
    switch (t1) {
      case VarT v -> result.merge(v, new Bounded<>(v, typeF.oneSideBound(side, t2)), this::merge);
      case ComposedT c1 -> {
        if (t2.equals(side.edge())) {
          var reversed = side.reversed();
          c1.covars().forEach(t -> inferImpl((T) t, side.edge(), side, result));
          c1.contravars().forEach(t -> inferImpl((T) t, reversed.edge(), reversed, result));
        } else if (t1.getClass().equals(t2.getClass())) {
          var c2 = (ComposedT) t2;
          var c1Covars = c1.covars();
          var c2Covars = c2.covars();
          var c1Contravars = c1.contravars();
          var c2Contravars = c2.contravars();
          if (c1Covars.size() == c2Covars.size() && c1Contravars.size() == c2Contravars.size()) {
            inferImplForEach(c1Covars, c2Covars, side, result);
            inferImplForEach(c1Contravars, c2Contravars, side.reversed(), result);
          }
        }
      }
      default -> {}
    }
  }

  private void inferImplForEach(ImmutableList<Type> types1, ImmutableList<Type> types2,
      Side<T> side, Map<VarT, Bounded<T>> result) {
    for (int i = 0; i < types1.size(); i++) {
      inferImpl((T) types1.get(i), (T) types2.get(i), side, result);
    }
  }

  public T mapVarsLower(T type, VarBounds<T> varBounds) {
    return mapVars(type, varBounds, typeF.lower());
  }

  public T mapVars(T type, VarBounds<T> varBounds, Side<T> side) {
    if (type.isPolytype()) {
      return switch (type) {
        case VarT var -> mapVarsInVar(type, varBounds, side, var);
        case ComposedT composedT -> {
          var covars = map(
              composedT.covars(), p -> mapVars((T) p, varBounds, side));
          var contravars = map(
              composedT.contravars(), p -> mapVars((T) p, varBounds, side.reversed()));
          yield rebuildComposed(type, covars, contravars);
        }
        default -> type;
      };
    }
    return type;
  }

  private T mapVarsInVar(T type, VarBounds<T> varBounds, Side<T> side, VarT var) {
    Bounded<T> bounded = varBounds.map().get(var);
    if (bounded == null) {
      return type;
    } else {
      return bounded.bounds().get(side);
    }
  }

  public T mergeUp(T type1, T type2) {
    return merge(type1, type2, typeF.upper());
  }

  public T mergeDown(T type1, T type2) {
    return merge(type1, type2, typeF.lower());
  }

  public T merge(T type1, T type2, Side<T> direction) {
    Type reversedEdge = direction.reversed().edge();
    if (reversedEdge.equals(type2)) {
      return type1;
    } else if (reversedEdge.equals(type1)) {
      return type2;
    } else if (type1.equals(type2)) {
      return type1;
    } else if (type1 instanceof ComposedT c1) {
      if (type1.getClass().equals(type2.getClass())) {
        var c2 = (ComposedT) type2;
        var c1covars = c1.covars();
        var c2covars = c2.covars();
        var c1contravars = c1.contravars();
        var c2contravars = c2.contravars();
        if (c1covars.size() == c2covars.size() && c1contravars.size() == c2contravars.size()) {
          var contravars = zip(c1contravars, c2contravars,
              (a, b) -> merge((T) a, (T) b, direction.reversed()));
          var covars = zip(c1covars, c2covars,
              (a, b) -> merge((T) a, (T) b, direction));
          return rebuildComposed(type1, covars, contravars);
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
        merge(bounds1.lower(), bounds2.lower(), typeF.upper()),
        merge(bounds1.upper(), bounds2.upper(), typeF.lower()));
  }

  public T openVars(T type) {
    if (!type.hasClosedVars()) {
      return type;
    }
    return switch (type) {
      case ComposedT composedT -> rebuildComposed(type,
          map(composedT.covars(), t -> openVars((T) t)),
          map(composedT.contravars(), t -> openVars((T) t)));
      case ClosedVarT closedVarT -> (T) typeF.oVar(closedVarT.name());
      default -> throw unexpectedCaseExc(type);
    };
  }

  public T closeVars(T type) {
    if (!type.hasOpenVars()) {
      return type;
    }
    return switch (type) {
      case ComposedT composedT -> rebuildComposed(type,
          map(composedT.covars(), t -> closeVars((T) t)),
          map(composedT.contravars(), t -> closeVars((T) t)));
      case OpenVarT openVarT -> (T) typeF.cVar(openVarT.name());
      default -> throw unexpectedCaseExc(type);
    };
  }

  public VarBounds<T> closeVars(VarBounds<T> varBounds) {
    var map = varBounds.map().values().stream()
        .map(bounded -> new Bounded<>((VarT) closeVars((T) bounded.var()), bounded.bounds()))
        .collect(toImmutableMap(Bounded::var, b -> b));
    return new VarBounds<>(map);
  }

  public T rebuildComposed(T type, ImmutableList<T> covars, ImmutableList<T> contravars) {
    if (!(type instanceof ComposedT composedT)) {
      throw unexpectedCaseExc(type);
    }
    if (composedT.covars().equals(covars) && composedT.contravars().equals(contravars)) {
      return type;
    }
    return switch (composedT) {
      case ArrayT array -> (T) typeF.array(covars.get(0));
      case FuncT func -> (T) typeF.func(covars.get(0), contravars);
      case TupleT func -> (T) typeF.tuple(covars);
    };
  }

  public TypeF<T> typeF() {
    return typeF;
  }
}
