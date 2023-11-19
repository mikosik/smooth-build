package org.smoothbuild.stdlib.java.javac;

import static org.smoothbuild.run.eval.FileStruct.fileContent;
import static org.smoothbuild.run.eval.FileStruct.filePath;
import static org.smoothbuild.stdlib.java.util.JavaNaming.binaryNameToPackage;
import static org.smoothbuild.stdlib.java.util.JavaNaming.toBinaryName;

import java.io.InputStream;
import java.net.URI;
import java.util.Objects;
import javax.tools.SimpleJavaFileObject;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;

public class InputClassFile extends SimpleJavaFileObject {
  private final TupleB file;
  private final String binaryName;
  private final String aPackage;

  public InputClassFile(TupleB file) {
    this(file, filePath(file).toJ());
  }

  public InputClassFile(TupleB file, String filePath) {
    super(URI.create("jar:///" + ":" + filePath), Kind.CLASS);

    if (!filePath.endsWith(Kind.CLASS.extension)) {
      throw new IllegalArgumentException();
    }

    this.file = file;
    this.binaryName = toBinaryName(filePath);
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
