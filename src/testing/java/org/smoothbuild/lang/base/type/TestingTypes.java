package org.smoothbuild.lang.base.type;

import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Named.named;
import static org.smoothbuild.util.collect.NamedList.namedList;

import org.smoothbuild.lang.base.type.api.AnyType;
import org.smoothbuild.lang.base.type.api.ArrayType;
import org.smoothbuild.lang.base.type.api.BaseType;
import org.smoothbuild.lang.base.type.api.BlobType;
import org.smoothbuild.lang.base.type.api.BoolType;
import org.smoothbuild.lang.base.type.api.FunctionType;
import org.smoothbuild.lang.base.type.api.IntType;
import org.smoothbuild.lang.base.type.api.NothingType;
import org.smoothbuild.lang.base.type.api.StringType;
import org.smoothbuild.lang.base.type.api.StructType;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.TypeFactory;
import org.smoothbuild.lang.base.type.api.Variable;
import org.smoothbuild.testing.TestingContextImpl;
import org.smoothbuild.util.collect.NamedList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class TestingTypes {
  private static final TestingContextImpl CONTEXT = new TestingContextImpl();
  private static final TypeFactory FACTORY = CONTEXT.sTypeFactory();

  public static final ImmutableSet<BaseType> BASE_TYPES = FACTORY.baseTypes();
  public static final ImmutableSet<BaseType> INFERABLE_BASE_TYPES = FACTORY.inferableBaseTypes();

  public static final AnyType ANY = FACTORY.any();
  public static final BlobType BLOB = FACTORY.blob();
  public static final BoolType BOOL = FACTORY.bool();
  public static final IntType INT = FACTORY.int_();
  public static final NothingType NOTHING = FACTORY.nothing();
  public static final StringType STRING = FACTORY.string();
  public static final StructType PERSON = struct(
      "Person", namedList(list(named("firstName", STRING), named("lastName", STRING))));
  public static final StructType FLAG = struct("Flag", namedList(list(named("flab", BOOL))));
  public static final StructType DATA = struct("Data", namedList(list(named("data", BLOB))));
  public static final Variable A = variable("A");
  public static final Variable B = variable("B");
  public static final Variable C = variable("C");
  public static final Variable D = variable("D");
  public static final Variable X = variable("X");

  public static final ImmutableList<Type> ELEMENTARY_TYPES = ImmutableList.<Type>builder()
      .addAll(BASE_TYPES)
      .add(PERSON)
      .build();

  public static final FunctionType STRING_GETTER_FUNCTION = f(STRING);
  public static final FunctionType PERSON_GETTER_FUNCTION = f(PERSON);
  public static final FunctionType STRING_MAP_FUNCTION = f(STRING, STRING);
  public static final FunctionType PERSON_MAP_FUNCTION = f(PERSON, PERSON);
  public static final FunctionType IDENTITY_FUNCTION = f(A, A);
  public static final FunctionType ARRAY_HEAD_FUNCTION = f(A, a(A));
  public static final FunctionType ARRAY_LENGTH_FUNCTION = f(STRING, a(A));

  public static final ImmutableList<Type> FUNCTION_TYPES =
      list(
          STRING_GETTER_FUNCTION,
          PERSON_GETTER_FUNCTION,
          STRING_MAP_FUNCTION,
          PERSON_MAP_FUNCTION,
          IDENTITY_FUNCTION,
          ARRAY_HEAD_FUNCTION,
          ARRAY_LENGTH_FUNCTION);

  public static final ImmutableList<Type> ALL_TESTED_TYPES =
      ImmutableList.<Type>builder()
          .addAll(ELEMENTARY_TYPES)
          .addAll(FUNCTION_TYPES)
          .add(X)
          .build();

  public static ArrayType a(Type elemType) {
    return FACTORY.array(elemType);
  }

  public static FunctionType f(Type resultType) {
    return FACTORY.function(resultType, list());
  }

  public static FunctionType f(Type resultType, Type... params) {
    return f(resultType, list(params));
  }

  public static FunctionType f(Type resultType, ImmutableList<Type> params) {
    return FACTORY.function(resultType, params);
  }

  public static Variable variable(String a) {
    return FACTORY.variable(a);
  }

  public static StructType struct(String name, NamedList<? extends Type> fields) {
    return FACTORY.struct(name, fields);
  }
}
