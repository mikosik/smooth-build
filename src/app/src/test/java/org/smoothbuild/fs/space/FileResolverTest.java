package org.smoothbuild.fs.space;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.io.Okios.writeAndClose;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.util.fs.base.PathS;
import org.smoothbuild.util.fs.base.PathState;
import org.smoothbuild.util.fs.mem.MemoryFileSystem;
import org.smoothbuild.vm.bytecode.hashed.Hash;

import com.google.common.collect.ImmutableMap;

public class FileResolverTest {
  private MemoryFileSystem fileSystem;
  private FileResolver fileResolver;

  @BeforeEach
  public void setUp() {
    fileSystem = new MemoryFileSystem();
    fileResolver = new FileResolver(ImmutableMap.of(Space.PRJ, fileSystem));
  }

  @Nested
  class _hash {
    @Test
    public void of_file() throws IOException {
      String content = "some string";
      PathS path = PathS.path("file.txt");
      createFile(path, content);

      Hash hash = fileResolver.hashOf(FilePath.filePath(Space.PRJ, path));

      assertThat(hash)
          .isEqualTo(Hash.of(content));
    }

    @Test
    public void is_cached() throws IOException {
      String content = "some string";
      PathS path = PathS.path("file.txt");
      FilePath filePath = FilePath.filePath(Space.PRJ, path);
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
      PathS path = PathS.path("file.txt");
      FilePath filePath = FilePath.filePath(Space.PRJ, path);
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
      PathS path = PathS.path("file.txt");
      createFile(path, "some string");
      assertThat(fileResolver.pathState(FilePath.filePath(Space.PRJ, path)))
          .isEqualTo(PathState.FILE);
    }

    @Test
    public void of_directory() throws IOException {
      PathS path = PathS.path("directory");
      fileSystem.createDir(path);
      assertThat(fileResolver.pathState(FilePath.filePath(Space.PRJ, path)))
          .isEqualTo(PathState.DIR);
    }

    @Test
    public void of_nothing() {
      PathS path = PathS.path("file.txt");
      assertThat(fileResolver.pathState(FilePath.filePath(Space.PRJ, path)))
          .isEqualTo(PathState.NOTHING);
    }
  }

  private void createFile(PathS path, String string) throws IOException {
    writeAndClose(fileSystem.sink(path), s -> s.writeUtf8(string));
  }
}
