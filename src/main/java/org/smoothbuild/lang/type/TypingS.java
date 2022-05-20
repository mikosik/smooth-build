package org.smoothbuild.lang.type;

import static org.smoothbuild.util.Throwables.unexpectedCaseExc;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Lists.zip;
import static org.smoothbuild.util.type.Side.LOWER;
import static org.smoothbuild.util.type.Side.UPPER;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.inject.Inject;

import org.smoothbuild.util.type.Side;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.MultimapBuilder.SetMultimapBuilder;
import com.google.common.collect.SetMultimap;

public class TypingS {
  private final TypeFS typeFS;

  @Inject
  public TypingS(TypeFS typeFS) {
    this.typeFS = typeFS;
  }

  public MonoTS mergeUp(MonoTS type1, MonoTS type2) {
    return merge(type1, type2, UPPER);
  }

  public MonoTS mergeDown(MonoTS type1, MonoTS type2) {
    return merge(type1, type2, LOWER);
  }

  public MonoTS merge(MonoTS type1, MonoTS type2, Side direction) {
    MonoTS otherEdge = typeFS.edge(direction.other());
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
          return rebuildComposed(c1, covars, contravars);
        }
      }
    }
    return typeFS.edge(direction);
  }

  public MonoTS resolveMerges(MonoTS type) {
    return switch (type) {
      case ComposedTS composedT -> {
        var covars = map(composedT.covars(), this::resolveMerges);
        var contravars = map(composedT.contravars(), this::resolveMerges);
        yield rebuildComposed(composedT, covars, contravars);
      }
      case MergeTS mergeT -> resolveMerges(mergeT);
      default -> type;
    };
  }

  private MonoTS resolveMerges(MergeTS mergeT) {
    return resolveMerge(mergeT.elems(), mergeT.direction());
  }

  public MonoTS resolveMerge(Collection<MonoTS> elems, Side direction) {
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
      return typeFS.edge(direction);
    }
    var funcEntries = funcTs.asMap().entrySet();
    if (1 < others.size() + (arrayTs.isEmpty() ? 0 : 1) + funcEntries.size()) {
      return typeFS.edge(direction);
    }
    if (!arrayTs.isEmpty()) {
      var reducedElems = resolveMerge(map(arrayTs, ArrayTS::elem), direction);
      return typeF().array(reducedElems);
    }

    if (!funcEntries.isEmpty()) {
      var entry = funcEntries.iterator().next();
      var reducedElems = resolveMerge(map(entry.getValue(), MonoFuncTS::res), direction);
      int paramCount = entry.getKey();
      var reducedParams = new ArrayList<MonoTS>();
      for (int i = 0; i < paramCount; i++) {
        int n = i;
        var nthParams = map(entry.getValue(), f -> f.params().get(n));
        reducedParams.add(resolveMerge(nthParams, direction.other()));
      }
      return typeF().func(reducedElems, reducedParams);
    }

    if (!others.isEmpty()) {
      return others.iterator().next();
    }

    return zero;
  }

  public MonoTS rebuildComposed(
      ComposedTS composedT, ImmutableList<MonoTS> covars, ImmutableList<MonoTS> contravars) {
    if (composedT.covars().equals(covars) && composedT.contravars().equals(contravars)) {
      return composedT;
    }
    return switch (composedT) {
      case ArrayTS array -> typeFS.array(covars.get(0));
      case MonoFuncTS func -> typeFS.func(covars.get(0), contravars);
    };
  }

  public TypeFS typeF() {
    return typeFS;
  }
}
