package org.smoothbuild.lang.base.type.compound;

import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.base.type.Types;

public class PropertiesUtils {
  static Type increaseCoreDepth(Type type, int delta) {
    Type result = type;
    for (int i = 0; i < delta; i++) {
      result = Types.array(result);
    }
    return result;
  }
}
