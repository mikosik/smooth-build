package org.smoothbuild.lang.type;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.smoothbuild.lang.type.ArrayType.arrayOf;
import static org.smoothbuild.lang.type.Conversions.canConvert;
import static org.smoothbuild.lang.type.Types.BLOB;
import static org.smoothbuild.lang.type.Types.FILE;
import static org.smoothbuild.lang.type.Types.NOTHING;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.smoothbuild.lang.type.Types.allTypes;
import static org.smoothbuild.lang.type.Types.basicTypes;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.google.common.base.Objects;
import com.google.common.testing.EqualsTester;

public class TypesTest {
  private ArrayType type;

  @Test
  public void basic_types() {
    when(basicTypes());
    thenReturned(containsInAnyOrder(STRING, BLOB, FILE, NOTHING));
  }

  @Test
  public void all_types() {
    when(allTypes());
    thenReturned(containsInAnyOrder(STRING, BLOB, FILE, NOTHING, arrayOf(STRING), arrayOf(BLOB),
        arrayOf(FILE), arrayOf(NOTHING)));
  }

  @Test
  public void array_elem_types() {
    assertEquals(arrayOf(STRING).elemType(), STRING);
    assertEquals(arrayOf(BLOB).elemType(), BLOB);
    assertEquals(arrayOf(FILE).elemType(), FILE);
    assertEquals(arrayOf(NOTHING).elemType(), NOTHING);
  }

  @Test
  public void equals_and_hashcode() {
    EqualsTester tester = new EqualsTester();

    tester.addEqualityGroup(NOTHING);
    tester.addEqualityGroup(STRING);
    tester.addEqualityGroup(BLOB);
    tester.addEqualityGroup(FILE);
    tester.addEqualityGroup(arrayOf(STRING), arrayOf(STRING));
    tester.addEqualityGroup(arrayOf(BLOB), arrayOf(BLOB));
    tester.addEqualityGroup(arrayOf(FILE), arrayOf(FILE));
    tester.addEqualityGroup(arrayOf(NOTHING), arrayOf(NOTHING));

    tester.testEquals();
  }

  @Test
  public void to_string() {
    assertEquals("String", STRING.toString());
  }

  @Test
  public void all_types_returns_list_sorted_by_super_type_dependency() {
    Set<Type> visited = new HashSet<>();
    for (Type type : allTypes()) {
      for (Type visitedType : visited) {
        assertFalse(canConvert(visitedType, type));
      }
      visited.add(type);
    }
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
  public void common_super_type() throws Exception {
    StringBuilder builder = new StringBuilder();

    assertCommonSuperType(STRING, STRING, STRING, builder);
    assertCommonSuperType(STRING, BLOB, null, builder);
    assertCommonSuperType(STRING, FILE, null, builder);
    assertCommonSuperType(STRING, NOTHING, STRING, builder);
    assertCommonSuperType(STRING, arrayOf(STRING), null, builder);
    assertCommonSuperType(STRING, arrayOf(BLOB), null, builder);
    assertCommonSuperType(STRING, arrayOf(FILE), null, builder);
    assertCommonSuperType(STRING, arrayOf(NOTHING), null, builder);

    assertCommonSuperType(BLOB, BLOB, BLOB, builder);
    assertCommonSuperType(BLOB, FILE, BLOB, builder);
    assertCommonSuperType(BLOB, NOTHING, BLOB, builder);
    assertCommonSuperType(BLOB, arrayOf(STRING), null, builder);
    assertCommonSuperType(BLOB, arrayOf(BLOB), null, builder);
    assertCommonSuperType(BLOB, arrayOf(FILE), null, builder);
    assertCommonSuperType(BLOB, arrayOf(NOTHING), null, builder);

    assertCommonSuperType(FILE, FILE, FILE, builder);
    assertCommonSuperType(FILE, NOTHING, FILE, builder);
    assertCommonSuperType(FILE, arrayOf(STRING), null, builder);
    assertCommonSuperType(FILE, arrayOf(BLOB), null, builder);
    assertCommonSuperType(FILE, arrayOf(FILE), null, builder);
    assertCommonSuperType(FILE, arrayOf(NOTHING), null, builder);

    assertCommonSuperType(NOTHING, NOTHING, NOTHING, builder);
    assertCommonSuperType(NOTHING, arrayOf(STRING), arrayOf(STRING), builder);
    assertCommonSuperType(NOTHING, arrayOf(BLOB), arrayOf(BLOB), builder);
    assertCommonSuperType(NOTHING, arrayOf(FILE), arrayOf(FILE), builder);
    assertCommonSuperType(NOTHING, arrayOf(NOTHING), arrayOf(NOTHING), builder);

    assertCommonSuperType(arrayOf(STRING), arrayOf(STRING), arrayOf(STRING), builder);
    assertCommonSuperType(arrayOf(STRING), arrayOf(BLOB), null, builder);
    assertCommonSuperType(arrayOf(STRING), arrayOf(FILE), null, builder);
    assertCommonSuperType(arrayOf(STRING), arrayOf(NOTHING), arrayOf(STRING), builder);

    assertCommonSuperType(arrayOf(BLOB), arrayOf(BLOB), arrayOf(BLOB), builder);
    assertCommonSuperType(arrayOf(BLOB), arrayOf(FILE), arrayOf(BLOB), builder);
    assertCommonSuperType(arrayOf(BLOB), arrayOf(NOTHING), arrayOf(BLOB), builder);

    assertCommonSuperType(arrayOf(FILE), arrayOf(FILE), arrayOf(FILE), builder);
    assertCommonSuperType(arrayOf(FILE), arrayOf(NOTHING), arrayOf(FILE), builder);

    assertCommonSuperType(arrayOf(NOTHING), arrayOf(NOTHING), arrayOf(NOTHING), builder);

    String errors = builder.toString();
    if (0 < errors.length()) {
      fail(errors);
    }
  }

  private static void assertCommonSuperType(Type type1, Type type2, Type expected,
      StringBuilder builder) {
    assertCommonSuperTypeImpl(type1, type2, expected, builder);
    assertCommonSuperTypeImpl(type2, type1, expected, builder);
  }

  private static void assertCommonSuperTypeImpl(Type type1, Type type2, Type expected,
      StringBuilder builder) {
    Type actual = Types.commonSuperType(type1, type2);
    if (!Objects.equal(expected, actual)) {
      builder.append("commonSuperType(" + type1 + "," + type2 + ") = " + actual + " but should = "
          + expected + "\n");
    }
  }
}
