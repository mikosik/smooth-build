package org.smoothbuild.lang.type;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import static org.smoothbuild.lang.type.Types.arrayTypeContaining;
import static org.smoothbuild.lang.type.Types.basicTypes;
import static org.smoothbuild.lang.type.Types.parameterJTypeToType;
import static org.smoothbuild.lang.type.Types.parameterTypes;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;
import com.google.common.testing.EqualsTester;

public class TypesTest {

  @Test
  public void basic_types() {
    when(basicTypes());
    thenReturned(containsInAnyOrder(STRING, BLOB, FILE));
  }

  @Test
  public void param_types() {
    when(parameterTypes());
    thenReturned(containsInAnyOrder(STRING, BLOB, FILE, STRING_ARRAY, BLOB_ARRAY, FILE_ARRAY, NIL));
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
    Set<Type> visited = Sets.newHashSet();
    for (Type type : allTypes()) {
      for (Type visitedType : visited) {
        assertFalse(canConvert(visitedType, type));
      }
      visited.add(type);
    }
  }

  @Test
  public void paramJTypeToType_works_for_all_types() {
    for (Type type : asList(STRING, BLOB, FILE, STRING_ARRAY, BLOB_ARRAY, FILE_ARRAY)) {
      assertEquals(type, parameterJTypeToType(type.jType()));
    }
  }

  @Test
  public void arrayType_containing() {
    assertEquals(STRING_ARRAY, arrayTypeContaining(STRING));
    assertEquals(BLOB_ARRAY, arrayTypeContaining(BLOB));
    assertEquals(FILE_ARRAY, arrayTypeContaining(FILE));
    assertEquals(NIL, arrayTypeContaining(NOTHING));
  }
}
