package org.smoothbuild.stdlib.common;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.log.base.Log.error;

import org.junit.jupiter.api.Test;
import org.smoothbuild.stdlib.StandardLibraryTestContext;

public class FilterByMaskTest extends StandardLibraryTestContext {
  @Test
  void returns_filtered_list() throws Exception {
    var userModule =
        """
        result = filterByMask([1, 2, 3, 4, 5, 6], [true, false, true, false, true, false]);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bInt(1), bInt(3), bInt(5)));
  }

  @Test
  void returns_whole_list_when_all_masks_are_true() throws Exception {
    var userModule =
        """
        result = filterByMask([1, 2, 3, 4, 5, 6], [true, true, true, true, true, true]);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bInt(1), bInt(2), bInt(3), bInt(4), bInt(5), bInt(6)));
  }

  @Test
  void fails_when_list_is_longer_than_masks() throws Exception {
    var userModule = """
        result = filterByMask([1, 2], [true]);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(logs()).containsExactly(error("'array' has more elements than 'masks'."));
  }

  @Test
  void fails_when_list_is_shorter_than_masks() throws Exception {
    var userModule = """
        result = filterByMask([1], [true, true]);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(logs()).containsExactly(error("'array' has less elements than 'masks'."));
  }
}
