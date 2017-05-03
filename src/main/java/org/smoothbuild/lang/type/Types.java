package org.smoothbuild.lang.type;

import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.Nothing;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Value;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.TypeLiteral;

public class Types {
  public static final Type STRING = new StringType();
  public static final Type BLOB = new BlobType();
  public static final Type FILE = new FileType();
  public static final Type NOTHING = new NothingType();
  public static final ArrayType STRING_ARRAY = new ArrayType(STRING,
      new TypeLiteral<Array<SString>>() {});
  public static final ArrayType BLOB_ARRAY = new ArrayType(BLOB, new TypeLiteral<Array<Blob>>() {});
  public static final ArrayType FILE_ARRAY =
      new ArrayType(FILE, new TypeLiteral<Array<SFile>>() {});
  public static final ArrayType NIL = new ArrayType(NOTHING, new TypeLiteral<Array<Nothing>>() {});

  /*
   * Not each type can be used in every place. Each set below represent one
   * place where smooth type can be used and contains all smooth types that can
   * be used there.
   */

  /**
   * NOTHING is not a basic type as it is not possible to create instance of
   * that type.
   */
  private static final ImmutableSet<Type> BASIC_TYPES = ImmutableSet.of(STRING, BLOB, FILE,
      NOTHING);
  public static final ImmutableSet<ArrayType> ARRAY_TYPES = ImmutableSet.of(STRING_ARRAY,
      BLOB_ARRAY, FILE_ARRAY, NIL);

  public static final ImmutableSet<Type> ALL_TYPES = ImmutableSet.of(STRING, BLOB, FILE, NOTHING,
      STRING_ARRAY, BLOB_ARRAY, FILE_ARRAY, NIL);

  /*
   * Some of the set above converted to java types.
   */

  private static final ImmutableSet<TypeLiteral<?>> BASIC_JTYPES = toJTypes(BASIC_TYPES);

  /*
   * A few handy mappings.
   */

  private static final ImmutableMap<TypeLiteral<?>, Type> JTYPE_TO_TYPE =
      createToTypeMap(ALL_TYPES);
  private static final ImmutableMap<Type, ArrayType> ELEM_TYPE_TO_ARRAY_TYPE =
      createElemTypeToArrayTypeMap(ARRAY_TYPES);

  public static ImmutableSet<Type> basicTypes() {
    return BASIC_TYPES;
  }

  /**
   * All smooth types available in smooth language. Returned list is sorted
   * using type - subtype relationship. Each type comes before all of its
   * subtypes.
   */
  public static ImmutableSet<Type> allTypes() {
    return ALL_TYPES;
  }

  public static ImmutableSet<TypeLiteral<?>> arrayElementJTypes() {
    return BASIC_JTYPES;
  }

  public static Type jTypeToType(TypeLiteral<?> jType) {
    return JTYPE_TO_TYPE.get(jType);
  }

  public static <T extends Value> ArrayType arrayOf(Type elemType) {
    return ELEM_TYPE_TO_ARRAY_TYPE.get(elemType);
  }

  private static ImmutableSet<TypeLiteral<?>> toJTypes(Iterable<Type> types) {
    ImmutableSet.Builder<TypeLiteral<?>> builder = ImmutableSet.builder();
    for (Type type : types) {
      builder.add(type.jType());
    }
    return builder.build();
  }

  private static ImmutableMap<TypeLiteral<?>, Type> createToTypeMap(Iterable<Type> types) {
    ImmutableMap.Builder<TypeLiteral<?>, Type> builder = ImmutableMap.builder();
    for (Type type : types) {
      builder.put(type.jType(), type);
    }
    return builder.build();
  }

  private static ImmutableMap<Type, ArrayType> createElemTypeToArrayTypeMap(
      ImmutableSet<ArrayType> arrayTypes) {
    ImmutableMap.Builder<Type, ArrayType> builder = ImmutableMap.builder();
    for (ArrayType type : arrayTypes) {
      builder.put(type.elemType(), type);
    }
    return builder.build();
  }

  public static Type fromString(String string) {
    Type basicType = basicTypeFromString(string);
    if (basicType != null) {
      return basicType;
    }
    if (string.startsWith("[") && string.endsWith("]")) {
      String elementTypeString = string.substring(1, string.length() - 1);
      for (Type type : BASIC_TYPES) {
        if (type.name().equals(elementTypeString)) {
          return arrayOf(type);
        }
      }
    }
    return null;
  }

  public static Type basicTypeFromString(String string) {
    for (Type type : BASIC_TYPES) {
      if (type.name().equals(string)) {
        return type;
      }
    }
    return null;
  }
}
