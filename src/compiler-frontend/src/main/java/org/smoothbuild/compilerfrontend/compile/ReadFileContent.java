package org.smoothbuild.compilerfrontend.compile;

import static okio.Okio.buffer;
import static org.smoothbuild.common.Constants.CHARSET;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.task.Output.output;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILE_FRONT_LABEL;

import jakarta.inject.Inject;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import org.smoothbuild.common.bucket.base.Filesystem;
import org.smoothbuild.common.bucket.base.FullPath;
import org.smoothbuild.common.task.Output;
import org.smoothbuild.common.task.Task1;

public class ReadFileContent implements Task1<FullPath, String> {
  private final Filesystem filesystem;

  @Inject
  public ReadFileContent(Filesystem filesystem) {
    this.filesystem = filesystem;
  }

  @Override
  public Output<String> execute(FullPath fullPath) {
    var label = COMPILE_FRONT_LABEL.append("readFileContent");
    try {
      var content = contentOf(fullPath);
      return output(content, label, list());
    } catch (NoSuchFileException e) {
      var error = error(fullPath.q() + " doesn't exist.");
      return output(label, list(error));
    } catch (IOException e) {
      var error = error("Cannot read build script file " + fullPath.q() + ".");
      return output(label, list(error));
    }
  }

  private String contentOf(FullPath fullPath) throws IOException {
    try (var source = buffer(filesystem.source(fullPath))) {
      return source.readString(CHARSET);
    }
  }
}
