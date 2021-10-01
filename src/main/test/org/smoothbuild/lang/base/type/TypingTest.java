package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.type.TestingTypes.BLOB;
import static org.smoothbuild.lang.base.type.TestingTypes.PERSON;
import static org.smoothbuild.lang.base.type.TestingTypes.a;
import static org.smoothbuild.lang.base.type.TestingTypes.f;
import static org.smoothbuild.lang.base.type.TestingTypes.item;
import static org.smoothbuild.lang.base.type.Types.BASE_TYPES;
import static org.smoothbuild.util.Lists.map;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class TypingTest extends TestingContext {
  @ParameterizedTest
  @MethodSource("strip_test_data")
  public void strip(Type type, Type expected) {
    assertThat(typing().strip(type))
        .isEqualTo(expected);
  }

  public static List<Arguments> strip_test_data() {
    ImmutableList<Type> unchangedByStripping = ImmutableList.<Type>builder()
        .addAll(BASE_TYPES)
        .add(PERSON)
        .add(f(BLOB))
        .add(f(BLOB, BLOB))
        .add(f(f(BLOB), BLOB))
        .add(f(BLOB, f(BLOB)))
        .build();
    ImmutableList<Type> unchanged = ImmutableList.<Type>builder()
        .addAll(map(unchangedByStripping, t -> t))
        .addAll(map(unchangedByStripping, t -> a(t)))
        .addAll(map(unchangedByStripping, t -> a(a(t))))
        .build();

    return ImmutableList.<Arguments>builder()
        .addAll(map(unchanged, t -> Arguments.of(t, t)))
        .add(Arguments.of(f(BLOB, item(BLOB, "p")), f(BLOB, BLOB)))
        .add(Arguments.of(f(f(BLOB, item(BLOB, "p")), BLOB), f(f(BLOB, BLOB), BLOB)))
        .add(Arguments.of(f(BLOB, f(BLOB, item(BLOB, "p"))), f(BLOB, f(BLOB, BLOB))))
        .add(Arguments.of(a(f(BLOB, item(BLOB, "p"))), a(f(BLOB, BLOB))))
        .build();
  }
}
