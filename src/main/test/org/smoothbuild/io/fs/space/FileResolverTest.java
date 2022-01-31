package org.smoothbuild.io.fs.space;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.io.fs.base.PathS.path;
import static org.smoothbuild.io.fs.base.PathState.DIR;
import static org.smoothbuild.io.fs.base.PathState.FILE;
import static org.smoothbuild.io.fs.base.PathState.NOTHING;
import static org.smoothbuild.io.fs.space.FilePath.filePath;
import static org.smoothbuild.io.fs.space.Space.PRJ;
import static org.smoothbuild.util.io.Okios.writeAndClose;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.db.Hash;
import org.smoothbuild.io.fs.base.PathS;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;

import com.google.common.collect.ImmutableMap;

public class FileResolverTest {
  private MemoryFileSystem fileSystem;
  private FileResolver fileResolver;

  @BeforeEach
  public void setUp() {
    fileSystem = new MemoryFileSystem();
    fileResolver = new FileResolver(ImmutableMap.of(PRJ, fileSystem));
  }

  @Nested
  class _hash {
    @Test
    public void of_file() throws IOException {
      String content = "some string";
      PathS path = path("file.txt");
      createFile(path, content);

      Hash hash = fileResolver.hashOf(filePath(PRJ, path));

      assertThat(hash)
          .isEqualTo(Hash.of(content));
    }

    @Test
    public void is_cached() throws IOException {
      String content = "some string";
      PathS path = path("file.txt");
      FilePath filePath = filePath(PRJ, path);
      createFile(path, content);
      Hash hash1 = fileResolver.hashOf(filePath);
      createFile(path, content + "something more");
      Hash hash2 = fileResolver.hashOf(filePath);

      assertThat(hash2)
          .isEqualTo(hash1);
    }

    @Test
    public void is_cached_when_calling_readFileContentAndCacheHash() throws IOException {
      String content = "some string";
      PathS path = path("file.txt");
      FilePath filePath = filePath(PRJ, path);
      createFile(path, content);
      fileResolver.readFileContentAndCacheHash(filePath);
      createFile(path, content + "something more");
      Hash hash = fileResolver.hashOf(filePath);

      assertThat(hash)
          .isEqualTo(Hash.of(content));
    }
  }

  @Nested
  class _path_state {
    @Test
    public void of_file() throws IOException {
      PathS path = path("file.txt");
      createFile(path, "some string");
      assertThat(fileResolver.pathState(filePath(PRJ, path)))
          .isEqualTo(FILE);
    }

    @Test
    public void of_directory() throws IOException {
      PathS path = path("directory");
      fileSystem.createDir(path);
      assertThat(fileResolver.pathState(filePath(PRJ, path)))
          .isEqualTo(DIR);
    }

    @Test
    public void of_nothing() {
      PathS path = path("file.txt");
      assertThat(fileResolver.pathState(filePath(PRJ, path)))
          .isEqualTo(NOTHING);
    }
  }

  private void createFile(PathS path, String string) throws IOException {
    writeAndClose(fileSystem.sink(path), s -> s.writeUtf8(string));
  }
}
