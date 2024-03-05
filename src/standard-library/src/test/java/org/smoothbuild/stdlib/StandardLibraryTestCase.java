package org.smoothbuild.stdlib;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.smoothbuild.testing.accept.AcceptanceTestCase;

public class StandardLibraryTestCase extends AcceptanceTestCase {
  @BeforeEach
  @Override
  public void beforeEach() throws IOException {
    super.beforeEach();
    createLibraryModule(findStandardLibrarySmooth(), findStandardLibraryJar());
  }

  private static Path findStandardLibraryJar() {
    return Paths.get("./build/libs/std_lib.jar").toAbsolutePath();
  }

  private static Path findStandardLibrarySmooth() {
    return Paths.get("./src/main/smooth/std_lib.smooth").toAbsolutePath();
  }
}
