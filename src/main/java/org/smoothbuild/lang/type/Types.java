package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.type.ArrayType.arrayOf;

import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.Nothing;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;

import com.google.common.collect.ImmutableSet;

public class Types {
  public static final Type STRING = new Type("String", SString.class);
  public static final Type BLOB = new Type("Blob", Blob.class);
  public static final Type FILE = new StructType("File", SFile.class, BLOB);
  public static final Type NOTHING = new Type("Nothing", Nothing.class);

  /*
   * Not each type can be used in every place. Each set below represent one place
   * where smooth type can be used and contains all smooth types that can be used
   * there.
   */

  private static final ImmutableSet<Type> BASIC_TYPES = ImmutableSet.of(STRING, BLOB, FILE,
      NOTHING);
  public static final ImmutableSet<ArrayType> ARRAY_TYPES = ImmutableSet.of(arrayOf(STRING),
      arrayOf(BLOB), arrayOf(FILE), arrayOf(NOTHING));

  public static final ImmutableSet<Type> ALL_TYPES = ImmutableSet.of(STRING, BLOB, FILE, NOTHING,
      arrayOf(STRING), arrayOf(BLOB), arrayOf(FILE), arrayOf(NOTHING));

  /*
   * A few handy mappings.
   */

  public static ImmutableSet<Type> basicTypes() {
    return BASIC_TYPES;
  }

  /**
   * All smooth types available in smooth language. Returned list is sorted using
   * type - subtype relationship. Each type comes before all of its subtypes.
   */
  public static ImmutableSet<Type> allTypes() {
    return ALL_TYPES;
  }

  public static Type basicTypeFromString(String string) {
    for (Type type : BASIC_TYPES) {
      if (type.name().equals(string)) {
        return type;
      }
    }
    return null;
  }

  public static Type commonSuperType(Type type1, Type type2) {
    if (type1.equals(type2)) {
      return type1;
    }
    if (type1.equals(NOTHING)) {
      return type2;
    }
    if (type2.equals(NOTHING)) {
      return type1;
    }
    if (type1 instanceof ArrayType && type2 instanceof ArrayType) {
      return commonSuperType((ArrayType) type1, (ArrayType) type2);
    }
    if (type1.equals(BLOB) && type2.equals(FILE)) {
      return BLOB;
    }
    if (type1.equals(FILE) && type2.equals(BLOB)) {
      return BLOB;
    }
    return null;
  }

  private static Type commonSuperType(ArrayType type1, ArrayType type2) {
    Type commonSuperType = commonSuperType(type1.elemType(), type2.elemType());
    return commonSuperType == null ? null : arrayOf(commonSuperType);
  }
}
