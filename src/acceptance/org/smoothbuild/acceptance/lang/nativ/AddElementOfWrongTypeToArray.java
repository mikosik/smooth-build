package org.smoothbuild.acceptance.lang.nativ;

import static org.smoothbuild.lang.type.Types.BLOB;

import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.SString;

public class AddElementOfWrongTypeToArray {
  @SmoothFunction
  public static Array addElementOfWrongTypeToArray(Container container) {
    ArrayBuilder arrayBuilder = container.create().arrayBuilder(BLOB);
    SString string = container.create().string("abc");
    arrayBuilder.add(string);
    return arrayBuilder.build();
  }
}
