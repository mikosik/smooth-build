package org.smoothbuild.stdlib.java.javac;

import static org.smoothbuild.common.Constants.CHARSET;
import static org.smoothbuild.virtualmachine.bytecode.helper.FileStruct.fileContent;
import static org.smoothbuild.virtualmachine.bytecode.helper.FileStruct.filePath;

import java.io.IOException;
import java.net.URI;
import java.util.Scanner;
import javax.tools.SimpleJavaFileObject;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;

public class InputSourceFile extends SimpleJavaFileObject {
  private final TupleB file;

  public InputSourceFile(TupleB file) throws BytecodeException {
    super(URI.create("string:///" + filePath(file).toJavaString()), Kind.SOURCE);
    this.file = file;
  }

  @Override
  public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
    try (Scanner scanner = scanner()) {
      scanner.useDelimiter("\\A");
      return scanner.hasNext() ? scanner.next() : "";
    } catch (BytecodeException e) {
      throw e.toIOException();
    }
  }

  private Scanner scanner() throws BytecodeException {
    return new Scanner(fileContent(file).source().inputStream(), CHARSET);
  }
}
