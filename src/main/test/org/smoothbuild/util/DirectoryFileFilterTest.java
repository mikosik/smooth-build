package org.smoothbuild.util;

import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class DirectoryFileFilterTest {
  private DirectoryFileFilter directoryFileFilter;
  private File file;

  @Before
  public void before() {
    givenTest(this);
    given(directoryFileFilter = new DirectoryFileFilter());
  }

  @Test
  public void directory_file_is_accepted() {
    given(willReturn(true), file).isDirectory();
    when(directoryFileFilter.accept(file));
    thenReturned(true);
  }

  @Test
  public void normal_file_is_not_accepted() {
    given(willReturn(false), file).isDirectory();
    when(directoryFileFilter.accept(file));
    thenReturned(false);
  }
}
