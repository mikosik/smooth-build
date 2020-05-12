package org.smoothbuild.slib.java.javac;

import static org.smoothbuild.slib.java.util.JavaNaming.binaryNameToPackage;
import static org.smoothbuild.slib.java.util.JavaNaming.toBinaryName;

import java.io.InputStream;
import java.net.URI;
import java.util.Objects;

import javax.tools.SimpleJavaFileObject;

import org.smoothbuild.lang.object.base.Blob;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.base.Struct;

public class InputClassFile extends SimpleJavaFileObject {
  private final Struct file;
  private final String binaryName;
  private final String aPackage;

  public InputClassFile(Struct file) {
    super(URI.create("jar:///" + ":" + ((SString) file.get("path")).jValue()), Kind.CLASS);

    if (!((SString) file.get("path")).jValue().endsWith(Kind.CLASS.extension)) {
      throw new IllegalArgumentException();
    }

    this.file = file;
    this.binaryName = toBinaryName(((SString) file.get("path")).jValue());
    this.aPackage = binaryNameToPackage(binaryName);
  }

  public String binaryName() {
    return binaryName;
  }

  public String aPackage() {
    return aPackage;
  }

  @Override
  public InputStream openInputStream() {
    return ((Blob) file.get("content")).source().inputStream();
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof InputClassFile
        && Objects.equals(file.get("path"), ((InputClassFile) object).file.get("path"));
  }

  @Override
  public int hashCode() {
    return file.get("path").hashCode();
  }
}
