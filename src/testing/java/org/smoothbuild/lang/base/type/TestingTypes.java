package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.Location.internal;
import static org.smoothbuild.lang.base.type.Types.BASIC_TYPES;
import static org.smoothbuild.lang.base.type.Types.array;
import static org.smoothbuild.lang.base.type.Types.blob;
import static org.smoothbuild.lang.base.type.Types.bool;
import static org.smoothbuild.lang.base.type.Types.generic;
import static org.smoothbuild.lang.base.type.Types.nothing;
import static org.smoothbuild.lang.base.type.Types.string;
import static org.smoothbuild.lang.base.type.Types.struct;
import static org.smoothbuild.testing.common.TestingLocation.loc;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.Item;

import com.google.common.collect.ImmutableList;

public class TestingTypes {
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
  public static final Type A = generic("A");
  public static final Type B = generic("B");

  public static final ArrayType ARRAY_BOOL = array(BOOL);
  public static final ArrayType ARRAY_STRING = array(STRING);
  public static final ArrayType ARRAY_BLOB = array(BLOB);
  public static final ArrayType ARRAY_NOTHING = array(NOTHING);
  public static final ArrayType ARRAY_DATA = array(DATA);
  public static final ArrayType ARRAY_FLAG = array(FLAG);
  public static final ArrayType ARRAY_PERSON = array(PERSON);
  public static final ArrayType ARRAY_A = array(A);
  public static final ArrayType ARRAY_B = array(B);

  public static final ArrayType ARRAY2_BOOL = array(ARRAY_BOOL);
  public static final ArrayType ARRAY2_STRING = array(ARRAY_STRING);
  public static final ArrayType ARRAY2_BLOB = array(ARRAY_BLOB);
  public static final ArrayType ARRAY2_NOTHING = array(ARRAY_NOTHING);
  public static final ArrayType ARRAY2_PERSON = array(ARRAY_PERSON);
  public static final ArrayType ARRAY2_A = array(ARRAY_A);
  public static final ArrayType ARRAY2_B = array(ARRAY_B);

  public static final ImmutableList<Type> ELEMENTARY_TYPES = ImmutableList.<Type>builder()
      .addAll(BASIC_TYPES)
      .add(PERSON)
      .add(A)
      .build();

  public static final ImmutableList<Type> ELEMENTARY_NON_STRUCT_TYPES =
      ImmutableList.<Type>builder()
          .addAll(BASIC_TYPES)
          .add(A)
          .build();

  public static final ImmutableList<Type> ELEMENTARY_NON_GENERIC_TYPES =
      ImmutableList.<Type>builder()
          .addAll(BASIC_TYPES)
          .add(PERSON)
          .build();
}
