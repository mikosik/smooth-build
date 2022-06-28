package org.smoothbuild.testing.type;

import static java.util.Arrays.stream;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class TestedAssignCasesB {
  public static final TestedAssignCasesB TESTED_ASSIGN_CASES_B =
      new TestedAssignCasesB(new TestedTBF());

  private final TestedTBF testedTF;
  private final TestingTB testingT;
  private final TestedTB any;
  private final TestedTB blob;
  private final TestedTB int_;
  private final TestedTB nothing;
  private final TestedTB string;
  private final TestedTB tuple;

  public TestedAssignCasesB(TestedTBF testedTF) {
    this.testedTF = testedTF;
    this.testingT = testedTF.testingT();
    this.any = testedTF.any();
    this.blob = testedTF.blob();
    this.int_ = testedTF.int_();
    this.nothing = testedTF.nothing();
    this.string = testedTF.string();
    this.tuple = testedTF.tuple();
  }

  public TestedTBF testedTF() {
    return testedTF;
  }

  public TestingTB testingT() {
    return testingT;
  }

  private TestedAssignSpecB illegalAssignment(TestedTB target, TestedTB source) {
    return testedTF.testedAssignmentSpec(target, source, false);
  }

  private TestedAssignSpecB allowedAssignment(TestedTB target, TestedTB source) {
    return testedTF.testedAssignmentSpec(target, source, true);
  }

  public List<TestedAssignSpecB> assignment_test_specs(boolean includeAny) {
    var r = new ArrayList<TestedAssignSpecB>();
    if (includeAny) {
      gen(r, any, includeAny, mAll());
    }
    gen(r, blob, includeAny, oneOf(blob, nothing));
    gen(r, nothing, includeAny, oneOf(nothing));
    gen(r, tuple, includeAny, oneOf(tuple, nothing));

    if (includeAny) {
      gen(r, a(any), includeAny, TestedTB::isArray, mNothing());
    }
    gen(r, a(blob), includeAny, oneOf(a(blob), a(nothing), nothing));
    gen(r, a(nothing), includeAny, oneOf(a(nothing), nothing));
    gen(r, a(tuple), includeAny, oneOf(a(tuple), a(nothing), nothing));
    if (includeAny) {
      gen(r, a2(any), includeAny, TestedTB::isArrayOfArrays, t -> t.isArrayOf(nothing), mNothing());
    }
    gen(r, a2(blob), includeAny, oneOf(a2(blob), a2(nothing), a(nothing), nothing));
    gen(r, a2(nothing), includeAny, oneOf(a2(nothing), a(nothing), nothing));
    gen(r, a2(tuple), includeAny, oneOf(a2(tuple), a2(nothing), a(nothing), nothing));

    if (includeAny) {
      gen(r, f(any), includeAny, mNothing(), mFunc(mAll()));
    }
    gen(r, f(blob), includeAny, mNothing(), mFunc(oneOf(blob, nothing)));
    gen(r, f(nothing), includeAny, mNothing(), mFunc(oneOf(nothing)));

    if (includeAny) {
      gen(r, f(any, any), includeAny, mNothing(), mFunc(mAll(), oneOf(any)));
      gen(r, f(any, blob), includeAny, mNothing(), mFunc(mAll(), oneOf(any, blob)));
      gen(r, f(any, nothing), includeAny, mNothing(), mFunc(mAll(), mAll()));
      gen(r, f(blob, any), includeAny, mNothing(), mFunc(oneOf(blob, nothing), oneOf(any)));
    }
    gen(r, f(blob, blob), includeAny, mNothing(), mFunc(oneOf(blob, nothing), oneOf(any, blob)));
    gen(r, f(blob, nothing), includeAny, mNothing(), mFunc(oneOf(blob, nothing), mAll()));
    if (includeAny) {
      gen(r, f(nothing, any), includeAny, mNothing(), mFunc(oneOf(nothing), oneOf(any)));
    }
    gen(r, f(nothing, blob), includeAny, mNothing(), mFunc(oneOf(nothing), oneOf(any, blob)));
    gen(r, f(nothing, nothing), includeAny, mNothing(), mFunc(oneOf(nothing), mAll()));

    r.add(illegalAssignment(f(blob, string), f(blob, string, int_)));

    // funcs
    r.add(illegalAssignment(f(a(blob)), a(blob)));
    r.add(illegalAssignment(f(a(nothing)), a(nothing)));
    r.add(illegalAssignment(f(a(tuple)), a(tuple)));
    r.add(illegalAssignment(f(a2(blob)), a2(blob)));
    r.add(illegalAssignment(f(a2(nothing)), a2(nothing)));
    r.add(illegalAssignment(f(a2(tuple)), a2(tuple)));

    // funcs (as func result type)
    r.add(allowedAssignment(f(f(blob)), f(f(blob))));
    r.add(allowedAssignment(f(f(blob)), f(f(nothing))));
    r.add(illegalAssignment(f(f(nothing)), f(f(blob))));

    r.add(allowedAssignment(f(f(blob, string)), f(f(blob, string))));
    r.add(illegalAssignment(f(f(blob, string)), f(f(blob, nothing))));
    r.add(allowedAssignment(f(f(blob, nothing)), f(f(blob, string))));

    // funcs (as func result type - nested twice)
    r.add(allowedAssignment(f(f(f(blob))), f(f(f(blob)))));
    r.add(allowedAssignment(f(f(f(blob))), f(f(f(nothing)))));
    r.add(illegalAssignment(f(f(f(nothing))), f(f(f(blob)))));

    r.add(allowedAssignment(f(f(f(blob, string))), f(f(f(blob, string)))));
    r.add(illegalAssignment(f(f(f(blob, string))), f(f(f(blob, nothing)))));
    r.add(allowedAssignment(f(f(f(blob, nothing))), f(f(f(blob, string)))));

    // funcs (as func param type)
    r.add(allowedAssignment(f(blob, f(string)), f(blob, f(string))));
    r.add(illegalAssignment(f(blob, f(string)), f(blob, f(nothing))));
    r.add(allowedAssignment(f(blob, f(nothing)), f(blob, f(string))));

    r.add(allowedAssignment(f(blob, f(blob, string)), f(blob, f(blob, string))));
    r.add(allowedAssignment(f(blob, f(blob, string)), f(blob, f(blob, nothing))));
    r.add(illegalAssignment(f(blob, f(blob, nothing)), f(blob, f(blob, string))));

    // funcs (as func param type - nested twice)
    r.add(allowedAssignment(f(blob, f(blob, f(string))), f(blob, f(blob, f(string)))));
    r.add(allowedAssignment(f(blob, f(blob, f(string))), f(blob, f(blob, f(nothing)))));
    r.add(illegalAssignment(f(blob, f(blob, f(nothing))), f(blob, f(blob, f(string)))));

    r.add(allowedAssignment(f(blob, f(blob, f(blob, string))), f(blob, f(blob, f(blob, string)))));
    r.add(illegalAssignment(f(blob, f(blob, f(blob, string))), f(blob, f(blob, f(blob, nothing)))));
    r.add(allowedAssignment(f(blob, f(blob, f(blob, nothing))), f(blob, f(blob, f(blob, string)))));

    return r;
  }

  /**
   * Match a func.
   */
  private <X extends TestedTB> Predicate<X> mFunc(
      Predicate<? super TestedTB> result,
      Predicate<? super TestedTB>... params) {
    var list = list(params);
    return (X t) -> t.isFunc(result, list);
  }

  /**
   * Match anything.
   */
  private Predicate<TestedTB> mAll() {
    return t -> true;
  }

  /**
   * Match nothing.
   */
  private Predicate<TestedTB> mNothing() {
    return type -> type.equals(nothing);
  }

  private Predicate<TestedTB> oneOf(TestedTB... types) {
    return Set.of(types)::contains;
  }

  private List<TestedAssignSpecB> gen(List<TestedAssignSpecB> result, TestedTB target, boolean includeAny,
      Predicate<? super TestedTB>... allowedPredicates) {
    for (TestedTB type : generateTypes(2, includeAny)) {
      boolean allowed = stream(allowedPredicates).anyMatch(predicate -> predicate.test(type));
      result.add(testedTF.testedAssignmentSpec(target, type, allowed));
    }
    return result;
  }

  private ImmutableList<TestedTB> generateTypes(int depth, boolean includeAny) {
    Builder<TestedTB> builder = ImmutableList.builder();
    builder.add(blob);
    builder.add(nothing);
    if (includeAny) {
      builder.add(any);
    }
    if (0 < depth) {
      List<TestedTB> types = generateTypes(depth - 1, includeAny);
      for (TestedTB type : types) {
        builder.add(a(type));
        builder.add(tuple(type));
        builder.add(f(type, list()));
        for (TestedTB type2 : types) {
          builder.add(f(type, list(type2)));
        }
      }
    }
    return builder.build();
  }

  private TestedTB a(TestedTB type) {
    return testedTF.array(type);
  }

  private TestedTB a2(TestedTB type) {
    return testedTF.array2(type);
  }

  private TestedTB tuple(TestedTB type) {
    return testedTF.tuple(list(type));
  }

  private TestedTB f(TestedTB resT, ImmutableList<TestedTB> paramTs) {
    return testedTF.func(resT, paramTs);
  }

  private TestedTB f(TestedTB resT, TestedTB... paramTs) {
    return testedTF.func(resT, list(paramTs));
  }
}
