package org.smoothbuild.lang.type.solver;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.type.solver.DeduceVarMap.deduceVarMap;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.testing.type.TestingTS.ANY;
import static org.smoothbuild.testing.type.TestingTS.BLOB;
import static org.smoothbuild.testing.type.TestingTS.BOOL;
import static org.smoothbuild.testing.type.TestingTS.INT;
import static org.smoothbuild.testing.type.TestingTS.NOTHING;
import static org.smoothbuild.testing.type.TestingTS.STRING;
import static org.smoothbuild.testing.type.TestingTS.VAR_A;
import static org.smoothbuild.testing.type.TestingTS.VAR_B;
import static org.smoothbuild.testing.type.TestingTS.VAR_C;
import static org.smoothbuild.testing.type.TestingTS.a;
import static org.smoothbuild.testing.type.TestingTS.f;
import static org.smoothbuild.testing.type.TestingTS.struct;
import static org.smoothbuild.util.collect.NList.nList;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.lang.type.MonoTS;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.lang.type.VarS;

import com.google.common.collect.ImmutableMap;

public class DeduceVarMapTest {

  @ParameterizedTest
  @MethodSource("deduce")
  public void deduce(MonoTS source, MonoTS target, Map<VarS, TypeS> expected) {
    assertThat(deduceVarMap(source, target))
        .isEqualTo(expected);
  }

  public static List<Arguments> deduce() {
    return List.of(
        arguments(INT, INT, empty()),
        arguments(a(INT), a(INT), empty()),
        arguments(f(INT), f(INT), empty()),

        arguments(NOTHING, INT, empty()),
        arguments(a(NOTHING), a(INT), empty()),
        arguments(f(NOTHING), f(INT), empty()),
        arguments(f(INT, BLOB), f(INT, NOTHING), empty()),

        arguments(INT, ANY, empty()),
        arguments(a(INT), a(ANY), empty()),
        arguments(f(INT), f(ANY), empty()),
        arguments(f(INT, ANY), f(INT, BLOB), empty()),

        arguments(f(INT, VAR_A, VAR_A), f(INT, NOTHING, BLOB), map(VAR_A, BLOB)),

        arguments(VAR_A, INT, map(VAR_A, INT)),
        arguments(a(VAR_A), a(INT), map(VAR_A, INT)),
        arguments(a(a(VAR_A)), a(a(INT)), map(VAR_A, INT)),
        arguments(struct("MyStruct", nList()), struct("MyStruct", nList()), empty()),
        arguments(f(VAR_A), f(INT), map(VAR_A, INT)),
        arguments(f(VAR_A, VAR_A), f(INT, INT), map(VAR_A, INT)),
        arguments(f(INT, VAR_A), f(INT, BLOB), map(VAR_A, BLOB)),
        arguments(
            f(VAR_A, VAR_B),
            f(INT, BLOB),
            ImmutableMap.of(VAR_A, INT, VAR_B, BLOB)),
        arguments(
            f(VAR_A, VAR_B, VAR_C),
            f(INT, BLOB, BOOL),
            ImmutableMap.of(VAR_A, INT, VAR_B, BLOB, VAR_C, BOOL))
    );
  }

  @ParameterizedTest
  @MethodSource("deduction_causes_exception")
  public void deduction_causes_exception(MonoTS source, MonoTS target) {
    assertCall(() -> deduceVarMap(source, target))
        .throwsException(IllegalArgumentException.class);
  }

  public static List<Arguments> deduction_causes_exception() {
    return List.of(
        arguments(
            STRING, INT
        ),

        arguments(
            struct("MyStruct", nList()),
            struct("MyStruct2", nList())),

        // deduce var bounds doesn't fulfill lower-bound < upper bound
        arguments(
            f(VAR_A, VAR_A),
            f(INT, BOOL))
    );
  }

  @Test
  public void REMOVE() {
    assertCall(() -> deduceVarMap(f(VAR_A, VAR_A), f(INT, BOOL)))
        .throwsException(IllegalArgumentException.class);
  }

  private static ImmutableMap<VarS, MonoTS> map(VarS key, MonoTS value) {
    return ImmutableMap.of(key, value);
  }

  private static ImmutableMap<Object, Object> empty() {
    return ImmutableMap.of();
  }
}
