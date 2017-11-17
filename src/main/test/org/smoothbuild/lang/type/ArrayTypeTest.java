package org.smoothbuild.lang.type;

import static org.junit.Assert.assertEquals;
import static org.smoothbuild.lang.type.ArrayType.arrayOf;
import static org.smoothbuild.lang.type.Types.BLOB;
import static org.smoothbuild.lang.type.Types.FILE;
import static org.smoothbuild.lang.type.Types.NOTHING;
import static org.smoothbuild.lang.type.Types.STRING;

import org.junit.Test;

public class ArrayTypeTest {
  @Test
  public void array_elem_types() {
    assertEquals(STRING, arrayOf(STRING).elemType());
    assertEquals(BLOB, arrayOf(BLOB).elemType());
    assertEquals(FILE, arrayOf(FILE).elemType());
    assertEquals(NOTHING, arrayOf(NOTHING).elemType());

    assertEquals(arrayOf(STRING), arrayOf(arrayOf(STRING)).elemType());
    assertEquals(arrayOf(BLOB), arrayOf(arrayOf(BLOB)).elemType());
    assertEquals(arrayOf(FILE), arrayOf(arrayOf(FILE)).elemType());
    assertEquals(arrayOf(NOTHING), arrayOf(arrayOf(NOTHING)).elemType());
  }
}
