package org.smoothbuild.io.util;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

public class TempDirectoryManager {
  private final Provider<TempDirectory> tempDirectoryProvider;
  private final List<TempDirectory> tempDirectories;

  @Inject
  public TempDirectoryManager(Provider<TempDirectory> tempDirectoryProvider) {
    this.tempDirectoryProvider = tempDirectoryProvider;
    this.tempDirectories = new ArrayList<>();
  }

  public TempDirectory createTempDirectory() {
    TempDirectory tempDirectory = tempDirectoryProvider.get();
    tempDirectories.add(tempDirectory);
    return tempDirectory;
  }
}
