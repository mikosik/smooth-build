package org.smoothbuild.io.temp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.when;

import org.junit.Test;

import com.google.inject.util.Providers;

public class TempDirectoryManagerTest {
  TempDirectory tempDirectory = mock(TempDirectory.class);
  TempDirectoryManager tempDirectoryManager = new TempDirectoryManager(Providers.of(tempDirectory));

  TempDirectory created;

  @Test
  public void create_returns_object_from_provider() {
    assertThat(tempDirectoryManager.createTempDirectory()).isSameAs(tempDirectory);
  }

  @Test
  public void destroy_is_forwarded_to_created_temp_directories() throws Exception {
    given(created = tempDirectoryManager.createTempDirectory());
    when(tempDirectoryManager).destroyTempDirectories();
    thenCalled(created).destroy();
  }
}
