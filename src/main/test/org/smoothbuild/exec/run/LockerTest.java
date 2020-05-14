package org.smoothbuild.exec.run;

import static com.google.common.truth.Truth.assertThat;
import static java.nio.file.Files.createFile;
import static org.smoothbuild.exec.run.Locker.acquireFileLock;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.io.IOException;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Path;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@SuppressWarnings("ClassCanBeStatic")
public class LockerTest {
  @Nested
  class lock_can_be_acquired {
    @Test
    public void when_it_exists(@TempDir Path tempDir) throws IOException {
      Path lockFile = tempDir.resolve("lockFile");
      createFile(lockFile);
      assertThat(acquireFileLock(lockFile))
          .isNull();
    }

    @Test
    public void when_it_doesnt_exist(@TempDir Path tempDir) {
      assertThat(acquireFileLock(tempDir.resolve("lockFile")))
          .isNull();
    }

    @Test
    public void when_parent_directory_doesnt_exist(@TempDir Path tempDir) {
      assertThat(acquireFileLock(tempDir.resolve("subdir/lockFile")))
          .isNull();
    }
  }

  @Nested
  class lock_can_not_be_acquired {
    @Test
    public void when_it_is_already_acquired_by_our_jvm(@TempDir Path tempDir) {
      Path lockFile = tempDir.resolve("lockFile");
      assertThat(acquireFileLock(lockFile))
          .isNull();
      assertCall(() -> acquireFileLock(lockFile))
          .throwsException(OverlappingFileLockException.class);
    }

  }
}
