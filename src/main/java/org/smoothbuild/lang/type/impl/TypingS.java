package org.smoothbuild.lang.type.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.type.api.Side.LOWER;
import static org.smoothbuild.lang.type.api.Side.UPPER;
import static org.smoothbuild.lang.type.impl.VarSetS.varSetS;
import static org.smoothbuild.util.Throwables.unexpectedCaseExc;
import static org.smoothbuild.util.collect.Lists.allMatch;
import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Lists.zip;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.inject.Inject;

import org.smoothbuild.lang.type.api.FuncT;
import org.smoothbuild.lang.type.api.Side;
import org.smoothbuild.lang.type.api.Sides;
import org.smoothbuild.lang.type.api.Type;
import org.smoothbuild.lang.type.api.Var;
import org.smoothbuild.lang.type.api.VarBoundsS;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class TypingS {
  private final TypeFS typeFS;

  @Inject
  public TypingS(TypeFS typeFS) {
    this.typeFS = typeFS;
  }

  public boolean contains(TypeS type, TypeS inner) {
    if (type.equals(inner)) {
      return true;
    }
    if (type instanceof ComposedTS composedTS) {
      return composedTS.covars().stream().anyMatch(t -> contains(t, inner))
          || composedTS.contravars().stream().anyMatch(t -> contains(t, inner));
    } else {
      return false;
    }
  }

  public TypeS inferCallResT(FuncT funcT, ImmutableList<TypeS> argTs,
      Supplier<RuntimeException> illegalArgsExcThrower) {
    var funcTS = (FuncTS) funcT;
    ImmutableList<TypeS> paramTs = funcTS.params();
    allMatchOtherwise(
        paramTs,
        argTs,
        this::isParamAssignable,
        (expectedSize, actualSize) -> { throw illegalArgsExcThrower.get(); },
        i -> { throw illegalArgsExcThrower.get(); }
    );
    var varBounds = inferVarBoundsLower(paramTs, argTs);
    TypeS res = funcTS.res();
    return mapVarsLower(res, varBounds);
  }

  public boolean isAssignable(TypeS target, TypeS source) {
    return inequal(target, source, LOWER);
  }

  public boolean isParamAssignable(TypeS target, TypeS source) {
    return inequalParam(target, source, LOWER)
        && areConsistent(inferVarBoundsLower(target, source));
  }

  public boolean inequal(TypeS type1, TypeS type2, Side side) {
    return inequalImpl(type1, type2, side, this::inequal);
  }

  public boolean inequalParam(TypeS type1, TypeS type2, Side side) {
    return (type1 instanceof Var)
        || inequalImpl(type1, type2, side, this::inequalParam);
  }

  private boolean inequalImpl(TypeS type1, TypeS type2, Side side, InequalFunc inequalityFunc) {
    return inequalByEdgeCases(type1, type2, side)
        || inequalByConstruction(type1, type2, side, inequalityFunc);
  }

  private boolean inequalByEdgeCases(TypeS type1, TypeS type2, Side side) {
    return type2.equals(typeFS.edge(side))
        || type1.equals(typeFS.edge(side.other()));
  }

  private boolean inequalByConstruction(Type t1, Type t2, Side side, InequalFunc isInequal) {
    if (t1 instanceof ComposedTS c1) {
      if (t1.getClass().equals(t2.getClass())) {
        var c2 = (ComposedTS) t2;
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
    public boolean apply(TypeS type1, TypeS type2, Side side);
  }

  private boolean areConsistent(VarBoundsS varBounds) {
    return varBounds.map().values().stream()
        .allMatch(b -> isAssignable(b.bounds().upper(), b.bounds().lower()));
  }

  public VarBoundsS inferVarBoundsLower(List<? extends TypeS> types1,
      List<? extends TypeS> types2) {
    return inferVarBounds(types1, types2, LOWER);
  }

  public VarBoundsS inferVarBounds(
      List<? extends TypeS> types1, List<? extends TypeS> types2, Side side) {
    checkArgument(types1.size() == types2.size());
    var result = new HashMap<VarS, BoundedS>();
    for (int i = 0; i < types1.size(); i++) {
      inferImpl(types1.get(i), types2.get(i), side, result);
    }
    return typeFS.varBounds(ImmutableMap.copyOf(result));
  }

  public VarBoundsS inferVarBoundsLower(TypeS type1, TypeS type2) {
    return inferVarBounds(type1, type2, LOWER);
  }

  public VarBoundsS inferVarBounds(TypeS type1, TypeS type2, Side side) {
    var result = new HashMap<VarS, BoundedS>();
    inferImpl(type1, type2, side, result);
    return typeFS.varBounds(ImmutableMap.copyOf(result));
  }

  private void inferImpl(TypeS t1, TypeS t2, Side side, Map<VarS, BoundedS> result) {
    switch (t1) {
      case VarS v -> result.merge(v, typeFS.bounded(v, typeFS.oneSideBound(side, t2)), this::merge);
      case ComposedTS c1 -> {
        TypeS sideEdge = typeFS.edge(side);
        if (t2.equals(sideEdge)) {
          var other = side.other();
          c1.covars().forEach(t -> inferImpl(t, sideEdge, side, result));
          c1.contravars().forEach(t -> inferImpl(t, typeFS.edge(other), other, result));
        } else if (t1.getClass().equals(t2.getClass())) {
          var c2 = (ComposedTS) t2;
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

  private void inferImplForEach(ImmutableList<TypeS> types1, ImmutableList<TypeS> types2,
      Side side, Map<VarS, BoundedS> result) {
    for (int i = 0; i < types1.size(); i++) {
      inferImpl(types1.get(i), types2.get(i), side, result);
    }
  }

  public TypeS mapVarsLower(TypeS type, VarBoundsS varBounds) {
    return mapVars(type, varBounds, LOWER);
  }

  public TypeS mapVars(TypeS type, VarBoundsS varBounds, Side side) {
    if (!type.vars().isEmpty()) {
      return switch (type) {
        case Var var -> mapVarsInVar(type, varBounds, side, var);
        case ComposedTS composedT -> {
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

  private TypeS mapVarsInVar(TypeS type, VarBoundsS varBounds, Side side, Var var) {
    BoundedS bounded = varBounds.map().get(var);
    if (bounded == null) {
      return type;
    } else {
      return bounded.bounds().get(side);
    }
  }

  public TypeS mergeUp(TypeS type1, TypeS type2) {
    return merge(type1, type2, UPPER);
  }

  public TypeS mergeDown(TypeS type1, TypeS type2) {
    return merge(type1, type2, LOWER);
  }

  public TypeS merge(TypeS type1, TypeS type2, Side direction) {
    Type otherEdge = typeFS.edge(direction.other());
    if (otherEdge.equals(type2)) {
      return type1;
    } else if (otherEdge.equals(type1)) {
      return type2;
    } else if (type1.equals(type2)) {
      return type1;
    } else if (type1 instanceof ComposedTS c1) {
      if (type1.getClass().equals(type2.getClass())) {
        var c2 = (ComposedTS) type2;
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
    return typeFS.edge(direction);
  }

  public BoundedS merge(BoundedS a, BoundedS b) {
    return typeFS.bounded(a.var(), merge(a.bounds(), b.bounds()));
  }

  public Sides<TypeS> merge(Sides<TypeS> bounds1, Sides<TypeS> bounds2) {
    return new Sides<>(
        merge(bounds1.lower(), bounds2.lower(), UPPER),
        merge(bounds1.upper(), bounds2.upper(), LOWER));
  }

  public TypeS rebuildComposed(
      TypeS type, ImmutableList<TypeS> covars, ImmutableList<TypeS> contravars) {
    if (!(type instanceof ComposedTS composedT)) {
      throw unexpectedCaseExc(type);
    }
    if (composedT.covars().equals(covars) && composedT.contravars().equals(contravars)) {
      return type;
    }
    return switch (composedT) {
      case ArrayTS array -> typeFS.array(covars.get(0));
      case FuncTS func -> typeFS.func(varSetS(), covars.get(0), contravars);
    };
  }

  public TypeFS typeF() {
    return typeFS;
  }
}
