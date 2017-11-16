package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.type.ArrayType.arrayOf;

import java.util.ArrayDeque;
import java.util.Deque;

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

  public static Type closestCommonConvertibleTo(Type type1, Type type2) {
    /*
     * Algorithm below works correctly for all smooth types currently existing in
     * smooth but doesn't work when it is possible to define user struct types. It
     * will fail when conversion chain contains cycle (for example struct type is
     * convertible to itself) or conversion chain has infinite length (for example
     * structure X is convertible to its array [X])
     */
    Deque<Type> chain1 = conversionChain(type1);
    Deque<Type> chain2 = conversionChain(type2);

    Type type = null;
    while (!chain1.isEmpty() && !chain2.isEmpty() && chain1.peekLast().equals(chain2.peekLast())) {
      type = chain1.peekLast();
      chain1.removeLast();
      chain2.removeLast();
    }

    if (type == null) {
      Type last1 = chain1.peekLast();
      Type last2 = chain2.peekLast();
      boolean isNothing1 = last1.coreType().equals(NOTHING);
      boolean isNothing2 = last2.coreType().equals(NOTHING);
      if (isNothing1 && isNothing2) {
        type = last1.coreDepth() < last2.coreDepth() ? last2 : last1;
      } else if (isNothing1) {
        type = firstWithDepthNotLowerThan(chain2, last1.coreDepth());
      } else if (isNothing2) {
        type = firstWithDepthNotLowerThan(chain1, last2.coreDepth());
      }
    }
    return type;
  }

  private static Deque<Type> conversionChain(Type type) {
    Deque<Type> chain = new ArrayDeque<>();
    while (type != null) {
      chain.add(type);
      type = type.directConvertibleTo();
    }
    return chain;
  }

  private static Type firstWithDepthNotLowerThan(Deque<Type> chain, int depth) {
    return chain
        .stream()
        .filter(t -> depth <= t.coreDepth())
        .findFirst()
        .orElse(null);
  }
}
