package org.smoothbuild.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Test;

public class DirectoryFileFilterTest {
  DirectoryFileFilter directoryFileFilter = new DirectoryFileFilter();

  @Test
  public void directory_file_is_accepted() {
    File file = mock(File.class);
    when(file.isDirectory()).thenReturn(true);
    assertThat(directoryFileFilter.accept(file)).isTrue();
  }

  @Test
  public void normal_file_is_not_accepted() {
    File file = mock(File.class);
    when(file.isDirectory()).thenReturn(false);
    assertThat(directoryFileFilter.accept(file)).isFalse();
  }
}
