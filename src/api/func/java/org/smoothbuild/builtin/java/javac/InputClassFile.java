package org.smoothbuild.builtin.java.javac;

import static org.smoothbuild.builtin.java.util.JavaNaming.binaryNameToPackage;
import static org.smoothbuild.builtin.java.util.JavaNaming.toBinaryName;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Objects;

import javax.tools.SimpleJavaFileObject;

import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;

public class InputClassFile extends SimpleJavaFileObject {
  private final Struct file;
  private final String binaryName;
  private final String aPackage;

  public InputClassFile(Struct file) {
    super(URI.create("jar:///" + ":" + ((SString) file.get("path")).data()), Kind.CLASS);

    if (!((SString) file.get("path")).data().endsWith(Kind.CLASS.extension)) {
      throw new IllegalArgumentException();
    }

    this.file = file;
    this.binaryName = toBinaryName(((SString) file.get("path")).data());
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
    return ((Blob) file.get("content")).source().inputStream();
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof InputClassFile && equals((InputClassFile) object);
  }

  private boolean equals(InputClassFile inputClassFile) {
    return Objects.equals(file.get("path"), inputClassFile.file.get("path"));
  }

  @Override
  public int hashCode() {
    return ((SString) file.get("path")).hashCode();
  }
}
