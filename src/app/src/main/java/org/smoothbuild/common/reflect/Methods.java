package org.smoothbuild.common.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;

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

  public static <T extends Annotation> Optional<T> getAnnotation(Method method, Class<T> clazz) {
    if (method.isAnnotationPresent(clazz)) {
      return Optional.of(method.getAnnotation(clazz));
    } else {
      return Optional.empty();
    }
  }
}
