package org.smoothbuild.stdlib.java.junit;

import static java.lang.ClassLoader.getPlatformClassLoader;
import static org.smoothbuild.common.filesystem.base.PathS.path;
import static org.smoothbuild.common.reflect.ClassLoaders.mapClassLoader;
import static org.smoothbuild.stdlib.compress.UnzipHelper.filesFromJar;
import static org.smoothbuild.stdlib.compress.UnzipHelper.filesFromLibJars;
import static org.smoothbuild.stdlib.java.junit.JUnitCoreWrapper.newInstance;
import static org.smoothbuild.stdlib.java.util.JavaNaming.isClassFilePredicate;
import static org.smoothbuild.stdlib.java.util.JavaNaming.toBinaryName;
import static org.smoothbuild.virtualmachine.bytecode.helper.FileStruct.fileContent;
import static org.smoothbuild.virtualmachine.bytecode.helper.FileStruct.filePath;

import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import org.smoothbuild.common.filesystem.base.PathS;
import org.smoothbuild.stdlib.file.match.IllegalPathPatternException;
import org.smoothbuild.stdlib.file.match.PathMatcher;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.StringB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class JunitFunc {
  public static ValueB func(NativeApi nativeApi, TupleB args)
      throws IOException, BytecodeException {
    TupleB tests = (TupleB) args.get(0);
    ArrayB deps = (ArrayB) args.get(1);
    StringB include = (StringB) args.get(2);

    try {
      var filesFromTests = filesFromJar(nativeApi, tests);
      if (filesFromTests == null) {
        return null;
      }
      var filesFromDeps = filesFromLibJars(nativeApi, deps, isClassFilePredicate());
      if (filesFromDeps == null) {
        return null;
      }
      assertJunitCoreIsPresent(filesFromDeps);
      var classLoader = classLoader(concatMaps(filesFromTests, filesFromDeps));
      var jUnitCore = createJUnitCore(nativeApi, classLoader);
      var filter = createFilter(include);
      int testCount = 0;
      for (var file : filesFromTests.keySet()) {
        var path = path(filePath(filesFromTests.get(file)).toJ());
        if (filter.test(path)) {
          testCount++;
          var testClass = loadClass(classLoader, toBinaryName(file));
          var resWrapper = jUnitCore.run(testClass);
          if (!resWrapper.wasSuccessful()) {
            for (var failureWrapper : resWrapper.getFailures()) {
              nativeApi
                  .log()
                  .error("test failed: " + failureWrapper.toString() + "\n"
                      + failureWrapper.getTrace());
            }
            return nativeApi.factory().string("FAILURE");
          }
        }
      }
      if (testCount == 0) {
        nativeApi.log().warning("No junit tests found.");
      }
      return nativeApi.factory().string("SUCCESS");
    } catch (JunitException e) {
      nativeApi.log().error(e.getMessage());
      return null;
    }
  }

  private static ClassLoader classLoader(ImmutableMap<String, TupleB> filesMap) {
    return mapClassLoader(getPlatformClassLoader(), path -> {
      try {
        TupleB file = filesMap.get(path);
        return file == null ? null : fileContent(file).source().inputStream();
      } catch (BytecodeException e) {
        throw e.toIOException();
      }
    });
  }

  private static ImmutableMap<String, TupleB> concatMaps(
      Map<String, TupleB> testClasses, Map<String, TupleB> libClasses) throws JunitException {
    var allFiles = new HashMap<>(libClasses);
    for (var entry : testClasses.entrySet()) {
      if (allFiles.containsKey(entry.getKey())) {
        throw new JunitException("Both 'tests' and 'deps' contains class " + entry.getValue());
      } else {
        allFiles.put(entry.getKey(), entry.getValue());
      }
    }
    return ImmutableMap.copyOf(allFiles);
  }

  private static JUnitCoreWrapper createJUnitCore(NativeApi nativeApi, ClassLoader classLoader)
      throws JunitException {
    return newInstance(nativeApi, loadClass(classLoader, "org.junit.runner.JUnitCore"));
  }

  private static void assertJunitCoreIsPresent(Map<String, TupleB> files) throws JunitException {
    if (!files.containsKey("org/junit/runner/JUnitCore.class")) {
      throw new JunitException(
          "Cannot find org.junit.runner.JUnitCore. Is junit.jar added to 'deps'?");
    }
  }

  private static Class<?> loadClass(ClassLoader classLoader, String binaryName)
      throws JunitException {
    try {
      return classLoader.loadClass(binaryName);
    } catch (ClassNotFoundException e) {
      throw new JunitException("Couldn't find class for binaryName = " + binaryName);
    }
  }

  private static Predicate<PathS> createFilter(StringB includeParam)
      throws JunitException, BytecodeException {
    try {
      return new PathMatcher(includeParam.toJ());
    } catch (IllegalPathPatternException e) {
      throw new JunitException("Parameter 'include' has illegal value. " + e.getMessage());
    }
  }
}
