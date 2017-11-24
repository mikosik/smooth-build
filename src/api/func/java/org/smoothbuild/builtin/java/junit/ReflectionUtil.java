package org.smoothbuild.builtin.java.junit;

import static org.smoothbuild.lang.message.MessageException.errorException;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.smoothbuild.lang.message.MessageException;

public class ReflectionUtil {
  public static Object newInstance(Class<?> clazz) {
    try {
      return clazz.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw junitError("Cannot instantiate " + clazz.getCanonicalName());
    }
  }

  public static <T> T runReflexivelyAndCast(Class<T> resultType, Object object, String method,
      Object... args) {
    Object result = runReflexively(object, method, args);
    if (resultType.isInstance(result)) {
      return (T) result;
    }
    throw junitError("Call to " + fullMethodName(object, method) + " did not return instance of "
        + resultType.getCanonicalName() + " but instance of "
        + result.getClass().getCanonicalName());
  }

  public static Object runReflexively(Object object, String method, Object... args) {
    try {
      Class<?>[] paramTypes = Arrays.stream(args)
          .map(a -> a.getClass())
          .toArray(Class<?>[]::new);
      return object
          .getClass()
          .getMethod(method, paramTypes)
          .invoke(object, args);
    } catch (IllegalAccessException | IllegalArgumentException | SecurityException e) {
      throw junitError("Cannot invoke " + fullMethodName(object, method) + ": "
          + className(e) + " " + e.getMessage());
    } catch (InvocationTargetException e) {
      throw junitError("Invocation of " + fullMethodName(object, method) + " failed with "
          + e.getCause());
    } catch (NoSuchMethodException e) {
      throw junitError(className(object) + " doesn't have " + fullMethodName(object, method)
          + " method.");
    }
  }

  private static String fullMethodName(Object object, String method) {
    return className(object) + "." + method + "()";
  }

  private static String className(Object object) {
    return object.getClass().getName();
  }

  public static MessageException junitError(String message) {
    return errorException("JUnit implementation seems invalid: " + message);
  }
}
