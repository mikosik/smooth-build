package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.type.Type.toItemSignatures;
import static org.smoothbuild.util.Lists.list;

import java.util.Optional;

import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class TestingTypes {
  private static final Typing TYPING = new TestingContext().typing();

  public static final ImmutableSet<BaseType> BASE_TYPES = TYPING.baseTypes();
  public static final ImmutableSet<BaseType> INFERABLE_BASE_TYPES = TYPING.inferableBaseTypes();

  public static final AnyType ANY = TYPING.any();
  public static final BlobType BLOB = TYPING.blob();
  public static final BoolType BOOL = TYPING.bool();
  public static final IntType INT = TYPING.int_();
  public static final NothingType NOTHING = TYPING.nothing();
  public static final StringType STRING = TYPING.string();
  public static final StructType PERSON = struct("Person", list(
      new ItemSignature(STRING, "firstName", Optional.empty()),
      new ItemSignature(STRING, "lastName", Optional.empty())));
  public static final StructType FLAG = struct("Flag", list(
          new ItemSignature(BOOL, "flag", Optional.empty())));
  public static final StructType DATA = struct("Data", list(
          new ItemSignature(BLOB, "data", Optional.empty())));
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
    return TYPING.array(elemType);
  }

  public static FunctionType f(Type resultType) {
    return TYPING.function(resultType, list());
  }

  public static FunctionType f(Type resultType, Type... paramTypes) {
    return TYPING.function(resultType, toItemSignatures(list(paramTypes)));
  }

  public static FunctionType f(Type resultType, ItemSignature... params) {
    return f(resultType, list(params));
  }

  public static FunctionType f(Type resultType, ImmutableList<ItemSignature> params) {
    return TYPING.function(resultType, params);
  }

  public static Variable variable(String a) {
    return TYPING.variable(a);
  }

  public static StructType struct(String name, ImmutableList<ItemSignature> fields) {
    return TYPING.struct(name, fields);
  }

  public static ItemSignature item(Type type, String name) {
    return new ItemSignature(type, name, Optional.empty());
  }

  public static ItemSignature item(Type type) {
    return new ItemSignature(type, Optional.empty(), Optional.empty());
  }
}
