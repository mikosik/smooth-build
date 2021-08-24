package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.type.Type.toItemSignatures;
import static org.smoothbuild.lang.base.type.Types.BASE_TYPES;
import static org.smoothbuild.lang.base.type.Types.anyT;
import static org.smoothbuild.lang.base.type.Types.blobT;
import static org.smoothbuild.lang.base.type.Types.boolT;
import static org.smoothbuild.lang.base.type.Types.intT;
import static org.smoothbuild.lang.base.type.Types.nothingT;
import static org.smoothbuild.lang.base.type.Types.stringT;
import static org.smoothbuild.lang.base.type.Types.structT;
import static org.smoothbuild.lang.base.type.Types.variable;
import static org.smoothbuild.util.Lists.list;

import java.util.Optional;

import com.google.common.collect.ImmutableList;

public class TestingTypes {
  public static final AnyType ANY = anyT();
  public static final BlobType BLOB = blobT();
  public static final BoolType BOOL = boolT();
  public static final IntType INT = intT();
  public static final NothingType NOTHING = nothingT();
  public static final StringType STRING = stringT();
  public static final StructType PERSON = structT("Person", list(
          new ItemSignature(STRING, "firstName", Optional.empty()),
          new ItemSignature(STRING, "lastName", Optional.empty())));
  public static final StructType FLAG = structT("Flag", list(
          new ItemSignature(BOOL, "flag", Optional.empty())));
  public static final StructType DATA = structT("Data", list(
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
    return Types.arrayT(elemType);
  }

  public static FunctionType f(Type resultType) {
    return Types.functionT(resultType, list());
  }

  public static FunctionType f(Type resultType, Type... paramTypes) {
    return Types.functionT(resultType, toItemSignatures(list(paramTypes)));
  }

  public static FunctionType f(Type resultType, ItemSignature... params) {
    return Types.functionT(resultType, list(params));
  }

  public static ItemSignature item(Type type, String name) {
    return new ItemSignature(type, name, Optional.empty());
  }

  public static ItemSignature item(Type type) {
    return new ItemSignature(type, Optional.empty(), Optional.empty());
  }
}
