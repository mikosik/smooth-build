package org.smoothbuild.stdlib;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.smoothbuild.evaluator.testing.EvaluatorTestContext;

public class StandardLibraryTestCase extends EvaluatorTestContext {
  @BeforeEach
  @Override
  public void beforeEach() throws IOException {
    super.beforeEach();
    createLibraryModule(standardLibraryPath(), standardLibraryJarPath());
  }

  private static Path standardLibraryJarPath() {
    return Paths.get("./build/libs/std_lib.jar").toAbsolutePath();
  }

  private static Path standardLibraryPath() {
    return Paths.get("./src/main/smooth/std_lib.smooth").toAbsolutePath();
  }
}
