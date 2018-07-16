package org.smoothbuild.lang.type;

import static com.google.common.base.Preconditions.checkArgument;

public class TypeUtils {
  public static Type actualCoreType(Type generic, Type actual) {
    checkArgument(generic.isGeneric(), "'generic' argument is not generic.");
    if (generic.isArray()) {
      checkArgument(actual.isArray(), "'generic' is array but 'actual' is not.");
      return actualCoreType(((ArrayType) generic).elemType(), ((ArrayType) actual).elemType());
    }
    return actual;
  }
}
