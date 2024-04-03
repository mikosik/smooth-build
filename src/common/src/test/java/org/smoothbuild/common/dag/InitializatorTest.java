package org.smoothbuild.common.dag;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.base.Try.failure;
import static org.smoothbuild.common.log.base.Try.success;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.log.base.Try;

public class InitializatorTest {
  @Test
  void fails_when_initializable_fails() {
    Try<Void> failure = failure(error("message"));
    var initializable = mock(Initializable.class);
    when(initializable.initialize()).thenReturn(failure);

    var result = new Initializator(Set.of(initializable)).apply();

    assertThat(result).isEqualTo(failure);
  }

  @Test
  void succeeds_when_initializable_succeeds() {
    Try<Void> success = success(null);
    var initializable = mock(Initializable.class);
    when(initializable.initialize()).thenReturn(success);

    var result = new Initializator(Set.of(initializable)).apply();

    assertThat(result).isEqualTo(success(null));
  }
}
