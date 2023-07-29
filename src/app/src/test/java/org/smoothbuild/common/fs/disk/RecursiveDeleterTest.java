package org.smoothbuild.common.fs.disk;

import static com.google.common.truth.Truth.assertThat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RecursiveDeleterTest {
  private List<File> files;
  private File root;

  @BeforeEach
  public void before() throws IOException {
    files = new ArrayList<>();
    root = Files.createTempDirectory("temp").toFile();
    files.add(root);
  }

  @AfterEach
  public void after() {
    for (int i = files.size() - 1; 0 <= i; i--) {
      File file = files.get(i);
      if (file.exists()) {
        file.delete();
      }
    }
  }

  @Test
  public void deleting_non_existent_file_succeeds() throws Exception {
    File dir = createDir(root, "dir");
    File file = new File(dir, "file");
    RecursiveDeleter.deleteRecursively(file.toPath());
    assertThat(file.exists())
        .isFalse();
  }

  @Test
  public void deleting_sigle_file() throws Exception {
    File file = createEmptyFile(root, "file");
    RecursiveDeleter.deleteRecursively(file.toPath());
    assertThat(file.exists())
        .isFalse();
  }

  @Test
  public void deleting_sigle_file_does_not_delete_its_dir() throws Exception {
    File dir = createDir(root, "dir");
    File file = createEmptyFile(dir, "file");

    RecursiveDeleter.deleteRecursively(file.toPath());

    assertThat(dir.exists())
        .isTrue();
  }

  @Test
  public void deleting_symbolic_link_to_file() throws Exception {
    File file = createEmptyFile(root, "file");
    File link = new File(root, "link");
    Files.createSymbolicLink(link.toPath(), file.toPath());

    RecursiveDeleter.deleteRecursively(link.toPath());

    assertThat(link.exists())
        .isFalse();
    assertThat(file.exists())
        .isTrue();
  }

  @Test
  public void deleting_symbolic_link_to_dir() throws Exception {
    File dir = createDir(root, "dir");
    File file = createEmptyFile(dir, "file");
    File link = new File(root, "link");
    Files.createSymbolicLink(link.toPath(), dir.toPath());

    RecursiveDeleter.deleteRecursively(link.toPath());

    assertThat(dir.exists())
        .isTrue();
    assertThat(file.exists())
        .isTrue();
    assertThat(link.exists())
        .isFalse();
  }

  @Test
  public void deleting_recursively_dir() throws Exception {
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
    assertThat(fileOutside.exists())
        .isTrue();

    assertThat(mainDir.exists())
        .isFalse();
    assertThat(directFile.exists())
        .isFalse();
    assertThat(directDir.exists())
        .isFalse();
    assertThat(notDirectFile.exists())
        .isFalse();
    assertThat(notDirectDir.exists())
        .isFalse();
  }

  @Test
  public void deleting_recursively_dir_with_symbolic_link_does_not_delete_link_target()
      throws Exception {
    File file = createEmptyFile(root, "file");
    File dir = createDir(root, "dir");
    File link = new File(dir, "link");
    Files.createSymbolicLink(link.toPath(), file.toPath());

    RecursiveDeleter.deleteRecursively(dir.toPath());

    assertThat(file.exists())
        .isTrue();
    assertThat(dir.exists())
        .isFalse();
    assertThat(link.exists())
        .isFalse();
  }

  private File createDir(File root, String dirName) {
    File dir = new File(root, dirName);
    dir.mkdirs();
    files.add(dir);
    return dir;
  }

  private File createEmptyFile(File root, String fileName) throws IOException {
    File file = new File(root, fileName);
    new FileOutputStream(file).close();
    files.add(file);
    return file;
  }
}
