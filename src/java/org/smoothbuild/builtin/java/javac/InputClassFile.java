package org.smoothbuild.builtin.java.javac;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.Path;

public class InputClassFile extends SimpleJavaFileObject {
  private final File file;
  private final Path jarFilePath;
  private final String binaryName;
  private final String aPackage;

  public InputClassFile(Path jarFilePath, File file) {
    super(URI.create("jar:///" + jarFilePath.value() + ":" + file.path().value()), Kind.CLASS);

    checkArgument(file.path().value().endsWith(Kind.CLASS.extension));

    this.file = file;
    this.jarFilePath = jarFilePath;
    this.binaryName = toBinaryName(file);
    this.aPackage = binaryNameToPackage(binaryName);
  }

  public String binaryName() {
    return binaryName;
  }

  public String aPackage() {
    return aPackage;
  }

  public Path jarFileName() {
    return jarFilePath;
  }

  @Override
  public InputStream openInputStream() throws IOException {
    return file.openInputStream();
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof InputClassFile) {
      InputClassFile that = (InputClassFile) object;
      return this.file.path().equals(that.file.path());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return file.path().hashCode();
  }

  private static String toBinaryName(File file) {
    String path = file.path().value();
    String extensionRemoved = path.substring(0, path.length() - Kind.CLASS.extension.length());
    return extensionRemoved.replace('/', '.');
  }

  private static String binaryNameToPackage(String binaryName) {
    int lastIndex = binaryName.lastIndexOf('.');
    if (lastIndex == -1) {
      return "";
    } else {
      return binaryName.substring(0, lastIndex);
    }
  }
}
