package org.smoothbuild.builtin.java.javac;

import static org.smoothbuild.io.fs.base.Path.path;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.ArrayBuilder;
import org.smoothbuild.lang.plugin.NativeApi;

public class SandboxedJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
  private final NativeApi nativeApi;
  private final Map<String, Set<JavaFileObject>> packageToJavaFileObjects;
  private final ArrayBuilder resultClassFiles;

  SandboxedJavaFileManager(StandardJavaFileManager fileManager, NativeApi nativeApi,
      Iterable<InputClassFile> objects) {
    super(fileManager);
    this.nativeApi = nativeApi;
    this.packageToJavaFileObjects = groupIntoPackages(objects);
    this.resultClassFiles = nativeApi.factory().arrayBuilder(nativeApi.factory().fileType());
  }

  private static Map<String, Set<JavaFileObject>> groupIntoPackages(
      Iterable<InputClassFile> objects) {
    HashMap<String, Set<JavaFileObject>> result = new HashMap<>();
    for (InputClassFile object : objects) {
      String packageName = object.aPackage();
      Set<JavaFileObject> aPackage = result.computeIfAbsent(packageName, k -> new HashSet<>());
      aPackage.add(object);
    }
    return result;
  }

  public Array resultClassfiles() {
    return resultClassFiles.build();
  }

  @Override
  public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind,
      FileObject sibling) throws IOException {
    if (location == StandardLocation.CLASS_OUTPUT && kind == Kind.CLASS) {
      Path classFilePath = path(className.replace('.', '/') + ".class");
      return new OutputClassFile(resultClassFiles, classFilePath, nativeApi);
    } else {
      return super.getJavaFileForOutput(location, className, kind, sibling);
    }
  }

  @Override
  public String inferBinaryName(Location location, JavaFileObject file) {
    if (file instanceof InputClassFile) {
      return ((InputClassFile) file).binaryName();
    } else {
      return super.inferBinaryName(location, file);
    }
  }

  @Override
  public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds,
      boolean recurse) throws IOException {
    if (location == StandardLocation.CLASS_PATH) {
      if (recurse) {
        throw new UnsupportedOperationException(
            "recurse is not supported by SandboxedJavaFileManager.list()");
      }
      Set<JavaFileObject> result = packageToJavaFileObjects.get(packageName);
      return result == null ? List.of() : result;
    } else {
      return super.list(location, packageName, kinds, recurse);
    }
  }
}
