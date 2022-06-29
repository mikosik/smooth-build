package org.smoothbuild.lang.type.solver;

import static org.smoothbuild.lang.type.EdgeTS.edgeTS;
import static org.smoothbuild.util.Throwables.unexpectedCaseExc;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.smoothbuild.lang.type.ArrayTS;
import org.smoothbuild.lang.type.ComposedTS;
import org.smoothbuild.lang.type.EdgeTS;
import org.smoothbuild.lang.type.MergeTS;
import org.smoothbuild.lang.type.MonoFuncTS;
import org.smoothbuild.lang.type.MonoTS;
import org.smoothbuild.lang.type.Side;
import org.smoothbuild.lang.type.TypeFS;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.MultimapBuilder.SetMultimapBuilder;
import com.google.common.collect.SetMultimap;

public class ResolveMerges {
  public static MonoTS resolveMerges(MonoTS type) {
    return switch (type) {
      case ComposedTS composedT -> {
        var covars = map(composedT.covars(), ResolveMerges::resolveMerges);
        var contravars = map(composedT.contravars(), ResolveMerges::resolveMerges);
        yield rebuildComposed(composedT, covars, contravars);
      }
      case MergeTS mergeT -> resolveMerges(mergeT);
      default -> type;
    };
  }

  private static MonoTS resolveMerges(MergeTS mergeT) {
    return resolveMerge(mergeT.elems(), mergeT.direction());
  }

  public static MonoTS resolveMerge(Collection<MonoTS> elems, Side direction) {
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
      return edgeTS(direction);
    }
    var funcEntries = funcTs.asMap().entrySet();
    if (1 < others.size() + (arrayTs.isEmpty() ? 0 : 1) + funcEntries.size()) {
      return edgeTS(direction);
    }
    if (!arrayTs.isEmpty()) {
      var reducedElems = resolveMerge(map(arrayTs, ArrayTS::elem), direction);
      return TypeFS.array(reducedElems);
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
      return TypeFS.func(reducedElems, reducedParams);
    }

    if (!others.isEmpty()) {
      return others.iterator().next();
    }

    return zero;
  }

  private static MonoTS rebuildComposed(
      ComposedTS composedT, ImmutableList<MonoTS> covars, ImmutableList<MonoTS> contravars) {
    if (composedT.covars().equals(covars) && composedT.contravars().equals(contravars)) {
      return composedT;
    }
    return switch (composedT) {
      case ArrayTS array -> TypeFS.array(covars.get(0));
      case MonoFuncTS func -> TypeFS.func(covars.get(0), contravars);
    };
  }
}
