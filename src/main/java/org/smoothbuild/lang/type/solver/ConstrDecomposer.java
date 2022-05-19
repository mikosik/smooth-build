package org.smoothbuild.lang.type.solver;

import static org.smoothbuild.lang.type.ConstrS.constrS;

import org.smoothbuild.lang.type.AnyTS;
import org.smoothbuild.lang.type.ArrayTS;
import org.smoothbuild.lang.type.BaseTS;
import org.smoothbuild.lang.type.ConstrS;
import org.smoothbuild.lang.type.FuncTS;
import org.smoothbuild.lang.type.JoinTS;
import org.smoothbuild.lang.type.MeetTS;
import org.smoothbuild.lang.type.MergingTS;
import org.smoothbuild.lang.type.NothingTS;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.lang.type.VarS;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * Converts constraint into (possible empty) set of elementary constraints.
 */
public class ConstrDecomposer {
  public ImmutableSet<ConstrS> decompose(ConstrS constr) throws ConstrDecomposeExc {
    Builder<ConstrS> builder = ImmutableSet.builder();
    decompose(constr.lower(), constr.upper(), builder);
    return builder.build();
  }

  private void decompose(TypeS lower, TypeS upper, Builder<ConstrS> builder)
      throws ConstrDecomposeExc {
    if (lower instanceof JoinTS join) {
      for (TypeS elem : join.elems()) {
        decompose(elem, upper, builder);
      }
    } else if (upper instanceof MeetTS meet) {
      for (TypeS elem : meet.elems()) {
        decompose(lower, elem, builder);
      }
    } else if (isElementary(lower, upper)) {
      if (!lower.equals(upper)) {
        builder.add(constrS(lower, upper));
      }
    } else if (lower instanceof NothingTS) {
      // such constraint is always true
    } else if (upper instanceof AnyTS) {
      // such constraint is always true
    } else if (lower instanceof BaseTS base) {
      elementarizeBase(base, upper);
    } else if (lower instanceof ArrayTS array) {
      elementarizeArray(array, upper, builder);
    } else if (lower instanceof FuncTS func) {
      elementarizeFunc(func, upper, builder);
    } else {
      throw new ConstrDecomposeExc(constrS(lower, upper));
    }
  }

  private static boolean isElementary(TypeS lower, TypeS upper) {
    return 3 <= weight(lower) + weight(upper);
  }

  private static int weight(TypeS lower) {
    return switch (lower) {
      case VarS var -> 2;
      case MergingTS merging -> 0;
      default -> 1;
    };
  }

  private void elementarizeBase(BaseTS base, TypeS upper) throws ConstrDecomposeExc {
    if (!(base.getClass().equals(upper.getClass()))) {
      throw new ConstrDecomposeExc(constrS(base, upper));
    }
  }

  private void elementarizeArray(ArrayTS array, TypeS upper, Builder<ConstrS> builder)
      throws ConstrDecomposeExc {
    if (upper instanceof ArrayTS other) {
      decompose(array.elem(), other.elem(), builder);
    } else {
      throw new ConstrDecomposeExc(constrS(array, upper));
    }
  }

  private void elementarizeFunc(FuncTS func, TypeS upper, Builder<ConstrS> builder)
      throws ConstrDecomposeExc {
    if (upper instanceof FuncTS other) {
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
