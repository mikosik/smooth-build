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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.fs.base.exc.CannotCreateFileException;
import org.smoothbuild.fs.base.exc.FileSystemException;
import org.smoothbuild.fs.base.exc.NoSuchDirException;
import org.smoothbuild.fs.base.exc.NoSuchFileException;
import org.smoothbuild.plugin.api.Path;

import com.google.common.annotations.VisibleForTesting;

@Singleton
public class DiskFileSystem implements FileSystem {
  private final String projectRoot;

  @Inject
  public DiskFileSystem() {
    this(null);
  }

  @VisibleForTesting
  DiskFileSystem(String projectRoot) {
    this.projectRoot = projectRoot;
  }

  @Override
  public Path root() {
    return Path.rootPath();
  }

  @Override
  public boolean pathExists(Path path) {
    return jdkFile(path).exists();
  }

  @Override
  public boolean pathExistsAndIsDirectory(Path path) {
    return jdkFile(path).isDirectory();
  }

  public boolean pathExistsAndIsFile(Path path) {
    return jdkFile(path).isFile();
  }

  @Override
  public Iterable<String> childNames(Path directory) {
    String[] list = jdkFile(directory).list();
    if (list == null) {
      throw new NoSuchDirException(directory);
    } else {
      return Arrays.asList(list);
    }
  }

  @Override
  public Iterable<Path> filesFrom(Path directory) {
    return new RecursiveFilesIterable(this, directory);
  }

  private void createDirectory(Path path) {
    File directory = jdkFile(path);
    if (!directory.exists()) {
      createDirectory(path.parent());
      if (!directory.mkdir()) {
        throw new FileSystemException("Could not create directory '" + path + "'.");
      }
    }
  }

  @Override
  public InputStream openInputStream(Path path) {
    try {
      return new FileInputStream(jdkFile(path));
    } catch (FileNotFoundException e) {
      throw new NoSuchFileException(path, e);
    }
  }

  @Override
  public OutputStream openOutputStream(Path path) {
    if (path.isRoot()) {
      throw new FileSystemException("Cannot open file '" + path + "' as it is directory.");
    }
    createDirectory(path.parent());

    try {
      return new FileOutputStream(jdkFile(path));
    } catch (FileNotFoundException e) {
      throw new CannotCreateFileException(path, e);
    }
  }

  @Override
  public void copy(Path from, Path to) {
    createDirectory(to.parent());
    copyImpl(from, to);
  }

  private void copyImpl(Path from, Path to) {
    try {
      copy(jdkFile(from), jdkFile(to));
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

  public void deleteDirectoryRecursively(Path directory) {
    if (pathExistsAndIsDirectory(directory)) {
      try {
        RecursiveDirectoryDeleter.deleteRecursively(jdkFile(directory));
      } catch (IOException e) {
        throw new FileSystemException(e);
      }
    } else {
      throw new NoSuchDirException(directory);
    }
  }

  private File jdkFile(Path path) {
    return new File(projectRoot, path.value());
  }
}
