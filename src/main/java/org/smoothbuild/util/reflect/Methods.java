package org.smoothbuild.util.reflect;

import java.lang.reflect.Method;

public class Methods {

  public static String canonicalName(Method method) {
    return method.getDeclaringClass().getCanonicalName() + "." + method.getName();
  }

}
