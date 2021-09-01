package org.smoothbuild.exec.java;

import static java.util.Arrays.stream;
import static org.smoothbuild.exec.java.MapTypeToJType.mapTypeToJType;
import static org.smoothbuild.util.reflect.Classes.loadClass;
import static org.smoothbuild.util.reflect.Methods.isPublic;
import static org.smoothbuild.util.reflect.Methods.isStatic;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.exec.compute.Container;
import org.smoothbuild.exec.java.MethodPath.MethodPathParsingException;
import org.smoothbuild.io.fs.space.FilePath;
import org.smoothbuild.io.fs.space.FileResolver;
import org.smoothbuild.io.fs.space.JPathResolver;
import org.smoothbuild.lang.base.define.Function;
import org.smoothbuild.lang.base.define.GlobalReferencable;
import org.smoothbuild.lang.base.define.Item;
import org.smoothbuild.lang.base.define.Value;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.plugin.NativeApi;

/**
 * This class is thread-safe.
 */
@Singleton
public class MethodLoader {
  private final JPathResolver jPathResolver;
  private final FileResolver fileResolver;
  private final HashMap<CacheKey, Method> methodCache;

  @Inject
  public MethodLoader(JPathResolver jPathResolver, FileResolver fileResolver) {
    this.jPathResolver = jPathResolver;
    this.fileResolver = fileResolver;
    this.methodCache = new HashMap<>();
  }

  public synchronized Method load(GlobalReferencable referencable, String methodPath)
      throws LoadingMethodException {
    MethodPath path = parseMethodPath(referencable, methodPath);
    Method method = loadMethod(referencable, path);
    if (referencable instanceof Function function) {
      assertMethodMatchesFunctionRequirements(function, method, path);
    } else {
      assertMethodMatchesValueRequirements((Value) referencable, method, path);
    }
    return method;
  }

  private static MethodPath parseMethodPath(GlobalReferencable referencable, String path)
      throws LoadingMethodException {
    try {
      return MethodPath.parse(path);
    } catch (MethodPathParsingException e) {
      throw newLoadingException(referencable, path, e.getMessage(), e);
    }
  }

  private Method loadMethod(GlobalReferencable referencable, MethodPath methodPath)
      throws LoadingMethodException {
    FilePath jarFilePath = referencable.location().file().withExtension("jar");
    Hash jarHash = hashOf(referencable, methodPath, jarFilePath);
    CacheKey key = new CacheKey(jarHash, methodPath.toString());
    Method method = methodCache.get(key);
    if (method == null) {
      method = findMethod(referencable, jarFilePath, methodPath);
      methodCache.put(key, method);
    }
    return method;
  }

  private Hash hashOf(GlobalReferencable referencable, MethodPath methodPath, FilePath jarFilePath)
      throws LoadingMethodException {
    try {
      return fileResolver.hashOf(jarFilePath);
    } catch (FileNotFoundException e) {
      throw newLoadingException(
          referencable, methodPath, "File " + jarFilePath.q() + " doesn't exist.");
    } catch (IOException e) {
      throw newLoadingException(
          referencable, methodPath, "Error reading file " + jarFilePath.q() + ".");
    }
  }

  private Method findMethod(GlobalReferencable referencable, FilePath jarFilePath, MethodPath methodPath)
      throws LoadingMethodException {
    Method method = findClassMethod(referencable, jarFilePath, methodPath);
    if (!isPublic(method)) {
      throw newLoadingException(referencable, methodPath, "Providing method is not public.");
    } else if (!isStatic(method)) {
      throw newLoadingException(referencable, methodPath, "Providing method is not static.");
    } else if (!hasContainerParameter(method)) {
      throw newLoadingException(referencable, methodPath,
          "Providing method first parameter is not of type " + NativeApi.class.getCanonicalName()
              + ".");
    } else {
      return method;
    }
  }

  private Method findClassMethod(GlobalReferencable referencable, FilePath jarFilePath,
      MethodPath methodPath) throws LoadingMethodException {
    String methodName = methodPath.methodName();
    Class<?> clazz = findClass(referencable, jarFilePath, methodPath);
    return stream(clazz.getDeclaredMethods())
        .filter(m -> m.getName().equals(methodName))
        .findFirst()
        .orElseThrow(() -> newInvalidPathException(referencable, methodPath, "Class '" +
            clazz.getCanonicalName() + "' does not have '" + methodName + "' method."));
  }

  private Class<?> findClass(GlobalReferencable referencable, FilePath jarFilePath,
      MethodPath methodPath) throws LoadingMethodException {
      Path jarPath = jPathResolver.resolve(jarFilePath);
    try {
      return loadClass(jarPath, methodPath.classBinaryName());
    } catch (ClassNotFoundException e) {
      throw newInvalidPathException(referencable, methodPath,
          "Class '" + methodPath.classBinaryName() + "' does not exist in jar " + jarFilePath.q()
              + ".");
    }
  }

  private static boolean hasContainerParameter(Method method) {
    Class<?>[] types = method.getParameterTypes();
    return types.length != 0 && (types[0] == NativeApi.class || types[0] == Container.class);
  }

  private void assertMethodMatchesFunctionRequirements(Function function, Method method,
      MethodPath methodPath) throws LoadingMethodException {
    assertNativeResultMatchesDeclared(function, method, function.type().resultType(), methodPath);
    assertNativeParameterTypesMatchesFuncParameters(method, function, methodPath);
  }

  private void assertMethodMatchesValueRequirements(Value value, Method method,
      MethodPath methodPath) throws LoadingMethodException {
    assertNativeResultMatchesDeclared(value, method, value.type(), methodPath);
    assertNativeHasOneParameter(method, value, methodPath);
  }

  private static void assertNativeResultMatchesDeclared(GlobalReferencable referencable,
      Method method, Type resultType, MethodPath methodPath) throws LoadingMethodException {
    Class<?> resultJType = method.getReturnType();
    if (!mapTypeToJType(resultType).equals(resultJType)) {
      throw newLoadingException(referencable, methodPath, referencable.q() + " declares type "
          + resultType.q() + " so its native implementation result type must be "
          + mapTypeToJType(resultType).getCanonicalName() + " but it is "
          + resultJType.getCanonicalName() + ".");
    }
  }

  private static void assertNativeParameterTypesMatchesFuncParameters(Method method,
      Function function, MethodPath methodPath) throws LoadingMethodException {
    Parameter[] nativeParams = method.getParameters();
    List<Item> params = function.parameters();
    if (params.size() != nativeParams.length - 1) {
      throw newLoadingException(function, methodPath, "Function " + function.q() + " has "
          + params.size() + " parameter(s) but its native implementation has "
          + (nativeParams.length - 1) + " parameter(s).");
    }
    for (int i = 0; i < params.size(); i++) {
      String declaredName = params.get(i).name();
      Parameter nativeParam = nativeParams[i + 1];
      Type paramType = params.get(i).type();
      Class<?> paramJType = nativeParam.getType();
      Class<? extends Obj> expectedParamJType = mapTypeToJType(paramType);
      if (!expectedParamJType.equals(paramJType)) {
        throw newLoadingException(function, methodPath, "Function " + function.q() + " parameter `"
            + declaredName + "` has type " + paramType.q()
            + " so its native implementation type must be " + expectedParamJType.getCanonicalName()
            + " but it is " + paramJType.getCanonicalName() + ".");
      }
    }
  }

  private static void assertNativeHasOneParameter(Method method, Value value, MethodPath methodPath)
      throws LoadingMethodException {
    int paramCount = method.getParameters().length;
    if (paramCount != 1) {
      throw newLoadingException(value, methodPath, value.q()
          + " has native implementation that has too many parameter(s) = " + paramCount);
    }
  }

  private static LoadingMethodException newInvalidPathException(
      GlobalReferencable referencable, MethodPath methodPath, String message) {
    return newLoadingException(referencable, methodPath.toString(),
        "Invalid native path `" + methodPath + "`: " + message, null);
  }

  private static LoadingMethodException newLoadingException(
      GlobalReferencable referencable, MethodPath methodPath, String message) {
    return newLoadingException(referencable, methodPath.toString(), message, null);
  }

  private static LoadingMethodException newLoadingException(GlobalReferencable referencable,
      String path, String message, Exception e) {
    return new LoadingMethodException("Error loading native implementation for "
        + referencable.q() + " specified as `" + path + "`: " + message, e);
  }

  private static record CacheKey(Hash jarHash, String path) {}

  public static class LoadingMethodException extends Exception {
    public LoadingMethodException(String message, Throwable e) {
      super(message, e);
    }
  }
}
