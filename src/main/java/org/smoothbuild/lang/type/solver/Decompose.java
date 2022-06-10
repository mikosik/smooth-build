package org.smoothbuild.lang.type.solver;

import static org.smoothbuild.lang.type.ConstrS.constrS;

import org.smoothbuild.lang.type.AnyTS;
import org.smoothbuild.lang.type.ArrayTS;
import org.smoothbuild.lang.type.BaseTS;
import org.smoothbuild.lang.type.ConstrS;
import org.smoothbuild.lang.type.JoinTS;
import org.smoothbuild.lang.type.MeetTS;
import org.smoothbuild.lang.type.MergeTS;
import org.smoothbuild.lang.type.MonoFuncTS;
import org.smoothbuild.lang.type.MonoTS;
import org.smoothbuild.lang.type.NothingTS;
import org.smoothbuild.lang.type.StructTS;
import org.smoothbuild.lang.type.VarS;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * Decomposes constraint into (possible empty) set of elementary constraints.
 */
public class Decompose {
  public static ImmutableSet<ConstrS> decompose(ConstrS constr) throws ConstrDecomposeExc {
    Builder<ConstrS> builder = ImmutableSet.builder();
    decompose(constr.lower(), constr.upper(), builder);
    return builder.build();
  }

  private static void decompose(MonoTS lower, MonoTS upper, Builder<ConstrS> builder)
      throws ConstrDecomposeExc {
    if (lower instanceof JoinTS join) {
      for (MonoTS elem : join.elems()) {
        decompose(elem, upper, builder);
      }
    } else if (upper instanceof MeetTS meet) {
      for (MonoTS elem : meet.elems()) {
        decompose(lower, elem, builder);
      }
    } else if (lower instanceof NothingTS) {
      // such constraint is always true
    } else if (upper instanceof AnyTS) {
      // such constraint is always true
    } else if (isElementary(lower, upper)) {
      if (!lower.equals(upper)) {
        builder.add(constrS(lower, upper));
      }
    } else if (lower instanceof BaseTS base) {
      elementarizeBase(base, upper);
    } else if (lower instanceof StructTS struct) {
      elementarizeStruct(struct, upper);
    } else if (lower instanceof ArrayTS array) {
      elementarizeArray(array, upper, builder);
    } else if (lower instanceof MonoFuncTS func) {
      elementarizeFunc(func, upper, builder);
    } else {
      throw new ConstrDecomposeExc(constrS(lower, upper));
    }
  }

  private static boolean isElementary(MonoTS lower, MonoTS upper) {
    return 3 <= weight(lower) + weight(upper);
  }

  private static int weight(MonoTS lower) {
    return switch (lower) {
      case VarS var -> 2;
      case MergeTS merge -> 0;
      default -> 1;
    };
  }

  private static void elementarizeBase(BaseTS base, MonoTS upper) throws ConstrDecomposeExc {
    if (!(base.getClass().equals(upper.getClass()))) {
      throw new ConstrDecomposeExc(constrS(base, upper));
    }
  }

  private static void elementarizeStruct(StructTS struct, MonoTS upper) throws ConstrDecomposeExc {
    if (!struct.equals(upper)) {
      throw new ConstrDecomposeExc(constrS(struct, upper));
    }
  }

  private static void elementarizeArray(ArrayTS array, MonoTS upper, Builder<ConstrS> builder)
      throws ConstrDecomposeExc {
    if (upper instanceof ArrayTS other) {
      decompose(array.elem(), other.elem(), builder);
    } else {
      throw new ConstrDecomposeExc(constrS(array, upper));
    }
  }

  private static void elementarizeFunc(MonoFuncTS func, MonoTS upper, Builder<ConstrS> builder)
      throws ConstrDecomposeExc {
    if (upper instanceof MonoFuncTS other) {
      var params = func.params();
      var otherParams = other.params();
      if (params.size() == otherParams.size()) {
        decompose(func.res(), other.res(), builder);
        for (int i = 0; i < params.size(); i++) {
          decompose(otherParams.get(i), params.get(i), builder);
        }
      } else {
        throw new ConstrDecomposeExc(constrS(func, upper));
      }
    } else {
      throw new ConstrDecomposeExc(constrS(func, upper));
    }
  }
}
