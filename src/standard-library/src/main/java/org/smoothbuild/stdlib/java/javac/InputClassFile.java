package org.smoothbuild.stdlib.java.javac;

import static okio.Okio.buffer;
import static org.smoothbuild.stdlib.java.util.JavaNaming.binaryNameToPackage;
import static org.smoothbuild.stdlib.java.util.JavaNaming.toBinaryName;
import static org.smoothbuild.virtualmachine.bytecode.helper.FileStruct.fileContent;
import static org.smoothbuild.virtualmachine.bytecode.helper.FileStruct.filePath;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Objects;
import javax.tools.SimpleJavaFileObject;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;

public class InputClassFile extends SimpleJavaFileObject {
  private final BTuple file;
  private final String filePath;
  private final String binaryName;
  private final String aPackage;

  public InputClassFile(BTuple file) throws BytecodeException {
    this(file, filePath(file).toJavaString());
  }

  public InputClassFile(BTuple file, String filePath) {
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
    return buffer(fileContent(file).source()).inputStream();
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
