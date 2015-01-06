package org.smoothbuild.testing.integration;

import static org.smoothbuild.SmoothConstants.ARTIFACTS_DIR;
import static org.smoothbuild.SmoothConstants.DEFAULT_SCRIPT;
import static org.smoothbuild.SmoothConstants.SMOOTH_DIR;
import static org.smoothbuild.io.fs.base.Path.path;

import java.io.IOException;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.parse.ScriptBuilder;

public class IntegrationTestUtils {

  public static final Path ARTIFACTS_PATH = SMOOTH_DIR.append(ARTIFACTS_DIR);

  public static void script(FakeFileSystem fileSystem, String script) throws IOException {
    fileSystem.createFile(DEFAULT_SCRIPT, ScriptBuilder.script(script));
  }

  public static Path artifactPath(String artifact) {
    return ARTIFACTS_PATH.append(path(artifact));
  }
}
