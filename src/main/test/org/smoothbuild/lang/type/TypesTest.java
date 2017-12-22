package org.smoothbuild.lang.type;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.smoothbuild.lang.type.ArrayType.arrayOf;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.testing.EqualsTester;

public class TypesTest {
  private static final TypeSystem TYPE_SYSTEM = new TypeSystem();
  private static final Type STRING = TYPE_SYSTEM.string();
  private static final Type BLOB = TYPE_SYSTEM.blob();
  private static final Type FILE = TYPE_SYSTEM.file();
  private static final Type NOTHING = TYPE_SYSTEM.nothing();

  private ArrayType type;

  @Test
  public void core_type() throws Exception {
    assertEquals(STRING, STRING.coreType());
    assertEquals(personType(), personType().coreType());
    assertEquals(NOTHING, NOTHING.coreType());

    assertEquals(STRING, arrayOf(STRING).coreType());
    assertEquals(personType(), arrayOf(personType()).coreType());
    assertEquals(NOTHING, arrayOf(NOTHING).coreType());

    assertEquals(STRING, arrayOf(arrayOf(STRING)).coreType());
    assertEquals(personType(), arrayOf(arrayOf(personType())).coreType());
    assertEquals(NOTHING, arrayOf(arrayOf(NOTHING)).coreType());
  }

  @Test
  public void core_depth() throws Exception {
    assertEquals(0, STRING.coreDepth());
    assertEquals(0, personType().coreDepth());
    assertEquals(0, NOTHING.coreDepth());

    assertEquals(1, arrayOf(STRING).coreDepth());
    assertEquals(1, arrayOf(personType()).coreDepth());
    assertEquals(1, arrayOf(NOTHING).coreDepth());

    assertEquals(2, arrayOf(arrayOf(STRING)).coreDepth());
    assertEquals(2, arrayOf(arrayOf(personType())).coreDepth());
    assertEquals(2, arrayOf(arrayOf(NOTHING)).coreDepth());
  }

  @Test
  public void hierarchy() throws Exception {
    assertEquals(list(STRING), STRING.hierarchy());
    assertEquals(list(STRING, personType()), personType().hierarchy());
    assertEquals(list(NOTHING), NOTHING.hierarchy());

    assertEquals(list(arrayOf(STRING)), arrayOf(STRING).hierarchy());
    assertEquals(list(arrayOf(STRING), arrayOf(personType())), arrayOf(personType()).hierarchy());
    assertEquals(list(arrayOf(NOTHING)), arrayOf(NOTHING).hierarchy());

    assertEquals(list(arrayOf(arrayOf(STRING))), arrayOf(arrayOf(STRING)).hierarchy());
    assertEquals(list(arrayOf(arrayOf(STRING)), arrayOf(arrayOf(personType()))),
        arrayOf(arrayOf(personType())).hierarchy());
    assertEquals(list(arrayOf(arrayOf(NOTHING))), arrayOf(arrayOf(NOTHING)).hierarchy());
  }

  @Test
  public void is_nothing() throws Exception {
    assertTrue(NOTHING.isNothing());
    assertFalse(STRING.isNothing());
    assertFalse(BLOB.isNothing());
    assertFalse(FILE.isNothing());
    assertFalse(personType().isNothing());
  }

  @Test
  public void is_assignable_from() throws Exception {
    List<Type> types = asList(
        STRING, arrayOf(STRING), arrayOf(arrayOf(STRING)),
        BLOB, arrayOf(BLOB), arrayOf(arrayOf(BLOB)),
        FILE, arrayOf(FILE), arrayOf(arrayOf(FILE)),
        personType(), arrayOf(personType()), arrayOf(arrayOf(personType())),
        NOTHING, arrayOf(NOTHING), arrayOf(arrayOf(NOTHING)));
    Set<Conversion> conversions = ImmutableSet.of(
        new Conversion(STRING, STRING),
        new Conversion(STRING, personType()),
        new Conversion(STRING, NOTHING),
        new Conversion(BLOB, BLOB),
        new Conversion(BLOB, NOTHING),
        new Conversion(BLOB, FILE),
        new Conversion(FILE, FILE),
        new Conversion(FILE, NOTHING),
        new Conversion(personType(), personType()),
        new Conversion(personType(), NOTHING),
        new Conversion(NOTHING, NOTHING),

        new Conversion(arrayOf(STRING), arrayOf(STRING)),
        new Conversion(arrayOf(STRING), arrayOf(personType())),
        new Conversion(arrayOf(STRING), arrayOf(NOTHING)),
        new Conversion(arrayOf(STRING), NOTHING),
        new Conversion(arrayOf(BLOB), arrayOf(BLOB)),
        new Conversion(arrayOf(BLOB), arrayOf(NOTHING)),
        new Conversion(arrayOf(BLOB), NOTHING),
        new Conversion(arrayOf(BLOB), arrayOf(FILE)),
        new Conversion(arrayOf(FILE), arrayOf(FILE)),
        new Conversion(arrayOf(FILE), arrayOf(NOTHING)),
        new Conversion(arrayOf(FILE), NOTHING),
        new Conversion(arrayOf(personType()), arrayOf(personType())),
        new Conversion(arrayOf(personType()), arrayOf(NOTHING)),
        new Conversion(arrayOf(personType()), NOTHING),
        new Conversion(arrayOf(NOTHING), arrayOf(NOTHING)),
        new Conversion(arrayOf(NOTHING), NOTHING),

        new Conversion(arrayOf(arrayOf(STRING)), arrayOf(arrayOf(STRING))),
        new Conversion(arrayOf(arrayOf(STRING)), arrayOf(arrayOf(personType()))),
        new Conversion(arrayOf(arrayOf(STRING)), arrayOf(arrayOf(NOTHING))),
        new Conversion(arrayOf(arrayOf(STRING)), arrayOf(NOTHING)),
        new Conversion(arrayOf(arrayOf(STRING)), NOTHING),
        new Conversion(arrayOf(arrayOf(BLOB)), arrayOf(arrayOf(BLOB))),
        new Conversion(arrayOf(arrayOf(BLOB)), arrayOf(arrayOf(NOTHING))),
        new Conversion(arrayOf(arrayOf(BLOB)), arrayOf(arrayOf(FILE))),
        new Conversion(arrayOf(arrayOf(BLOB)), arrayOf(NOTHING)),
        new Conversion(arrayOf(arrayOf(BLOB)), NOTHING),
        new Conversion(arrayOf(arrayOf(FILE)), arrayOf(arrayOf(FILE))),
        new Conversion(arrayOf(arrayOf(FILE)), arrayOf(arrayOf(NOTHING))),
        new Conversion(arrayOf(arrayOf(FILE)), arrayOf(NOTHING)),
        new Conversion(arrayOf(arrayOf(FILE)), NOTHING),
        new Conversion(arrayOf(arrayOf(personType())), arrayOf(arrayOf(personType()))),
        new Conversion(arrayOf(arrayOf(personType())), arrayOf(arrayOf(NOTHING))),
        new Conversion(arrayOf(arrayOf(personType())), arrayOf(NOTHING)),
        new Conversion(arrayOf(arrayOf(personType())), NOTHING),
        new Conversion(arrayOf(arrayOf(NOTHING)), arrayOf(arrayOf(NOTHING))),
        new Conversion(arrayOf(arrayOf(NOTHING)), arrayOf(NOTHING)),
        new Conversion(arrayOf(arrayOf(NOTHING)), NOTHING));
    for (Type destination : types) {
      for (Type source : types) {
        boolean expected = conversions.contains(new Conversion(destination, source));
        assertEquals(destination.toString() + ".isAssignableFrom(" + source + ")",
            expected,
            destination.isAssignableFrom(source));
      }
    }
  }

  private static class Conversion {
    private final Type destination;
    private final Type source;

    public Conversion(Type destination, Type source) {
      this.destination = destination;
      this.source = source;
    }

    @Override
    public boolean equals(Object object) {
      if (!(object instanceof Conversion)) {
        return false;
      }
      Conversion that = (Conversion) object;
      return this.destination.equals(that.destination)
          && this.source.equals(that.source);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(destination, source);
    }
  }

  @Test
  public void equals_and_hashcode() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(STRING);
    tester.addEqualityGroup(BLOB);
    tester.addEqualityGroup(FILE);
    tester.addEqualityGroup(personType());
    tester.addEqualityGroup(NOTHING);

    tester.addEqualityGroup(arrayOf(STRING), arrayOf(STRING));
    tester.addEqualityGroup(arrayOf(BLOB), arrayOf(BLOB));
    tester.addEqualityGroup(arrayOf(FILE), arrayOf(FILE));
    tester.addEqualityGroup(arrayOf(personType()), arrayOf(personType()));
    tester.addEqualityGroup(arrayOf(NOTHING), arrayOf(NOTHING));

    tester.addEqualityGroup(arrayOf(arrayOf(STRING)), arrayOf(arrayOf(STRING)));
    tester.addEqualityGroup(arrayOf(arrayOf(BLOB)), arrayOf(arrayOf(BLOB)));
    tester.addEqualityGroup(arrayOf(arrayOf(FILE)), arrayOf(arrayOf(FILE)));
    tester.addEqualityGroup(arrayOf(arrayOf(personType())), arrayOf(arrayOf(personType())));
    tester.addEqualityGroup(arrayOf(arrayOf(NOTHING)), arrayOf(arrayOf(NOTHING)));
    tester.testEquals();
  }

  @Test
  public void to_string() {
    assertEquals("String", STRING.toString());
  }

  @Test
  public void string_array_name() throws Exception {
    given(type = arrayOf(STRING));
    when(() -> type.name());
    thenReturned("[String]");
  }

  @Test
  public void common_super_type() throws Exception {
    StringBuilder builder = new StringBuilder();

    assertCommon(STRING, STRING, STRING, builder);
    assertCommon(STRING, BLOB, null, builder);
    assertCommon(STRING, FILE, null, builder);
    assertCommon(STRING, NOTHING, STRING, builder);
    assertCommon(STRING, arrayOf(STRING), null, builder);
    assertCommon(STRING, arrayOf(BLOB), null, builder);
    assertCommon(STRING, arrayOf(FILE), null, builder);
    assertCommon(STRING, arrayOf(NOTHING), null, builder);

    assertCommon(BLOB, BLOB, BLOB, builder);
    assertCommon(BLOB, FILE, BLOB, builder);
    assertCommon(BLOB, NOTHING, BLOB, builder);
    assertCommon(BLOB, arrayOf(STRING), null, builder);
    assertCommon(BLOB, arrayOf(BLOB), null, builder);
    assertCommon(BLOB, arrayOf(FILE), null, builder);
    assertCommon(BLOB, arrayOf(NOTHING), null, builder);

    assertCommon(FILE, FILE, FILE, builder);
    assertCommon(FILE, NOTHING, FILE, builder);
    assertCommon(FILE, arrayOf(STRING), null, builder);
    assertCommon(FILE, arrayOf(BLOB), null, builder);
    assertCommon(FILE, arrayOf(FILE), null, builder);
    assertCommon(FILE, arrayOf(NOTHING), null, builder);

    assertCommon(NOTHING, NOTHING, NOTHING, builder);
    assertCommon(NOTHING, arrayOf(STRING), arrayOf(STRING), builder);
    assertCommon(NOTHING, arrayOf(BLOB), arrayOf(BLOB), builder);
    assertCommon(NOTHING, arrayOf(FILE), arrayOf(FILE), builder);
    assertCommon(NOTHING, arrayOf(NOTHING), arrayOf(NOTHING), builder);

    assertCommon(arrayOf(STRING), arrayOf(STRING), arrayOf(STRING), builder);
    assertCommon(arrayOf(STRING), arrayOf(BLOB), null, builder);
    assertCommon(arrayOf(STRING), arrayOf(FILE), null, builder);
    assertCommon(arrayOf(STRING), arrayOf(NOTHING), arrayOf(STRING), builder);
    assertCommon(arrayOf(STRING), NOTHING, arrayOf(STRING), builder);

    assertCommon(arrayOf(BLOB), arrayOf(BLOB), arrayOf(BLOB), builder);
    assertCommon(arrayOf(BLOB), arrayOf(FILE), arrayOf(BLOB), builder);
    assertCommon(arrayOf(BLOB), arrayOf(NOTHING), arrayOf(BLOB), builder);
    assertCommon(arrayOf(BLOB), NOTHING, arrayOf(BLOB), builder);

    assertCommon(arrayOf(FILE), arrayOf(FILE), arrayOf(FILE), builder);
    assertCommon(arrayOf(FILE), arrayOf(NOTHING), arrayOf(FILE), builder);
    assertCommon(arrayOf(FILE), NOTHING, arrayOf(FILE), builder);

    assertCommon(arrayOf(NOTHING), arrayOf(NOTHING), arrayOf(NOTHING), builder);
    assertCommon(arrayOf(NOTHING), NOTHING, arrayOf(NOTHING), builder);

    assertCommon(arrayOf(FILE), new StructType("Struct", ImmutableMap.of("field", arrayOf(BLOB))),
        arrayOf(BLOB), builder);

    String errors = builder.toString();
    if (0 < errors.length()) {
      fail(errors);
    }
  }

  private static void assertCommon(Type type1, Type type2, Type expected, StringBuilder builder) {
    assertCommonSuperTypeImpl(type1, type2, expected, builder);
    assertCommonSuperTypeImpl(type2, type1, expected, builder);
  }

  private static void assertCommonSuperTypeImpl(Type type1, Type type2, Type expected,
      StringBuilder builder) {
    Type actual = type1.commonSuperType(type2);
    if (!Objects.equal(expected, actual)) {
      builder.append(type1 + ".commonSuperType(" + type2 + ") = " + actual
          + " but should = " + expected + "\n");
    }
  }

  private static StructType personType() {
    return new StructType("Person", ImmutableMap.of("firstName", STRING, "lastName", STRING));
  }
}
