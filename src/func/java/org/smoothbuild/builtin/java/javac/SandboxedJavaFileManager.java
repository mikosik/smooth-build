package org.smoothbuild.builtin.java.javac;

import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.Path.validationError;

import java.io.IOException;
import java.util.Set;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;

import org.smoothbuild.builtin.java.javac.err.IncorrectClassNameGivenByJavaCompilerError;
import org.smoothbuild.lang.base.Array;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.ValueFactory;

import com.google.common.collect.Multimap;

public class SandboxedJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
  private final ValueFactory valueFactory;
  private final Multimap<String, JavaFileObject> packageToJavaFileObjects;
  private final ArrayBuilder<SFile> resultClassFiles;

  SandboxedJavaFileManager(StandardJavaFileManager fileManager, ValueFactory valueFactory,
      Multimap<String, JavaFileObject> packageToJavaFileObjects) {
    super(fileManager);
    this.valueFactory = valueFactory;
    this.packageToJavaFileObjects = packageToJavaFileObjects;
    this.resultClassFiles = valueFactory.arrayBuilder(SFile.class);
  }

  public Array<SFile> resultClassfiles() {
    return resultClassFiles.build();
  }

  @Override
  public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind,
      FileObject sibling) throws IOException {
    if (location == StandardLocation.CLASS_OUTPUT && kind == Kind.CLASS) {
      String classFilePath = className.replace('.', '/') + ".class";
      String message = validationError(classFilePath);
      if (message == null) {
        return new OutputClassFile(resultClassFiles, path(classFilePath), valueFactory);
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
