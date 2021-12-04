package org.smoothbuild.slib.java.junit;

import static org.smoothbuild.slib.java.junit.JunitExc.brokenJunitImplementation;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class ReflectionUtil {
  public static Object newInstance(Class<?> clazz) throws JunitExc {
    try {
      return clazz.getDeclaredConstructor().newInstance();
    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException
        | InvocationTargetException e) {
      throw brokenJunitImplementation("Cannot instantiate " + clazz.getCanonicalName());
    }
  }

  public static <T> T runReflexivelyAndCast(Class<T> resultType, Object object,
      String method, Object... args) throws JunitExc {
    Object result = runReflexively(object, method, args);
    if (resultType.isInstance(result)) {
      @SuppressWarnings("unchecked")
      T castResult = (T) result;
      return castResult;
    }
    throw brokenJunitImplementation("Call to " + fullMethodName(object, method)
        + " did not return instance of " + resultType.getCanonicalName() + " but instance of "
        + result.getClass().getCanonicalName());
  }

  public static Object runReflexively(Object object, String method,
      Object... args) throws JunitExc {
    try {
      Class<?>[] paramTs = Arrays.stream(args)
          .map(Object::getClass)
          .toArray(Class<?>[]::new);
      return object
          .getClass()
          .getMethod(method, paramTs)
          .invoke(object, args);
    } catch (IllegalAccessException | IllegalArgumentException | SecurityException e) {
      throw brokenJunitImplementation(
          "Cannot invoke " + fullMethodName(object, method) + ": " + className(e) + " ", e);
    } catch (InvocationTargetException e) {
      throw brokenJunitImplementation(
          "Invocation of " + fullMethodName(object, method) + " failed with: " + e.getMessage(), e);
    } catch (NoSuchMethodException e) {
      throw brokenJunitImplementation(
          className(object) + " doesn't have " + fullMethodName(object, method) + " method.", e);
    }
  }

  private static String fullMethodName(Object object, String method) {
    return className(object) + "." + method + "()";
  }

  private static String className(Object object) {
    return object.getClass().getName();
  }
}
