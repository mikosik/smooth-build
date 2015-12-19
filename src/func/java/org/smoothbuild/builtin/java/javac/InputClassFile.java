package org.smoothbuild.builtin.java.javac;

import static org.smoothbuild.builtin.java.util.JavaNaming.binaryNameToPackage;
import static org.smoothbuild.builtin.java.util.JavaNaming.toBinaryName;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Objects;

import javax.tools.SimpleJavaFileObject;

import org.smoothbuild.lang.value.SFile;

public class InputClassFile extends SimpleJavaFileObject {
  private final SFile file;
  private final String binaryName;
  private final String aPackage;

  public InputClassFile(SFile file) {
    super(URI.create("jar:///" + ":" + file.path().value()), Kind.CLASS);

    if (!file.path().value().endsWith(Kind.CLASS.extension)) {
      throw new IllegalArgumentException();
    }

    this.file = file;
    this.binaryName = toBinaryName(file.path().value());
    this.aPackage = binaryNameToPackage(binaryName);
  }

  public String binaryName() {
    return binaryName;
  }

  public String aPackage() {
    return aPackage;
  }

  @Override
  public InputStream openInputStream() throws IOException {
    return file.content().openInputStream();
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof InputClassFile && equals((InputClassFile) object);
  }

  public boolean equals(InputClassFile inputClassFile) {
    return Objects.equals(file.path(), inputClassFile.file.path());
  }

  @Override
  public int hashCode() {
    return file.path().hashCode();
  }
}
