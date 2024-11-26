package org.smoothbuild.stdlib.java.javac;

import static okio.Okio.buffer;
import static org.smoothbuild.common.Constants.CHARSET;
import static org.smoothbuild.virtualmachine.bytecode.helper.FileStruct.fileContent;
import static org.smoothbuild.virtualmachine.bytecode.helper.FileStruct.filePath;

import java.io.IOException;
import java.net.URI;
import java.util.Scanner;
import javax.tools.SimpleJavaFileObject;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;

public class InputSourceFile extends SimpleJavaFileObject {
  private final BTuple file;

  public InputSourceFile(BTuple file) throws BytecodeException {
    super(URI.create("string:///" + filePath(file).toJavaString()), Kind.SOURCE);
    this.file = file;
  }

  @Override
  public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
    try (Scanner scanner = scanner()) {
      scanner.useDelimiter("\\A");
      return scanner.hasNext() ? scanner.next() : "";
    }
  }

  private Scanner scanner() throws BytecodeException {
    return new Scanner(buffer(fileContent(file).source()).inputStream(), CHARSET);
  }
}
