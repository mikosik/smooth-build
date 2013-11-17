package org.smoothbuild.lang.function.def.args;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.function.base.Type.EMPTY_SET;
import static org.smoothbuild.lang.function.base.Type.FILE;
import static org.smoothbuild.lang.function.base.Type.FILE_SET;
import static org.smoothbuild.lang.function.base.Type.STRING;
import static org.smoothbuild.lang.function.base.Type.STRING_SET;

import java.util.Set;

import org.junit.Test;
import org.smoothbuild.lang.function.base.Type;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class HelpersTest {

  @Test
  public void createMap() {
    ImmutableMap<Type, Set<Object>> map = Helpers.createMap(ImmutableSet.of(STRING, STRING_SET));

    assertThat(map.get(STRING)).isEmpty();
    assertThat(map.get(STRING_SET)).isEmpty();

    assertThat(map).doesNotContainKey(FILE);
    assertThat(map).doesNotContainKey(FILE_SET);
    assertThat(map).doesNotContainKey(EMPTY_SET);
  }
}
