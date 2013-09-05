package org.smoothbuild.plugin.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.plugin.api.Path.path;

import org.junit.Test;

public class MutableFileSetTest {
  File file = file(path("my/file"));
  File file2 = file(path("my/file2"));

  MutableFileSet mutableFileSet = new MutableFileSet();

  @Test
  public void initiallyIteratorReturnsNothing() {
    assertThat(mutableFileSet.iterator()).isEmpty();
  }

  @Test
  public void iteratorReturnsAddedFiles() throws Exception {
    mutableFileSet.add(file);
    mutableFileSet.add(file2);

    assertThat(mutableFileSet).containsOnly(file, file2);
  }

  @Test
  public void containsAddedFile() throws Exception {
    mutableFileSet.add(file);
    assertThat(mutableFileSet.contains(file.path())).isTrue();
  }

  @Test
  public void doesNotContainsNotAddedFile() throws Exception {
    assertThat(mutableFileSet.contains(file.path())).isFalse();
  }

  @Test(expected = IllegalArgumentException.class)
  public void fetchingNonexistentFileThrowsException() throws Exception {
    mutableFileSet.file(path("nonexistent"));
  }

  @Test
  public void addedFileCanBeRetrieved() throws Exception {
    mutableFileSet.add(file);
    assertThat(mutableFileSet.file(file.path())).isSameAs(file);
  }

  @Test
  public void addingTwoFilesWithTheSamePathThrowsException() throws Exception {
    mutableFileSet.add(file);
    File twinFile = file(file.path());
    try {
      mutableFileSet.add(twinFile);
      fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  private static File file(Path path) {
    File file = mock(File.class);
    when(file.path()).thenReturn(path);
    return file;
  }
}
