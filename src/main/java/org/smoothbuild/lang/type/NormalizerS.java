package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.type.ConstrS.constrS;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.type.Side.LOWER;
import static org.smoothbuild.util.type.Side.UPPER;

import org.smoothbuild.util.type.Side;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public class NormalizerS {
  private final TypeSF typeF;
  private int currentId;

  public NormalizerS(TypeSF typeF) {
    this.typeF = typeF;
  }

  public ImmutableSet<ConstrS> normalize(ConstrS constr) {
    Builder<ConstrS> builder = ImmutableSet.builder();
    var lower = normalizeImpl(constr.lower(), LOWER, builder);
    var upper = normalizeImpl(constr.upper(), UPPER, builder);
    builder.add(constrS(lower, upper));
    return builder.build();
  }

  public TypeS normalizeImpl(TypeS type, Side side, Builder<ConstrS> constrBuilder) {
    return switch (type) {
      case VarS var -> var;
      case ArrayTS array -> normalizeArray(array, side, constrBuilder);
      case BaseTS base -> base;
      case FuncTS func -> normalizeFunc(func, side, constrBuilder);
      case StructTS struct -> struct;
      case MeetTS meet -> throw new UnsupportedOperationException();
      case JoinTS join -> throw new UnsupportedOperationException();
    };
  }

  private ArrayTS normalizeArray(ArrayTS array, Side side, Builder<ConstrS> constrBuilder) {
    var elem = normalizeToLeaf(array.elem(), side, constrBuilder);
    return typeF.array(elem);
  }

  private TypeS normalizeFunc(FuncTS func, Side side, Builder<ConstrS> constrBuilder) {
    var res = normalizeToLeaf(func.res(), side, constrBuilder);
    var params = map(func.params(), p -> normalizeToLeaf(p, side.other(), constrBuilder));
    return typeF.func(res, params);
  }

  private VarS normalizeToLeaf(TypeS type, Side side, Builder<ConstrS> constrBuilder) {
    var rootT = normalizeImpl(type, side, constrBuilder);
    var var = newTempVar();
    constrBuilder.add(constrS(var, rootT, side));
    return var;
  }

  public VarS newTempVar() {
    return typeF.var("_" + currentId++);
  }
}
