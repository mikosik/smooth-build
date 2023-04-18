package org.smoothbuild.stdlib.java.javac;

import static org.smoothbuild.fs.base.PathS.path;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;

import org.smoothbuild.fs.base.PathS;
import org.smoothbuild.util.collect.Lists;
import org.smoothbuild.vm.bytecode.expr.value.ArrayB;
import org.smoothbuild.vm.bytecode.expr.value.ArrayBBuilder;
import org.smoothbuild.vm.evaluate.plugin.NativeApi;

public class SandboxedJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
  private final NativeApi nativeApi;
  private final Map<String, Set<JavaFileObject>> packageToJavaFileObjects;
  private final ArrayBBuilder resClassFiles;

  SandboxedJavaFileManager(StandardJavaFileManager fileManager, NativeApi nativeApi,
      Iterable<InputClassFile> objects) {
    super(fileManager);
    this.nativeApi = nativeApi;
    this.packageToJavaFileObjects = groupIntoPackages(objects);
    this.resClassFiles = nativeApi.factory().arrayBuilderWithElems(nativeApi.factory().fileT());
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

  public ArrayB resultClassfiles() {
    return resClassFiles.build();
  }

  @Override
  public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind,
      FileObject sibling) throws IOException {
    if (location == StandardLocation.CLASS_OUTPUT && kind == Kind.CLASS) {
      PathS classFilePath = path(className.replace('.', '/') + ".class");
      return new OutputClassFile(resClassFiles, classFilePath, nativeApi);
    } else {
      return super.getJavaFileForOutput(location, className, kind, sibling);
    }
  }

  @Override
  public String inferBinaryName(Location location, JavaFileObject file) {
    if (file instanceof InputClassFile inputClassFile) {
      return inputClassFile.binaryName();
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
      return result == null ? Lists.list() : result;
    } else {
      return super.list(location, packageName, kinds, recurse);
    }
  }
}
