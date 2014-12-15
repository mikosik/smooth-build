package org.smoothbuild.lang.module;

import static org.smoothbuild.io.util.JarFile.jarFile;
import static org.smoothbuild.lang.function.nativ.NativeFunctionFactory.createNativeFunctions;
import static org.smoothbuild.util.Classes.CLASS_FILE_EXTENSION;
import static org.smoothbuild.util.Classes.binaryPathToBinaryName;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.smoothbuild.io.util.JarFile;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.function.nativ.err.NativeImplementationException;
import org.smoothbuild.util.ClassLoaders;

public class NativeModuleFactory {
  public static Module createNativeModule(Path jarPath) throws NativeImplementationException {
    try {
      return createNativeModuleImpl(jarFile(jarPath));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static Module createNativeModuleImpl(JarFile jar) throws IOException,
  NativeImplementationException {
    ModuleBuilder builder = new ModuleBuilder();
    ClassLoader classLoader = classLoader(jar);
    try (JarInputStream jarInputStream = newJarInputStream(jar)) {
      JarEntry entry;
      while ((entry = jarInputStream.getNextJarEntry()) != null) {
        String fileName = entry.getName();
        if (fileName.endsWith(CLASS_FILE_EXTENSION)) {
          Class<?> clazz = load(classLoader, binaryPathToBinaryName(fileName));
          for (NativeFunction function : createNativeFunctions(jar.hash(), clazz)) {
            builder.addFunction(function);
          }
        }
      }
    }
    return builder.build();
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
