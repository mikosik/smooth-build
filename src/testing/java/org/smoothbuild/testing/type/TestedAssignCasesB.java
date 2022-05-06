package org.smoothbuild.testing.type;

import static java.util.Arrays.stream;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.smoothbuild.lang.type.api.Type;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class TestedAssignCasesB {
  public static final TestedAssignCasesB TESTED_ASSIGN_CASES_B =
      new TestedAssignCasesB(new TestedTBF());

  private final TestedTBF testedTF;
  private final TestingTB testingT;
  private final TestedTB a;
  private final TestedTB b;
  private final TestedTB any;
  private final TestedTB blob;
  private final TestedTB int_;
  private final TestedTB nothing;
  private final TestedTB string;
  private final TestedTB struct;
  private final TestedTB tuple;
  private final boolean isStructSupported;
  private final boolean isTupleSupported;

  public TestedAssignCasesB(TestedTBF testedTF) {
    this.testedTF = testedTF;
    this.testingT = testedTF.testingT();
    this.a = testedTF.varA();
    this.b = testedTF.varB();
    this.any = testedTF.any();
    this.blob = testedTF.blob();
    this.int_ = testedTF.int_();
    this.nothing = testedTF.nothing();
    this.string = testedTF.string();
    this.isStructSupported = testingT.isStructSupported();
    this.isTupleSupported = testingT.isTupleSupported();
    this.struct = this.isStructSupported ? testedTF.struct() : null;
    this.tuple = this.isTupleSupported ? testedTF.tuple() : null;
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
    var result = new ArrayList<TestedAssignSpecB>();
    result.addAll(testSpecsCommonForNormalCaseAndParamAssignment(includeAny));
    result.addAll(testSpecSpecificForNormalAssignment(includeAny));
    return result;
  }

  public List<TestedAssignSpecB> param_assignment_test_specs(boolean includeAny) {
    var result = new ArrayList<TestedAssignSpecB>();
    result.addAll(testSpecsCommonForNormalCaseAndParamAssignment(includeAny));
    result.addAll(testSpecsSpecificForParamAssignment(includeAny));
    return result;
  }

  private List<TestedAssignSpecB> testSpecsCommonForNormalCaseAndParamAssignment(boolean includeAny) {
    var r = new ArrayList<TestedAssignSpecB>();
    if (includeAny) {
      gen(r, any, includeAny, mAll());
    }
    gen(r, blob, includeAny, oneOf(blob, nothing));
    gen(r, nothing, includeAny, oneOf(nothing));
    if (isStructSupported) {
      gen(r, struct, includeAny, oneOf(struct, nothing));
    }
    if (isTupleSupported) {
      gen(r, tuple, includeAny, oneOf(tuple, nothing));
    }

    if (includeAny) {
      gen(r, a(any), includeAny, TestedT::isArray, mNothing());
    }
    gen(r, a(blob), includeAny, oneOf(a(blob), a(nothing), nothing));
    gen(r, a(nothing), includeAny, oneOf(a(nothing), nothing));
    if (isStructSupported) {
      gen(r, a(struct), includeAny, oneOf(a(struct), a(nothing), nothing));
    }
    if (isTupleSupported) {
      gen(r, a(tuple), includeAny, oneOf(a(tuple), a(nothing), nothing));
    }
    if (includeAny) {
      gen(r, a2(any), includeAny, TestedT::isArrayOfArrays, t -> t.isArrayOf(nothing), mNothing());
    }
    gen(r, a2(blob), includeAny, oneOf(a2(blob), a2(nothing), a(nothing), nothing));
    gen(r, a2(nothing), includeAny, oneOf(a2(nothing), a(nothing), nothing));
    if (isStructSupported) {
      gen(r, a2(struct), includeAny, oneOf(a2(struct), a2(nothing), a(nothing), nothing));
    }
    if (isTupleSupported) {
      gen(r, a2(tuple), includeAny, oneOf(a2(tuple), a2(nothing), a(nothing), nothing));
    }

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
    if (isStructSupported) {
      r.add(illegalAssignment(f(a(struct)), a(struct)));
    }
    if (isTupleSupported) {
      r.add(illegalAssignment(f(a(tuple)), a(tuple)));
    }
    r.add(illegalAssignment(f(a2(blob)), a2(blob)));
    r.add(illegalAssignment(f(a2(nothing)), a2(nothing)));
    if (isStructSupported) {
      r.add(illegalAssignment(f(a2(struct)), a2(struct)));
    }
    if (isTupleSupported) {
      r.add(illegalAssignment(f(a2(tuple)), a2(tuple)));
    }

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

  private List<TestedAssignSpecB> testSpecSpecificForNormalAssignment(boolean includeAny) {
    List<TestedAssignSpecB> r = new ArrayList<>();
    gen(r, a, includeAny, oneOf(nothing, a));
    gen(r, b, includeAny, oneOf(nothing));

    gen(r, a(a), includeAny, oneOf(nothing, a(nothing), a(a)));
    gen(r, a(b), includeAny, oneOf(nothing, a(nothing)));
    gen(r, a2(a), includeAny, oneOf(nothing, a(nothing), a2(nothing), a2(a)));
    gen(r, a2(b), includeAny, oneOf(nothing, a(nothing), a2(nothing)));

    if (isTupleSupported) {
      gen(r, tuple(a), includeAny, oneOf(nothing, tuple(nothing), tuple(a)));
    }

    gen(r, f(a), includeAny, oneOf(nothing), mFunc(oneOf(a, nothing)));

    r.addAll(list(
        // funcs
        illegalAssignment(f(a(a)), a(a)),
        illegalAssignment(f(a2(a)), a2(a)),

        allowedAssignment(f(a, a), f(a, a)),
        illegalAssignment(f(a, a, a), f(a, b, b)),
        illegalAssignment(f(a, a, a), f(b, a, b)),
        illegalAssignment(f(a, a), f(b, b)),
        illegalAssignment(f(a, a), f(a, nothing)),
        illegalAssignment(f(a, a), f(a, string)),
        allowedAssignment(f(a, a), f(nothing, a)),
        illegalAssignment(f(a, a), f(nothing, nothing)),
        illegalAssignment(f(a, a), f(nothing, string)),
        illegalAssignment(f(a, a), f(string, a)),
        illegalAssignment(f(a, a), f(string, nothing)),
        illegalAssignment(f(a, a), f(string, string)),
        allowedAssignment(f(a, nothing), f(a, a)),
        allowedAssignment(f(a, nothing), f(a, nothing)),
        allowedAssignment(f(a, nothing), f(a, string)),
        allowedAssignment(f(a, nothing), f(nothing, a)),
        allowedAssignment(f(a, nothing), f(nothing, nothing)),
        allowedAssignment(f(a, nothing), f(nothing, string)),
        illegalAssignment(f(a, nothing), f(string, a)),
        illegalAssignment(f(a, nothing), f(string, nothing)),
        illegalAssignment(f(a, nothing), f(string, string)),

        illegalAssignment(f(a, string), f(a, a)),
        illegalAssignment(f(a, string), f(a, nothing)),
        allowedAssignment(f(a, string), f(a, string)),
        illegalAssignment(f(a, string), f(nothing, a)),
        illegalAssignment(f(a, string), f(nothing, nothing)),
        allowedAssignment(f(a, string), f(nothing, string)),
        illegalAssignment(f(a, string), f(string, a)),
        illegalAssignment(f(a, string), f(string, nothing)),
        illegalAssignment(f(a, string), f(string, string)),

        illegalAssignment(f(nothing, a), f(a, a)),
        illegalAssignment(f(nothing, a), f(a, nothing)),
        illegalAssignment(f(nothing, a), f(a, string)),
        allowedAssignment(f(nothing, a), f(nothing, a)),
        illegalAssignment(f(nothing, a), f(nothing, nothing)),
        illegalAssignment(f(nothing, a), f(nothing, string)),
        illegalAssignment(f(nothing, a), f(string, a)),
        illegalAssignment(f(nothing, a), f(string, nothing)),
        illegalAssignment(f(nothing, a), f(string, string)),

        illegalAssignment(f(string, a), f(a, a)),
        illegalAssignment(f(string, a), f(a, nothing)),
        illegalAssignment(f(string, a), f(a, string)),
        allowedAssignment(f(string, a), f(nothing, a)),
        illegalAssignment(f(string, a), f(nothing, nothing)),
        illegalAssignment(f(string, a), f(nothing, string)),
        allowedAssignment(f(string, a), f(string, a)),
        illegalAssignment(f(string, a), f(string, nothing)),
        illegalAssignment(f(string, a), f(string, string))
    ));
    return r;
  }

  private List<TestedAssignSpecB> testSpecsSpecificForParamAssignment(boolean includeAny) {
    List<TestedAssignSpecB> r = new ArrayList<>();
    gen(r, a, includeAny, mAll());
    gen(r, b, includeAny, mAll());
    gen(r, a(a), includeAny, mNothing(), TestedT::isArray);
    gen(r, a(b), includeAny, mNothing(), TestedT::isArray);
    gen(r, a2(a), includeAny, oneOf(nothing, a(nothing)), TestedT::isArrayOfArrays);
    gen(r, a2(b), includeAny, oneOf(nothing, a(nothing)), TestedT::isArrayOfArrays);

    if (isTupleSupported) {
      gen(r, tuple(a), includeAny, mNothing(), TestedT::isTuple);
      gen(r, tuple(b), includeAny, mNothing(), TestedT::isTuple);

      gen(r, tuple(tuple(a)), includeAny, oneOf(nothing, tuple(nothing)), TestedT::isTupleOfTuple);
      gen(r, tuple(tuple(b)), includeAny, oneOf(nothing, tuple(nothing)), TestedT::isTupleOfTuple);
    }

    r.addAll(list(
        allowedAssignment(f(a, a), f(a, a)),
        illegalAssignment(f(a, a, a), f(a, b, a)),
        illegalAssignment(f(a, a, a), f(b, a, b)),
        allowedAssignment(f(a, a), f(b, b)),
        illegalAssignment(f(a, a, a), f(a, a, nothing)),
        illegalAssignment(f(a, a, a), f(a, a, blob)),
        allowedAssignment(f(a, a, a), f(nothing, a, a)),
        allowedAssignment(f(a, a), f(nothing, nothing)),
        allowedAssignment(f(a, a), f(nothing, blob)),
        illegalAssignment(f(a, a), f(blob, a)),
        illegalAssignment(f(a, a), f(blob, nothing)),
        allowedAssignment(f(a, a), f(blob, blob)),
        allowedAssignment(f(a, a, nothing), f(a, a, a)),
        allowedAssignment(f(a, a, nothing), f(a, a, nothing)),
        allowedAssignment(f(a, a, nothing), f(a, a, blob)),
        allowedAssignment(f(a, a, nothing), f(nothing, a, a)),
        allowedAssignment(f(a, a, nothing), f(nothing, a, nothing)),
        allowedAssignment(f(a, a, nothing), f(nothing, a, blob)),

        illegalAssignment(f(a, a, blob), f(a, a, a)),
        illegalAssignment(f(a, a, blob), f(a, a, nothing)),
        allowedAssignment(f(a, a, blob), f(a, a, blob)),
        illegalAssignment(f(a, a, blob), f(nothing, a, a)),
        illegalAssignment(f(a, a, blob), f(nothing, a, nothing)),
        allowedAssignment(f(a, a, blob), f(nothing, a, blob)),
        illegalAssignment(f(a, a, blob), f(blob, a, a)),
        illegalAssignment(f(a, a, blob), f(blob, a, nothing)),
        illegalAssignment(f(a, a, blob), f(blob, a, blob)),

        illegalAssignment(f(nothing, a), f(a, a)),
        illegalAssignment(f(nothing, a), f(a, nothing)),
        illegalAssignment(f(nothing, a), f(a, blob)),
        allowedAssignment(f(nothing, a), f(nothing, a)),
        allowedAssignment(f(nothing, a), f(nothing, nothing)),
        allowedAssignment(f(nothing, a), f(nothing, blob)),
        illegalAssignment(f(nothing, a), f(blob, a)),
        illegalAssignment(f(nothing, a), f(blob, nothing)),
        illegalAssignment(f(nothing, a), f(blob, blob)),

        illegalAssignment(f(blob, a), f(a, a)),
        illegalAssignment(f(blob, a), f(a, nothing)),
        illegalAssignment(f(blob, a), f(a, blob)),
        allowedAssignment(f(blob, a), f(nothing, a)),
        allowedAssignment(f(blob, a), f(nothing, nothing)),
        allowedAssignment(f(blob, a), f(nothing, blob)),
        allowedAssignment(f(blob, a), f(blob, a)),
        allowedAssignment(f(blob, a), f(blob, nothing)),
        allowedAssignment(f(blob, a), f(blob, blob))
    ));
    return r;
  }

  /**
   * Match a func.
   */
  private <X extends TestedT<? extends Type>> Predicate<X> mFunc(
      Predicate<? super TestedT<? extends Type>> result,
      Predicate<? super TestedT<? extends Type>>... params) {
    var list = list(params);
    return (X t) -> t.isFunc(result, list);
  }

  /**
   * Match anything.
   */
  private Predicate<TestedT<? extends Type>> mAll() {
    return t -> true;
  }

  /**
   * Match nothing.
   */
  private Predicate<TestedT<? extends Type>> mNothing() {
    return type -> type.equals(nothing);
  }

  private Predicate<TestedT<? extends Type>> oneOf(TestedTB... types) {
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
    if (isStructSupported) {
      builder.add(struct);
    }
    if (includeAny) {
      builder.add(any);
    }
    if (0 < depth) {
      List<TestedTB> types = generateTypes(depth - 1, includeAny);
      for (TestedTB type : types) {
        builder.add(a(type));
        if (isTupleSupported) {
          builder.add(tuple(type));
        }
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
