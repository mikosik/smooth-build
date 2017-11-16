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
    assertEquals(arrayOf(STRING).elemType(), STRING);
    assertEquals(arrayOf(BLOB).elemType(), BLOB);
    assertEquals(arrayOf(FILE).elemType(), FILE);
    assertEquals(arrayOf(NOTHING).elemType(), NOTHING);

    assertEquals(arrayOf(arrayOf(STRING)).elemType(), arrayOf(STRING));
    assertEquals(arrayOf(arrayOf(BLOB)).elemType(), arrayOf(BLOB));
    assertEquals(arrayOf(arrayOf(FILE)).elemType(), arrayOf(FILE));
    assertEquals(arrayOf(arrayOf(NOTHING)).elemType(), arrayOf(NOTHING));
  }
}
