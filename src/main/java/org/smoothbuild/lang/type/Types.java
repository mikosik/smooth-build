package org.smoothbuild.lang.type;

import static com.google.common.collect.Lists.reverse;
import static org.smoothbuild.lang.type.ArrayType.arrayOf;

import java.util.List;

import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.Nothing;
import org.smoothbuild.lang.value.SString;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class Types {
  public static final Type STRING = new Type("String", SString.class);
  public static final Type BLOB = new Type("Blob", Blob.class);
  public static final StructType FILE = createFileType();
  public static final Type NOTHING = new Type("Nothing", Nothing.class);

  private static StructType createFileType() {
    ImmutableMap<String, Type> fields = ImmutableMap.of(
        "content", BLOB,
        "path", STRING);
    return new StructType("File", fields);
  }

  /*
   * Not each type can be used in every place. Each set below represent one place where smooth type
   * can be used and contains all smooth types that can be used there.
   */

  private static final ImmutableSet<Type> BASIC_TYPES = ImmutableSet.of(STRING, BLOB, FILE,
      NOTHING);

  public static final ImmutableSet<Type> ALL_TYPES = ImmutableSet.of(STRING, BLOB, FILE, NOTHING,
      arrayOf(STRING), arrayOf(BLOB), arrayOf(FILE), arrayOf(NOTHING));

  /**
   * All smooth types available in smooth language. Returned list is sorted using type - subtype
   * relationship. Each type comes before all of its subtypes.
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

  public static Type closestCommonConvertibleTo(Type type1, Type type2) {
    /*
     * Algorithm below works correctly for all smooth types currently existing in smooth but doesn't
     * work when it is possible to define user struct types. It will fail when conversion chain
     * (hierarchy) contains cycle (for example struct type is convertible to itself) or conversion
     * chain has infinite length (for example structure X is convertible to its array [X]). This
     * comment will become obsolete once we get rid of conversion chains (and direct-convertible-to)
     * and instead create normal object oriented type hierarchy with one root (Value type).
     */
    List<Type> hierarchy1 = type1.hierarchy();
    List<Type> hierarchy2 = type2.hierarchy();
    Type type = closesCommonSuperType(hierarchy1, hierarchy2);
    if (type == null) {
      Type last1 = hierarchy1.get(0);
      Type last2 = hierarchy2.get(0);
      boolean isNothing1 = last1.coreType().equals(NOTHING);
      boolean isNothing2 = last2.coreType().equals(NOTHING);
      if (isNothing1 && isNothing2) {
        type = last1.coreDepth() < last2.coreDepth() ? last2 : last1;
      } else if (isNothing1) {
        type = firstWithDepthNotLowerThan(hierarchy2, last1.coreDepth());
      } else if (isNothing2) {
        type = firstWithDepthNotLowerThan(hierarchy1, last2.coreDepth());
      }
    }
    return type;
  }

  private static Type closesCommonSuperType(List<Type> hierarchy1, List<Type> hierarchy2) {
    int index = 0;
    Type type = null;
    while (index < hierarchy1.size() && index < hierarchy2.size()
        && hierarchy1.get(index).equals(hierarchy2.get(index))) {
      type = hierarchy1.get(index);
      index++;
    }
    return type;
  }

  private static Type firstWithDepthNotLowerThan(List<Type> hierarchy, int depth) {
    return reverse(hierarchy)
        .stream()
        .filter(t -> depth <= t.coreDepth())
        .findFirst()
        .orElse(null);
  }
}
