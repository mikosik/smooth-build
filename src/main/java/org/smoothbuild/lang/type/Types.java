package org.smoothbuild.lang.type;

import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.Nothing;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Value;

import com.google.common.collect.ImmutableSet;

public class Types {
  public static final Type STRING = new Type("String", SString.class);
  public static final Type BLOB = new Type("Blob", Blob.class);
  public static final Type FILE = new Type("File", SFile.class);
  public static final Type NOTHING = new Type("Nothing", Nothing.class);
  public static final ArrayType STRING_ARRAY = new ArrayType(STRING);
  public static final ArrayType BLOB_ARRAY = new ArrayType(BLOB);
  public static final ArrayType FILE_ARRAY = new ArrayType(FILE);
  public static final ArrayType NIL = new ArrayType(NOTHING);

  /*
   * Not each type can be used in every place. Each set below represent one place
   * where smooth type can be used and contains all smooth types that can be used
   * there.
   */

  /**
   * NOTHING is not a basic type as it is not possible to create instance of that
   * type.
   */
  private static final ImmutableSet<Type> BASIC_TYPES = ImmutableSet.of(STRING, BLOB, FILE,
      NOTHING);
  public static final ImmutableSet<ArrayType> ARRAY_TYPES = ImmutableSet.of(STRING_ARRAY,
      BLOB_ARRAY, FILE_ARRAY, NIL);

  public static final ImmutableSet<Type> ALL_TYPES = ImmutableSet.of(STRING, BLOB, FILE, NOTHING,
      STRING_ARRAY, BLOB_ARRAY, FILE_ARRAY, NIL);

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

  public static <T extends Value> ArrayType arrayOf(Type elemType) {
    return new ArrayType(elemType);
  }

  public static Type basicTypeFromString(String string) {
    for (Type type : BASIC_TYPES) {
      if (type.name().equals(string)) {
        return type;
      }
    }
    return null;
  }

  public static boolean isConvertible(Type from, Type to) {
    return to.equals(commonSuperType(to, from));
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
      Type elemType1 = ((ArrayType) type1).elemType();
      Type elemType2 = ((ArrayType) type2).elemType();
      Type commonSuperType = commonSuperType(elemType1, elemType2);
      return commonSuperType == null ? null : arrayOf(commonSuperType);
    }
    if (type1.equals(BLOB)) {
      if (type2.equals(BLOB) || type2.equals(FILE)) {
        return BLOB;
      } else {
        return null;
      }
    } else if (type1.equals(FILE)) {
      if (type2.equals(FILE)) {
        return FILE;
      } else if (type2.equals(BLOB)) {
        return BLOB;
      } else {
        return null;
      }
    }
    return null;
  }
}
