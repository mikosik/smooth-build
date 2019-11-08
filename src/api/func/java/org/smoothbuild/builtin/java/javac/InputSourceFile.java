package org.smoothbuild.builtin.java.javac;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.net.URI;
import java.util.Scanner;

import javax.tools.SimpleJavaFileObject;

import org.smoothbuild.lang.object.base.Blob;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.base.Struct;

public class InputSourceFile extends SimpleJavaFileObject {
  private final Struct file;

  public InputSourceFile(Struct file) {
    super(URI.create("string:///" + ((SString) file.get("path")).data()), Kind.SOURCE);
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
    return new Scanner(((Blob) file.get("content")).source().inputStream(), UTF_8);
  }
}
