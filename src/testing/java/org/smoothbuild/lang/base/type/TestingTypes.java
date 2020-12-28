package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.Location.internal;
import static org.smoothbuild.lang.base.type.Types.BASE_TYPES;
import static org.smoothbuild.lang.base.type.Types.any;
import static org.smoothbuild.lang.base.type.Types.blob;
import static org.smoothbuild.lang.base.type.Types.bool;
import static org.smoothbuild.lang.base.type.Types.nothing;
import static org.smoothbuild.lang.base.type.Types.string;
import static org.smoothbuild.lang.base.type.Types.struct;
import static org.smoothbuild.lang.base.type.Types.typeVariable;
import static org.smoothbuild.testing.common.TestingLocation.loc;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.Item;

import com.google.common.collect.ImmutableList;

public class TestingTypes {
  public static final AnyType ANY = any();
  public static final BoolType BOOL = bool();
  public static final StringType STRING = string();
  public static final BlobType BLOB = blob();
  public static final NothingType NOTHING = nothing();
  public static final StructType PERSON = struct("Person", loc(), List.of(
          new Item(STRING, "firstName", Optional.empty(), internal()),
          new Item(STRING, "lastName", Optional.empty(),internal())));
  public static final StructType FLAG = struct("Flag", loc(), List.of(
          new Item(BOOL, "flag", Optional.empty(),internal())));
  public static final StructType DATA = struct("Data", loc(), List.of(
          new Item(BLOB, "data", Optional.empty(),internal())));
  public static final TypeVariable A = typeVariable("A");
  public static final TypeVariable B = typeVariable("B");

  public static final ArrayType ARRAY_ANY = a(ANY);
  public static final ArrayType ARRAY_BOOL = a(BOOL);
  public static final ArrayType ARRAY_STRING = a(STRING);
  public static final ArrayType ARRAY_BLOB = a(BLOB);
  public static final ArrayType ARRAY_NOTHING = a(NOTHING);
  public static final ArrayType ARRAY_DATA = a(DATA);
  public static final ArrayType ARRAY_FLAG = a(FLAG);
  public static final ArrayType ARRAY_PERSON = a(PERSON);
  public static final ArrayType ARRAY_A = a(A);
  public static final ArrayType ARRAY_B = a(B);

  public static final ArrayType ARRAY2_ANY = a(ARRAY_ANY);
  public static final ArrayType ARRAY2_BOOL = a(ARRAY_BOOL);
  public static final ArrayType ARRAY2_STRING = a(ARRAY_STRING);
  public static final ArrayType ARRAY2_BLOB = a(ARRAY_BLOB);
  public static final ArrayType ARRAY2_NOTHING = a(ARRAY_NOTHING);
  public static final ArrayType ARRAY2_PERSON = a(ARRAY_PERSON);
  public static final ArrayType ARRAY2_A = a(ARRAY_A);
  public static final ArrayType ARRAY2_B = a(ARRAY_B);

  public static final ImmutableList<Type> ELEMENTARY_TYPES = ImmutableList.<Type>builder()
      .addAll(BASE_TYPES)
      .add(PERSON)
      .add(A)
      .build();

  public static final ImmutableList<Type> ELEMENTARY_NON_STRUCT_TYPES =
      ImmutableList.<Type>builder()
          .addAll(BASE_TYPES)
          .add(A)
          .build();

  public static final ImmutableList<Type> ELEMENTARY_NON_POLYTYPE_TYPES =
      ImmutableList.<Type>builder()
          .addAll(BASE_TYPES)
          .add(PERSON)
          .build();

  public static ArrayType a(Type elemType) {
    return Types.array(elemType);
  }
}
