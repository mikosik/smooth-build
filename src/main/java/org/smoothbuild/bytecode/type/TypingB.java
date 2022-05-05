package org.smoothbuild.bytecode.type;

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

import javax.inject.Inject;

import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.CallableTB;
import org.smoothbuild.bytecode.type.val.ComposedTB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.lang.type.api.Bounded;
import org.smoothbuild.lang.type.api.FuncT;
import org.smoothbuild.lang.type.api.Side;
import org.smoothbuild.lang.type.api.Sides;
import org.smoothbuild.lang.type.api.Type;
import org.smoothbuild.lang.type.api.Var;
import org.smoothbuild.lang.type.api.VarBounds;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class TypingB {
  private final TypeFB typeFB;

  @Inject
  public TypingB(TypeFB typeFB) {
    this.typeFB = typeFB;
  }

  public boolean contains(TypeB type, TypeB inner) {
    if (type.equals(inner)) {
      return true;
    }
    if (type instanceof ComposedTB composedTB) {
      return composedTB.covars().stream().anyMatch(t -> contains(t, inner))
          || composedTB.contravars().stream().anyMatch(t -> contains(t, inner));
    } else {
      return false;
    }
  }

  public TypeB inferCallResT(FuncT funcT, ImmutableList<TypeB> argTs,
      Supplier<RuntimeException> illegalArgsExcThrower) {
    var callableTB = (CallableTB) funcT;
    ImmutableList<TypeB> paramTs = callableTB.params();
    allMatchOtherwise(
        paramTs,
        argTs,
        this::isParamAssignable,
        (expectedSize, actualSize) -> { throw illegalArgsExcThrower.get(); },
        i -> { throw illegalArgsExcThrower.get(); }
    );
    var varBounds = inferVarBoundsLower(paramTs, argTs);
    TypeB res = callableTB.res();
    return mapVarsLower(res, varBounds);
  }

  public boolean isAssignable(TypeB target, TypeB source) {
    return inequal(target, source, LOWER);
  }

  public boolean isParamAssignable(TypeB target, TypeB source) {
    return inequalParam(target, source, LOWER)
        && areConsistent(inferVarBoundsLower(target, source));
  }

  public boolean inequal(TypeB type1, TypeB type2, Side side) {
    return inequalImpl(type1, type2, side, this::inequal);
  }

  public boolean inequalParam(TypeB type1, TypeB type2, Side side) {
    return (type1 instanceof Var)
        || inequalImpl(type1, type2, side, this::inequalParam);
  }

  private boolean inequalImpl(TypeB type1, TypeB type2, Side side, InequalFunc inequalityFunc) {
    return inequalByEdgeCases(type1, type2, side)
        || inequalByConstruction(type1, type2, side, inequalityFunc);
  }

  private boolean inequalByEdgeCases(Type type1, Type type2, Side side) {
    return type2.equals(typeFB.edge(side))
        || type1.equals(typeFB.edge(side.other()));
  }

  private boolean inequalByConstruction(Type t1, Type t2, Side side, InequalFunc isInequal) {
    if (t1 instanceof ComposedTB c1) {
      if (t1.getClass().equals(t2.getClass())) {
        var c2 = (ComposedTB) t2;
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
    public boolean apply(TypeB type1, TypeB type2, Side side);
  }

  private boolean areConsistent(VarBounds<TypeB> varBounds) {
    return varBounds.map().values().stream()
        .allMatch(b -> isAssignable(b.bounds().upper(), b.bounds().lower()));
  }

  public VarBounds<TypeB> inferVarBoundsLower(List<? extends TypeB> types1,
      List<? extends TypeB> types2) {
    return inferVarBounds(types1, types2, LOWER);
  }

  public VarBounds<TypeB> inferVarBounds(
      List<? extends TypeB> types1, List<? extends TypeB> types2, Side side) {
    checkArgument(types1.size() == types2.size());
    var result = new HashMap<Var, Bounded<TypeB>>();
    for (int i = 0; i < types1.size(); i++) {
      inferImpl(types1.get(i), types2.get(i), side, result);
    }
    return typeFB.varBounds(ImmutableMap.copyOf(result));
  }

  public VarBounds<TypeB> inferVarBoundsLower(TypeB type1, TypeB type2) {
    return inferVarBounds(type1, type2, LOWER);
  }

  public VarBounds<TypeB> inferVarBounds(TypeB type1, TypeB type2, Side side) {
    var result = new HashMap<Var, Bounded<TypeB>>();
    inferImpl(type1, type2, side, result);
    return typeFB.varBounds(ImmutableMap.copyOf(result));
  }

  private void inferImpl(TypeB t1, TypeB t2, Side side, Map<Var, Bounded<TypeB>> result) {
    switch (t1) {
      case Var v -> result.merge(v, typeFB.bounded(v, typeFB.oneSideBound(side, t2)), this::merge);
      case ComposedTB c1 -> {
        TypeB sideEdge = typeFB.edge(side);
        if (t2.equals(sideEdge)) {
          var other = side.other();
          c1.covars().forEach(t -> inferImpl(t, sideEdge, side, result));
          c1.contravars().forEach(t -> inferImpl(t, typeFB.edge(other), other, result));
        } else if (t1.getClass().equals(t2.getClass())) {
          var c2 = (ComposedTB) t2;
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

  private void inferImplForEach(ImmutableList<TypeB> types1, ImmutableList<TypeB> types2,
      Side side, Map<Var, Bounded<TypeB>> result) {
    for (int i = 0; i < types1.size(); i++) {
      inferImpl(types1.get(i), types2.get(i), side, result);
    }
  }

  public TypeB mapVarsLower(TypeB type, VarBounds<TypeB> varBounds) {
    return mapVars(type, varBounds, LOWER);
  }

  public TypeB mapVars(TypeB type, VarBounds<TypeB> varBounds, Side side) {
    if (!type.vars().isEmpty()) {
      return switch (type) {
        case Var var -> mapVarsInVar(type, varBounds, side, var);
        case ComposedTB composedT -> {
          var covars = map(
              composedT.covars(), p -> mapVars(p, varBounds, side));
          var contravars = map(
              composedT.contravars(), p -> mapVars(p, varBounds, side.other()));
          yield rebuildComposed(type, covars, contravars);
        }
        default -> type;
      };
    }
    return type;
  }

  private TypeB mapVarsInVar(TypeB type, VarBounds<TypeB> varBounds, Side side, Var var) {
    Bounded<TypeB> bounded = varBounds.map().get(var);
    if (bounded == null) {
      return type;
    } else {
      return bounded.bounds().get(side);
    }
  }

  public TypeB mergeUp(TypeB type1, TypeB type2) {
    return merge(type1, type2, UPPER);
  }

  public TypeB mergeDown(TypeB type1, TypeB type2) {
    return merge(type1, type2, LOWER);
  }

  public TypeB merge(TypeB type1, TypeB type2, Side direction) {
    Type otherEdge = typeFB.edge(direction.other());
    if (otherEdge.equals(type2)) {
      return type1;
    } else if (otherEdge.equals(type1)) {
      return type2;
    } else if (type1.equals(type2)) {
      return type1;
    } else if (type1 instanceof ComposedTB c1) {
      if (type1.getClass().equals(type2.getClass())) {
        var c2 = (ComposedTB) type2;
        var c1covars = c1.covars();
        var c2covars = c2.covars();
        var c1contravars = c1.contravars();
        var c2contravars = c2.contravars();
        if (c1covars.size() == c2covars.size() && c1contravars.size() == c2contravars.size()) {
          var contravars = zip(c1contravars, c2contravars,
              (a, b) -> merge(a, b, direction.other()));
          var covars = zip(c1covars, c2covars,
              (a, b) -> merge(a, b, direction));
          return rebuildComposed(type1, covars, contravars);
        }
      }
    }
    return typeFB.edge(direction);
  }

  public Bounded<TypeB> merge(Bounded<TypeB> a, Bounded<TypeB> b) {
    return typeFB.bounded(a.var(), merge(a.bounds(), b.bounds()));
  }

  public Sides<TypeB> merge(Sides<TypeB> bounds1, Sides<TypeB> bounds2) {
    return new Sides<>(
        merge(bounds1.lower(), bounds2.lower(), UPPER),
        merge(bounds1.upper(), bounds2.upper(), LOWER));
  }

  public TypeB rebuildComposed(
      TypeB type, ImmutableList<TypeB> covars, ImmutableList<TypeB> contravars) {
    if (!(type instanceof ComposedTB composedT)) {
      throw unexpectedCaseExc(type);
    }
    if (composedT.covars().equals(covars) && composedT.contravars().equals(contravars)) {
      return type;
    }
    return switch (composedT) {
      case ArrayTB array -> typeFB.array(covars.get(0));
      case FuncTB func -> typeFB.func(typeFB.varSet(set()), covars.get(0),
          contravars);
      case TupleTB tuple -> typeFB.tuple(covars);
    };
  }

  public TypeFB typeF() {
    return typeFB;
  }
}
