package org.smoothbuild.fs.base;

import static org.smoothbuild.fs.base.PathUtils.WORKING_DIR;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.List;


// TODO test this class
public class DiskFileSystem implements FileSystem {
  @Override
  public boolean pathExists(String path) {
    return new File(path).exists();
  }

  @Override
  public boolean isDirectory(String path) {
    return new File(path).isDirectory();
  }

  @Override
  public List<String> childNames(String directory) {
    String[] list = new File(directory).list();
    if (list == null) {
      throw new FileSystemException("Path '" + directory + "' is not a directory");
    } else {
      return Arrays.asList(list);
    }
  }

  @Override
  public Iterable<String> filesFrom(String directory) {
    return new RecursiveFilesIterable(this, directory);
  }

  private void createDirectory(String path) {
    File directory = new File(path);
    if (!directory.exists()) {
      createDirectory(PathUtils.parentOf(path));
      if (!directory.mkdir()) {
        throw new FileSystemException("Could not create directory '" + path + "'.");
      }
    }
  }

  @Override
  public InputStream createInputStream(String path) {
    try {
      return new FileInputStream(path);
    } catch (FileNotFoundException e) {
      throw new FileSystemException("Could not create InputStream for '" + path + "'", e);
    }
  }

  @Override
  public OutputStream createOutputStream(String path) {
    if (path.equals(WORKING_DIR)) {
      throw new FileSystemException("Cannot open file '" + path + "' as it is directory.");
    }
    createDirectory(PathUtils.parentOf(path));

    try {
      return new FileOutputStream(path);
    } catch (FileNotFoundException e) {
      throw new FileSystemException("Could not create OutputStream for '" + path + "'", e);
    }
  }

  @Override
  public void copy(String from, String to) {
    createDirectory(PathUtils.parentOf(to));
    copyImpl(from, to);
  }

  private void copyImpl(String from, String to) {
    try {
      copy(new File(from), new File(to));
    } catch (IOException e) {
      throw new FileSystemException("Could not copy from '" + from + "' to '" + to + "'", e);
    }
  }

  private static void copy(File from, File to) throws IOException {
    final RandomAccessFile fromFile = new RandomAccessFile(from, "r");
    try {
      final RandomAccessFile toFile = new RandomAccessFile(to, "rw");
      try {
        final FileChannel fromChannel = fromFile.getChannel();
        final FileChannel toChannel = toFile.getChannel();
        long toCopy = fromFile.length();
        long position = 0;
        while (toCopy > 0) {
          long copiedCount = fromChannel.transferTo(position, toCopy, toChannel);
          position += copiedCount;
          toCopy -= copiedCount;
        }
      } finally {
        toFile.close();
      }
    } finally {
      fromFile.close();
    }
  }
}
