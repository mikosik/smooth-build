package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.define.ItemSignature.itemSignature;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nList;

import org.smoothbuild.lang.base.define.ItemSignature;
import org.smoothbuild.lang.base.type.api.Bounds;
import org.smoothbuild.lang.base.type.api.BoundsMap;
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.lang.base.type.impl.AnyTypeS;
import org.smoothbuild.lang.base.type.impl.ArrayTypeS;
import org.smoothbuild.lang.base.type.impl.BaseTypeS;
import org.smoothbuild.lang.base.type.impl.BlobTypeS;
import org.smoothbuild.lang.base.type.impl.BoolTypeS;
import org.smoothbuild.lang.base.type.impl.FunctionTypeS;
import org.smoothbuild.lang.base.type.impl.IntTypeS;
import org.smoothbuild.lang.base.type.impl.NothingTypeS;
import org.smoothbuild.lang.base.type.impl.StringTypeS;
import org.smoothbuild.lang.base.type.impl.StructTypeS;
import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.lang.base.type.impl.TypingS;
import org.smoothbuild.lang.base.type.impl.VariableS;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class TestingTypesS {
  private static final TestingContext CONTEXT = new TestingContext();
  private static final TypeFactoryS FACTORY = CONTEXT.typeFactoryS();
  public static final TypingS TYPING = CONTEXT.typingS();

  public static final ImmutableList<BaseTypeS> BASE_TYPES = FACTORY.baseTypes();
  public static final ImmutableList<BaseTypeS> INFERABLE_BASE_TYPES = FACTORY.inferableBaseTypes();

  public static final AnyTypeS ANY = FACTORY.any();
  public static final BlobTypeS BLOB = FACTORY.blob();
  public static final BoolTypeS BOOL = FACTORY.bool();
  public static final IntTypeS INT = FACTORY.int_();
  public static final NothingTypeS NOTHING = FACTORY.nothing();
  public static final StringTypeS STRING = FACTORY.string();
  public static final StructTypeS PERSON = struct("Person",
      nList(itemSignature("firstName", STRING), itemSignature("lastName", STRING)));
  public static final StructTypeS FLAG = struct("Flag", nList(itemSignature("flab", BOOL)));
  public static final StructTypeS DATA = struct("Data", nList(itemSignature("data", BLOB)));
  public static final VariableS A = variable("A");
  public static final VariableS B = variable("B");
  public static final VariableS C = variable("C");
  public static final VariableS D = variable("D");
  public static final VariableS X = variable("X");
  public static final Side<TypeS> LOWER = FACTORY.lower();
  public static final Side<TypeS> UPPER = FACTORY.upper();

  public static final ImmutableList<TypeS> ELEMENTARY_TYPES = ImmutableList.<TypeS>builder()
      .addAll(BASE_TYPES)
      .add(PERSON)
      .build();

  public static final FunctionTypeS STRING_GETTER_FUNCTION = f(STRING);
  public static final FunctionTypeS PERSON_GETTER_FUNCTION = f(PERSON);
  public static final FunctionTypeS STRING_MAP_FUNCTION = f(STRING, STRING);
  public static final FunctionTypeS PERSON_MAP_FUNCTION = f(PERSON, PERSON);
  public static final FunctionTypeS IDENTITY_FUNCTION = f(A, A);
  public static final FunctionTypeS ARRAY_HEAD_FUNCTION = f(A, a(A));
  public static final FunctionTypeS ARRAY_LENGTH_FUNCTION = f(STRING, a(A));

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

  public static ArrayTypeS a(TypeS elemType) {
    return FACTORY.array(elemType);
  }

  public static FunctionTypeS f(TypeS resultType) {
    return FACTORY.function(resultType, list());
  }

  public static FunctionTypeS f(TypeS resultType, TypeS... params) {
    return f(resultType, list(params));
  }

  public static FunctionTypeS f(TypeS resultType, ImmutableList<TypeS> params) {
    return FACTORY.function(resultType, params);
  }

  public static VariableS variable(String a) {
    return FACTORY.variable(a);
  }

  public static StructTypeS struct(String name, NList<ItemSignature> fields) {
    return FACTORY.struct(name, fields);
  }

  public static Bounds<TypeS> oneSideBound(Side<TypeS> side, TypeS type) {
    return FACTORY.oneSideBound(side, type);
  }

  public static BoundsMap<TypeS> bm(
      VariableS var1, Side<TypeS> side1, TypeS bound1,
      VariableS var2, Side<TypeS> side2, TypeS bound2) {
    return CONTEXT.bmST(var1, side1, bound1, var2, side2, bound2);
  }

  public static BoundsMap<TypeS> bm(VariableS var, Side<TypeS> side, TypeS bound) {
    return CONTEXT.bmST(var, side, bound);
  }

  public static BoundsMap<TypeS> bm() {
    return CONTEXT.bmST();
  }
}
