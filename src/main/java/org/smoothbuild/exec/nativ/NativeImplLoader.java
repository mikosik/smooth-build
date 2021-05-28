package org.smoothbuild.exec.nativ;

import static java.util.Arrays.stream;
import static org.smoothbuild.exec.nativ.MapTypeToJType.mapTypeToJType;
import static org.smoothbuild.io.util.JarFile.jarFile;
import static org.smoothbuild.plugin.Caching.Scope.MACHINE;
import static org.smoothbuild.util.reflect.Classes.loadClass;
import static org.smoothbuild.util.reflect.Methods.getAnnotation;
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
import org.smoothbuild.exec.nativ.JavaMethodPath.JavaMethodPathParsingException;
import org.smoothbuild.install.FullPathResolver;
import org.smoothbuild.io.util.JarFile;
import org.smoothbuild.lang.base.define.Function;
import org.smoothbuild.lang.base.define.Item;
import org.smoothbuild.lang.base.define.NativeBody;
import org.smoothbuild.lang.base.define.Referencable;
import org.smoothbuild.lang.base.define.Value;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.plugin.Caching;
import org.smoothbuild.plugin.Caching.Scope;
import org.smoothbuild.plugin.NativeApi;

@Singleton
public class NativeImplLoader {
  private final FullPathResolver pathResolver;
  private final HashMap<CacheKey, Native> cache;

  @Inject
  public NativeImplLoader(FullPathResolver pathResolver) {
    this.pathResolver = pathResolver;
    this.cache = new HashMap<>();
  }

  public synchronized Native loadNative(Function function) throws LoadingNativeImplException {
    JavaMethodPath path = parseMethodPath(function, ((NativeBody) function.body()).implementedBy());
    Native nativ = loadNativeImpl(function, path);
    assertNativeResultMatchesDeclared(function, nativ, function.type().resultType(), path);
    assertNativeParameterTypesMatchesFuncParameters(nativ, function, path);
    return nativ;
  }

  public synchronized Native loadNative(Value value) throws LoadingNativeImplException {
    JavaMethodPath path = parseMethodPath(value, ((NativeBody) value.body()).implementedBy());
    Native nativ = loadNativeImpl(value, path);
    assertNativeResultMatchesDeclared(value, nativ, value.type(), path);
    assertNativeHasOneParameter(nativ, value, path);
    return nativ;
  }

  private JavaMethodPath parseMethodPath(Referencable referencable, String path)
      throws LoadingNativeImplException {
    try {
      return JavaMethodPath.parse(path);
    } catch (JavaMethodPathParsingException e) {
      throw newLoadingException(referencable, path, e.getMessage(), e);
    }
  }

  private Native loadNativeImpl(Referencable referencable, JavaMethodPath path)
      throws LoadingNativeImplException {
    JarFile jarFile = jarFileChained(referencable, path);
    CacheKey key = new CacheKey(jarFile, path.toString());
    Native nativ = cache.get(key);
    if (nativ == null) {
      Method method = findMethod(referencable, jarFile, path);
      Scope cachingScope = getAnnotation(method, Caching.class).map(Caching::scope).orElse(MACHINE);
      nativ = new Native(method, cachingScope, jarFile);
      cache.put(key, nativ);
    }
    return nativ;
  }

  private JarFile jarFileChained(Referencable referencable, JavaMethodPath path)
      throws LoadingNativeImplException {
    Path jarPath = pathResolver.resolve(referencable.location().module().toNative());
    try {
      return jarFile(jarPath);
    } catch (FileNotFoundException e) {
      throw newLoadingException(referencable, path, "Cannot find file '" + jarPath + "'.", e);
    } catch (IOException e) {
      throw newLoadingException(referencable, path, "Error reading file '" + jarPath + "'.", e);
    }
  }

  private Method findMethod(Referencable referencable, JarFile jarFile, JavaMethodPath path)
      throws LoadingNativeImplException {
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
      JavaMethodPath methodPath) throws LoadingNativeImplException {
    String methodName = methodPath.methodName();
    Class<?> clazz = findClass(referencable, jarFile, methodPath);
    return stream(clazz.getDeclaredMethods())
        .filter(m -> m.getName().equals(methodName))
        .findFirst()
        .orElseThrow(() -> newInvalidPathException(referencable, methodPath, "Class '" +
            clazz.getCanonicalName() + "' does not have '" + methodName + "' method."));
  }

  private Class<?> findClass(Referencable referencable, JarFile jarFile, JavaMethodPath methodPath)
      throws LoadingNativeImplException {
    try {
      return loadClass(jarFile.path(), methodPath.classBinaryName());
    } catch (ClassNotFoundException e) {
      throw newInvalidPathException(referencable, methodPath, "Class '"
          + methodPath.classBinaryName() + "' does not exist in jar '" + jarFile.path() + "'.");
    }
  }

  private static boolean hasContainerParameter(Method method) {
    Class<?>[] types = method.getParameterTypes();
    return types.length != 0 && (types[0] == NativeApi.class || types[0] == Container.class);
  }

  private void assertNativeResultMatchesDeclared(Referencable referencable, Native nativ,
      Type resultType, JavaMethodPath path) throws LoadingNativeImplException {
    Method method = nativ.method();
    Class<?> resultJType = method.getReturnType();
    if (!mapTypeToJType(resultType).equals(resultJType)) {
      throw newLoadingException(referencable, path, referencable.q() + " declares type " + resultType.q()
          + " so its native implementation result type must be "
          + mapTypeToJType(resultType).getCanonicalName() + " but it is "
          + resultJType.getCanonicalName() + ".");
    }
  }

  private void assertNativeParameterTypesMatchesFuncParameters(Native nativ, Function function,
      JavaMethodPath path) throws LoadingNativeImplException {
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

  private void assertNativeHasOneParameter(Native nativ, Value value, JavaMethodPath path)
      throws LoadingNativeImplException {
    int paramCount = nativ.method().getParameters().length;
    if (paramCount != 1) {
      throw newLoadingException(value, path, value.q()
          + " has native implementation that has too many parameter(s) = " + paramCount);
    }
  }

  private static LoadingNativeImplException newInvalidPathException(
      Referencable referencable, JavaMethodPath path, String message) {
    return newLoadingException(
        referencable, path, "Invalid native path `" + path + "`: " + message, null);
  }

  private static LoadingNativeImplException newLoadingException(
      Referencable referencable, JavaMethodPath path, String message) {
    return newLoadingException(referencable, path, message, null);
  }

  private static LoadingNativeImplException newLoadingException(
      Referencable referencable, JavaMethodPath path, String message, Exception e) {
    return newLoadingException(referencable, path.toString(), message, e);
  }

  private static LoadingNativeImplException newLoadingException(Referencable referencable,
      String path, String message, Exception e) {
    return new LoadingNativeImplException("Error loading native implementation for `"
        + referencable.name() + "` specified as `" + path + "`: " + message, e);
  }

  private static record CacheKey(Hash jarFileHash, String path) {
    public CacheKey(JarFile jarFile, String path) {
      this(jarFile.hash(), path);
    }
  }
}
