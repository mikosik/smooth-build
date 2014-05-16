package org.smoothbuild.io.util;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import com.google.common.collect.Lists;

public class TempDirectoryManager {
  private final Provider<TempDirectory> tempDirectoryProvider;
  private final List<TempDirectory> tempDirectories;

  @Inject
  public TempDirectoryManager(Provider<TempDirectory> tempDirectoryProvider) {
    this.tempDirectoryProvider = tempDirectoryProvider;
    this.tempDirectories = Lists.newArrayList();
  }

  public TempDirectory createTempDirectory() {
    TempDirectory tempDirectory = tempDirectoryProvider.get();
    tempDirectories.add(tempDirectory);
    return tempDirectory;
  }

  public void destroyTempDirectories() {
    for (TempDirectory tempDirectory : tempDirectories) {
      tempDirectory.destroy();
    }
  }
}
