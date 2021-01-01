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
  public static final StructType PERSON = struct("Person", List.of(
          new Item(STRING, "firstName", Optional.empty(), internal()),
          new Item(STRING, "lastName", Optional.empty(),internal())));
  public static final StructType FLAG = struct("Flag", List.of(
          new Item(BOOL, "flag", Optional.empty(),internal())));
  public static final StructType DATA = struct("Data", List.of(
          new Item(BLOB, "data", Optional.empty(),internal())));
  public static final TypeVariable A = typeVariable("A");
  public static final TypeVariable B = typeVariable("B");

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
