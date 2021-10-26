package org.smoothbuild.lang.base.type;

import static java.util.Arrays.stream;
import static org.smoothbuild.lang.base.type.TestedType.A;
import static org.smoothbuild.lang.base.type.TestedType.ANY;
import static org.smoothbuild.lang.base.type.TestedType.B;
import static org.smoothbuild.lang.base.type.TestedType.BLOB;
import static org.smoothbuild.lang.base.type.TestedType.INT;
import static org.smoothbuild.lang.base.type.TestedType.NOTHING;
import static org.smoothbuild.lang.base.type.TestedType.STRING;
import static org.smoothbuild.lang.base.type.TestedType.STRUCT;
import static org.smoothbuild.lang.base.type.TestedType.a;
import static org.smoothbuild.lang.base.type.TestedType.a2;
import static org.smoothbuild.lang.base.type.TestedType.f;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public record TestedAssignmentSpec(TestedAssignment assignment, boolean allowed) {
  TestedAssignmentSpec(TestedType target, TestedType source, boolean allowed) {
    this(new TestedAssignment(target, source), allowed);
  }

  public TestedType source() {
    return assignment.source();
  }

  public TestedType target() {
    return assignment.target();
  }

  public String declarations() {
    return assignment.declarations();
  }

  public String typeDeclarations() {
    return assignment.typeDeclarations();
  }

  @Override
  public String toString() {
    return assignment.toString() + " :" + (allowed ? "allowed" : "illegal");
  }

  public static TestedAssignmentSpec illegalAssignment(TestedType target, TestedType source) {
    return new TestedAssignmentSpec(target, source, false);
  }

  public static TestedAssignmentSpec allowedAssignment(TestedType target, TestedType source) {
    return new TestedAssignmentSpec(target, source, true);
  }

  public static List<TestedAssignmentSpec> assignment_test_specs(boolean includeAny) {
    var result = new ArrayList<TestedAssignmentSpec>();
    result.addAll(testSpecsCommonForNormalCaseAndParameterAssignment(includeAny));
    result.addAll(testSpecSpecificForNormalAssignment(includeAny));
    return result;
  }

  public static List<TestedAssignmentSpec> parameter_assignment_test_specs(boolean includeAny) {
    var result = new ArrayList<TestedAssignmentSpec>();
    result.addAll(testSpecsCommonForNormalCaseAndParameterAssignment(includeAny));
    result.addAll(testSpecsSpecificForParameterAssignment(includeAny));
    return result;
  }

  private static List<TestedAssignmentSpec> testSpecsCommonForNormalCaseAndParameterAssignment(
      boolean includeAny) {
    var r = new ArrayList<TestedAssignmentSpec>();
    if (includeAny) {
      gen(r, ANY, includeAny, mAll());
    }
    gen(r, BLOB, includeAny, oneOf(BLOB, NOTHING));
    gen(r, NOTHING, includeAny, oneOf(NOTHING));
    gen(r, STRUCT, includeAny, oneOf(STRUCT, NOTHING));

    if (includeAny) {
      gen(r, a(ANY), includeAny, TestedType::isArray, mNothing());
    }
    gen(r, a(BLOB), includeAny, oneOf(a(BLOB), a(NOTHING), NOTHING));
    gen(r, a(NOTHING), includeAny, oneOf(a(NOTHING), NOTHING));
    gen(r, a(STRUCT), includeAny, oneOf(a(STRUCT), a(NOTHING), NOTHING));
    if (includeAny) {
      gen(r, a2(ANY), includeAny, TestedType::isArrayOfArrays, t -> t.isArrayOf(NOTHING), mNothing());
    }
    gen(r, a2(BLOB), includeAny, oneOf(a2(BLOB), a2(NOTHING), a(NOTHING), NOTHING));
    gen(r, a2(NOTHING), includeAny, oneOf(a2(NOTHING), a(NOTHING), NOTHING));
    gen(r, a2(STRUCT), includeAny, oneOf(a2(STRUCT), a2(NOTHING), a(NOTHING), NOTHING));

    if (includeAny) {
      gen(r, f(ANY), includeAny, mNothing(), mFunc(mAll()));
    }
    gen(r, f(BLOB), includeAny, mNothing(), mFunc(oneOf(BLOB, NOTHING)));
    gen(r, f(NOTHING), includeAny, mNothing(), mFunc(oneOf(NOTHING)));

    if (includeAny) {
      gen(r, f(ANY, ANY), includeAny, mNothing(), mFunc(mAll(), oneOf(ANY)));
      gen(r, f(ANY, BLOB), includeAny, mNothing(), mFunc(mAll(), oneOf(ANY, BLOB)));
      gen(r, f(ANY, NOTHING), includeAny, mNothing(), mFunc(mAll(), mAll()));
      gen(r, f(BLOB, ANY), includeAny, mNothing(), mFunc(oneOf(BLOB, NOTHING), oneOf(ANY)));
    }
    gen(r, f(BLOB, BLOB), includeAny, mNothing(), mFunc(oneOf(BLOB, NOTHING), oneOf(ANY, BLOB)));
    gen(r, f(BLOB, NOTHING), includeAny, mNothing(), mFunc(oneOf(BLOB, NOTHING), mAll()));
    if (includeAny) {
      gen(r, f(NOTHING, ANY), includeAny, mNothing(), mFunc(oneOf(NOTHING), oneOf(ANY)));
    }
    gen(r, f(NOTHING, BLOB), includeAny, mNothing(), mFunc(oneOf(NOTHING), oneOf(ANY, BLOB)));
    gen(r, f(NOTHING, NOTHING), includeAny, mNothing(), mFunc(oneOf(NOTHING), mAll()));

    r.addAll(list(
        illegalAssignment(f(BLOB, STRING), f(BLOB, STRING, INT)),

        // functions
        illegalAssignment(f(a(BLOB)), a(BLOB)),
        illegalAssignment(f(a(NOTHING)), a(NOTHING)),
        illegalAssignment(f(a(STRUCT)), a(STRUCT)),
        illegalAssignment(f(a2(BLOB)), a2(BLOB)),
        illegalAssignment(f(a2(NOTHING)), a2(NOTHING)),
        illegalAssignment(f(a2(STRUCT)), a2(STRUCT)),

        // functions (as function result type)
        allowedAssignment(f(f(BLOB)), f(f(BLOB))),
        allowedAssignment(f(f(BLOB)), f(f(NOTHING))),
        illegalAssignment(f(f(NOTHING)), f(f(BLOB))),

        allowedAssignment(f(f(BLOB, STRING)), f(f(BLOB, STRING))),
        illegalAssignment(f(f(BLOB, STRING)), f(f(BLOB, NOTHING))),
        allowedAssignment(f(f(BLOB, NOTHING)), f(f(BLOB, STRING))),

        // functions (as function result type - nested twice)
        allowedAssignment(f(f(f(BLOB))), f(f(f(BLOB)))),
        allowedAssignment(f(f(f(BLOB))), f(f(f(NOTHING)))),
        illegalAssignment(f(f(f(NOTHING))), f(f(f(BLOB)))),

        allowedAssignment(f(f(f(BLOB, STRING))), f(f(f(BLOB, STRING)))),
        illegalAssignment(f(f(f(BLOB, STRING))), f(f(f(BLOB, NOTHING)))),
        allowedAssignment(f(f(f(BLOB, NOTHING))), f(f(f(BLOB, STRING)))),

        // functions (as function parameter type)
        allowedAssignment(f(BLOB, f(STRING)), f(BLOB, f(STRING))),
        illegalAssignment(f(BLOB, f(STRING)), f(BLOB, f(NOTHING))),
        allowedAssignment(f(BLOB, f(NOTHING)), f(BLOB, f(STRING))),

        allowedAssignment(f(BLOB, f(BLOB, STRING)), f(BLOB, f(BLOB, STRING))),
        allowedAssignment(f(BLOB, f(BLOB, STRING)), f(BLOB, f(BLOB, NOTHING))),
        illegalAssignment(f(BLOB, f(BLOB, NOTHING)), f(BLOB, f(BLOB, STRING))),

        // functions (as function parameter type - nested twice)
        allowedAssignment(f(BLOB, f(BLOB, f(STRING))), f(BLOB, f(BLOB, f(STRING)))),
        allowedAssignment(f(BLOB, f(BLOB, f(STRING))), f(BLOB, f(BLOB, f(NOTHING)))),
        illegalAssignment(f(BLOB, f(BLOB, f(NOTHING))), f(BLOB, f(BLOB, f(STRING)))),

        allowedAssignment(f(BLOB, f(BLOB, f(BLOB, STRING))), f(BLOB, f(BLOB, f(BLOB, STRING)))),
        illegalAssignment(f(BLOB, f(BLOB, f(BLOB, STRING))), f(BLOB, f(BLOB, f(BLOB, NOTHING)))),
        allowedAssignment(f(BLOB, f(BLOB, f(BLOB, NOTHING))), f(BLOB, f(BLOB, f(BLOB, STRING))))
    ));
    return r;
  }

  private static List<TestedAssignmentSpec> testSpecSpecificForNormalAssignment(
      boolean includeAny) {
    List<TestedAssignmentSpec> r = new ArrayList<>();
    gen(r, A, includeAny, oneOf(NOTHING, A));
    gen(r, B, includeAny, oneOf(NOTHING));

    gen(r, a(A), includeAny, oneOf(NOTHING, a(NOTHING), a(A)));
    gen(r, a(B), includeAny, oneOf(NOTHING, a(NOTHING)));
    gen(r, a2(A), includeAny, oneOf(NOTHING, a(NOTHING), a2(NOTHING), a2(A)));
    gen(r, a2(B), includeAny, oneOf(NOTHING, a(NOTHING), a2(NOTHING)));

    gen(r, f(A), includeAny, oneOf(NOTHING), mFunc(oneOf(A, NOTHING)));

    r.addAll(list(
        // functions
        illegalAssignment(f(a(A)), a(A)),
        illegalAssignment(f(a2(A)), a2(A)),

        allowedAssignment(f(A, A), f(A, A)),
        illegalAssignment(f(A, A, A), f(A, B, B)),
        illegalAssignment(f(A, A, A), f(B, A, B)),
        illegalAssignment(f(A, A), f(B, B)),
        illegalAssignment(f(A, A), f(A, NOTHING)),
        illegalAssignment(f(A, A), f(A, STRING)),
        allowedAssignment(f(A, A), f(NOTHING, A)),
        illegalAssignment(f(A, A), f(NOTHING, NOTHING)),
        illegalAssignment(f(A, A), f(NOTHING, STRING)),
        illegalAssignment(f(A, A), f(STRING, A)),
        illegalAssignment(f(A, A), f(STRING, NOTHING)),
        illegalAssignment(f(A, A), f(STRING, STRING)),
        allowedAssignment(f(A, NOTHING), f(A, A)),
        allowedAssignment(f(A, NOTHING), f(A, NOTHING)),
        allowedAssignment(f(A, NOTHING), f(A, STRING)),
        allowedAssignment(f(A, NOTHING), f(NOTHING, A)),
        allowedAssignment(f(A, NOTHING), f(NOTHING, NOTHING)),
        allowedAssignment(f(A, NOTHING), f(NOTHING, STRING)),
        illegalAssignment(f(A, NOTHING), f(STRING, A)),
        illegalAssignment(f(A, NOTHING), f(STRING, NOTHING)),
        illegalAssignment(f(A, NOTHING), f(STRING, STRING)),

        illegalAssignment(f(A, STRING), f(A, A)),
        illegalAssignment(f(A, STRING), f(A, NOTHING)),
        allowedAssignment(f(A, STRING), f(A, STRING)),
        illegalAssignment(f(A, STRING), f(NOTHING, A)),
        illegalAssignment(f(A, STRING), f(NOTHING, NOTHING)),
        allowedAssignment(f(A, STRING), f(NOTHING, STRING)),
        illegalAssignment(f(A, STRING), f(STRING, A)),
        illegalAssignment(f(A, STRING), f(STRING, NOTHING)),
        illegalAssignment(f(A, STRING), f(STRING, STRING)),

        illegalAssignment(f(NOTHING, A), f(A, A)),
        illegalAssignment(f(NOTHING, A), f(A, NOTHING)),
        illegalAssignment(f(NOTHING, A), f(A, STRING)),
        allowedAssignment(f(NOTHING, A), f(NOTHING, A)),
        illegalAssignment(f(NOTHING, A), f(NOTHING, NOTHING)),
        illegalAssignment(f(NOTHING, A), f(NOTHING, STRING)),
        illegalAssignment(f(NOTHING, A), f(STRING, A)),
        illegalAssignment(f(NOTHING, A), f(STRING, NOTHING)),
        illegalAssignment(f(NOTHING, A), f(STRING, STRING)),

        illegalAssignment(f(STRING, A), f(A, A)),
        illegalAssignment(f(STRING, A), f(A, NOTHING)),
        illegalAssignment(f(STRING, A), f(A, STRING)),
        allowedAssignment(f(STRING, A), f(NOTHING, A)),
        illegalAssignment(f(STRING, A), f(NOTHING, NOTHING)),
        illegalAssignment(f(STRING, A), f(NOTHING, STRING)),
        allowedAssignment(f(STRING, A), f(STRING, A)),
        illegalAssignment(f(STRING, A), f(STRING, NOTHING)),
        illegalAssignment(f(STRING, A), f(STRING, STRING))
    ));
    return r;
  }

  private static List<TestedAssignmentSpec> testSpecsSpecificForParameterAssignment(
      boolean includeAny) {
    List<TestedAssignmentSpec> r = new ArrayList<>();
    gen(r, A, includeAny, mAll());
    gen(r, B, includeAny, mAll());
    gen(r, a(A), includeAny, mNothing(), TestedType::isArray);
    gen(r, a(B), includeAny, mNothing(), TestedType::isArray);
    gen(r, a2(A), includeAny, oneOf(NOTHING, a(NOTHING)), TestedType::isArrayOfArrays);
    gen(r, a2(B), includeAny, oneOf(NOTHING, a(NOTHING)), TestedType::isArrayOfArrays);

    r.addAll(list(
        allowedAssignment(f(A, A), f(A, A)),
        illegalAssignment(f(A, A, A), f(A, B, A)),
        illegalAssignment(f(A, A, A), f(B, A, B)),
        allowedAssignment(f(A, A), f(B, B)),
        illegalAssignment(f(A, A, A), f(A, A, NOTHING)),
        illegalAssignment(f(A, A, A), f(A, A, BLOB)),
        allowedAssignment(f(A, A, A), f(NOTHING, A, A)),
        allowedAssignment(f(A, A), f(NOTHING, NOTHING)),
        allowedAssignment(f(A, A), f(NOTHING, BLOB)),
        illegalAssignment(f(A, A), f(BLOB, A)),
        illegalAssignment(f(A, A), f(BLOB, NOTHING)),
        allowedAssignment(f(A, A), f(BLOB, BLOB)),
        allowedAssignment(f(A, A, NOTHING), f(A, A, A)),
        allowedAssignment(f(A, A, NOTHING), f(A, A, NOTHING)),
        allowedAssignment(f(A, A, NOTHING), f(A, A, BLOB)),
        allowedAssignment(f(A, A, NOTHING), f(NOTHING, A, A)),
        allowedAssignment(f(A, A, NOTHING), f(NOTHING, A, NOTHING)),
        allowedAssignment(f(A, A, NOTHING), f(NOTHING, A, BLOB)),

        illegalAssignment(f(A, A, BLOB), f(A, A, A)),
        illegalAssignment(f(A, A, BLOB), f(A, A, NOTHING)),
        allowedAssignment(f(A, A, BLOB), f(A, A, BLOB)),
        illegalAssignment(f(A, A, BLOB), f(NOTHING, A, A)),
        illegalAssignment(f(A, A, BLOB), f(NOTHING, A, NOTHING)),
        allowedAssignment(f(A, A, BLOB), f(NOTHING, A, BLOB)),
        illegalAssignment(f(A, A, BLOB), f(BLOB, A, A)),
        illegalAssignment(f(A, A, BLOB), f(BLOB, A, NOTHING)),
        illegalAssignment(f(A, A, BLOB), f(BLOB, A, BLOB)),

        illegalAssignment(f(NOTHING, A), f(A, A)),
        illegalAssignment(f(NOTHING, A), f(A, NOTHING)),
        illegalAssignment(f(NOTHING, A), f(A, BLOB)),
        allowedAssignment(f(NOTHING, A), f(NOTHING, A)),
        allowedAssignment(f(NOTHING, A), f(NOTHING, NOTHING)),
        allowedAssignment(f(NOTHING, A), f(NOTHING, BLOB)),
        illegalAssignment(f(NOTHING, A), f(BLOB, A)),
        illegalAssignment(f(NOTHING, A), f(BLOB, NOTHING)),
        illegalAssignment(f(NOTHING, A), f(BLOB, BLOB)),

        illegalAssignment(f(BLOB, A), f(A, A)),
        illegalAssignment(f(BLOB, A), f(A, NOTHING)),
        illegalAssignment(f(BLOB, A), f(A, BLOB)),
        allowedAssignment(f(BLOB, A), f(NOTHING, A)),
        allowedAssignment(f(BLOB, A), f(NOTHING, NOTHING)),
        allowedAssignment(f(BLOB, A), f(NOTHING, BLOB)),
        allowedAssignment(f(BLOB, A), f(BLOB, A)),
        allowedAssignment(f(BLOB, A), f(BLOB, NOTHING)),
        allowedAssignment(f(BLOB, A), f(BLOB, BLOB))
    ));
    return r;
  }

  /**
   * Match a function.
   */
  private static Predicate<TestedType> mFunc(Predicate<TestedType> result,
      Predicate<TestedType>... parameters) {
    return t -> t.isFunction(result, parameters);
  }

  /**
   * Match anything.
   */
  private static Predicate<TestedType> mAll() {
    return t -> true;
  }

  /**
   * Match nothing.
   */
  private static Predicate<TestedType> mNothing() {
    return type -> type.equals(NOTHING);
  }

  private static Predicate<TestedType> oneOf(TestedType... types) {
    return Set.of(types)::contains;
  }

  private static List<TestedAssignmentSpec> gen(List<TestedAssignmentSpec> result,
      TestedType target, boolean includeAny, Predicate<TestedType>... allowedPredicates) {
    for (TestedType type : generateTypes(2, includeAny)) {
      boolean allowed = stream(allowedPredicates).anyMatch(predicate -> predicate.test(type));
      result.add(new TestedAssignmentSpec(target, type, allowed));
    }
    return result;
  }

  private static ImmutableList<TestedType> generateTypes(int depth, boolean includeAny) {
    Builder<TestedType> builder = ImmutableList.builder();
    builder.add(BLOB, NOTHING, STRUCT);
    if (includeAny) {
      builder.add(ANY);
    }
    if (0 < depth) {
      List<TestedType> types = generateTypes(depth - 1, includeAny);
      for (TestedType type : types) {
        builder.add(a(type));
        builder.add(f(type));
        for (TestedType type2 : types) {
          builder.add(f(type, type2));
        }
      }
    }
    return builder.build();
  }
}
