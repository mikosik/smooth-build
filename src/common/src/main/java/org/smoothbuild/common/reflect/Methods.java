package org.smoothbuild.common.reflect;

import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.smoothbuild.common.collect.Maybe;

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

  public static <T extends Annotation> Maybe<T> getAnnotation(Method method, Class<T> clazz) {
    if (method.isAnnotationPresent(clazz)) {
      return some(method.getAnnotation(clazz));
    } else {
      return none();
    }
  }
}
