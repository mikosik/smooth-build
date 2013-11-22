package org.smoothbuild.lang.function.def.args;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.type.Type.EMPTY_ARRAY;
import static org.smoothbuild.lang.type.Type.FILE;
import static org.smoothbuild.lang.type.Type.FILE_ARRAY;
import static org.smoothbuild.lang.type.Type.STRING;
import static org.smoothbuild.lang.type.Type.STRING_ARRAY;

import java.util.Set;

import org.junit.Test;
import org.smoothbuild.lang.type.Type;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class HelpersTest {

  @Test
  public void createMap() {
    ImmutableMap<Type, Set<Object>> map = Helpers.createMap(ImmutableSet.of(STRING, STRING_ARRAY));

    assertThat(map.get(STRING)).isEmpty();
    assertThat(map.get(STRING_ARRAY)).isEmpty();

    assertThat(map).doesNotContainKey(FILE);
    assertThat(map).doesNotContainKey(FILE_ARRAY);
    assertThat(map).doesNotContainKey(EMPTY_ARRAY);
  }
}
