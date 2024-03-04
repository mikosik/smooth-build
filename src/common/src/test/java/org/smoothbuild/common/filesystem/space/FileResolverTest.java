package org.smoothbuild.common.filesystem.space;

import static com.google.common.truth.Truth.assertThat;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.filesystem.base.Path.path;
import static org.smoothbuild.common.filesystem.base.PathState.DIR;
import static org.smoothbuild.common.filesystem.base.PathState.FILE;
import static org.smoothbuild.common.filesystem.base.PathState.NOTHING;
import static org.smoothbuild.common.filesystem.space.FilePath.filePath;
import static org.smoothbuild.common.testing.TestingSpace.space;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.filesystem.base.Path;
import org.smoothbuild.common.filesystem.mem.MemoryFileSystem;

public class FileResolverTest {
  private MemoryFileSystem fileSystem;
  private FileResolver fileResolver;

  @BeforeEach
  public void setUp() {
    fileSystem = new MemoryFileSystem();
    fileResolver = new FileResolver(map(space("project"), fileSystem));
  }

  @Nested
  class _contentOf {
    @Test
    void contentOf() throws IOException {
      var path = path("file.txt");
      var content = "some string";
      createFile(path, content);
      assertThat(fileResolver.contentOf(filePath(space("project"), path), UTF_8))
          .isEqualTo(content);
    }
  }

  @Nested
  class _path_state {
    @Test
    public void of_file() throws IOException {
      var path = path("file.txt");
      createFile(path, "some string");
      assertThat(fileResolver.pathState(filePath(space("project"), path))).isEqualTo(FILE);
    }

    @Test
    public void of_directory() throws IOException {
      var path = path("directory");
      fileSystem.createDir(path);
      assertThat(fileResolver.pathState(filePath(space("project"), path))).isEqualTo(DIR);
    }

    @Test
    public void of_nothing() {
      var path = path("file.txt");
      assertThat(fileResolver.pathState(filePath(space("project"), path))).isEqualTo(NOTHING);
    }
  }

  private void createFile(Path path, String string) throws IOException {
    try (var bufferedSink = fileSystem.sink(path)) {
      bufferedSink.writeUtf8(string);
    }
  }
}
