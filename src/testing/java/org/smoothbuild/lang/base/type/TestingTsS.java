package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.define.ItemSigS.itemSigS;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nList;

import org.smoothbuild.lang.base.define.ItemSigS;
import org.smoothbuild.lang.base.type.api.Bounds;
import org.smoothbuild.lang.base.type.api.BoundsMap;
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.lang.base.type.impl.AnyTS;
import org.smoothbuild.lang.base.type.impl.ArrayTS;
import org.smoothbuild.lang.base.type.impl.BaseTS;
import org.smoothbuild.lang.base.type.impl.BlobTS;
import org.smoothbuild.lang.base.type.impl.BoolTS;
import org.smoothbuild.lang.base.type.impl.FuncTS;
import org.smoothbuild.lang.base.type.impl.IntTS;
import org.smoothbuild.lang.base.type.impl.NothingTS;
import org.smoothbuild.lang.base.type.impl.StringTS;
import org.smoothbuild.lang.base.type.impl.StructTS;
import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.lang.base.type.impl.TypingS;
import org.smoothbuild.lang.base.type.impl.VarS;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class TestingTsS {
  private static final TestingContext CONTEXT = new TestingContext();
  private static final TypeFactoryS FACTORY = CONTEXT.typeFactoryS();
  public static final TypingS TYPING = CONTEXT.typingS();

  public static final ImmutableList<BaseTS> BASE_TYPES = FACTORY.baseTs();
  public static final ImmutableList<BaseTS> INFERABLE_BASE_TYPES = FACTORY.inferableBaseTs();

  public static final AnyTS ANY = FACTORY.any();
  public static final BlobTS BLOB = FACTORY.blob();
  public static final BoolTS BOOL = FACTORY.bool();
  public static final IntTS INT = FACTORY.int_();
  public static final NothingTS NOTHING = FACTORY.nothing();
  public static final StringTS STRING = FACTORY.string();
  public static final StructTS PERSON = struct("Person",
      nList(itemSigS("firstName", STRING), itemSigS("lastName", STRING)));
  public static final StructTS FLAG = struct("Flag", nList(itemSigS("flab", BOOL)));
  public static final StructTS DATA = struct("Data", nList(itemSigS("data", BLOB)));
  public static final VarS A = var("A");
  public static final VarS B = var("B");
  public static final VarS C = var("C");
  public static final VarS D = var("D");
  public static final VarS X = var("X");
  public static final Side<TypeS> LOWER = FACTORY.lower();
  public static final Side<TypeS> UPPER = FACTORY.upper();

  public static final ImmutableList<TypeS> ELEMENTARY_TYPES = ImmutableList.<TypeS>builder()
      .addAll(BASE_TYPES)
      .add(PERSON)
      .build();

  public static final FuncTS STRING_GETTER_FUNCTION = f(STRING);
  public static final FuncTS PERSON_GETTER_FUNCTION = f(PERSON);
  public static final FuncTS STRING_MAP_FUNCTION = f(STRING, STRING);
  public static final FuncTS PERSON_MAP_FUNCTION = f(PERSON, PERSON);
  public static final FuncTS IDENTITY_FUNCTION = f(A, A);
  public static final FuncTS ARRAY_HEAD_FUNCTION = f(A, a(A));
  public static final FuncTS ARRAY_LENGTH_FUNCTION = f(STRING, a(A));

  public static final ImmutableList<TypeS> FUNCTION_TYPES =
      list(
          STRING_GETTER_FUNCTION,
          PERSON_GETTER_FUNCTION,
          STRING_MAP_FUNCTION,
          PERSON_MAP_FUNCTION,
          IDENTITY_FUNCTION,
          ARRAY_HEAD_FUNCTION,
          ARRAY_LENGTH_FUNCTION);

  public static final ImmutableList<TypeS> ALL_TESTED_TYPES =
      ImmutableList.<TypeS>builder()
          .addAll(ELEMENTARY_TYPES)
          .addAll(FUNCTION_TYPES)
          .add(X)
          .build();

  public static ArrayTS a(TypeS elemT) {
    return FACTORY.array(elemT);
  }

  public static FuncTS f(TypeS resT) {
    return FACTORY.func(resT, list());
  }

  public static FuncTS f(TypeS resT, TypeS... paramTs) {
    return f(resT, list(paramTs));
  }

  public static FuncTS f(TypeS resT, ImmutableList<TypeS> paramTs) {
    return FACTORY.func(resT, paramTs);
  }

  public static VarS var(String a) {
    return FACTORY.var(a);
  }

  public static StructTS struct(String name, NList<ItemSigS> fields) {
    return FACTORY.struct(name, fields);
  }

  public static Bounds<TypeS> oneSideBound(Side<TypeS> side, TypeS type) {
    return FACTORY.oneSideBound(side, type);
  }

  public static BoundsMap<TypeS> bm(
      VarS var1, Side<TypeS> side1, TypeS bound1,
      VarS var2, Side<TypeS> side2, TypeS bound2) {
    return CONTEXT.bmS(var1, side1, bound1, var2, side2, bound2);
  }

  public static BoundsMap<TypeS> bm(VarS var, Side<TypeS> side, TypeS bound) {
    return CONTEXT.bmS(var, side, bound);
  }

  public static BoundsMap<TypeS> bm() {
    return CONTEXT.bmS();
  }
}
