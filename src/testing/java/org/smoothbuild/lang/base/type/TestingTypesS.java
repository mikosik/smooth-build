package org.smoothbuild.lang.base.type;

import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Named.named;
import static org.smoothbuild.util.collect.NamedList.namedList;

import org.smoothbuild.lang.base.type.api.Bounds;
import org.smoothbuild.lang.base.type.api.BoundsMap;
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.lang.base.type.impl.AnySType;
import org.smoothbuild.lang.base.type.impl.ArraySType;
import org.smoothbuild.lang.base.type.impl.BaseSType;
import org.smoothbuild.lang.base.type.impl.BlobSType;
import org.smoothbuild.lang.base.type.impl.BoolSType;
import org.smoothbuild.lang.base.type.impl.FunctionSType;
import org.smoothbuild.lang.base.type.impl.IntSType;
import org.smoothbuild.lang.base.type.impl.NothingSType;
import org.smoothbuild.lang.base.type.impl.StringSType;
import org.smoothbuild.lang.base.type.impl.StructSType;
import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.lang.base.type.impl.TypingS;
import org.smoothbuild.lang.base.type.impl.VariableSType;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.util.collect.NamedList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class TestingTypesS {
  private static final TestingContext CONTEXT = new TestingContext();
  private static final TypeFactoryS FACTORY = CONTEXT.typeFactoryS();
  public static final TypingS TYPING = CONTEXT.typingS();

  public static final ImmutableSet<BaseSType> BASE_TYPES = FACTORY.baseTypes();
  public static final ImmutableSet<BaseSType> INFERABLE_BASE_TYPES = FACTORY.inferableBaseTypes();

  public static final AnySType ANY = FACTORY.any();
  public static final BlobSType BLOB = FACTORY.blob();
  public static final BoolSType BOOL = FACTORY.bool();
  public static final IntSType INT = FACTORY.int_();
  public static final NothingSType NOTHING = FACTORY.nothing();
  public static final StringSType STRING = FACTORY.string();
  public static final StructSType PERSON = struct(
      "Person", namedList(list(named("firstName", STRING), named("lastName", STRING))));
  public static final StructSType FLAG = struct("Flag", namedList(list(named("flab", BOOL))));
  public static final StructSType DATA = struct("Data", namedList(list(named("data", BLOB))));
  public static final VariableSType A = variable("A");
  public static final VariableSType B = variable("B");
  public static final VariableSType C = variable("C");
  public static final VariableSType D = variable("D");
  public static final VariableSType X = variable("X");
  public static final Side LOWER = FACTORY.lower();
  public static final Side UPPER = FACTORY.upper();

  public static final ImmutableList<TypeS> ELEMENTARY_TYPES = ImmutableList.<TypeS>builder()
      .addAll(BASE_TYPES)
      .add(PERSON)
      .build();

  public static final FunctionSType STRING_GETTER_FUNCTION = f(STRING);
  public static final FunctionSType PERSON_GETTER_FUNCTION = f(PERSON);
  public static final FunctionSType STRING_MAP_FUNCTION = f(STRING, STRING);
  public static final FunctionSType PERSON_MAP_FUNCTION = f(PERSON, PERSON);
  public static final FunctionSType IDENTITY_FUNCTION = f(A, A);
  public static final FunctionSType ARRAY_HEAD_FUNCTION = f(A, a(A));
  public static final FunctionSType ARRAY_LENGTH_FUNCTION = f(STRING, a(A));

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

  public static ArraySType a(TypeS elemType) {
    return FACTORY.array(elemType);
  }

  public static FunctionSType f(TypeS resultType) {
    return FACTORY.function(resultType, list());
  }

  public static FunctionSType f(TypeS resultType, TypeS... params) {
    return f(resultType, list(params));
  }

  public static FunctionSType f(TypeS resultType, ImmutableList<TypeS> params) {
    return FACTORY.function(resultType, params);
  }

  public static VariableSType variable(String a) {
    return FACTORY.variable(a);
  }

  public static StructSType struct(String name, NamedList<? extends TypeS> fields) {
    return FACTORY.struct(name, fields);
  }

  public static Bounds oneSideBound(Side side, TypeS type) {
    return FACTORY.oneSideBound(side, type);
  }

  public static BoundsMap bm(
      VariableSType var1, Side side1, TypeS bound1,
      VariableSType var2, Side side2, TypeS bound2) {
    return CONTEXT.bmST(var1, side1, bound1, var2, side2, bound2);
  }

  public static BoundsMap bm(VariableSType var, Side side, TypeS bound) {
    return CONTEXT.bmST(var, side, bound);
  }

  public static BoundsMap bm() {
    return CONTEXT.bmST();
  }
}
