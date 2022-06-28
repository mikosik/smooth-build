package org.smoothbuild.lang.type.solver;

import static org.smoothbuild.lang.type.ConstrS.constrS;
import static org.smoothbuild.lang.type.Side.LOWER;
import static org.smoothbuild.lang.type.Side.UPPER;
import static org.smoothbuild.util.Throwables.unexpectedCaseExc;
import static org.smoothbuild.util.collect.Lists.map;

import org.smoothbuild.lang.type.ArrayTS;
import org.smoothbuild.lang.type.BaseTS;
import org.smoothbuild.lang.type.ConstrS;
import org.smoothbuild.lang.type.JoinTS;
import org.smoothbuild.lang.type.MeetTS;
import org.smoothbuild.lang.type.MonoFuncTS;
import org.smoothbuild.lang.type.MonoTS;
import org.smoothbuild.lang.type.Side;
import org.smoothbuild.lang.type.StructTS;
import org.smoothbuild.lang.type.TypeFS;
import org.smoothbuild.lang.type.VarS;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public class Normalizer {
  private final TypeFS typeF;
  private int currentId;

  public Normalizer(TypeFS typeF) {
    this.typeF = typeF;
  }

  public ImmutableSet<ConstrS> normalize(ConstrS constr) {
    Builder<ConstrS> builder = ImmutableSet.builder();
    var lower = normalizeImpl(constr.lower(), LOWER, builder);
    var upper = normalizeImpl(constr.upper(), UPPER, builder);
    builder.add(constrS(lower, upper));
    return builder.build();
  }

  public MonoTS normalizeImpl(MonoTS type, Side side, Builder<ConstrS> constrBuilder) {
    return switch (type) {
      case ArrayTS array -> normalizeArray(array, side, constrBuilder);
      case BaseTS base -> base;
      case MonoFuncTS func -> normalizeFunc(func, side, constrBuilder);
      case JoinTS join -> throw unexpectedCaseExc(join);
      case MeetTS meet -> throw unexpectedCaseExc(meet);
      case StructTS struct -> struct;
      case VarS var -> var;
    };
  }

  private ArrayTS normalizeArray(ArrayTS array, Side side, Builder<ConstrS> constrBuilder) {
    var elem = normalizeToLeaf(array.elem(), side, constrBuilder);
    return typeF.array(elem);
  }

  private MonoTS normalizeFunc(MonoFuncTS func, Side side, Builder<ConstrS> constrBuilder) {
    var res = normalizeToLeaf(func.res(), side, constrBuilder);
    var params = map(func.params(), p -> normalizeToLeaf(p, side.other(), constrBuilder));
    return typeF.func(res, params);
  }

  private VarS normalizeToLeaf(MonoTS type, Side side, Builder<ConstrS> constrBuilder) {
    var rootT = normalizeImpl(type, side, constrBuilder);
    var var = newTempVar();
    constrBuilder.add(constrS(var, rootT, side));
    return var;
  }

  public VarS newTempVar() {
    return typeF.var(Integer.toString(currentId++)).prefixed("_");
  }
}
