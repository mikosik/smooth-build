package org.smoothbuild.testing.io.fs.base;

import static org.smoothbuild.util.Streams.inputStreamToString;

import java.io.IOException;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;

public class FileSystems {
  public static String fileContent(FileSystem fileSystem, Path path) throws IOException {
    return inputStreamToString(fileSystem.openInputStream(path));
  }
}
