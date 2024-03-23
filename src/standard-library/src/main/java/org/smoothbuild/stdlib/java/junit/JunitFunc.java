package org.smoothbuild.stdlib.java.junit;

import static java.lang.ClassLoader.getPlatformClassLoader;
import static org.smoothbuild.common.bucket.base.Path.path;
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
import org.smoothbuild.common.bucket.base.Path;
import org.smoothbuild.stdlib.file.match.IllegalPathPatternException;
import org.smoothbuild.stdlib.file.match.PathMatcher;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class JunitFunc {
  public static BValue func(NativeApi nativeApi, BTuple args)
      throws IOException, BytecodeException {
    BTuple tests = (BTuple) args.get(0);
    BArray deps = (BArray) args.get(1);
    BString include = (BString) args.get(2);

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
        var path = path(filePath(filesFromTests.get(file)).toJavaString());
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

  private static ClassLoader classLoader(ImmutableMap<String, BTuple> filesMap) {
    return mapClassLoader(getPlatformClassLoader(), path -> {
      try {
        BTuple file = filesMap.get(path);
        return file == null ? null : fileContent(file).source().inputStream();
      } catch (BytecodeException e) {
        throw e.toIOException();
      }
    });
  }

  private static ImmutableMap<String, BTuple> concatMaps(
      Map<String, BTuple> testClasses, Map<String, BTuple> libClasses) throws JunitException {
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

  private static void assertJunitCoreIsPresent(Map<String, BTuple> files) throws JunitException {
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

  private static Predicate<Path> createFilter(BString includeParam)
      throws JunitException, BytecodeException {
    try {
      return new PathMatcher(includeParam.toJavaString());
    } catch (IllegalPathPatternException e) {
      throw new JunitException("Parameter 'include' has illegal value. " + e.getMessage());
    }
  }
}
