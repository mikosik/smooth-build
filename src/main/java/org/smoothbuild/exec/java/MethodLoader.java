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
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.exec.compute.Container;
import org.smoothbuild.exec.java.MethodPath.MethodPathParsingException;
import org.smoothbuild.io.fs.space.FilePath;
import org.smoothbuild.io.fs.space.FileResolver;
import org.smoothbuild.io.fs.space.JPathResolver;
import org.smoothbuild.lang.base.define.Item;
import org.smoothbuild.lang.base.define.RealFunction;
import org.smoothbuild.lang.base.define.Referencable;
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

  public synchronized Method load(Referencable referencable, String methodPath)
      throws LoadingMethodException {
    MethodPath path = parseMethodPath(referencable, methodPath);
    Method method = loadMethod(referencable, path);
    if (referencable instanceof RealFunction function) {
      assertMethodMatchesFunctionRequirements(function, method, path);
    } else {
      assertMethodMatchesValueRequirements((Value) referencable, method, path);
    }
    return method;
  }

  private static MethodPath parseMethodPath(Referencable referencable, String path)
      throws LoadingMethodException {
    try {
      return MethodPath.parse(path);
    } catch (MethodPathParsingException e) {
      throw newLoadingException(referencable, path, e.getMessage(), e);
    }
  }

  private Method loadMethod(Referencable referencable, MethodPath path)
      throws LoadingMethodException {
    FilePath jarFilePath = referencable.location().file().withExtension("jar");
    Hash jarHash = hashOf(referencable, path, jarFilePath);
    CacheKey key = new CacheKey(jarHash, path.toString());
    Method method = methodCache.get(key);
    if (method == null) {
      method = findMethod(referencable, jarFilePath, path);
      methodCache.put(key, method);
    }
    return method;
  }

  private Hash hashOf(Referencable referencable, MethodPath path, FilePath jarFilePath)
      throws LoadingMethodException {
    try {
      return fileResolver.hashOf(jarFilePath);
    } catch (FileNotFoundException e) {
      throw newLoadingException(referencable, path, "File " + jarFilePath.q() + " doesn't exist.");
    } catch (IOException e) {
      throw newLoadingException(referencable, path, "Error reading file " + jarFilePath.q() + ".");
    }
  }

  private Method findMethod(Referencable referencable, FilePath jarFile, MethodPath path)
      throws LoadingMethodException {
    Method method = findClassMethod(referencable, jarFile, path);
    if (!isPublic(method)) {
      throw newLoadingException(referencable, path, "Providing method is not public.");
    } else if (!isStatic(method)) {
      throw newLoadingException(referencable, path, "Providing method is not static.");
    } else if (!hasContainerParameter(method)) {
      throw newLoadingException(referencable, path,
          "Providing method first parameter is not of type " + NativeApi.class.getCanonicalName()
              + ".");
    } else {
      return method;
    }
  }

  private Method findClassMethod(Referencable referencable, FilePath jarFile,
      MethodPath methodPath) throws LoadingMethodException {
    String methodName = methodPath.methodName();
    Class<?> clazz = findClass(referencable, jarFile, methodPath);
    return stream(clazz.getDeclaredMethods())
        .filter(m -> m.getName().equals(methodName))
        .findFirst()
        .orElseThrow(() -> newInvalidPathException(referencable, methodPath, "Class '" +
            clazz.getCanonicalName() + "' does not have '" + methodName + "' method."));
  }

  private Class<?> findClass(Referencable referencable, FilePath jarFile,
      MethodPath methodPath) throws LoadingMethodException {
      Path jarPath = jPathResolver.resolve(jarFile);
    try {
      return loadClass(jarPath, methodPath.classBinaryName());
    } catch (ClassNotFoundException e) {
      throw newInvalidPathException(referencable, methodPath,
          "Class '" + methodPath.classBinaryName() + "' does not exist in jar " + jarFile.q()
              + ".");
    }
  }

  private static boolean hasContainerParameter(Method method) {
    Class<?>[] types = method.getParameterTypes();
    return types.length != 0 && (types[0] == NativeApi.class || types[0] == Container.class);
  }

  private void assertMethodMatchesFunctionRequirements(RealFunction function, Method method,
      MethodPath path) throws LoadingMethodException {
    assertNativeResultMatchesDeclared(function, method, function.type().resultType(), path);
    assertNativeParameterTypesMatchesFuncParameters(method, function, path);
  }

  private void assertMethodMatchesValueRequirements(Value value, Method method, MethodPath path)
      throws LoadingMethodException {
    assertNativeResultMatchesDeclared(value, method, value.type(), path);
    assertNativeHasOneParameter(method, value, path);
  }

  private static void assertNativeResultMatchesDeclared(Referencable referencable,
      Method method, Type resultType, MethodPath path) throws LoadingMethodException {
    Class<?> resultJType = method.getReturnType();
    if (!mapTypeToJType(resultType).equals(resultJType)) {
      throw newLoadingException(referencable, path, referencable.q() + " declares type "
          + resultType.q() + " so its native implementation result type must be "
          + mapTypeToJType(resultType).getCanonicalName() + " but it is "
          + resultJType.getCanonicalName() + ".");
    }
  }

  private static void assertNativeParameterTypesMatchesFuncParameters(Method method,
      RealFunction function, MethodPath path) throws LoadingMethodException {
    Parameter[] nativeParams = method.getParameters();
    List<Item> params = function.parameters();
    if (params.size() != nativeParams.length - 1) {
      throw newLoadingException(function, path, "Function " + function.q() + " has "
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
        throw newLoadingException(function, path, "Function " + function.q() + " parameter `"
            + declaredName + "` has type " + paramType.q()
            + " so its native implementation type must be " + expectedParamJType.getCanonicalName()
            + " but it is " + paramJType.getCanonicalName() + ".");
      }
    }
  }

  private static void assertNativeHasOneParameter(Method method, Value value, MethodPath path)
      throws LoadingMethodException {
    int paramCount = method.getParameters().length;
    if (paramCount != 1) {
      throw newLoadingException(value, path, value.q()
          + " has native implementation that has too many parameter(s) = " + paramCount);
    }
  }

  private static LoadingMethodException newInvalidPathException(
      Referencable referencable, MethodPath path, String message) {
    return newLoadingException(referencable, path.toString(),
        "Invalid native path `" + path + "`: " + message, null);
  }

  private static LoadingMethodException newLoadingException(
      Referencable referencable, MethodPath path, String message) {
    return newLoadingException(referencable, path.toString(), message, null);
  }

  private static LoadingMethodException newLoadingException(Referencable referencable,
      String path, String message, Exception e) {
    return new LoadingMethodException("Error loading native implementation for "
        + referencable.q() + " specified as `" + path + "`: " + message, e);
  }

  private static record CacheKey(Hash jarHash, String path) {}
}
