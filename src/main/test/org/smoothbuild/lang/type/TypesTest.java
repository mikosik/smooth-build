package org.smoothbuild.lang.type;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.smoothbuild.lang.type.Conversions.canConvert;
import static org.smoothbuild.lang.type.Types.BLOB;
import static org.smoothbuild.lang.type.Types.BLOB_ARRAY;
import static org.smoothbuild.lang.type.Types.FILE;
import static org.smoothbuild.lang.type.Types.FILE_ARRAY;
import static org.smoothbuild.lang.type.Types.NIL;
import static org.smoothbuild.lang.type.Types.NOTHING;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.smoothbuild.lang.type.Types.STRING_ARRAY;
import static org.smoothbuild.lang.type.Types.allTypes;
import static org.smoothbuild.lang.type.Types.arrayOf;
import static org.smoothbuild.lang.type.Types.basicTypes;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

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
    thenReturned(containsInAnyOrder(STRING, BLOB, FILE, NOTHING, STRING_ARRAY, BLOB_ARRAY,
        FILE_ARRAY, NIL));
  }

  @Test
  public void array_elem_types() {
    assertEquals(STRING_ARRAY.elemType(), STRING);
    assertEquals(BLOB_ARRAY.elemType(), BLOB);
    assertEquals(FILE_ARRAY.elemType(), FILE);
    assertEquals(NIL.elemType(), NOTHING);
  }

  @Test
  public void equals_and_hashcode() {
    EqualsTester tester = new EqualsTester();

    tester.addEqualityGroup(STRING);
    tester.addEqualityGroup(BLOB);
    tester.addEqualityGroup(FILE);
    tester.addEqualityGroup(STRING_ARRAY);
    tester.addEqualityGroup(BLOB_ARRAY);
    tester.addEqualityGroup(FILE_ARRAY);
    tester.addEqualityGroup(NIL);

    tester.testEquals();
  }

  @Test
  public void to_string() {
    assertEquals("'String'", STRING.toString());
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
  public void array_of() {
    assertEquals(STRING_ARRAY, arrayOf(STRING));
    assertEquals(BLOB_ARRAY, arrayOf(BLOB));
    assertEquals(FILE_ARRAY, arrayOf(FILE));
    assertEquals(NIL, arrayOf(NOTHING));
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
    given(type = Types.arrayOf(STRING));
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
    assertCommonSuperType(STRING, STRING_ARRAY, null, builder);
    assertCommonSuperType(STRING, BLOB_ARRAY, null, builder);
    assertCommonSuperType(STRING, FILE_ARRAY, null, builder);
    assertCommonSuperType(STRING, NIL, null, builder);

    assertCommonSuperType(BLOB, BLOB, BLOB, builder);
    assertCommonSuperType(BLOB, FILE, BLOB, builder);
    assertCommonSuperType(BLOB, NOTHING, BLOB, builder);
    assertCommonSuperType(BLOB, STRING_ARRAY, null, builder);
    assertCommonSuperType(BLOB, BLOB_ARRAY, null, builder);
    assertCommonSuperType(BLOB, FILE_ARRAY, null, builder);
    assertCommonSuperType(BLOB, NIL, null, builder);

    assertCommonSuperType(FILE, FILE, FILE, builder);
    assertCommonSuperType(FILE, NOTHING, FILE, builder);
    assertCommonSuperType(FILE, STRING_ARRAY, null, builder);
    assertCommonSuperType(FILE, BLOB_ARRAY, null, builder);
    assertCommonSuperType(FILE, FILE_ARRAY, null, builder);
    assertCommonSuperType(FILE, NIL, null, builder);

    assertCommonSuperType(NOTHING, NOTHING, NOTHING, builder);
    assertCommonSuperType(NOTHING, STRING_ARRAY, STRING_ARRAY, builder);
    assertCommonSuperType(NOTHING, BLOB_ARRAY, BLOB_ARRAY, builder);
    assertCommonSuperType(NOTHING, FILE_ARRAY, FILE_ARRAY, builder);
    assertCommonSuperType(NOTHING, NIL, NIL, builder);

    assertCommonSuperType(STRING_ARRAY, STRING_ARRAY, STRING_ARRAY, builder);
    assertCommonSuperType(STRING_ARRAY, BLOB_ARRAY, null, builder);
    assertCommonSuperType(STRING_ARRAY, FILE_ARRAY, null, builder);
    assertCommonSuperType(STRING_ARRAY, NIL, STRING_ARRAY, builder);

    assertCommonSuperType(BLOB_ARRAY, BLOB_ARRAY, BLOB_ARRAY, builder);
    assertCommonSuperType(BLOB_ARRAY, FILE_ARRAY, BLOB_ARRAY, builder);
    assertCommonSuperType(BLOB_ARRAY, NIL, BLOB_ARRAY, builder);

    assertCommonSuperType(FILE_ARRAY, FILE_ARRAY, FILE_ARRAY, builder);
    assertCommonSuperType(FILE_ARRAY, NIL, FILE_ARRAY, builder);

    assertCommonSuperType(NIL, NIL, NIL, builder);

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
    if (expected != actual) {
      builder.append("commonSuperType(" + type1 + "," + type2 + ") = " + actual + " but should = "
          + expected + "\n");
    }
  }
}
