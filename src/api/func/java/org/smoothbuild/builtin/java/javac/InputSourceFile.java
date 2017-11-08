package org.smoothbuild.builtin.java.javac;

import java.io.IOException;
import java.net.URI;
import java.util.Scanner;

import javax.tools.SimpleJavaFileObject;

import org.smoothbuild.lang.value.SFile;

public class InputSourceFile extends SimpleJavaFileObject {
  private final SFile file;

  public InputSourceFile(SFile file) {
    super(URI.create("string:///" + file.path().value()), Kind.SOURCE);
    this.file = file;
  }

  @Override
  public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
    try (Scanner scanner = new Scanner(file.content().openInputStream(), "UTF-8")) {
      scanner.useDelimiter("\\A");
      return scanner.hasNext() ? scanner.next() : "";
    }
  }
}
