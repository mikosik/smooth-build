package org.smoothbuild.lang.type;

import static org.junit.Assert.assertEquals;
import static org.smoothbuild.lang.type.ArrayType.arrayOf;
import static org.smoothbuild.lang.type.Types.BLOB;
import static org.smoothbuild.lang.type.Types.FILE;
import static org.smoothbuild.lang.type.Types.NOTHING;
import static org.smoothbuild.lang.type.Types.STRING;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class ArrayTypeTest {
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
