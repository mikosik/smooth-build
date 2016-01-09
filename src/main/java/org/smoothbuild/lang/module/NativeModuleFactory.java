package org.smoothbuild.lang.module;

import static java.nio.file.Files.list;
import static java.util.stream.Collectors.toList;
import static org.smoothbuild.io.util.JarFile.jarFile;
import static org.smoothbuild.lang.function.nativ.NativeFunctionFactory.nativeFunctions;
import static org.smoothbuild.util.Classes.CLASS_FILE_EXTENSION;
import static org.smoothbuild.util.Classes.binaryPathToBinaryName;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.smoothbuild.io.util.JarFile;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.function.nativ.NativeFunctionImplementationException;
import org.smoothbuild.util.ClassLoaders;

import com.google.common.collect.ImmutableMap;

public class NativeModuleFactory {
  public static ImmutableMap<Name, Function> loadNativeModulesFromDir(Path libsPath) {
    return loadNativeModules(listJars(libsPath));
  }

  private static List<Path> listJars(Path libsPath) {
    try {
      return list(libsPath).filter(path -> path.toFile().isFile()).collect(toList());
    } catch (IOException e) {
      throw new RuntimeException("IO error reading while reading from " + libsPath, e);
    }
  }

  static ImmutableMap<Name, Function> loadNativeModules(List<Path> jars) {
    Map<Name, Function> result = new HashMap<>();
    for (Path path : jars) {
      ImmutableMap<Name, Function> functions = loadNativeModule(path);
      for (Entry<Name, Function> entry : functions.entrySet()) {
        Name name = entry.getKey();
        if (result.containsKey(name)) {
          throw new IllegalArgumentException("Duplicate function " + name);
        }
        result.put(entry.getKey(), entry.getValue());
      }
    }
    return ImmutableMap.copyOf(result);
  }

  public static ImmutableMap<Name, Function> loadNativeModule(Path jarPath)
      throws NativeFunctionImplementationException {
    try {
      return createNativeModuleImpl(jarFile(jarPath));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static ImmutableMap<Name, Function> createNativeModuleImpl(JarFile jar)
      throws IOException,
      NativeFunctionImplementationException {
    Map<Name, Function> result = new HashMap<>();
    ClassLoader classLoader = classLoader(jar);
    try (JarInputStream jarInputStream = newJarInputStream(jar)) {
      JarEntry entry;
      while ((entry = jarInputStream.getNextJarEntry()) != null) {
        String fileName = entry.getName();
        if (fileName.endsWith(CLASS_FILE_EXTENSION)) {
          Class<?> clazz = load(classLoader, binaryPathToBinaryName(fileName));
          for (NativeFunction function : nativeFunctions(clazz, jar.hash())) {
            Name name = function.name();
            if (result.containsKey(name)) {
              throw new IllegalArgumentException("Duplicate function " + name);
            } else {
              result.put(name, function);
            }
          }
        }
      }
    }
    return ImmutableMap.copyOf(result);
  }

  private static ClassLoader classLoader(JarFile jar) {
    ClassLoader parentClassLoader = NativeModuleFactory.class.getClassLoader();
    return ClassLoaders.jarClassLoader(parentClassLoader, jar.path());
  }

  private static Class<?> load(ClassLoader classLoader, String binaryName) {
    try {
      return classLoader.loadClass(binaryName);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private static JarInputStream newJarInputStream(JarFile jar) throws IOException {
    return new JarInputStream(new BufferedInputStream(jar.openInputStream()));
  }
}
