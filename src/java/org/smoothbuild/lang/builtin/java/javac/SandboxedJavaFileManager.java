package org.smoothbuild.lang.builtin.java.javac;

import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.Path.validationError;
import static org.smoothbuild.lang.type.STypes.FILE_ARRAY;

import java.io.IOException;
import java.util.Set;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;

import org.smoothbuild.io.cache.value.build.ArrayBuilder;
import org.smoothbuild.lang.builtin.java.javac.err.IncorrectClassNameGivenByJavaCompilerError;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SFile;

import com.google.common.collect.Multimap;

public class SandboxedJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
  private final NativeApi nativeApi;
  private final Multimap<String, JavaFileObject> packageToJavaFileObjects;
  private final ArrayBuilder<SFile> resultClassFiles;

  SandboxedJavaFileManager(StandardJavaFileManager fileManager, NativeApi nativeApi,
      Multimap<String, JavaFileObject> packageToJavaFileObjects) {
    super(fileManager);
    this.nativeApi = nativeApi;
    this.packageToJavaFileObjects = packageToJavaFileObjects;
    this.resultClassFiles = nativeApi.arrayBuilder(FILE_ARRAY);
  }

  public SArray<SFile> resultClassfiles() {
    return resultClassFiles.build();
  }

  @Override
  public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind,
      FileObject sibling) throws IOException {
    if (location == StandardLocation.CLASS_OUTPUT && kind == Kind.CLASS) {
      String classFilePath = className.replace('.', '/') + ".class";
      String message = validationError(classFilePath);
      if (message == null) {
        return new OutputClassFile(resultClassFiles, path(classFilePath), nativeApi);
      } else {
        throw new IncorrectClassNameGivenByJavaCompilerError(className);
      }
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
      return packageToJavaFileObjects.get(packageName);
    } else {
      return super.list(location, packageName, kinds, recurse);
    }
  }
}
