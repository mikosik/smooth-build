package org.smoothbuild.bytecode.type;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.Throwables.unexpectedCaseExc;
import static org.smoothbuild.util.collect.Lists.allMatch;
import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Lists.zip;
import static org.smoothbuild.util.type.Side.LOWER;
import static org.smoothbuild.util.type.Side.UPPER;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.inject.Inject;

import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.BoundedB;
import org.smoothbuild.bytecode.type.val.CallableTB;
import org.smoothbuild.bytecode.type.val.ComposedTB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.bytecode.type.val.VarB;
import org.smoothbuild.bytecode.type.val.VarBoundsB;
import org.smoothbuild.util.type.Bounds;
import org.smoothbuild.util.type.Side;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class TypingB {
  private final TypeBF typeBF;

  @Inject
  public TypingB(TypeBF typeBF) {
    this.typeBF = typeBF;
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

  public TypeB inferCallResT(CallableTB funcT, ImmutableList<TypeB> argTs,
      Supplier<RuntimeException> illegalArgsExcThrower) {
    allMatchOtherwise(
        funcT.params(),
        argTs,
        this::isParamAssignable,
        (expectedSize, actualSize) -> { throw illegalArgsExcThrower.get(); },
        i -> { throw illegalArgsExcThrower.get(); }
    );
    var varBounds = inferVarBoundsLower(funcT.params(), argTs);
    TypeB res = funcT.res();
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
    return (type1 instanceof VarB)
        || inequalImpl(type1, type2, side, this::inequalParam);
  }

  private boolean inequalImpl(TypeB type1, TypeB type2, Side side, InequalFunc inequalityFunc) {
    return inequalByEdgeCases(type1, type2, side)
        || inequalByConstruction(type1, type2, side, inequalityFunc);
  }

  private boolean inequalByEdgeCases(TypeB type1, TypeB type2, Side side) {
    return type2.equals(typeBF.edge(side))
        || type1.equals(typeBF.edge(side.other()));
  }

  private boolean inequalByConstruction(TypeB t1, TypeB t2, Side side, InequalFunc isInequal) {
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

  private boolean areConsistent(VarBoundsB varBounds) {
    return varBounds.map().values().stream()
        .allMatch(b -> isAssignable(b.bounds().upper(), b.bounds().lower()));
  }

  public VarBoundsB inferVarBoundsLower(List<? extends TypeB> types1,
      List<? extends TypeB> types2) {
    return inferVarBounds(types1, types2, LOWER);
  }

  public VarBoundsB inferVarBounds(
      List<? extends TypeB> types1, List<? extends TypeB> types2, Side side) {
    checkArgument(types1.size() == types2.size());
    var result = new HashMap<VarB, BoundedB>();
    for (int i = 0; i < types1.size(); i++) {
      inferImpl(types1.get(i), types2.get(i), side, result);
    }
    return new VarBoundsB(ImmutableMap.copyOf(result));
  }

  public VarBoundsB inferVarBoundsLower(TypeB type1, TypeB type2) {
    return inferVarBounds(type1, type2, LOWER);
  }

  public VarBoundsB inferVarBounds(TypeB type1, TypeB type2, Side side) {
    var result = new HashMap<VarB, BoundedB>();
    inferImpl(type1, type2, side, result);
    return new VarBoundsB(ImmutableMap.copyOf(result));
  }

  private void inferImpl(TypeB t1, TypeB t2, Side side, Map<VarB, BoundedB> result) {
    switch (t1) {
      case VarB v -> result.merge(v, new BoundedB(v, typeBF.oneSideBound(side, t2)), this::merge);
      case ComposedTB c1 -> {
        TypeB sideEdge = typeBF.edge(side);
        if (t2.equals(sideEdge)) {
          var other = side.other();
          c1.covars().forEach(t -> inferImpl(t, sideEdge, side, result));
          c1.contravars().forEach(t -> inferImpl(t, typeBF.edge(other), other, result));
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
      Side side, Map<VarB, BoundedB> result) {
    for (int i = 0; i < types1.size(); i++) {
      inferImpl(types1.get(i), types2.get(i), side, result);
    }
  }

  public TypeB mapVarsLower(TypeB type, VarBoundsB varBounds) {
    return mapVars(type, varBounds, LOWER);
  }

  public TypeB mapVars(TypeB type, VarBoundsB varBounds, Side side) {
    if (!type.vars().isEmpty()) {
      return switch (type) {
        case VarB var -> mapVarsInVar(type, varBounds, side, var);
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

  private TypeB mapVarsInVar(TypeB type, VarBoundsB varBounds, Side side, VarB var) {
    BoundedB bounded = varBounds.map().get(var);
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
    TypeB otherEdge = typeBF.edge(direction.other());
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
    return typeBF.edge(direction);
  }

  public BoundedB merge(BoundedB a, BoundedB b) {
    return new BoundedB(a.var(), merge(a.bounds(), b.bounds()));
  }

  public Bounds<TypeB> merge(Bounds<TypeB> bounds1, Bounds<TypeB> bounds2) {
    return new Bounds<>(
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
      case ArrayTB array -> typeBF.array(covars.get(0));
      case FuncTB func -> typeBF.func(covars.get(0), contravars);
      case TupleTB tuple -> typeBF.tuple(covars);
    };
  }

  public TypeBF typeF() {
    return typeBF;
  }
}
