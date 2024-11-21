package org.smoothbuild.common.testing;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.Set.set;
import static org.smoothbuild.common.filesystem.base.Alias.alias;
import static org.smoothbuild.common.filesystem.base.FileSystemPart.fileSystemPart;
import static org.smoothbuild.common.filesystem.base.Path.path;
import static org.smoothbuild.common.testing.TestingFileSystem.createFile;
import static org.smoothbuild.common.testing.TestingFileSystem.directoryToFileMap;

import java.util.Map;
import okio.ByteString;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.filesystem.mem.MemoryFileSystem;

public class TestingFileSystemTest {
  @Test
  void directory_to_file_map() throws Exception {
    var fullFileSystem = new MemoryFileSystem(set(alias("alias1")));
    var fileSystem = fileSystemPart(fullFileSystem, alias("alias1").root());
    var path1 = path("file1");
    var path2 = path("dir/file2");
    var content1 = ByteString.encodeUtf8("abc");
    var content2 = ByteString.encodeUtf8("def");
    createFile(fileSystem, path1, content1);
    createFile(fileSystem, path2, content2);

    assertThat(directoryToFileMap(fileSystem)).isEqualTo(Map.of(path1, content1, path2, content2));
  }
}
