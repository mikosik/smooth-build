package org.smoothbuild.lang.type.solver;

import static org.smoothbuild.lang.type.EdgeTS.edgeTS;
import static org.smoothbuild.lang.type.JoinTS.join;
import static org.smoothbuild.lang.type.MeetTS.meet;
import static org.smoothbuild.util.collect.Lists.map;

import javax.inject.Inject;

import org.smoothbuild.lang.type.ArrayTS;
import org.smoothbuild.lang.type.Bounds;
import org.smoothbuild.lang.type.ComposedTS;
import org.smoothbuild.lang.type.JoinTS;
import org.smoothbuild.lang.type.MeetTS;
import org.smoothbuild.lang.type.MonoFuncTS;
import org.smoothbuild.lang.type.MonoTS;
import org.smoothbuild.lang.type.Side;
import org.smoothbuild.lang.type.TypeFS;
import org.smoothbuild.lang.type.TypingS;
import org.smoothbuild.lang.type.VarS;
import org.smoothbuild.util.collect.Sets;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class DenormalizerS {
  private final TypeFS typeFS;
  private final ConstrGraphS graph;
  private final TypingS typing;

  @Inject
  public DenormalizerS(TypeFS typeFS, ConstrGraphS graph) {
    this.typeFS = typeFS;
    this.graph = graph;
    this.typing = new TypingS(typeFS);
  }

  public MonoTS denormalizeVars(MonoTS type, Side side) {
    if (type.vars().isEmpty()) {
      return type;
    }
    return switch (type) {
      case VarS var -> denormalizeVar(var, side);
      case ComposedTS composedT -> {
        var covars = map(
            composedT.covars(), p -> denormalizeVars(p, side));
        var contravars = map(
            composedT.contravars(), p -> denormalizeVars(p, side.other()));
        yield rebuildComposed(composedT, covars, contravars);
      }
      case JoinTS joinT -> {
        ImmutableSet<MonoTS> elems = denormalizeElems(joinT.elems(), side);
        yield join(elems);
      }
      case MeetTS meetT -> meet(denormalizeElems(meetT.elems(), side));
      default -> type;
    };
  }

  private ImmutableSet<MonoTS> denormalizeElems(ImmutableSet<MonoTS> elems, Side side) {
    return Sets.map(elems, e -> denormalizeVars(e, side));
  }

  private MonoTS denormalizeVar(VarS var, Side side) {
    if (!var.hasPrefix()) {
      return var;
    }
    var neighbours = graph.neighbours(var, side);
    return switch (neighbours.size()) {
      case 0 -> {
        Bounds<MonoTS> bounds = graph.varBounds().get(var);
        if (bounds == null) {
          // If `var` has not been constrained during constraint solving then it is not
          // present in the graph at all. Its bounds are <nothing, any>.
          yield edgeTS(side);
        }
        yield denormalizeVars(bounds.get(side), side);
      }
      case 1 -> denormalizeVar(neighbours.iterator().next(), side);
      // TODO resolveMerge() doesn't take into account neighbours
      // but there's probably corner case where it should (see paper notes)
      default -> typing.resolveMerge(map(neighbours, t -> denormalizeVars(t, side)), side.other());
    };
  }

  private MonoTS rebuildComposed(
      ComposedTS composedT, ImmutableList<MonoTS> covars, ImmutableList<MonoTS> contravars) {
    if (composedT.covars().equals(covars) && composedT.contravars().equals(contravars)) {
      return composedT;
    }
    return switch (composedT) {
      case ArrayTS array -> typeFS.array(covars.get(0));
      case MonoFuncTS func -> typeFS.func(covars.get(0), contravars);
    };
  }
}
