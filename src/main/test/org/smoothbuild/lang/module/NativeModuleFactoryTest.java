package org.smoothbuild.lang.module;

import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.util.Classes.binaryPath;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.util.Classes;

import com.google.common.io.ByteStreams;

public class NativeModuleFactoryTest {
  private Map<Name, Function> module;

  @Test
  public void module_with_zero_functions_is_allowed() throws Exception {
    given(module = createNativeModule(ModuleWithNoFunctions.class));
    when(module.keySet());
    thenReturned(Matchers.emptyIterable());
  }

  public static class ModuleWithNoFunctions {}

  @Test
  public void available_names_contains_all_function_names() throws Exception {
    given(module = createNativeModule(ModuleWithTwoFunctions.class));
    when(module.keySet());
    thenReturned(contains(name("func1"), name("func2")));
  }

  public static class ModuleWithTwoFunctions {
    public interface Parameters {}

    @SmoothFunction
    public static SString func1(Container container) {
      return null;
    }

    @SmoothFunction
    public static SString func2(Container container) {
      return null;
    }
  }

  public void two_functions_with_same_name_are_forbidden() throws Exception {
    when(() -> createNativeModule(ModuleAWithFuncA.class, ModuleBWithFuncA.class));
    thenThrown(IllegalArgumentException.class);
  }

  public static class ModuleAWithFuncA {
    @SmoothFunction
    public static SString funcA(Container container) {
      return null;
    }
  }

  public static class ModuleBWithFuncA {
    @SmoothFunction
    public static SString funcA(Container container) {
      return null;
    }
  }

  public static Map<Name, Function> createNativeModule(Class<?>... classes) throws Exception {
    File tempJarFile = File.createTempFile("tmp", ".jar");
    try (JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(tempJarFile))) {
      for (Class<?> clazz : classes) {
        jarOutputStream.putNextEntry(new ZipEntry(binaryPath(clazz)));
        try (InputStream byteCodeInputStream = Classes.byteCodeAsInputStream(clazz)) {
          ByteStreams.copy(byteCodeInputStream, jarOutputStream);
        }
      }
    }

    return NativeModuleFactory.createNativeModule(tempJarFile.toPath());
  }
}
