package org.smoothbuild.builtin.java.javac;

import java.io.IOException;
import java.net.URI;
import java.util.Scanner;

import javax.tools.SimpleJavaFileObject;

import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;

public class InputSourceFile extends SimpleJavaFileObject {
  private final Struct file;

  public InputSourceFile(Struct file) {
    super(URI.create("string:///" + ((SString) file.get("path")).data()), Kind.SOURCE);
    this.file = file;
  }

  @Override
  public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
    try (Scanner scanner = new Scanner(((Blob) file.get("content")).openInputStream(), "UTF-8")) {
      scanner.useDelimiter("\\A");
      return scanner.hasNext() ? scanner.next() : "";
    }
  }
}
