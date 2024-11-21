package org.smoothbuild.common.testing;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.Set.set;
import static org.smoothbuild.common.filesystem.base.Alias.alias;
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
    var alias = alias("alias1");
    var fullFileSystem = new MemoryFileSystem(set(alias));
    var file1 = path("dir/file1");
    var file2 = path("dir/subdir/file2");
    var path0 = alias.append("file0");
    var dir = alias.append("dir");
    var path1 = dir.append(file1);
    var path2 = dir.append(file2);
    var content1 = ByteString.encodeUtf8("abc");
    var content2 = ByteString.encodeUtf8("def");
    createFile(fullFileSystem, path0, "");
    createFile(fullFileSystem, path1, content1);
    createFile(fullFileSystem, path2, content2);

    var fileMap = directoryToFileMap(fullFileSystem, dir);
    assertThat(fileMap).isEqualTo(Map.of(file1, content1, file2, content2));
  }
}
