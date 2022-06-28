package org.smoothbuild.lang.type.solver;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.type.solver.DeduceVarMap.deduceVarMap;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.testing.type.TestingTS.A;
import static org.smoothbuild.testing.type.TestingTS.ANY;
import static org.smoothbuild.testing.type.TestingTS.B;
import static org.smoothbuild.testing.type.TestingTS.BLOB;
import static org.smoothbuild.testing.type.TestingTS.BOOL;
import static org.smoothbuild.testing.type.TestingTS.C;
import static org.smoothbuild.testing.type.TestingTS.INT;
import static org.smoothbuild.testing.type.TestingTS.NOTHING;
import static org.smoothbuild.testing.type.TestingTS.STRING;
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

        arguments(f(INT, A, A), f(INT, NOTHING, BLOB), map(A, BLOB)),

        arguments(A, INT, map(A, INT)),
        arguments(a(A), a(INT), map(A, INT)),
        arguments(a(a(A)), a(a(INT)), map(A, INT)),
        arguments(struct("MyStruct", nList()), struct("MyStruct", nList()), empty()),
        arguments(f(A), f(INT), map(A, INT)),
        arguments(f(A, A), f(INT, INT), map(A, INT)),
        arguments(f(INT, A), f(INT, BLOB), map(A, BLOB)),
        arguments(
            f(A, B),
            f(INT, BLOB),
            ImmutableMap.of(A, INT, B, BLOB)),
        arguments(
            f(A, B, C),
            f(INT, BLOB, BOOL),
            ImmutableMap.of(A, INT, B, BLOB, C, BOOL))
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
            f(A, A),
            f(INT, BOOL))
    );
  }

  @Test
  public void REMOVE() {
    assertCall(() -> deduceVarMap(f(A, A), f(INT, BOOL)))
        .throwsException(IllegalArgumentException.class);
  }

  private static ImmutableMap<VarS, MonoTS> map(VarS key, MonoTS value) {
    return ImmutableMap.of(key, value);
  }

  private static ImmutableMap<Object, Object> empty() {
    return ImmutableMap.of();
  }
}
