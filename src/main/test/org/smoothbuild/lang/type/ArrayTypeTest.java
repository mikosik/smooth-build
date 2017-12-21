package org.smoothbuild.lang.type;

import static org.junit.Assert.assertEquals;
import static org.smoothbuild.lang.type.ArrayType.arrayOf;
import static org.smoothbuild.lang.type.ArrayType.arrayWithDepth;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class ArrayTypeTest {
  private static final TypeSystem TYPE_SYSTEM = new TypeSystem();
  private static final Type STRING = TYPE_SYSTEM.string();
  private static final Type BLOB = TYPE_SYSTEM.blob();
  private static final Type FILE = TYPE_SYSTEM.file();
  private static final Type NOTHING = TYPE_SYSTEM.nothing();

  @Test
  public void array_with_depth_fails_for_negative_depth() throws Exception {
    when(() -> arrayWithDepth(STRING, -1));
    thenThrown(IllegalArgumentException.class);
  }

  @Test
  public void array_with_depth() throws Exception {
    assertEquals(STRING, arrayWithDepth(STRING, 0));
    assertEquals(arrayOf(STRING), arrayWithDepth(STRING, 1));
    assertEquals(arrayOf(arrayOf(STRING)), arrayWithDepth(STRING, 2));
  }

  @Test
  public void array_elem_types() {
    assertEquals(STRING, arrayOf(STRING).elemType());
    assertEquals(BLOB, arrayOf(BLOB).elemType());
    assertEquals(personType(), arrayOf(personType()).elemType());
    assertEquals(NOTHING, arrayOf(NOTHING).elemType());

    assertEquals(arrayOf(STRING), arrayOf(arrayOf(STRING)).elemType());
    assertEquals(arrayOf(BLOB), arrayOf(arrayOf(BLOB)).elemType());
    assertEquals(arrayOf(personType()), arrayOf(arrayOf(personType())).elemType());
    assertEquals(arrayOf(NOTHING), arrayOf(arrayOf(NOTHING)).elemType());
  }

  @Test
  public void direct_convertible_to() throws Exception {
    assertEquals(arrayOf(BLOB), arrayOf(FILE).directConvertibleTo());
    assertEquals(arrayOf(STRING), arrayOf(personType()).directConvertibleTo());
    assertEquals(null, arrayOf(STRING).directConvertibleTo());
    assertEquals(null, arrayOf(NOTHING).directConvertibleTo());
  }

  private static StructType personType() {
    return new StructType("Person", ImmutableMap.of("firstName", STRING, "lastName", STRING));
  }
}
