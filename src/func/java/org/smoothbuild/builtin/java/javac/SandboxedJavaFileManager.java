package org.smoothbuild.builtin.java.javac;

import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.Path.validationError;

import java.io.IOException;
import java.util.ArrayList;
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

import org.smoothbuild.lang.message.ErrorMessage;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.SFile;

public class SandboxedJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
  private final Container container;
  private final Map<String, Set<JavaFileObject>> packageToJavaFileObjects;
  private final ArrayBuilder<SFile> resultClassFiles;

  SandboxedJavaFileManager(StandardJavaFileManager fileManager, Container container,
      Iterable<InputClassFile> objects) {
    super(fileManager);
    this.container = container;
    this.packageToJavaFileObjects = groupIntoPackages(objects);
    this.resultClassFiles = container.create().arrayBuilder(SFile.class);
  }

  private static Map<String, Set<JavaFileObject>> groupIntoPackages(
      Iterable<InputClassFile> objects) {
    HashMap<String, Set<JavaFileObject>> result = new HashMap<>();
    for (InputClassFile object : objects) {
      String packageName = object.aPackage();
      Set<JavaFileObject> aPackage = result.get(packageName);
      if (aPackage == null) {
        aPackage = new HashSet<>();
        result.put(packageName, aPackage);
      }
      aPackage.add(object);
    }
    return result;
  }

  public Array<SFile> resultClassfiles() {
    return resultClassFiles.build();
  }

  public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind,
      FileObject sibling) throws IOException {
    if (location == StandardLocation.CLASS_OUTPUT && kind == Kind.CLASS) {
      String classFilePath = className.replace('.', '/') + ".class";
      String message = validationError(classFilePath);
      if (message == null) {
        return new OutputClassFile(resultClassFiles, path(classFilePath), container);
      } else {
        throw new ErrorMessage("Internal Error: JavaCompiler passed illegal class name = '"
            + className + "' to JavaFileManager.");
      }
    } else {
      return super.getJavaFileForOutput(location, className, kind, sibling);
    }
  }

  public String inferBinaryName(Location location, JavaFileObject file) {
    if (file instanceof InputClassFile) {
      return ((InputClassFile) file).binaryName();
    } else {
      return super.inferBinaryName(location, file);
    }
  }

  public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds,
      boolean recurse) throws IOException {
    if (location == StandardLocation.CLASS_PATH) {
      if (recurse) {
        throw new UnsupportedOperationException(
            "recurse is not supported by SandboxedJavaFileManager.list()");
      }
      Set<JavaFileObject> result = packageToJavaFileObjects.get(packageName);
      if (result == null) {
        return new ArrayList<>();
      } else {
        return result;
      }
    } else {
      return super.list(location, packageName, kinds, recurse);
    }
  }
}
