package org.smoothbuild.stdlib.java.javac;

import static org.smoothbuild.run.eval.FileStruct.fileContent;
import static org.smoothbuild.run.eval.FileStruct.filePath;
import static org.smoothbuild.stdlib.java.util.JavaNaming.binaryNameToPackage;
import static org.smoothbuild.stdlib.java.util.JavaNaming.toBinaryName;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Objects;
import javax.tools.SimpleJavaFileObject;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;

public class InputClassFile extends SimpleJavaFileObject {
  private final TupleB file;
  private final String filePath;
  private final String binaryName;
  private final String aPackage;

  public InputClassFile(TupleB file) throws BytecodeException {
    this(file, filePath(file).toJ());
  }

  public InputClassFile(TupleB file, String filePath) {
    super(URI.create("jar:///" + ":" + filePath), Kind.CLASS);

    if (!filePath.endsWith(Kind.CLASS.extension)) {
      throw new IllegalArgumentException();
    }

    this.file = file;
    this.filePath = filePath;
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
  public InputStream openInputStream() throws IOException {
    try {
      return fileContent(file).source().inputStream();
    } catch (BytecodeException e) {
      throw e.toIOException();
    }
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof InputClassFile that && Objects.equals(this.filePath, that.filePath);
  }

  @Override
  public int hashCode() {
    return filePath.hashCode();
  }
}
