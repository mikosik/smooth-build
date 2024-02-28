package org.smoothbuild.compile.frontend;

import static org.smoothbuild.common.Constants.CHARSET;
import static org.smoothbuild.common.log.Log.error;
import static org.smoothbuild.common.log.Try.failure;
import static org.smoothbuild.common.log.Try.success;

import jakarta.inject.Inject;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.function.Function;
import org.smoothbuild.common.filesystem.space.FilePath;
import org.smoothbuild.common.filesystem.space.FileResolver;
import org.smoothbuild.common.log.Try;

public class ReadFileContent implements Function<FilePath, Try<String>> {
  private final FileResolver fileResolver;

  @Inject
  public ReadFileContent(FileResolver fileResolver) {
    this.fileResolver = fileResolver;
  }

  @Override
  public Try<String> apply(FilePath filePath) {
    try {
      return success(fileResolver.contentOf(filePath, CHARSET));
    } catch (NoSuchFileException e) {
      return failure(error(filePath.q() + " doesn't exist."));
    } catch (IOException e) {
      return failure(error("Cannot read build script file " + filePath.q() + "."));
    }
  }
}
