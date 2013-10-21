package org.smoothbuild.type.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.fs.base.Path.path;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.object.FileObject;
import org.smoothbuild.object.HashedDb;
import org.smoothbuild.object.ObjectsDb;
import org.smoothbuild.testing.common.StreamTester;
import org.smoothbuild.testing.fs.base.FakeFileSystem;
import org.smoothbuild.testing.type.impl.FileTester;

import com.google.common.base.Charsets;

public class StoredFileTest {
  FakeFileSystem fileSystem = new FakeFileSystem();
  Path filePath = path("xyz/test.txt");

  StoredFile storedFile = new StoredFile(fileSystem, filePath);

  @Test
  public void testPath() throws Exception {
    assertThat(storedFile.path()).isEqualTo(filePath);
  }

  @Test
  public void fileSystem() throws Exception {
    assertThat(storedFile.fileSystem()).isSameAs(fileSystem);
  }

  @Test
  public void createInputStream() throws Exception {
    fileSystem.createFileContainingItsPath(filePath);
    FileTester.assertContentContainsFilePath(storedFile);
  }

  @Test
  public void testToString() throws Exception {
    assertThat(storedFile.toString()).isEqualTo("StoredFile(" + filePath.toString() + ")");
  }

  @Test
  public void hashIsTheSameAsForFileObjectImplementation() throws Exception {
    Path path = Path.path("my/file");
    String content = "content";

    MutableStoredFile mutableStoredFile = new MutableStoredFile(fileSystem, path);
    StreamTester.writeAndClose(mutableStoredFile.openOutputStream(), content);

    ObjectsDb objectsDb = new ObjectsDb(new HashedDb(new FakeFileSystem()));
    FileObject fileObject = objectsDb.file(path, content.getBytes(Charsets.UTF_8));

    assertThat(mutableStoredFile.hash()).isEqualTo(fileObject.hash());
  }
}
