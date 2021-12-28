package org.smoothbuild.slib.java.javac;

import static org.smoothbuild.exec.base.FileStruct.fileContent;
import static org.smoothbuild.exec.base.FileStruct.filePath;
import static org.smoothbuild.slib.java.util.JavaNaming.binaryNameToPackage;
import static org.smoothbuild.slib.java.util.JavaNaming.toBinaryName;

import java.io.InputStream;
import java.net.URI;
import java.util.Objects;

import javax.tools.SimpleJavaFileObject;

import org.smoothbuild.db.bytecode.obj.val.TupleB;

public class InputClassFile extends SimpleJavaFileObject {
  private final TupleB file;
  private final String binaryName;
  private final String aPackage;

  public InputClassFile(TupleB file) {
    super(URI.create("jar:///" + ":" + filePath(file).toJ()), Kind.CLASS);

    if (!filePath(file).toJ().endsWith(Kind.CLASS.extension)) {
      throw new IllegalArgumentException();
    }

    this.file = file;
    this.binaryName = toBinaryName(filePath(file).toJ());
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
