package org.smoothbuild.stdlib.java.javac;

import static org.smoothbuild.common.filesystem.base.Path.path;

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
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.filesystem.base.Path;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArrayBuilder;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class SandboxedJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
  private final NativeApi nativeApi;
  private final Map<String, Set<JavaFileObject>> packageToJavaFileObjects;
  private final BArrayBuilder resClassFiles;

  SandboxedJavaFileManager(
      StandardJavaFileManager fileManager, NativeApi nativeApi, Iterable<InputClassFile> objects)
      throws BytecodeException {
    super(fileManager);
    this.nativeApi = nativeApi;
    this.packageToJavaFileObjects = groupIntoPackages(objects);
    this.resClassFiles =
        nativeApi.factory().arrayBuilderWithElements(nativeApi.factory().fileType());
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

  public BArray resultClassfiles() throws BytecodeException {
    return resClassFiles.build();
  }

  @Override
  public JavaFileObject getJavaFileForOutput(
      Location location, String className, Kind kind, FileObject sibling) throws IOException {
    try {
      if (location == StandardLocation.CLASS_OUTPUT && kind == Kind.CLASS) {
        Path classFilePath = path(className.replace('.', '/') + ".class");
        return new OutputClassFile(resClassFiles, classFilePath, nativeApi);
      } else {
        return super.getJavaFileForOutput(location, className, kind, sibling);
      }
    } catch (BytecodeException e) {
      throw e.toIOException();
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
  public Iterable<JavaFileObject> list(
      Location location, String packageName, Set<Kind> kinds, boolean recurse) throws IOException {
    if (location == StandardLocation.CLASS_PATH) {
      if (recurse) {
        throw new UnsupportedOperationException(
            "recurse is not supported by SandboxedJavaFileManager.list()");
      }
      Set<JavaFileObject> result = packageToJavaFileObjects.get(packageName);
      return result == null ? List.list() : result;
    } else {
      return super.list(location, packageName, kinds, recurse);
    }
  }
}
