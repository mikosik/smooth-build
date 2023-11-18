package org.smoothbuild.compile.fs;

import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.out.log.Maybe.maybe;
import static org.smoothbuild.out.log.Maybe.maybeLogs;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.function.Function;

import org.smoothbuild.filesystem.space.FilePath;
import org.smoothbuild.filesystem.space.FileResolver;
import org.smoothbuild.out.log.Maybe;

import jakarta.inject.Inject;

public class ReadFileContent implements Function<FilePath, Maybe<String>> {
  private final FileResolver fileResolver;

  @Inject
  public ReadFileContent(FileResolver fileResolver) {
    this.fileResolver = fileResolver;
  }

  @Override
  public Maybe<String> apply(FilePath filePath) {
    try {
      return maybe(fileResolver.readFileContentAndCacheHash(filePath));
    } catch (NoSuchFileException e) {
      return maybeLogs(error(filePath.q() + " doesn't exist."));
    } catch (IOException e) {
      return maybeLogs(error("Cannot read build script file " + filePath.q() + "."));
    }
  }
}
