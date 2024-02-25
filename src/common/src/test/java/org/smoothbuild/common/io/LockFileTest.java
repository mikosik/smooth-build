package org.smoothbuild.common.io;

import static com.google.common.truth.Truth.assertThat;
import static java.io.Writer.nullWriter;
import static java.nio.file.Files.createFile;
import static org.smoothbuild.common.io.LockFile.lockFile;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class LockFileTest {
  private final PrintWriter writer = new PrintWriter(nullWriter());

  @Nested
  class file_can_be_locked {
    @Test
    public void when_it_exists(@TempDir Path tempDir) throws IOException {
      Path lockFile = tempDir.resolve("lockFile");
      createFile(lockFile);
      assertThat(lockFile(writer, lockFile)).isNotNull();
    }

    @Test
    public void when_it_doesnt_exist(@TempDir Path tempDir) {
      assertThat(lockFile(writer, tempDir.resolve("lockFile"))).isNotNull();
    }

    @Test
    public void when_parent_directory_doesnt_exist(@TempDir Path tempDir) {
      assertThat(lockFile(writer, tempDir.resolve("subdir/lockFile"))).isNotNull();
    }
  }

  @Nested
  class file_cannot_be_locked {
    @Test
    public void when_it_is_already_acquired_by_our_jvm(@TempDir Path tempDir) {
      Path lockFile = tempDir.resolve("lockFile");
      assertThat(lockFile(writer, lockFile)).isNotNull();
      assertThat(lockFile(writer, lockFile)).isNull();
    }
  }
}
