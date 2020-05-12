package org.smoothbuild.slib.java.junit;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.smoothbuild.lang.plugin.AbortException;
import org.smoothbuild.lang.plugin.NativeApi;

public class ReflectionUtil {
  public static Object newInstance(NativeApi nativeApi, Class<?> clazz) {
    try {
      return clazz.getDeclaredConstructor().newInstance();
    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException
        | InvocationTargetException e) {
      reportJunitError(nativeApi, "Cannot instantiate " + clazz.getCanonicalName());
      throw new AbortException();
    }
  }

  public static <T> T runReflexivelyAndCast(NativeApi nativeApi, Class<T> resultType, Object object,
      String method, Object... args) {
    Object result = runReflexively(nativeApi, object, method, args);
    if (resultType.isInstance(result)) {
      @SuppressWarnings("unchecked")
      T castResult = (T) result;
      return castResult;
    }
    reportJunitError(nativeApi, "Call to " + fullMethodName(object, method)
        + " did not return instance of "
        + resultType.getCanonicalName() + " but instance of "
        + result.getClass().getCanonicalName());
    throw new AbortException();
  }

  public static Object runReflexively(NativeApi nativeApi, Object object, String method,
      Object... args) {
    try {
      Class<?>[] paramTypes = Arrays.stream(args)
          .map(Object::getClass)
          .toArray(Class<?>[]::new);
      return object
          .getClass()
          .getMethod(method, paramTypes)
          .invoke(object, args);
    } catch (IllegalAccessException | IllegalArgumentException | SecurityException e) {
      reportJunitError(nativeApi, "Cannot invoke " + fullMethodName(object, method) + ": "
          + className(e) + " " + e.getMessage());
      throw new AbortException();
    } catch (InvocationTargetException e) {
      reportJunitError(nativeApi, "Invocation of " + fullMethodName(object, method)
          + " failed with "
          + e.getCause());
      throw new AbortException();
    } catch (NoSuchMethodException e) {
      reportJunitError(nativeApi, className(object) + " doesn't have " + fullMethodName(object,
          method) + " method.");
      throw new AbortException();
    }
  }

  private static String fullMethodName(Object object, String method) {
    return className(object) + "." + method + "()";
  }

  private static String className(Object object) {
    return object.getClass().getName();
  }

  public static void reportJunitError(NativeApi nativeApi, String message) {
    nativeApi.log().error("JUnit implementation seems invalid: " + message);
  }
}
