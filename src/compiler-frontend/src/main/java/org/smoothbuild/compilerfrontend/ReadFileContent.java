package org.smoothbuild.compilerfrontend;

import static org.smoothbuild.common.Constants.CHARSET;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.base.Try.failure;
import static org.smoothbuild.common.log.base.Try.success;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILE_PREFIX;

import jakarta.inject.Inject;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import org.smoothbuild.common.bucket.base.FileResolver;
import org.smoothbuild.common.bucket.base.FullPath;
import org.smoothbuild.common.dag.TryFunction1;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Try;

public class ReadFileContent implements TryFunction1<FullPath, String> {
  private final FileResolver fileResolver;

  @Inject
  public ReadFileContent(FileResolver fileResolver) {
    this.fileResolver = fileResolver;
  }

  @Override
  public Label label() {
    return Label.label(COMPILE_PREFIX, "read_file_content");
  }

  @Override
  public Try<String> apply(FullPath fullPath) {
    try {
      return success(fileResolver.contentOf(fullPath, CHARSET));
    } catch (NoSuchFileException e) {
      return failure(error(fullPath.q() + " doesn't exist."));
    } catch (IOException e) {
      return failure(error("Cannot read build script file " + fullPath.q() + "."));
    }
  }
}
