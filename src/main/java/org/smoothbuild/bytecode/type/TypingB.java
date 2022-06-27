package org.smoothbuild.bytecode.type;

import static org.smoothbuild.util.Throwables.unexpectedCaseExc;
import static org.smoothbuild.util.collect.Lists.zip;
import static org.smoothbuild.util.type.Side.LOWER;
import static org.smoothbuild.util.type.Side.UPPER;

import javax.inject.Inject;

import org.smoothbuild.bytecode.type.cnst.ArrayTB;
import org.smoothbuild.bytecode.type.cnst.ComposedTB;
import org.smoothbuild.bytecode.type.cnst.FuncTB;
import org.smoothbuild.bytecode.type.cnst.TupleTB;
import org.smoothbuild.bytecode.type.cnst.TypeB;
import org.smoothbuild.util.type.Bounds;
import org.smoothbuild.util.type.Side;

import com.google.common.collect.ImmutableList;

public class TypingB {
  private final TypeFB typeFB;

  @Inject
  public TypingB(TypeFB typeFB) {
    this.typeFB = typeFB;
  }

  public TypeB mergeUp(TypeB type1, TypeB type2) {
    return merge(type1, type2, UPPER);
  }

  public TypeB mergeDown(TypeB type1, TypeB type2) {
    return merge(type1, type2, LOWER);
  }

  public TypeB merge(TypeB type1, TypeB type2, Side direction) {
    TypeB otherEdge = typeFB.edge(direction.other());
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
    return typeFB.edge(direction);
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
      case ArrayTB array -> typeFB.array(covars.get(0));
      case FuncTB func -> typeFB.func(covars.get(0), contravars);
      case TupleTB tuple -> typeFB.tuple(covars);
    };
  }

  public TypeFB typeF() {
    return typeFB;
  }
}
