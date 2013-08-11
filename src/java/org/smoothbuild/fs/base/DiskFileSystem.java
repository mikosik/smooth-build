package org.smoothbuild.fs.base;

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

import org.smoothbuild.lang.type.Path;

// TODO test this class
public class DiskFileSystem implements FileSystem {
  @Override
  public boolean pathExists(Path path) {
    return new File(path.value()).exists();
  }

  @Override
  public boolean isDirectory(Path path) {
    return new File(path.value()).isDirectory();
  }

  @Override
  public Iterable<String> childNames(Path directory) {
    String[] list = new File(directory.value()).list();
    if (list == null) {
      throw new FileSystemException("Path " + directory + " is not a directory");
    } else {
      return Arrays.asList(list);
    }
  }

  @Override
  public Iterable<Path> filesFrom(Path directory) {
    return new RecursiveFilesIterable(this, directory);
  }

  private void createDirectory(Path path) {
    File directory = new File(path.value());
    if (!directory.exists()) {
      createDirectory(path.parent());
      if (!directory.mkdir()) {
        throw new FileSystemException("Could not create directory '" + path + "'.");
      }
    }
  }

  @Override
  public InputStream createInputStream(Path path) {
    try {
      return new FileInputStream(path.value());
    } catch (FileNotFoundException e) {
      throw new FileSystemException("Could not create InputStream for '" + path + "'", e);
    }
  }

  @Override
  public OutputStream createOutputStream(Path path) {
    if (path.isRoot()) {
      throw new FileSystemException("Cannot open file '" + path + "' as it is directory.");
    }
    createDirectory(path.parent());

    try {
      return new FileOutputStream(path.value());
    } catch (FileNotFoundException e) {
      throw new FileSystemException("Could not create OutputStream for '" + path + "'", e);
    }
  }

  @Override
  public void copy(Path from, Path to) {
    createDirectory(to.parent());
    copyImpl(from, to);
  }

  private void copyImpl(Path from, Path to) {
    try {
      copy(new File(from.value()), new File(to.value()));
    } catch (IOException e) {
      throw new FileSystemException("Could not copy from '" + from + "' to '" + to + "'", e);
    }
  }

  private static void copy(File from, File to) throws IOException {
    try (RandomAccessFile fromFile = new RandomAccessFile(from, "r");
        RandomAccessFile toFile = new RandomAccessFile(to, "rw");) {
      final FileChannel fromChannel = fromFile.getChannel();
      final FileChannel toChannel = toFile.getChannel();
      long toCopy = fromFile.length();
      long position = 0;
      while (toCopy > 0) {
        long copiedCount = fromChannel.transferTo(position, toCopy, toChannel);
        position += copiedCount;
        toCopy -= copiedCount;
      }
    }
  }
}
