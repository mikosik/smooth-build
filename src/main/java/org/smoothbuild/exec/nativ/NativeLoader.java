package org.smoothbuild.exec.nativ;

import static java.util.Arrays.stream;
import static org.smoothbuild.exec.nativ.MapTypeToJType.mapTypeToJType;
import static org.smoothbuild.util.reflect.Classes.loadClass;
import static org.smoothbuild.util.reflect.Methods.isPublic;
import static org.smoothbuild.util.reflect.Methods.isStatic;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.exec.compute.Container;
import org.smoothbuild.exec.nativ.JavaMethodPath.JavaMethodPathParsingException;
import org.smoothbuild.io.util.JarFile;
import org.smoothbuild.lang.base.define.Function;
import org.smoothbuild.lang.base.define.Item;
import org.smoothbuild.lang.base.define.Referencable;
import org.smoothbuild.lang.base.define.Value;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.plugin.NativeApi;

/**
 * This class is thread-safe.
 */
@Singleton
public class NativeLoader {
  private final HashMap<CacheKey, Native> nativeCache;
  private final HashMap<Hash, JarFile> jarFileCache;

  @Inject
  public NativeLoader() {
    this.nativeCache = new HashMap<>();
    this.jarFileCache = new HashMap<>();
  }

  public synchronized void storeJarFile(JarFile jarFile) {
    jarFileCache.put(jarFile.hash(), jarFile);
  }

  public synchronized Native loadNative(Function function, String methodPath, Hash contentHash)
      throws LoadingNativeException {
    JavaMethodPath path = parseMethodPath(function, methodPath);
    Native nativ = loadNativeImpl(function, path, contentHash);
    assertNativeResultMatchesDeclared(function, nativ, function.type().resultType(), path);
    assertNativeParameterTypesMatchesFuncParameters(nativ, function, path);
    return nativ;
  }

  public synchronized Native loadNative(Value value, String methodPath, Hash contentHash)
      throws LoadingNativeException {
    JavaMethodPath path = parseMethodPath(value, methodPath);
    Native nativ = loadNativeImpl(value, path, contentHash);
    assertNativeResultMatchesDeclared(value, nativ, value.type(), path);
    assertNativeHasOneParameter(nativ, value, path);
    return nativ;
  }

  private static JavaMethodPath parseMethodPath(Referencable referencable, String path)
      throws LoadingNativeException {
    try {
      return JavaMethodPath.parse(path);
    } catch (JavaMethodPathParsingException e) {
      throw newLoadingException(referencable, path, e.getMessage(), e);
    }
  }

  private Native loadNativeImpl(Referencable referencable, JavaMethodPath path, Hash contentHash)
      throws LoadingNativeException {
    JarFile jarFile = jarFileCache.get(contentHash);
    CacheKey key = new CacheKey(jarFile, path.toString());
    Native nativ = nativeCache.get(key);
    if (nativ == null) {
      Method method = findMethod(referencable, jarFile, path);
      nativ = new Native(method, jarFile);
      nativeCache.put(key, nativ);
    }
    return nativ;
  }

  private static Method findMethod(Referencable referencable, JarFile jarFile, JavaMethodPath path)
      throws LoadingNativeException {
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

  private static Method findClassMethod(Referencable referencable, JarFile jarFile,
      JavaMethodPath methodPath) throws LoadingNativeException {
    String methodName = methodPath.methodName();
    Class<?> clazz = findClass(referencable, jarFile, methodPath);
    return stream(clazz.getDeclaredMethods())
        .filter(m -> m.getName().equals(methodName))
        .findFirst()
        .orElseThrow(() -> newInvalidPathException(referencable, methodPath, "Class '" +
            clazz.getCanonicalName() + "' does not have '" + methodName + "' method."));
  }

  private static Class<?> findClass(Referencable referencable, JarFile jarFile,
      JavaMethodPath methodPath) throws LoadingNativeException {
    try {
      return loadClass(jarFile.resolvedPath(), methodPath.classBinaryName());
    } catch (ClassNotFoundException e) {
      throw newInvalidPathException(referencable, methodPath,
          "Class '" + methodPath.classBinaryName() + "' does not exist in jar '"
          + jarFile.location().prefixedPath() + "'.");
    }
  }

  private static boolean hasContainerParameter(Method method) {
    Class<?>[] types = method.getParameterTypes();
    return types.length != 0 && (types[0] == NativeApi.class || types[0] == Container.class);
  }

  private static void assertNativeResultMatchesDeclared(Referencable referencable, Native nativ,
      Type resultType, JavaMethodPath path) throws LoadingNativeException {
    Method method = nativ.method();
    Class<?> resultJType = method.getReturnType();
    if (!mapTypeToJType(resultType).equals(resultJType)) {
      throw newLoadingException(referencable, path, referencable.q() + " declares type " + resultType.q()
          + " so its native implementation result type must be "
          + mapTypeToJType(resultType).getCanonicalName() + " but it is "
          + resultJType.getCanonicalName() + ".");
    }
  }

  private static void assertNativeParameterTypesMatchesFuncParameters(
      Native nativ, Function function, JavaMethodPath path) throws LoadingNativeException {
    Parameter[] nativeParams = nativ.method().getParameters();
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

  private static void assertNativeHasOneParameter(Native nativ, Value value, JavaMethodPath path)
      throws LoadingNativeException {
    int paramCount = nativ.method().getParameters().length;
    if (paramCount != 1) {
      throw newLoadingException(value, path, value.q()
          + " has native implementation that has too many parameter(s) = " + paramCount);
    }
  }

  private static LoadingNativeException newInvalidPathException(
      Referencable referencable, JavaMethodPath path, String message) {
    return newLoadingException(referencable, path.toString(),
        "Invalid native path `" + path + "`: " + message, null);
  }

  private static LoadingNativeException newLoadingException(
      Referencable referencable, JavaMethodPath path, String message) {
    return newLoadingException(referencable, path.toString(), message, null);
  }

  private static LoadingNativeException newLoadingException(Referencable referencable,
      String path, String message, Exception e) {
    return new LoadingNativeException("Error loading native implementation for "
        + referencable.q() + " specified as `" + path + "`: " + message, e);
  }

  private static record CacheKey(Hash jarFileHash, String path) {
    public CacheKey(JarFile jarFile, String path) {
      this(jarFile.hash(), path);
    }
  }
}
