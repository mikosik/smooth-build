package org.smoothbuild.util.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Methods {
  public static boolean isPublic(Method method) {
    return Modifier.isPublic(method.getModifiers());
  }

  public static boolean isStatic(Method method) {
    return Modifier.isStatic(method.getModifiers());
  }

  public static String canonicalName(Method method) {
    return method.getDeclaringClass().getCanonicalName() + "." + method.getName();
  }
}
