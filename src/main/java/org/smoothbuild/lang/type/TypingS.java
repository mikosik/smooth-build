package org.smoothbuild.lang.type;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.Throwables.unexpectedCaseExc;
import static org.smoothbuild.util.collect.Lists.allMatch;
import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Lists.zip;
import static org.smoothbuild.util.type.Side.LOWER;
import static org.smoothbuild.util.type.Side.UPPER;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.inject.Inject;

import org.smoothbuild.util.type.Bounds;
import org.smoothbuild.util.type.Side;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MultimapBuilder.SetMultimapBuilder;
import com.google.common.collect.SetMultimap;

public class TypingS {
  private final TypeSF typeSF;

  @Inject
  public TypingS(TypeSF typeSF) {
    this.typeSF = typeSF;
  }

  public boolean contains(MonoTS type, MonoTS inner) {
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

  public MonoTS inferCallResT(MonoFuncTS funcT, ImmutableList<MonoTS> argTs,
      Supplier<RuntimeException> illegalArgsExcThrower) {
    allMatchOtherwise(
        funcT.params(),
        argTs,
        this::isParamAssignable,
        (expectedSize, actualSize) -> { throw illegalArgsExcThrower.get(); },
        i -> { throw illegalArgsExcThrower.get(); }
    );
    var varBounds = inferVarBoundsLower(funcT.params(), argTs);
    MonoTS res = funcT.res();
    return mapVarsLower(res, varBounds);
  }

  public boolean isAssignable(MonoTS target, MonoTS source) {
    return inequal(target, source, LOWER);
  }

  public boolean isParamAssignable(MonoTS target, MonoTS source) {
    return inequalParam(target, source, LOWER)
        && areConsistent(inferVarBoundsLower(target, source));
  }

  public boolean inequal(MonoTS type1, MonoTS type2, Side side) {
    return inequalImpl(type1, type2, side, this::inequal);
  }

  public boolean inequalParam(MonoTS type1, MonoTS type2, Side side) {
    return (type1 instanceof VarS)
        || inequalImpl(type1, type2, side, this::inequalParam);
  }

  private boolean inequalImpl(MonoTS type1, MonoTS type2, Side side, InequalFunc inequalityFunc) {
    return inequalByEdgeCases(type1, type2, side)
        || inequalByConstruction(type1, type2, side, inequalityFunc);
  }

  private boolean inequalByEdgeCases(MonoTS type1, MonoTS type2, Side side) {
    return type2.equals(typeSF.edge(side))
        || type1.equals(typeSF.edge(side.other()));
  }

  private boolean inequalByConstruction(MonoTS t1, MonoTS t2, Side side, InequalFunc isInequal) {
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
    public boolean apply(MonoTS type1, MonoTS type2, Side side);
  }

  private boolean areConsistent(VarBoundsS varBounds) {
    return varBounds.map().values().stream()
        .allMatch(b -> isAssignable(b.bounds().upper(), b.bounds().lower()));
  }

  public VarBoundsS inferVarBoundsLower(List<? extends MonoTS> types1,
      List<? extends MonoTS> types2) {
    return inferVarBounds(types1, types2, LOWER);
  }

  public VarBoundsS inferVarBounds(
      List<? extends MonoTS> types1, List<? extends MonoTS> types2, Side side) {
    checkArgument(types1.size() == types2.size());
    var result = new HashMap<VarS, BoundedS>();
    for (int i = 0; i < types1.size(); i++) {
      inferImpl(types1.get(i), types2.get(i), side, result);
    }
    return new VarBoundsS(ImmutableMap.copyOf(result));
  }

  public VarBoundsS inferVarBoundsLower(MonoTS type1, MonoTS type2) {
    return inferVarBounds(type1, type2, LOWER);
  }

  public VarBoundsS inferVarBounds(MonoTS type1, MonoTS type2, Side side) {
    var result = new HashMap<VarS, BoundedS>();
    inferImpl(type1, type2, side, result);
    return new VarBoundsS(ImmutableMap.copyOf(result));
  }

  private void inferImpl(MonoTS t1, MonoTS t2, Side side, Map<VarS, BoundedS> result) {
    switch (t1) {
      case VarS v -> result.merge(v, new BoundedS(v, typeSF.oneSideBound(side, t2)), this::merge);
      case ComposedTS c1 -> {
        MonoTS sideEdge = typeSF.edge(side);
        if (t2.equals(sideEdge)) {
          var other = side.other();
          c1.covars().forEach(t -> inferImpl(t, sideEdge, side, result));
          c1.contravars().forEach(t -> inferImpl(t, typeSF.edge(other), other, result));
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

  private void inferImplForEach(ImmutableList<MonoTS> types1, ImmutableList<MonoTS> types2,
      Side side, Map<VarS, BoundedS> result) {
    for (int i = 0; i < types1.size(); i++) {
      inferImpl(types1.get(i), types2.get(i), side, result);
    }
  }

  public MonoTS mapVarsLower(MonoTS type, VarBoundsS varBounds) {
    return mapVars(type, varBounds, LOWER);
  }

  public MonoTS mapVars(MonoTS type, VarBoundsS varBounds, Side side) {
    if (!type.vars().isEmpty()) {
      return switch (type) {
        case VarS var -> mapVarsInVar(type, varBounds, side, var);
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

  private MonoTS mapVarsInVar(MonoTS type, VarBoundsS varBounds, Side side, VarS var) {
    BoundedS bounded = varBounds.map().get(var);
    if (bounded == null) {
      return type;
    } else {
      return bounded.bounds().get(side);
    }
  }

  public MonoTS mergeUp(MonoTS type1, MonoTS type2) {
    return merge(type1, type2, UPPER);
  }

  public MonoTS mergeDown(MonoTS type1, MonoTS type2) {
    return merge(type1, type2, LOWER);
  }

  public MonoTS merge(MonoTS type1, MonoTS type2, Side direction) {
    MonoTS otherEdge = typeSF.edge(direction.other());
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
    return typeSF.edge(direction);
  }

  public BoundedS merge(BoundedS a, BoundedS b) {
    return new BoundedS(a.var(), merge(a.bounds(), b.bounds()));
  }

  public Bounds<MonoTS> merge(Bounds<MonoTS> bounds1, Bounds<MonoTS> bounds2) {
    return new Bounds<>(
        merge(bounds1.lower(), bounds2.lower(), UPPER),
        merge(bounds1.upper(), bounds2.upper(), LOWER));
  }

  public MonoTS resolveMerges(MonoTS type) {
    return switch (type) {
      case ComposedTS composedT -> {
        var covars = map(composedT.covars(), this::resolveMerges);
        var contravars = map(composedT.contravars(), this::resolveMerges);
        yield rebuildComposed(type, covars, contravars);
      }
      case MergeTS mergeT -> resolveMerges(mergeT);
      default -> type;
    };
  }

  private MonoTS resolveMerges(MergeTS mergeT) {
    return resolveMergeElems(mergeT.elems(), mergeT.direction());
  }

  private MonoTS resolveMergeElems(Collection<MonoTS> elems, Side direction) {
    var arrayTs = new ArrayList<ArrayTS>(elems.size());
    SetMultimap<Integer, MonoFuncTS> funcTs = SetMultimapBuilder.hashKeys().hashSetValues().build();
    var others = new HashSet<MonoTS>();
    MonoTS zero = null;
    for (MonoTS elem : elems) {
      switch (elem) {
        case EdgeTS edge:
          if (edge.side().equals(direction)) {
            return edge;
          } else {
            zero = edge;
          }
          break;
        case MonoFuncTS funcT:
          funcTs.put(funcT.params().size(), funcT);
          break;
        case ArrayTS arrayT:
          arrayTs.add(arrayT);
          break;
        case MergeTS mergeT:
          throw unexpectedCaseExc(mergeT);
        default:
          others.add(elem);
      }
    }

    if (1 < others.size()) {
      return typeSF.edge(direction);
    }
    var funcEntries = funcTs.asMap().entrySet();
    if (1 < others.size() + (arrayTs.isEmpty() ? 0 : 1) + funcEntries.size()) {
      return typeSF.edge(direction);
    }
    if (!arrayTs.isEmpty()) {
      var reducedElems = resolveMergeElems(map(arrayTs, ArrayTS::elem), direction);
      return typeF().array(reducedElems);
    }

    if (!funcEntries.isEmpty()) {
      var entry = funcEntries.iterator().next();
      var reducedElems = resolveMergeElems(map(entry.getValue(), MonoFuncTS::res), direction);
      int paramCount = entry.getKey();
      var reducedParams = new ArrayList<MonoTS>();
      for (int i = 0; i < paramCount; i++) {
        int n = i;
        var nthParams = map(entry.getValue(), f -> f.params().get(n));
        reducedParams.add(resolveMergeElems(nthParams, direction.other()));
      }
      return typeF().func(reducedElems, reducedParams);
    }

    if (!others.isEmpty()) {
      return others.iterator().next();
    }

    return zero;
  }

  public MonoTS rebuildComposed(
      MonoTS type, ImmutableList<MonoTS> covars, ImmutableList<MonoTS> contravars) {
    if (!(type instanceof ComposedTS composedT)) {
      throw unexpectedCaseExc(type);
    }
    if (composedT.covars().equals(covars) && composedT.contravars().equals(contravars)) {
      return type;
    }
    return switch (composedT) {
      case ArrayTS array -> typeSF.array(covars.get(0));
      case MonoFuncTS func -> typeSF.func(covars.get(0), contravars);
    };
  }

  public TypeSF typeF() {
    return typeSF;
  }
}
