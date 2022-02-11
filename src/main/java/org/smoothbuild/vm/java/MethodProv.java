package org.smoothbuild.vm.java;

import static java.util.Arrays.asList;
import static org.smoothbuild.util.collect.Lists.filter;
import static org.smoothbuild.util.collect.Maps.computeIfAbsent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.bytecode.obj.val.BlobB;

/**
 * This class is thread-safe.
 */
@Singleton
public class MethodProv {
  private final ClassLoaderProv classLoaderProv;
  private final HashMap<MethodSpec, Method> methodCache;

  @Inject
  public MethodProv(ClassLoaderProv classLoaderProv) {
    this.classLoaderProv = classLoaderProv;
    this.methodCache = new HashMap<>();
  }

  public synchronized Method provide(BlobB jar, String classBinaryName, String methodName)
      throws MethodProvExc {
    var methodSpec = new MethodSpec(jar, classBinaryName, methodName);
    return computeIfAbsent(methodCache, methodSpec, n -> findMethod(methodSpec));
  }

  private Method findMethod(MethodSpec methodSpec) throws MethodProvExc {
    var clazz = findClass(methodSpec);
    var declaredMethods = asList(clazz.getDeclaredMethods());
    var methods = filter(declaredMethods, m -> m.getName().equals(methodSpec.methodName()));
    return switch (methods.size()) {
      case 0 -> throw newMissingMethodExc(methodSpec);
      case 1 -> methods.get(0);
      default -> throw newOverloadedMethodExc(methodSpec);
    };
  }

  private Class<?> findClass(MethodSpec methodSpec) throws MethodProvExc {
    try {
      var classLoader = classLoaderProv.classLoaderFor(methodSpec.jar());
      return classLoader.loadClass(methodSpec.classBinaryName());
    } catch (ClassNotFoundException e) {
      throw new MethodProvExc("Class not found in jar.");
    } catch (FileNotFoundException | ClassLoaderProvExc e) {
      throw new MethodProvExc(e.getMessage(), e);
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  private static MethodProvExc newMissingMethodExc(MethodSpec methodSpec) {
    return new MethodProvExc("Class '" + methodSpec.classBinaryName() + "' does not have '"
        + methodSpec.methodName() + "' method.");
  }

  private static MethodProvExc newOverloadedMethodExc(MethodSpec methodSpec) {
    return new MethodProvExc("Class '" + methodSpec.classBinaryName + "' has more than one '"
        + methodSpec.methodName() +  "' method.");
  }

  private record MethodSpec(BlobB jar, String classBinaryName, String methodName) {}
}
