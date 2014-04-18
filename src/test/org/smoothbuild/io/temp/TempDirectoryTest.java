package org.smoothbuild.io.temp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.testing.common.StreamTester.assertContent;
import static org.smoothbuild.testing.lang.type.FileTester.assertContentContains;

import java.nio.file.Paths;

import org.junit.After;
import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;

import com.google.common.collect.Iterables;

public class TempDirectoryTest {
  private final Path path = path("my/path");
  private final String content = "content";

  private final FakeObjectsDb objectsDb = new FakeObjectsDb();
  private final java.nio.file.Path rootPath = Paths.get("/fake/path");
  private final FakeFileSystem fileSystem = new FakeFileSystem();
  private final TempDirectory tempDirectory = new TempDirectory(objectsDb, rootPath, fileSystem);

  @After
  public void after() {
    try {
      tempDirectory.destroy();
    } catch (IllegalStateException e) {
      // ignore exception as tempDirectory might have been already destroyed by
      // test and destroying it second time causes exception
    }
  }

  @Test
  public void rootOsPath() {
    assertThat(tempDirectory.rootOsPath()).isEqualTo(rootPath.toString());
  }

  @Test
  public void file_is_written_to_file_system() throws Exception {
    SFile file = objectsDb.file(path, content);
    tempDirectory.writeFile(file);
    fileSystem.assertFileContains(path, content);
  }

  @Test
  public void writing_file_after_destroy_throws_exception() throws Exception {
    tempDirectory.destroy();

    SFile file = objectsDb.file(path, content);

    try {
      tempDirectory.writeFile(file);
      fail("exception should be thrown");
    } catch (IllegalStateException e) {
      // expected
    }
  }

  @Test
  public void content_and_path_is_written_to_file_system() throws Exception {
    tempDirectory.writeFile(path, objectsDb.blob(content));
    fileSystem.assertFileContains(path, content);
  }

  @Test
  public void writing_content_after_destroy_throws_exception() throws Exception {
    tempDirectory.destroy();

    try {
      tempDirectory.writeFile(path, objectsDb.blob(content));
      fail("exception should be thrown");
    } catch (IllegalStateException e) {
      // expected
    }
  }

  @Test
  public void files_are_written_to_file_system() throws Exception {
    SFile file = objectsDb.file(path, content);
    SArray<SFile> array = objectsDb.array(FILE_ARRAY, file);

    tempDirectory.writeFiles(array);

    fileSystem.assertFileContains(path, content);
  }

  @Test
  public void writing_files_after_destroy_throws_exception() throws Exception {
    tempDirectory.destroy();

    SFile file = objectsDb.file(path, content);
    SArray<SFile> array = objectsDb.array(FILE_ARRAY, file);

    try {
      tempDirectory.writeFiles(array);
      fail("exception should be thrown");
    } catch (IllegalStateException e) {
      // expected
    }
  }

  @Test
  public void files_are_read_from_file_system() throws Exception {
    fileSystem.createFile(path, content);

    SArray<SFile> files = tempDirectory.readFiles();
    assertThat(Iterables.size(files)).isEqualTo(1);
    assertContentContains(files.iterator().next(), content);
  }

  @Test
  public void reading_files_after_destroy_throws_exception() throws Exception {
    tempDirectory.destroy();

    try {
      tempDirectory.readFiles();
      fail("exception should be thrown");
    } catch (IllegalStateException e) {
      // expected
    }
  }

  @Test
  public void content_is_read_from_file_system() throws Exception {
    fileSystem.createFile(path, content);
    SBlob blobContent = tempDirectory.readContent(path);
    assertContent(blobContent.openInputStream(), content);
  }

  @Test
  public void reading_content_after_destroy_throws_exception() throws Exception {
    fileSystem.createFile(path, content);
    tempDirectory.destroy();

    try {
      tempDirectory.readContent(path);
      fail("exception should be thrown");
    } catch (IllegalStateException e) {
      // expected
    }
  }
}
