package org.smoothbuild.io.fs.disk;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
    assertFalse(file.exists());
  }

  @Test
  public void deleting_sigle_file_does_not_delete_its_directory() throws Exception {
    File directory = createDir(root, "dir");
    File file = createEmptyFile(directory, "file");

    RecursiveDeleter.deleteRecursively(file.toPath());

    assertTrue(directory.exists());
  }

  @Test
  public void deleting_symbolic_link_to_file() throws Exception {
    File file = createEmptyFile(root, "file");
    File link = new File(root, "link");
    Files.createSymbolicLink(link.toPath(), file.toPath());

    RecursiveDeleter.deleteRecursively(link.toPath());

    assertFalse(link.exists());
    assertTrue(file.exists());
  }

  @Test
  public void deleting_symbolic_link_to_directory() throws Exception {
    File dir = createDir(root, "dir");
    File file = createEmptyFile(dir, "file");
    File link = new File(root, "link");
    Files.createSymbolicLink(link.toPath(), dir.toPath());

    RecursiveDeleter.deleteRecursively(link.toPath());

    assertTrue(dir.exists());
    assertTrue(file.exists());
    assertFalse(link.exists());
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
    assertTrue(fileOutside.exists());

    assertFalse(mainDir.exists());
    assertFalse(directFile.exists());
    assertFalse(directDir.exists());
    assertFalse(notDirectFile.exists());
    assertFalse(notDirectDir.exists());
  }

  @Test
  public void deleting_recursively_directory_with_symbolic_link_does_not_delete_link_target()
      throws Exception {
    File file = createEmptyFile(root, "file");
    File dir = createDir(root, "dir");
    File link = new File(dir, "link");
    Files.createSymbolicLink(link.toPath(), file.toPath());

    RecursiveDeleter.deleteRecursively(dir.toPath());

    assertTrue(file.exists());
    assertFalse(dir.exists());
    assertFalse(link.exists());
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
