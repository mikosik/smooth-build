package org.smoothbuild.fs.disk;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.fs.disk.RecursiveDeleter;
import org.smoothbuild.testing.common.JdkFileTester;

import com.google.common.collect.Lists;

public class RecursiveDeleterTest {
  List<File> files;
  File root;

  @Before
  public void before() throws IOException {
    files = Lists.newArrayList();
    root = com.google.common.io.Files.createTempDir();
    files.add(root);
  }

  @After
  public void after() {
    for (int i = files.size() - 1; 0 <= i; i--) {
      File file = files.get(i);
      if (file.exists()) {
        file.delete();
      }
    }
  }

  @Test
  public void deleting_sigle_file() throws Exception {
    File file = createEmptyFile(root, "file");
    RecursiveDeleter.deleteRecursively(file.toPath());
    assertThat(file.exists()).isFalse();
  }

  @Test
  public void deleting_sigle_file_does_not_delete_its_directory() throws Exception {
    File directory = createDir(root, "dir");
    File file = createEmptyFile(directory, "file");

    RecursiveDeleter.deleteRecursively(file.toPath());

    assertThat(directory.exists()).isTrue();
  }

  @Test
  public void deleting_symbolic_link_to_file() throws Exception {
    File file = createEmptyFile(root, "file");
    File link = new File(root, "link");
    Files.createSymbolicLink(link.toPath(), file.toPath());

    RecursiveDeleter.deleteRecursively(link.toPath());

    assertThat(link.exists()).isFalse();
    assertThat(file.exists()).isTrue();
  }

  @Test
  public void deleting_symbolic_link_to_directory() throws Exception {
    File dir = createDir(root, "dir");
    File file = createEmptyFile(dir, "file");
    File link = new File(root, "link");
    Files.createSymbolicLink(link.toPath(), dir.toPath());

    RecursiveDeleter.deleteRecursively(link.toPath());

    assertThat(dir.exists()).isTrue();
    assertThat(file.exists()).isTrue();
    assertThat(link.exists()).isFalse();
  }

  @Test
  public void deleting_recursively_directory() throws Exception {
    // given
    File fileOutside = createEmptyFile(root, "fileOutsideMain");

    String mainDirName = "mainDir";
    File mainDir = createDir(root, mainDirName);

    String directFileName = "directFile";
    File directFile = createEmptyFile(mainDir, directFileName);

    String directDirName = "directDir";
    File directDir = createDir(mainDir, directDirName);

    String notDirectFileName = "notDirectFile";
    File notDirectFile = createEmptyFile(directDir, notDirectFileName);

    String notDirectDirName = "notDirectDir";
    File notDirectDir = createDir(directDir, notDirectDirName);

    // when
    RecursiveDeleter.deleteRecursively(mainDir.toPath());

    // then
    assertThat(fileOutside.exists()).isTrue();

    assertThat(mainDir.exists()).isFalse();
    assertThat(directFile.exists()).isFalse();
    assertThat(directDir.exists()).isFalse();
    assertThat(notDirectFile.exists()).isFalse();
    assertThat(notDirectDir.exists()).isFalse();
  }

  @Test
  public void deleting_recursively_directory_that_contains_symbolic_link() throws Exception {
    File file = createEmptyFile(root, "file");
    File dir = createDir(root, "dir");
    File link = new File(dir, "link");
    Files.createSymbolicLink(link.toPath(), file.toPath());

    RecursiveDeleter.deleteRecursively(dir.toPath());

    assertThat(file.exists()).isTrue();
    assertThat(dir.exists()).isFalse();
    assertThat(link.exists()).isFalse();
  }

  @Test
  public void deleting_directory_recursively_does_not_follow_symbolic_links() throws Exception {

  }

  private File createDir(File root, String dirName) {
    File file = JdkFileTester.createDir(root, dirName);
    files.add(file);
    return file;
  }

  private File createEmptyFile(File root, String fileName) throws IOException {
    File file = JdkFileTester.createEmptyFile(root, fileName);
    files.add(file);
    return file;
  }
}
