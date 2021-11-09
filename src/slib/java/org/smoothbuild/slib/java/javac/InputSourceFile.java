package org.smoothbuild.slib.java.javac;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.smoothbuild.exec.base.FileStruct.fileContent;
import static org.smoothbuild.exec.base.FileStruct.filePath;

import java.net.URI;
import java.util.Scanner;

import javax.tools.SimpleJavaFileObject;

import org.smoothbuild.db.object.obj.val.TupleH;

public class InputSourceFile extends SimpleJavaFileObject {
  private final TupleH file;

  public InputSourceFile(TupleH file) {
    super(URI.create("string:///" + filePath(file).jValue()), Kind.SOURCE);
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
