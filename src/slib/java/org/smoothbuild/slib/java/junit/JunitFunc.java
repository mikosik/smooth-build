package org.smoothbuild.slib.java.junit;

import static java.lang.ClassLoader.getPlatformClassLoader;
import static java.lang.Thread.currentThread;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.smoothbuild.eval.artifact.FileStruct.fileContent;
import static org.smoothbuild.eval.artifact.FileStruct.filePath;
import static org.smoothbuild.io.fs.base.PathS.path;
import static org.smoothbuild.slib.file.match.PathMatcher.pathMatcher;
import static org.smoothbuild.slib.java.junit.JUnitCoreWrapper.newInstance;
import static org.smoothbuild.slib.java.util.JavaNaming.isClassFilePredicate;
import static org.smoothbuild.slib.java.util.JavaNaming.toBinaryName;
import static org.smoothbuild.util.reflect.ClassLoaders.mapClassLoader;

import java.io.IOException;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.io.fs.base.PathS;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.slib.file.match.IllegalPathPatternExc;
import org.smoothbuild.util.collect.DuplicatesDetector;
import org.smoothbuild.util.function.ThrowingSupplier;

import com.google.common.collect.ImmutableMap;

import net.lingala.zip4j.exception.ZipException;

public class JunitFunc {
  public static StringB func(NativeApi nativeApi, TupleB tests, ArrayB deps, StringB include)
      throws IOException {
    try {
      var filesFromTests = filesFromJar(nativeApi, tests, "tests");
      var filesFromDeps = filesFromLibJars(nativeApi, deps);
      assertJunitCoreIsPresent(filesFromDeps);
      var allFiles = concatMaps(filesFromTests, filesFromDeps);

      var classLoader = classLoader(allFiles);
      return withClassLoader(classLoader, () -> {
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
      });
    } catch (JunitExc e) {
      nativeApi.log().error(e.getMessage());
      return null;
    }
  }

  private static URLClassLoader classLoader(ImmutableMap<String, TupleB> filesMap) {
    return mapClassLoader(getPlatformClassLoader(), path -> {
      TupleB file = filesMap.get(path);
      return file == null ? null : fileContent(file).source().inputStream();
    });
  }

  private static <T> T withClassLoader(ClassLoader classLoader,
      ThrowingSupplier<T, JunitExc> supplier) throws JunitExc {
    var currentThread = currentThread();
    var previousClassLoader = currentThread.getContextClassLoader();
    currentThread.setContextClassLoader(classLoader);
    try {
      return supplier.get();
    } finally {
      currentThread.setContextClassLoader(previousClassLoader);
    }
  }

  public static Map<String, TupleB> filesFromLibJars(NativeApi nativeApi, ArrayB libJars)
      throws IOException, JunitExc {
    var duplicatesDetector = new DuplicatesDetector<String>();
    var result = new HashMap<String, TupleB>();
    var jars = libJars.elems(TupleB.class);
    for (int i = 0; i < jars.size(); i++) {
      var jarFile = jars.get(i);
      var classes = filesFromJar(nativeApi, jarFile, "deps[" + i + "]");
      for (var entry : classes.entrySet()) {
        var path = entry.getKey();
        if (duplicatesDetector.addValue(path)) {
          throw new JunitExc(
              "File " + path + " is contained by two different library jar files.");
        } else {
          result.put(path, entry.getValue());
        }
      }
    }
    return result;
  }

  private static Map<String, TupleB> filesFromJar(NativeApi nativeApi, TupleB jarFile,
      String jarName) throws IOException, JunitExc {
    try {
      return filesFromJar(nativeApi, jarFile);
    } catch (ZipException e) {
      throw new JunitExc("Cannot read archive from '" + jarName
          + "' param. Corrupted data? Internal message: " + e.getMessage());
    }
  }

  private static Map<String, TupleB> filesFromJar(NativeApi nativeApi, TupleB jarFile)
      throws IOException {
    ArrayB unzipped = nativeApi.unzipper().unzip(fileContent(jarFile), isClassFilePredicate());
    return unzipped.elems(TupleB.class)
        .stream()
        .collect(toMap(f -> filePath(f).toJ(), identity()));
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
