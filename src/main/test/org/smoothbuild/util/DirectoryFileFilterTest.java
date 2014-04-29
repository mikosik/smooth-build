package org.smoothbuild.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.willReturn;

import java.io.File;

import org.junit.Test;

public class DirectoryFileFilterTest {
  DirectoryFileFilter directoryFileFilter = new DirectoryFileFilter();

  @Test
  public void directory_file_is_accepted() {
    File file = mock(File.class);
    given(willReturn(true), file).isDirectory();
    assertThat(directoryFileFilter.accept(file)).isTrue();
  }

  @Test
  public void normal_file_is_not_accepted() {
    File file = mock(File.class);
    given(willReturn(false), file).isDirectory();
    assertThat(directoryFileFilter.accept(file)).isFalse();
  }
}
