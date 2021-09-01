package org.smoothbuild.slib.java.javac;

import static org.smoothbuild.exec.base.FileStruct.fileContent;
import static org.smoothbuild.exec.base.FileStruct.filePath;
import static org.smoothbuild.slib.java.util.JavaNaming.binaryNameToPackage;
import static org.smoothbuild.slib.java.util.JavaNaming.toBinaryName;

import java.io.InputStream;
import java.net.URI;
import java.util.Objects;

import javax.tools.SimpleJavaFileObject;

import org.smoothbuild.db.object.obj.val.Rec;

public class InputClassFile extends SimpleJavaFileObject {
  private final Rec file;
  private final String binaryName;
  private final String aPackage;

  public InputClassFile(Rec file) {
    super(URI.create("jar:///" + ":" + filePath(file).jValue()), Kind.CLASS);

    if (!filePath(file).jValue().endsWith(Kind.CLASS.extension)) {
      throw new IllegalArgumentException();
    }

    this.file = file;
    this.binaryName = toBinaryName(filePath(file).jValue());
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
    return fileContent(file).source().inputStream();
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof InputClassFile that
        && Objects.equals(filePath(this.file), filePath(that.file));
  }

  @Override
  public int hashCode() {
    return filePath(file).hashCode();
  }
}
