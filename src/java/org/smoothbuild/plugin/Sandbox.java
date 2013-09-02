package org.smoothbuild.plugin;

public interface Sandbox {
  public FileList resultFileList();

  public File resultFile(Path path);
}
