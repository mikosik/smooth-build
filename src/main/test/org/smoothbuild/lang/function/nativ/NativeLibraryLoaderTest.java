package org.smoothbuild.lang.function.nativ;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.lang.function.nativ.NativeLibraryLoader.loadNativeModules;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.util.Classes;

public class NativeLibraryLoaderTest {
  @Test
  public void module_with_zero_functions_is_allowed() throws Exception {
    when(loadNativeModules(asList(module(ModuleWithNoFunctions.class))));
    thenReturned(Matchers.emptyIterable());
  }

  public static class ModuleWithNoFunctions {}

  @Test
  public void all_functions_are_loaded() throws Exception {
    when(toNames(loadNativeModules(asList(module(TwoFunctions.class)))));
    thenReturned(contains(new Name("func1"), new Name("func2")));
  }

  public static class TwoFunctions {
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

  @Test
  public void loading_module_with_two_functions_with_the_same_name_fails() throws Exception {
    when(() -> loadNativeModules(asList(module(FunctionA.class, FunctionA2.class))));
    thenThrown(IllegalArgumentException.class);
  }

  @Test
  public void loading_two_modules_having_function_with_the_same_name_fails() throws Exception {
    when(() -> loadNativeModules(asList(module(FunctionA.class), module(FunctionA2.class))));
    thenThrown(IllegalArgumentException.class);
  }

  public static class FunctionA {
    @SmoothFunction
    public static SString funcA(Container container) {
      return null;
    }
  }

  public static class FunctionA2 {
    @SmoothFunction
    public static SString funcA(Container container) {
      return null;
    }
  }

  private static Path module(Class<?>... classes) throws IOException,
      FileNotFoundException {
    File tempJarFile = File.createTempFile("tmp", ".jar");
    Classes.saveBytecodeInJar(tempJarFile, classes);
    return tempJarFile.toPath();
  }

  private static List<Name> toNames(Collection<Function> functions) {
    return functions.stream().map(Function::name).collect(toList());
  }
}
