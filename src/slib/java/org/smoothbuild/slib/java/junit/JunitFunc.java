package org.smoothbuild.slib.java.junit;

import static java.lang.ClassLoader.getPlatformClassLoader;
import static org.smoothbuild.fs.base.PathS.path;
import static org.smoothbuild.run.eval.FileStruct.fileContent;
import static org.smoothbuild.run.eval.FileStruct.filePath;
import static org.smoothbuild.slib.compress.UnzipHelper.filesFromJar;
import static org.smoothbuild.slib.compress.UnzipHelper.filesFromLibJars;
import static org.smoothbuild.slib.file.match.PathMatcher.pathMatcher;
import static org.smoothbuild.slib.java.junit.JUnitCoreWrapper.newInstance;
import static org.smoothbuild.slib.java.util.JavaNaming.toBinaryName;
import static org.smoothbuild.util.reflect.ClassLoaders.mapClassLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.smoothbuild.bytecode.obj.cnst.ArrayB;
import org.smoothbuild.bytecode.obj.cnst.StringB;
import org.smoothbuild.bytecode.obj.cnst.TupleB;
import org.smoothbuild.fs.base.PathS;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.slib.file.match.IllegalPathPatternExc;

import com.google.common.collect.ImmutableMap;

public class JunitFunc {
  public static StringB func(NativeApi nativeApi, TupleB args) throws IOException {
    TupleB tests = (TupleB) args.get(0);
    ArrayB deps = (ArrayB) args.get(1);
    StringB include = (StringB) args.get(2);

    try {
      var filesFromTests = filesFromJar(nativeApi, tests);
      if (filesFromTests == null) {
        return null;
      }
      var filesFromDeps = filesFromLibJars(nativeApi, deps);
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
              nativeApi.log().error(
                  "test failed: " + failureWrapper.toString() + "\n" + failureWrapper.getTrace());
            }
            return nativeApi.factory().string("FAILURE");
          }
        }
      }
      if (testCount == 0) {
        nativeApi.log().warning("No junit tests found.");
      }
      return nativeApi.factory().string("SUCCESS");
    } catch (JunitExc e) {
      nativeApi.log().error(e.getMessage());
      return null;
    }
  }

  private static ClassLoader classLoader(ImmutableMap<String, TupleB> filesMap) {
    return mapClassLoader(getPlatformClassLoader(), path -> {
      TupleB file = filesMap.get(path);
      return file == null ? null : fileContent(file).source().inputStream();
    });
  }

  private static ImmutableMap<String, TupleB> concatMaps(Map<String, TupleB> testClasses,
      Map<String, TupleB> libClasses) throws JunitExc {
    var allFiles = new HashMap<>(libClasses);
    for (var entry : testClasses.entrySet()) {
      if (allFiles.containsKey(entry.getKey())) {
        throw new JunitExc("Both 'tests' and 'deps' contains class " + entry.getValue());
      } else {
        allFiles.put(entry.getKey(), entry.getValue());
      }
    }
    return ImmutableMap.copyOf(allFiles);
  }

  private static JUnitCoreWrapper createJUnitCore(NativeApi nativeApi, ClassLoader classLoader)
      throws JunitExc {
    return newInstance(nativeApi, loadClass(classLoader, "org.junit.runner.JUnitCore"));
  }

  private static void assertJunitCoreIsPresent(Map<String, TupleB> files) throws JunitExc {
    if (!files.containsKey("org/junit/runner/JUnitCore.class")) {
      throw new JunitExc(
          "Cannot find org.junit.runner.JUnitCore. Is junit.jar added to 'deps'?");
    }
  }

  private static Class<?> loadClass(ClassLoader classLoader, String binaryName)
      throws JunitExc {
    try {
      return classLoader.loadClass(binaryName);
    } catch (ClassNotFoundException e) {
      throw new JunitExc("Couldn't find class for binaryName = " + binaryName);
    }
  }

  private static Predicate<PathS> createFilter(StringB includeParam) throws JunitExc {
    try {
      return pathMatcher(includeParam.toJ());
    } catch (IllegalPathPatternExc e) {
      throw new JunitExc("Parameter 'include' has illegal value. " + e.getMessage());
    }
  }
}
