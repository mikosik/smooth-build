package org.smoothbuild.compilerfrontend.compile;

import static okio.Okio.buffer;
import static org.smoothbuild.common.Constants.CHARSET;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILER_FRONT_LABEL;

import jakarta.inject.Inject;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Task1;

public class ReadFileContent implements Task1<FullPath, String> {
  private final FileSystem<FullPath> fileSystem;

  @Inject
  public ReadFileContent(FileSystem<FullPath> fileSystem) {
    this.fileSystem = fileSystem;
  }

  @Override
  public Output<String> execute(FullPath fullPath) {
    var label = COMPILER_FRONT_LABEL.append(":readFileContent");
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
    try (var source = buffer(fileSystem.source(fullPath))) {
      return source.readString(CHARSET);
    }
  }
}
