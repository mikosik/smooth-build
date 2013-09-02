package org.smoothbuild.function.plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.smoothbuild.function.plugin.exc.InvokingMethodFailedException;

public class ReflexiveUtils {

  public static boolean isPublic(Constructor<?> constructor) {
    return hasPublicFlag(constructor.getModifiers());
  }

  public static boolean isPublic(Method method) {
    return hasPublicFlag(method.getModifiers());
  }

  public static boolean isStatic(Method method) {
    return hasStaticFlag(method.getModifiers());
  }

  private static boolean hasPublicFlag(int modifiers) {
    return Modifier.isPublic(modifiers);
  }

  private static boolean hasStaticFlag(int modifiers) {
    return Modifier.isStatic(modifiers);
  }

  public static Object invokeMethod(Object object, Method method, Object... parameters)
      throws InvokingMethodFailedException {
    try {
      return method.invoke(object, parameters);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new InvokingMethodFailedException(method, e);
    }
  }
}
