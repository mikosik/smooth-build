package org.smoothbuild.common.dag;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.Set.set;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.base.Log.info;
import static org.smoothbuild.common.log.base.Try.failure;
import static org.smoothbuild.common.log.base.Try.success;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.init.Initializable;
import org.smoothbuild.common.init.Initializer;
import org.smoothbuild.common.log.base.Try;

public class InitializerTest {
  @Test
  void fails_when_initializable_fails() {
    Try<Void> failure = failure(error("message"));
    Initializable initializable = () -> failure;

    var result = new Initializer(Set.of(initializable)).apply();

    assertThat(result).isEqualTo(failure);
  }

  @Test
  void succeeds_when_initializable_succeeds() {
    var info = info("message");
    Try<Void> success = success(null, info);
    Initializable initializable = () -> success;

    var result = new Initializer(Set.of(initializable)).apply();

    assertThat(result).isEqualTo(success(null, info));
  }

  @Test
  void on_success_returned_result_contains_logs_from_all_initializables() {
    var info1 = info("message1");
    Try<Void> success1 = success(null, info1);
    Initializable initializable1 = () -> success1;
    var info2 = info("message2");
    Try<Void> success2 = success(null, info2);
    Initializable initializable2 = () -> success2;

    var result = new Initializer(set(initializable1, initializable2)).apply();

    assertThat(result).isEqualTo(success(null, info1, info2));
  }

  @Test
  void on_failure_returned_result_contains_logs_from_failed_initializable_and_all_preceding() {
    var info1 = info("message1");
    Try<Void> success1 = success(null, info1);
    Initializable initializable1 = () -> success1;

    var error = error("message");
    Try<Void> failure = failure(error);
    Initializable failedInitializable = () -> failure;

    var info2 = info("message2");
    Try<Void> success2 = success(null, info2);
    Initializable initializable2 = () -> success2;

    var result = new Initializer(set(initializable1, failedInitializable, initializable2)).apply();

    assertThat(result).isEqualTo(failure(info1, error));
  }
}
