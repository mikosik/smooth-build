package org.smoothbuild.io.temp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import com.google.inject.util.Providers;

public class TempDirectoryManagerTest {
  TempDirectory tempDirectory = mock(TempDirectory.class);
  TempDirectoryManager tempDirectoryManager = new TempDirectoryManager(Providers.of(tempDirectory));

  @Test
  public void create_returns_object_from_provider() {
    assertThat(tempDirectoryManager.createTempDirectory()).isSameAs(tempDirectory);
  }

  @Test
  public void destroy_is_forwarded_to_created_temp_directories() throws Exception {
    TempDirectory created = tempDirectoryManager.createTempDirectory();
    tempDirectoryManager.destroyTempDirectories();
    verify(created).destroy();
  }
}
