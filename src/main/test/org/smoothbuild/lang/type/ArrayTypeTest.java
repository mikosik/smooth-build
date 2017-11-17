package org.smoothbuild.lang.type;

import static org.junit.Assert.assertEquals;
import static org.smoothbuild.lang.type.ArrayType.arrayOf;
import static org.smoothbuild.lang.type.ArrayType.deepestElemType;
import static org.smoothbuild.lang.type.ArrayType.depthOf;
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

  @Test
  public void direct_convertible_to() throws Exception {
    assertEquals(arrayOf(BLOB), arrayOf(FILE).directConvertibleTo());
    assertEquals(null, arrayOf(STRING).directConvertibleTo());
    assertEquals(null, arrayOf(NOTHING).directConvertibleTo());
  }

  @Test
  public void depth_of() throws Exception {
    assertEquals(0, depthOf(STRING));
    assertEquals(0, depthOf(BLOB));
    assertEquals(0, depthOf(FILE));
    assertEquals(0, depthOf(NOTHING));

    assertEquals(1, depthOf(arrayOf(STRING)));
    assertEquals(1, depthOf(arrayOf(BLOB)));
    assertEquals(1, depthOf(arrayOf(FILE)));
    assertEquals(1, depthOf(arrayOf(NOTHING)));

    assertEquals(2, depthOf(arrayOf(arrayOf(STRING))));
    assertEquals(2, depthOf(arrayOf(arrayOf(BLOB))));
    assertEquals(2, depthOf(arrayOf(arrayOf(FILE))));
    assertEquals(2, depthOf(arrayOf(arrayOf(NOTHING))));
  }

  @Test
  public void deepest_elem_type() throws Exception {
    assertEquals(STRING, deepestElemType(STRING));
    assertEquals(FILE, deepestElemType(FILE));
    assertEquals(NOTHING, deepestElemType(NOTHING));

    assertEquals(STRING, deepestElemType(arrayOf(STRING)));
    assertEquals(FILE, deepestElemType(arrayOf(FILE)));
    assertEquals(NOTHING, deepestElemType(arrayOf(NOTHING)));

    assertEquals(STRING, deepestElemType(arrayOf(STRING)));
    assertEquals(FILE, deepestElemType(arrayOf(FILE)));
    assertEquals(NOTHING, deepestElemType(arrayOf(NOTHING)));
  }
}
