package org.smoothbuild.builtin.java.javac;

import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.fs.base.Path.validationError;

import java.io.IOException;
import java.util.Set;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;

import org.smoothbuild.builtin.java.javac.err.IncorrectClassNameGivenByJavaCompilerError;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.plugin.FileSet;
import org.smoothbuild.plugin.FileSetBuilder;
import org.smoothbuild.plugin.Sandbox;

import com.google.common.collect.Multimap;

public class SandboxedJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
  private final Sandbox sandbox;
  private final Multimap<String, JavaFileObject> packageToJavaFileObjects;
  private final FileSetBuilder resultClassFiles;

  SandboxedJavaFileManager(StandardJavaFileManager fileManager, Sandbox sandbox,
      Multimap<String, JavaFileObject> packageToJavaFileObjects) {
    super(fileManager);
    this.sandbox = sandbox;
    this.packageToJavaFileObjects = packageToJavaFileObjects;
    this.resultClassFiles = sandbox.fileSetBuilder();
  }

  public FileSet resultClassfiles() {
    return resultClassFiles.build();
  }

  @Override
  public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind,
      FileObject sibling) throws IOException {
    if (location == StandardLocation.CLASS_OUTPUT && kind == Kind.CLASS) {
      String classFilePath = className.replace('.', '/') + ".class";
      String message = validationError(classFilePath);
      if (message == null) {
        return new OutputClassFile(resultClassFiles, path(classFilePath), sandbox.fileBuilder());
      } else {
        Message errorMessage = new IncorrectClassNameGivenByJavaCompilerError(className);
        throw new ErrorMessageException(errorMessage);
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
