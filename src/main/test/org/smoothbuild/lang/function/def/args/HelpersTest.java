package org.smoothbuild.lang.function.def.args;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.base.STypes.FILE;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.base.STypes.NIL;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;

import java.util.Set;

import org.junit.Test;
import org.smoothbuild.lang.base.SType;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class HelpersTest {

  @Test
  public void createMap() {
    ImmutableSet<SType<?>> set = ImmutableSet.of(STRING, STRING_ARRAY);
    ImmutableMap<SType<?>, Set<Object>> map = Helpers.createMap(set);

    assertThat(map.get(STRING)).isEmpty();
    assertThat(map.get(STRING_ARRAY)).isEmpty();

    assertThat(map).doesNotContainKey(FILE);
    assertThat(map).doesNotContainKey(FILE_ARRAY);
    assertThat(map).doesNotContainKey(NIL);
  }
}
