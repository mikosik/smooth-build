package org.smoothbuild.lang.type;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.smoothbuild.lang.type.ArrayType.arrayOf;
import static org.smoothbuild.lang.type.Types.BLOB;
import static org.smoothbuild.lang.type.Types.FILE;
import static org.smoothbuild.lang.type.Types.NOTHING;
import static org.smoothbuild.lang.type.Types.STRING;
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
  public void string_basic_type_from_string() throws Exception {
    when(Types.basicTypeFromString("String"));
    thenReturned(STRING);
  }

  @Test
  public void blob_basic_type_from_string() throws Exception {
    when(Types.basicTypeFromString("Blob"));
    thenReturned(BLOB);
  }

  @Test
  public void file_basic_type_from_string() throws Exception {
    when(Types.basicTypeFromString("File"));
    thenReturned(FILE);
  }

  @Test
  public void nothing_basic_type_from_string() throws Exception {
    when(Types.basicTypeFromString("Nothing"));
    thenReturned(NOTHING);
  }

  @Test
  public void string_array_name() throws Exception {
    given(type = arrayOf(STRING));
    when(() -> type.name());
    thenReturned("[String]");
  }

  @Test
  public void string_array_basic_type_from_string() throws Exception {
    when(Types.basicTypeFromString("[String]"));
    thenReturned(null);
  }

  @Test
  public void blob_array_basic_type_from_string() throws Exception {
    when(Types.basicTypeFromString("[Blob]"));
    thenReturned(null);
  }

  @Test
  public void file_array_basic_type_from_string() throws Exception {
    when(Types.basicTypeFromString("[File]"));
    thenReturned(null);
  }

  @Test
  public void nil_basic_type_from_string() throws Exception {
    when(Types.basicTypeFromString("[Nothing]"));
    thenReturned(null);
  }

  @Test
  public void unknown_type_basic_type_from_string() throws Exception {
    when(Types.basicTypeFromString("notAType"));
    thenReturned(null);
  }

  @Test
  public void closest_common_convertible_to() throws Exception {
    StringBuilder builder = new StringBuilder();

    assertClosest(STRING, STRING, STRING, builder);
    assertClosest(STRING, BLOB, null, builder);
    assertClosest(STRING, FILE, null, builder);
    assertClosest(STRING, NOTHING, STRING, builder);
    assertClosest(STRING, arrayOf(STRING), null, builder);
    assertClosest(STRING, arrayOf(BLOB), null, builder);
    assertClosest(STRING, arrayOf(FILE), null, builder);
    assertClosest(STRING, arrayOf(NOTHING), null, builder);

    assertClosest(BLOB, BLOB, BLOB, builder);
    assertClosest(BLOB, FILE, BLOB, builder);
    assertClosest(BLOB, NOTHING, BLOB, builder);
    assertClosest(BLOB, arrayOf(STRING), null, builder);
    assertClosest(BLOB, arrayOf(BLOB), null, builder);
    assertClosest(BLOB, arrayOf(FILE), null, builder);
    assertClosest(BLOB, arrayOf(NOTHING), null, builder);

    assertClosest(FILE, FILE, FILE, builder);
    assertClosest(FILE, NOTHING, FILE, builder);
    assertClosest(FILE, arrayOf(STRING), null, builder);
    assertClosest(FILE, arrayOf(BLOB), null, builder);
    assertClosest(FILE, arrayOf(FILE), null, builder);
    assertClosest(FILE, arrayOf(NOTHING), null, builder);

    assertClosest(NOTHING, NOTHING, NOTHING, builder);
    assertClosest(NOTHING, arrayOf(STRING), arrayOf(STRING), builder);
    assertClosest(NOTHING, arrayOf(BLOB), arrayOf(BLOB), builder);
    assertClosest(NOTHING, arrayOf(FILE), arrayOf(FILE), builder);
    assertClosest(NOTHING, arrayOf(NOTHING), arrayOf(NOTHING), builder);

    assertClosest(arrayOf(STRING), arrayOf(STRING), arrayOf(STRING), builder);
    assertClosest(arrayOf(STRING), arrayOf(BLOB), null, builder);
    assertClosest(arrayOf(STRING), arrayOf(FILE), null, builder);
    assertClosest(arrayOf(STRING), arrayOf(NOTHING), arrayOf(STRING), builder);
    assertClosest(arrayOf(STRING), NOTHING, arrayOf(STRING), builder);

    assertClosest(arrayOf(BLOB), arrayOf(BLOB), arrayOf(BLOB), builder);
    assertClosest(arrayOf(BLOB), arrayOf(FILE), arrayOf(BLOB), builder);
    assertClosest(arrayOf(BLOB), arrayOf(NOTHING), arrayOf(BLOB), builder);
    assertClosest(arrayOf(BLOB), NOTHING, arrayOf(BLOB), builder);

    assertClosest(arrayOf(FILE), arrayOf(FILE), arrayOf(FILE), builder);
    assertClosest(arrayOf(FILE), arrayOf(NOTHING), arrayOf(FILE), builder);
    assertClosest(arrayOf(FILE), NOTHING, arrayOf(FILE), builder);

    assertClosest(arrayOf(NOTHING), arrayOf(NOTHING), arrayOf(NOTHING), builder);
    assertClosest(arrayOf(NOTHING), NOTHING, arrayOf(NOTHING), builder);

    assertClosest(arrayOf(FILE), new StructType("Struct", ImmutableMap.of("field", arrayOf(BLOB))),
        arrayOf(BLOB), builder);

    String errors = builder.toString();
    if (0 < errors.length()) {
      fail(errors);
    }
  }

  private static void assertClosest(Type type1, Type type2, Type expected, StringBuilder builder) {
    assertClosestImpl(type1, type2, expected, builder);
    assertClosestImpl(type2, type1, expected, builder);
  }

  private static void assertClosestImpl(Type type1, Type type2, Type expected,
      StringBuilder builder) {
    Type actual = Types.closestCommonConvertibleTo(type1, type2);
    if (!Objects.equal(expected, actual)) {
      builder.append("closestCommonConvertibleTo(" + type1 + "," + type2 + ") = " + actual
          + " but should = " + expected + "\n");
    }
  }

  private static StructType personType() {
    return new StructType("Person", ImmutableMap.of("firstName", STRING, "lastName", STRING));
  }
}
