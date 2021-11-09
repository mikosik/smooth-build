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
import org.smoothbuild.db.object.obj.base.ObjectH;
import org.smoothbuild.exec.compute.Container;
import org.smoothbuild.io.fs.space.FilePath;
import org.smoothbuild.io.fs.space.FileResolver;
import org.smoothbuild.io.fs.space.JPathResolver;
import org.smoothbuild.lang.base.define.Function;
import org.smoothbuild.lang.base.define.GlobalReferencable;
import org.smoothbuild.lang.base.define.Item;
import org.smoothbuild.lang.base.define.Value;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.plugin.NativeApi;

/**
 * This class is thread-safe.
 */
@Singleton
public class MethodLoader {
  private static final String NATIVE_METHOD_NAME = "function";
  private final JPathResolver jPathResolver;
  private final FileResolver fileResolver;
  private final HashMap<CacheKey, Method> methodCache;

  @Inject
  public MethodLoader(JPathResolver jPathResolver, FileResolver fileResolver) {
    this.jPathResolver = jPathResolver;
    this.fileResolver = fileResolver;
    this.methodCache = new HashMap<>();
  }

  public synchronized Method load(GlobalReferencable referencable, String classBinaryName)
      throws LoadingMethodException {
    Method method = loadMethod(referencable, classBinaryName);
    if (referencable instanceof Function function) {
      assertMethodMatchesFunctionRequirements(function, method, classBinaryName);
    } else {
      assertMethodMatchesValueRequirements((Value) referencable, method, classBinaryName);
    }
    return method;
  }

  private Method loadMethod(GlobalReferencable referencable, String classBinaryName)
      throws LoadingMethodException {
    FilePath jarFilePath = referencable.location().file().withExtension("jar");
    Hash jarHash = hashOf(referencable, classBinaryName, jarFilePath);
    CacheKey key = new CacheKey(jarHash, classBinaryName);
    Method method = methodCache.get(key);
    if (method == null) {
      method = findMethod(referencable, jarFilePath, classBinaryName);
      methodCache.put(key, method);
    }
    return method;
  }

  private Hash hashOf(GlobalReferencable referencable, String classBinaryName, FilePath jarFilePath)
      throws LoadingMethodException {
    try {
      return fileResolver.hashOf(jarFilePath);
    } catch (FileNotFoundException e) {
      throw newLoadingException(
          referencable, classBinaryName, "File " + jarFilePath.q() + " doesn't exist.");
    } catch (IOException e) {
      throw newLoadingException(
          referencable, classBinaryName, "Error reading file " + jarFilePath.q() + ".");
    }
  }

  private Method findMethod(GlobalReferencable referencable, FilePath jarFilePath,
      String classBinaryName) throws LoadingMethodException {
    Method method = findClassMethod(referencable, jarFilePath, classBinaryName);
    if (!isPublic(method)) {
      throw newLoadingException(referencable, classBinaryName, "Providing method is not public.");
    } else if (!isStatic(method)) {
      throw newLoadingException(referencable, classBinaryName, "Providing method is not static.");
    } else if (!hasContainerParameter(method)) {
      throw newLoadingException(referencable, classBinaryName,
          "Providing method first parameter is not of type " + NativeApi.class.getCanonicalName()
              + ".");
    } else {
      return method;
    }
  }

  private Method findClassMethod(GlobalReferencable referencable, FilePath jarFilePath,
      String classBinaryName) throws LoadingMethodException {
    Class<?> clazz = findClass(referencable, jarFilePath, classBinaryName);
    return stream(clazz.getDeclaredMethods())
        .filter(m -> m.getName().equals(NATIVE_METHOD_NAME))
        .findFirst()
        .orElseThrow(() -> newLoadingException(referencable, classBinaryName, "Class '" +
            clazz.getCanonicalName() + "' does not have '" + NATIVE_METHOD_NAME + "' method."));
  }

  private Class<?> findClass(GlobalReferencable referencable, FilePath jarFilePath,
      String classBinaryName) throws LoadingMethodException {
      Path jarPath = jPathResolver.resolve(jarFilePath);
    try {
      return loadClass(jarPath, classBinaryName);
    } catch (ClassNotFoundException e) {
      throw newLoadingException(referencable, classBinaryName,
          "Class '" + classBinaryName + "' does not exist in jar " + jarFilePath.q() + ".");
    }
  }

  private static boolean hasContainerParameter(Method method) {
    Class<?>[] types = method.getParameterTypes();
    return types.length != 0 && (types[0] == NativeApi.class || types[0] == Container.class);
  }

  private void assertMethodMatchesFunctionRequirements(Function function, Method method,
      String classBinaryName) throws LoadingMethodException {
    assertNativeResultMatchesDeclared(
        function, method, function.type().result(), classBinaryName);
    assertNativeParameterTypesMatchesFuncParameters(method, function, classBinaryName);
  }

  private void assertMethodMatchesValueRequirements(Value value, Method method,
      String classBinaryName) throws LoadingMethodException {
    assertNativeResultMatchesDeclared(value, method, value.type(), classBinaryName);
    assertNativeHasOneParameter(method, value, classBinaryName);
  }

  private static void assertNativeResultMatchesDeclared(GlobalReferencable referencable,
      Method method, TypeS resultType, String classBinaryName) throws LoadingMethodException {
    Class<?> resultJType = method.getReturnType();
    if (!mapTypeToJType(resultType).equals(resultJType)) {
      throw newLoadingException(referencable, classBinaryName, referencable.q() + " declares type "
          + resultType.q() + " so its native implementation result type must be "
          + mapTypeToJType(resultType).getCanonicalName() + " but it is "
          + resultJType.getCanonicalName() + ".");
    }
  }

  private static void assertNativeParameterTypesMatchesFuncParameters(Method method,
      Function function, String classBinaryName) throws LoadingMethodException {
    Parameter[] nativeParams = method.getParameters();
    List<Item> params = function.parameters();
    if (params.size() != nativeParams.length - 1) {
      throw newLoadingException(function, classBinaryName, "Function " + function.q() + " has "
          + params.size() + " parameter(s) but its native implementation has "
          + (nativeParams.length - 1) + " parameter(s).");
    }
    for (int i = 0; i < params.size(); i++) {
      Item param = params.get(i);
      Parameter nativeParam = nativeParams[i + 1];
      TypeS paramType = param.type();
      Class<?> paramJType = nativeParam.getType();
      Class<? extends ObjectH> expectedParamJType = mapTypeToJType(paramType);
      if (!expectedParamJType.equals(paramJType)) {
        throw newLoadingException(function, classBinaryName, "Function " + function.q()
            + " parameter `" + param.name() + "` has type " + paramType.q()
            + " so its native implementation type must be " + expectedParamJType.getCanonicalName()
            + " but it is " + paramJType.getCanonicalName() + ".");
      }
    }
  }

  private static void assertNativeHasOneParameter(
      Method method, Value value, String classBinaryName) throws LoadingMethodException {
    int paramCount = method.getParameters().length;
    if (paramCount != 1) {
      throw newLoadingException(value, classBinaryName, value.q()
          + " has native implementation that has too many parameter(s) = " + paramCount);
    }
  }

  private static LoadingMethodException newLoadingException(
      GlobalReferencable referencable, String classBinaryName, String message) {
    return newLoadingException(referencable, classBinaryName, message, null);
  }

  private static LoadingMethodException newLoadingException(GlobalReferencable referencable,
      String classBinaryName, String message, Exception e) {
    return new LoadingMethodException("Error loading native implementation for "
        + referencable.q() + " specified as `" + classBinaryName + "`: " + message, e);
  }

  private static record CacheKey(Hash jarHash, String path) {}

  public static class LoadingMethodException extends Exception {
    public LoadingMethodException(String message, Throwable e) {
      super(message, e);
    }
  }
}
