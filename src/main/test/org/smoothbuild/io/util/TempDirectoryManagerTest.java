package org.smoothbuild.io.util;

import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.common.Matchers.same;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.util.Providers;

public class TempDirectoryManagerTest {
  private TempDirectory tempDirectory;
  private TempDirectoryManager tempDirectoryManager;
  private TempDirectory created;

  @Before
  public void before() {
    givenTest(this);
    given(tempDirectoryManager = new TempDirectoryManager(Providers.of(tempDirectory)));
  }

  @Test
  public void create_returns_object_from_provider() {
    given(tempDirectoryManager = new TempDirectoryManager(Providers.of(tempDirectory)));
    when(tempDirectoryManager.createTempDirectory());
    thenReturned(same(tempDirectory));
  }

  @Test
  public void destroy_is_forwarded_to_created_temp_directories() {
    given(created = tempDirectoryManager.createTempDirectory());
    when(tempDirectoryManager).destroyTempDirectories();
    thenCalled(created).destroy();
  }
}
