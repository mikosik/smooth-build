package org.smoothbuild.lang.type;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.type.api.Side.LOWER;
import static org.smoothbuild.lang.type.api.Side.UPPER;
import static org.smoothbuild.util.Throwables.unexpectedCaseExc;
import static org.smoothbuild.util.collect.Lists.allMatch;
import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Lists.zip;
import static org.smoothbuild.util.collect.Sets.set;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.smoothbuild.lang.type.api.ArrayT;
import org.smoothbuild.lang.type.api.Bounded;
import org.smoothbuild.lang.type.api.ComposedT;
import org.smoothbuild.lang.type.api.FuncT;
import org.smoothbuild.lang.type.api.Side;
import org.smoothbuild.lang.type.api.Sides;
import org.smoothbuild.lang.type.api.TupleT;
import org.smoothbuild.lang.type.api.Type;
import org.smoothbuild.lang.type.api.TypeF;
import org.smoothbuild.lang.type.api.Var;
import org.smoothbuild.lang.type.api.VarBounds;
import org.smoothbuild.lang.type.api.VarSet;

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
    return inequal(target, source, LOWER);
  }

  public boolean isParamAssignable(T target, T source) {
    return inequalParam(target, source, LOWER)
        && areConsistent(inferVarBoundsLower(target, source));
  }

  public boolean inequal(Type type1, Type type2, Side side) {
    return inequalImpl(type1, type2, side, this::inequal);
  }

  public boolean inequalParam(Type type1, Type type2, Side side) {
    return (type1 instanceof Var)
        || inequalImpl(type1, type2, side, this::inequalParam);
  }

  private boolean inequalImpl(Type type1, Type type2, Side side, InequalFunc inequalityFunc) {
    return inequalByEdgeCases(type1, type2, side)
        || inequalByConstruction(type1, type2, side, inequalityFunc);
  }

  private boolean inequalByEdgeCases(Type type1, Type type2, Side side) {
    return type2.equals(typeF.edge(side))
        || type1.equals(typeF.edge(side.other()));
  }

  private boolean inequalByConstruction(Type t1, Type t2, Side side, InequalFunc isInequal) {
    if (t1 instanceof ComposedT c1) {
      if (t1.getClass().equals(t2.getClass())) {
        var c2 = (ComposedT) t2;
        return allMatch(c1.covars(), c2.covars(), (a, b) -> isInequal.apply(a, b, side))
            && allMatch(c1.contravars(), c2.contravars(),
            (a, b) -> isInequal.apply(a, b, side.other()));
      } else {
        return false;
      }
    } else {
      return t1.equals(t2);
    }
  }

  public static interface InequalFunc {
    public boolean apply(Type type1, Type type2, Side side);
  }

  private boolean areConsistent(VarBounds<T> varBounds) {
    return varBounds.map().values().stream()
        .allMatch(b -> isAssignable(b.bounds().upper(), b.bounds().lower()));
  }

  public VarBounds<T> inferVarBoundsLower(List<? extends T> types1, List<? extends T> types2) {
    return inferVarBounds(types1, types2, LOWER);
  }

  public VarBounds<T> inferVarBounds(
      List<? extends T> types1, List<? extends T> types2, Side side) {
    checkArgument(types1.size() == types2.size());
    var result = new HashMap<Var, Bounded<T>>();
    for (int i = 0; i < types1.size(); i++) {
      inferImpl(types1.get(i), types2.get(i), side, result);
    }
    return new VarBounds<>(ImmutableMap.copyOf(result));
  }

  public VarBounds<T> inferVarBoundsLower(T type1, T type2) {
    return inferVarBounds(type1, type2, LOWER);
  }

  public VarBounds<T> inferVarBounds(T type1, T type2, Side side) {
    var result = new HashMap<Var, Bounded<T>>();
    inferImpl(type1, type2, side, result);
    return new VarBounds<>(ImmutableMap.copyOf(result));
  }

  private void inferImpl(T t1, T t2, Side side, Map<Var, Bounded<T>> result) {
    switch (t1) {
      case Var v -> result.merge(v, new Bounded<>(v, typeF.oneSideBound(side, t2)), this::merge);
      case ComposedT c1 -> {
        T sideEdge = typeF.edge(side);
        if (t2.equals(sideEdge)) {
          var other = side.other();
          c1.covars().forEach(t -> inferImpl((T) t, sideEdge, side, result));
          c1.contravars().forEach(t -> inferImpl((T) t, typeF.edge(other), other, result));
        } else if (t1.getClass().equals(t2.getClass())) {
          var c2 = (ComposedT) t2;
          var c1Covars = c1.covars();
          var c2Covars = c2.covars();
          var c1Contravars = c1.contravars();
          var c2Contravars = c2.contravars();
          if (c1Covars.size() == c2Covars.size() && c1Contravars.size() == c2Contravars.size()) {
            inferImplForEach(c1Covars, c2Covars, side, result);
            inferImplForEach(c1Contravars, c2Contravars, side.other(), result);
          }
        }
      }
      default -> {}
    }
  }

  private void inferImplForEach(ImmutableList<Type> types1, ImmutableList<Type> types2,
      Side side, Map<Var, Bounded<T>> result) {
    for (int i = 0; i < types1.size(); i++) {
      inferImpl((T) types1.get(i), (T) types2.get(i), side, result);
    }
  }

  public T mapVarsLower(T type, VarBounds<T> varBounds) {
    return mapVars(type, varBounds, LOWER);
  }

  public T mapVars(T type, VarBounds<T> varBounds, Side side) {
    if (!type.vars().isEmpty()) {
      return switch (type) {
        case Var var -> mapVarsInVar(type, varBounds, side, var);
        case ComposedT composedT -> {
          var covars = map(
              composedT.covars(), p -> mapVars((T) p, varBounds, side));
          var contravars = map(
              composedT.contravars(), p -> mapVars((T) p, varBounds, side.other()));
          yield rebuildComposed(type, covars, contravars);
        }
        default -> type;
      };
    }
    return type;
  }

  private T mapVarsInVar(T type, VarBounds<T> varBounds, Side side, Var var) {
    Bounded<T> bounded = varBounds.map().get(var);
    if (bounded == null) {
      return type;
    } else {
      return bounded.bounds().get(side);
    }
  }

  public T mergeUp(T type1, T type2) {
    return merge(type1, type2, UPPER);
  }

  public T mergeDown(T type1, T type2) {
    return merge(type1, type2, LOWER);
  }

  public T merge(T type1, T type2, Side direction) {
    Type otherEdge = typeF.edge(direction.other());
    if (otherEdge.equals(type2)) {
      return type1;
    } else if (otherEdge.equals(type1)) {
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
              (a, b) -> merge((T) a, (T) b, direction.other()));
          var covars = zip(c1covars, c2covars,
              (a, b) -> merge((T) a, (T) b, direction));
          return rebuildComposed(type1, covars, contravars);
        }
      }
    }
    return typeF.edge(direction);
  }

  public Bounded<T> merge(Bounded<T> a, Bounded<T> b) {
    return new Bounded<>(a.var(), merge(a.bounds(), b.bounds()));
  }

  public Sides<T> merge(Sides<T> bounds1, Sides<T> bounds2) {
    return new Sides<>(
        merge(bounds1.lower(), bounds2.lower(), UPPER),
        merge(bounds1.upper(), bounds2.upper(), LOWER));
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
      case FuncT func -> (T) typeF.func((VarSet<T>) typeF.varSet(set()), covars.get(0), contravars);
      case TupleT func -> (T) typeF.tuple(covars);
    };
  }

  public TypeF<T> typeF() {
    return typeF;
  }
}
