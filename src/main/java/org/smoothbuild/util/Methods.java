package org.smoothbuild.util;

import java.lang.reflect.Method;

public class Methods {

  public static String canonicalName(Method method) {
    return method.getDeclaringClass().getCanonicalName() + "." + method.getName();
  }

}
