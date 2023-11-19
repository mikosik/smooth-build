package org.smoothbuild.stdlib.java.javac;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.smoothbuild.run.eval.FileStruct.fileContent;
import static org.smoothbuild.run.eval.FileStruct.filePath;

import java.net.URI;
import java.util.Scanner;
import javax.tools.SimpleJavaFileObject;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;

public class InputSourceFile extends SimpleJavaFileObject {
  private final TupleB file;

  public InputSourceFile(TupleB file) {
    super(URI.create("string:///" + filePath(file).toJ()), Kind.SOURCE);
    this.file = file;
  }

  @Override
  public CharSequence getCharContent(boolean ignoreEncodingErrors) {
    try (Scanner scanner = scanner()) {
      scanner.useDelimiter("\\A");
      return scanner.hasNext() ? scanner.next() : "";
    }
  }

  private Scanner scanner() {
    return new Scanner(fileContent(file).source().inputStream(), UTF_8);
  }
}
