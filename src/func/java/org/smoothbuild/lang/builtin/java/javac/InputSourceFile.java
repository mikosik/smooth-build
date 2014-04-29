package org.smoothbuild.lang.builtin.java.javac;

import static com.google.common.base.Charsets.UTF_8;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

import org.smoothbuild.lang.base.SFile;

import com.google.common.io.CharStreams;

public class InputSourceFile extends SimpleJavaFileObject {
  private final SFile file;

  public InputSourceFile(SFile file) {
    super(URI.create("string:///" + file.path().value()), Kind.SOURCE);
    this.file = file;
  }

  @Override
  public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
    try (InputStreamReader is = new InputStreamReader(file.content().openInputStream(), UTF_8)) {
      return CharStreams.toString(is);
    }
  }
}
