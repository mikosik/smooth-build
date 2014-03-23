package org.smoothbuild.io.temp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.type.STypes.FILE_ARRAY;
import static org.smoothbuild.testing.lang.type.FakeArray.fakeArray;
import static org.smoothbuild.testing.lang.type.FileTester.assertContentContains;

import java.nio.file.Paths;

import org.junit.After;
import org.junit.Test;
import org.smoothbuild.io.cache.value.build.SValueBuildersImpl;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.lang.type.SValueBuilders;
import org.smoothbuild.testing.io.cache.value.FakeValueDb;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.lang.type.BlobTester;
import org.smoothbuild.testing.lang.type.FakeBlob;
import org.smoothbuild.testing.lang.type.FakeFile;

import com.google.common.collect.Iterables;

public class TempDirectoryTest {
  Path path = path("my/path");
  String content = "content";

  SValueBuilders valueBuilders = new SValueBuildersImpl(new FakeValueDb());
  java.nio.file.Path rootPath = Paths.get("/fake/path");
  FakeFileSystem fileSystem = new FakeFileSystem();
  TempDirectory tempDirectory = new TempDirectory(valueBuilders, rootPath, fileSystem);

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
    FakeFile file = new FakeFile(path, content);
    tempDirectory.writeFile(file);
    fileSystem.assertFileContains(path, content);
  }

  @Test
  public void writing_file_after_destroy_throws_exception() throws Exception {
    tempDirectory.destroy();

    FakeFile file = new FakeFile(path, content);

    try {
      tempDirectory.writeFile(file);
      fail("exception should be thrown");
    } catch (IllegalStateException e) {
      // expected
    }
  }

  @Test
  public void content_and_path_is_written_to_file_system() throws Exception {
    tempDirectory.writeFile(path, new FakeBlob(content));
    fileSystem.assertFileContains(path, content);
  }

  @Test
  public void writing_content_after_destroy_throws_exception() throws Exception {
    tempDirectory.destroy();

    try {
      tempDirectory.writeFile(path, new FakeBlob(content));
      fail("exception should be thrown");
    } catch (IllegalStateException e) {
      // expected
    }
  }

  @Test
  public void files_are_written_to_file_system() throws Exception {
    FakeFile file = new FakeFile(path, content);
    SArray<SFile> array = fakeArray(FILE_ARRAY, file);

    tempDirectory.writeFiles(array);

    fileSystem.assertFileContains(path, content);
  }

  @Test
  public void writing_files_after_destroy_throws_exception() throws Exception {
    tempDirectory.destroy();

    FakeFile file = new FakeFile(path, content);
    SArray<SFile> array = fakeArray(FILE_ARRAY, file);

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
    BlobTester.assertContains(blobContent, content);
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
