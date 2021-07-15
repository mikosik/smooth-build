package org.smoothbuild.exec.java;

import static java.util.Arrays.stream;
import static org.smoothbuild.exec.java.MapTypeToJType.mapTypeToJType;
import static org.smoothbuild.util.reflect.Classes.loadClass;
import static org.smoothbuild.util.reflect.Methods.isPublic;
import static org.smoothbuild.util.reflect.Methods.isStatic;

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
import org.smoothbuild.exec.java.JavaMethodPath.JavaMethodPathParsingException;
import org.smoothbuild.io.fs.space.JPathResolver;
import org.smoothbuild.io.util.JarFile;
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
public class JavaCodeLoader {
  private final HashMap<CacheKey, JavaCode> javaCodeCache;
  private final HashMap<Hash, JarFile> jarFileCache;
  private final JPathResolver jPathResolver;

  @Inject
  public JavaCodeLoader(JPathResolver jPathResolver) {
    this.jPathResolver = jPathResolver;
    this.javaCodeCache = new HashMap<>();
    this.jarFileCache = new HashMap<>();
  }

  public synchronized void storeJarFile(JarFile jarFile) {
    jarFileCache.put(jarFile.hash(), jarFile);
  }

  public synchronized JavaCode load(RealFunction function, String methodPath, Hash jarHash)
      throws LoadingJavaCodeException {
    JavaMethodPath path = parseMethodPath(function, methodPath);
    JavaCode javaCode = loadNativeImpl(function, path, jarHash);
    assertNativeResultMatchesDeclared(function, javaCode, function.type().resultType(), path);
    assertNativeParameterTypesMatchesFuncParameters(javaCode, function, path);
    return javaCode;
  }

  public synchronized JavaCode load(Value value, String methodPath, Hash contentHash)
      throws LoadingJavaCodeException {
    JavaMethodPath path = parseMethodPath(value, methodPath);
    JavaCode javaCode = loadNativeImpl(value, path, contentHash);
    assertNativeResultMatchesDeclared(value, javaCode, value.type(), path);
    assertNativeHasOneParameter(javaCode, value, path);
    return javaCode;
  }

  private static JavaMethodPath parseMethodPath(Referencable referencable, String path)
      throws LoadingJavaCodeException {
    try {
      return JavaMethodPath.parse(path);
    } catch (JavaMethodPathParsingException e) {
      throw newLoadingException(referencable, path, e.getMessage(), e);
    }
  }

  private JavaCode loadNativeImpl(Referencable referencable, JavaMethodPath path, Hash contentHash)
      throws LoadingJavaCodeException {
    JarFile jarFile = jarFileCache.get(contentHash);
    CacheKey key = new CacheKey(jarFile, path.toString());
    JavaCode nativ = javaCodeCache.get(key);
    if (nativ == null) {
      Method method = findMethod(referencable, jarFile, path);
      nativ = new JavaCode(method, jarFile);
      javaCodeCache.put(key, nativ);
    }
    return nativ;
  }

  private Method findMethod(Referencable referencable, JarFile jarFile, JavaMethodPath path)
      throws LoadingJavaCodeException {
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

  private Method findClassMethod(Referencable referencable, JarFile jarFile,
      JavaMethodPath methodPath) throws LoadingJavaCodeException {
    String methodName = methodPath.methodName();
    Class<?> clazz = findClass(referencable, jarFile, methodPath);
    return stream(clazz.getDeclaredMethods())
        .filter(m -> m.getName().equals(methodName))
        .findFirst()
        .orElseThrow(() -> newInvalidPathException(referencable, methodPath, "Class '" +
            clazz.getCanonicalName() + "' does not have '" + methodName + "' method."));
  }

  private Class<?> findClass(Referencable referencable, JarFile jarFile,
      JavaMethodPath methodPath) throws LoadingJavaCodeException {
      Path jarPath = jPathResolver.resolve(jarFile.filePath());
    try {
      return loadClass(jarPath, methodPath.classBinaryName());
    } catch (ClassNotFoundException e) {
      throw newInvalidPathException(referencable, methodPath,
          "Class '" + methodPath.classBinaryName() + "' does not exist in jar "
          + jarFile.filePath().q() + ".");
    }
  }

  private static boolean hasContainerParameter(Method method) {
    Class<?>[] types = method.getParameterTypes();
    return types.length != 0 && (types[0] == NativeApi.class || types[0] == Container.class);
  }

  private static void assertNativeResultMatchesDeclared(Referencable referencable,
      JavaCode javaCode, Type resultType, JavaMethodPath path) throws LoadingJavaCodeException {
    Method method = javaCode.method();
    Class<?> resultJType = method.getReturnType();
    if (!mapTypeToJType(resultType).equals(resultJType)) {
      throw newLoadingException(referencable, path, referencable.q() + " declares type " + resultType.q()
          + " so its native implementation result type must be "
          + mapTypeToJType(resultType).getCanonicalName() + " but it is "
          + resultJType.getCanonicalName() + ".");
    }
  }

  private static void assertNativeParameterTypesMatchesFuncParameters(JavaCode javaCode,
      RealFunction function, JavaMethodPath path) throws LoadingJavaCodeException {
    Parameter[] nativeParams = javaCode.method().getParameters();
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

  private static void assertNativeHasOneParameter(
      JavaCode javaCode, Value value, JavaMethodPath path) throws LoadingJavaCodeException {
    int paramCount = javaCode.method().getParameters().length;
    if (paramCount != 1) {
      throw newLoadingException(value, path, value.q()
          + " has native implementation that has too many parameter(s) = " + paramCount);
    }
  }

  private static LoadingJavaCodeException newInvalidPathException(
      Referencable referencable, JavaMethodPath path, String message) {
    return newLoadingException(referencable, path.toString(),
        "Invalid native path `" + path + "`: " + message, null);
  }

  private static LoadingJavaCodeException newLoadingException(
      Referencable referencable, JavaMethodPath path, String message) {
    return newLoadingException(referencable, path.toString(), message, null);
  }

  private static LoadingJavaCodeException newLoadingException(Referencable referencable,
      String path, String message, Exception e) {
    return new LoadingJavaCodeException("Error loading native implementation for "
        + referencable.q() + " specified as `" + path + "`: " + message, e);
  }

  private static record CacheKey(Hash jarFileHash, String path) {
    public CacheKey(JarFile jarFile, String path) {
      this(jarFile.hash(), path);
    }
  }
}
