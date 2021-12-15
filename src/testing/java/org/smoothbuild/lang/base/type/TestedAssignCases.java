package org.smoothbuild.lang.base.type;

import static java.util.Arrays.stream;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.impl.TypeS;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class TestedAssignCases<
    T extends Type,
    TT extends TestedT<T>,
    S extends TestedAssignSpec<? extends TT>> {

  public static final TestedAssignCases<TypeS, TestedTS, TestedAssignSpecS> INSTANCE_S =
      new TestedAssignCases<>(new TestedTSFactory());
  public static final TestedAssignCases<TypeH, TestedTH, TestedAssignSpecH> INSTANCE_H =
      new TestedAssignCases<>(new TestedTHFactory());

  private final TT a;
  private final TT b;
  private final TT any;
  private final TT blob;
  private final TT int_;
  private final TT nothing;
  private final TT string;
  private final TT struct;
  private final TestedTFactory<T, TT, S> testedTFactory;

  private TestedAssignCases(TestedTFactory<T, TT, S> testedTFactory) {
    this.testedTFactory = testedTFactory;
    this.a = testedTFactory.varA();
    this.b = testedTFactory.varB();
    this.any = testedTFactory.any();
    this.blob = testedTFactory.blob();
    this.int_ = testedTFactory.int_();
    this.nothing = testedTFactory.nothing();
    this.string = testedTFactory.string();
    this.struct = testedTFactory.struct();
  }

  public TestingT<T> testingT() {
    return testedTFactory.testingT();
  }

  public TestedTFactory<T, TT, S> testedTFactory() {
    return testedTFactory;
  }

  private S illegalAssignment(TT target, TT source) {
    return testedTFactory.illegalAssignment(target, source);
  }

  private S allowedAssignment(TT target, TT source) {
    return testedTFactory.allowedAssignment(target, source);
  }

  public List<S> assignment_test_specs(boolean includeAny) {
    var result = new ArrayList<S>();
    result.addAll(testSpecsCommonForNormalCaseAndParamAssignment(includeAny));
    result.addAll(testSpecSpecificForNormalAssignment(includeAny));
    return result;
  }

  public List<S> param_assignment_test_specs(boolean includeAny) {
    var result = new ArrayList<S>();
    result.addAll(testSpecsCommonForNormalCaseAndParamAssignment(includeAny));
    result.addAll(testSpecsSpecificForParamAssignment(includeAny));
    return result;
  }

  private List<S> testSpecsCommonForNormalCaseAndParamAssignment(
      boolean includeAny) {
    var r = new ArrayList<S>();
    if (includeAny) {
      gen(r, any, includeAny, mAll());
    }
    gen(r, blob, includeAny, oneOf(blob, nothing));
    gen(r, nothing, includeAny, oneOf(nothing));
    gen(r, struct, includeAny, oneOf(struct, nothing));

    if (includeAny) {
      gen(r, a(any), includeAny, TestedT::isArray, mNothing());
    }
    gen(r, a(blob), includeAny, oneOf(a(blob), a(nothing), nothing));
    gen(r, a(nothing), includeAny, oneOf(a(nothing), nothing));
    gen(r, a(struct), includeAny, oneOf(a(struct), a(nothing), nothing));
    if (includeAny) {
      gen(r, a2(any), includeAny, TestedT::isArrayOfArrays, t -> t.isArrayOf(nothing), mNothing());
    }
    gen(r, a2(blob), includeAny, oneOf(a2(blob), a2(nothing), a(nothing), nothing));
    gen(r, a2(nothing), includeAny, oneOf(a2(nothing), a(nothing), nothing));
    gen(r, a2(struct), includeAny, oneOf(a2(struct), a2(nothing), a(nothing), nothing));

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

    r.addAll(list(
        illegalAssignment(f(blob, string), f(blob, string, int_)),

        // funcs
        illegalAssignment(f(a(blob)), a(blob)),
        illegalAssignment(f(a(nothing)), a(nothing)),
        illegalAssignment(f(a(struct)), a(struct)),
        illegalAssignment(f(a2(blob)), a2(blob)),
        illegalAssignment(f(a2(nothing)), a2(nothing)),
        illegalAssignment(f(a2(struct)), a2(struct)),

        // funcs (as func result type)
        allowedAssignment(f(f(blob)), f(f(blob))),
        allowedAssignment(f(f(blob)), f(f(nothing))),
        illegalAssignment(f(f(nothing)), f(f(blob))),

        allowedAssignment(f(f(blob, string)), f(f(blob, string))),
        illegalAssignment(f(f(blob, string)), f(f(blob, nothing))),
        allowedAssignment(f(f(blob, nothing)), f(f(blob, string))),

        // funcs (as func result type - nested twice)
        allowedAssignment(f(f(f(blob))), f(f(f(blob)))),
        allowedAssignment(f(f(f(blob))), f(f(f(nothing)))),
        illegalAssignment(f(f(f(nothing))), f(f(f(blob)))),

        allowedAssignment(f(f(f(blob, string))), f(f(f(blob, string)))),
        illegalAssignment(f(f(f(blob, string))), f(f(f(blob, nothing)))),
        allowedAssignment(f(f(f(blob, nothing))), f(f(f(blob, string)))),

        // funcs (as func param type)
        allowedAssignment(f(blob, f(string)), f(blob, f(string))),
        illegalAssignment(f(blob, f(string)), f(blob, f(nothing))),
        allowedAssignment(f(blob, f(nothing)), f(blob, f(string))),

        allowedAssignment(f(blob, f(blob, string)), f(blob, f(blob, string))),
        allowedAssignment(f(blob, f(blob, string)), f(blob, f(blob, nothing))),
        illegalAssignment(f(blob, f(blob, nothing)), f(blob, f(blob, string))),

        // funcs (as func param type - nested twice)
        allowedAssignment(f(blob, f(blob, f(string))), f(blob, f(blob, f(string)))),
        allowedAssignment(f(blob, f(blob, f(string))), f(blob, f(blob, f(nothing)))),
        illegalAssignment(f(blob, f(blob, f(nothing))), f(blob, f(blob, f(string)))),

        allowedAssignment(f(blob, f(blob, f(blob, string))), f(blob, f(blob, f(blob, string)))),
        illegalAssignment(f(blob, f(blob, f(blob, string))), f(blob, f(blob, f(blob, nothing)))),
        allowedAssignment(f(blob, f(blob, f(blob, nothing))), f(blob, f(blob, f(blob, string))))
    ));
    return r;
  }

  private List<S> testSpecSpecificForNormalAssignment(
      boolean includeAny) {
    List<S> r = new ArrayList<>();
    gen(r, a, includeAny, oneOf(nothing, a));
    gen(r, b, includeAny, oneOf(nothing));

    gen(r, a(a), includeAny, oneOf(nothing, a(nothing), a(a)));
    gen(r, a(b), includeAny, oneOf(nothing, a(nothing)));
    gen(r, a2(a), includeAny, oneOf(nothing, a(nothing), a2(nothing), a2(a)));
    gen(r, a2(b), includeAny, oneOf(nothing, a(nothing), a2(nothing)));

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

  private List<S> testSpecsSpecificForParamAssignment(
      boolean includeAny) {
    List<S> r = new ArrayList<>();
    gen(r, a, includeAny, mAll());
    gen(r, b, includeAny, mAll());
    gen(r, a(a), includeAny, mNothing(), TestedT::isArray);
    gen(r, a(b), includeAny, mNothing(), TestedT::isArray);
    gen(r, a2(a), includeAny, oneOf(nothing, a(nothing)), TestedT::isArrayOfArrays);
    gen(r, a2(b), includeAny, oneOf(nothing, a(nothing)), TestedT::isArrayOfArrays);

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

  private Predicate<TestedT<? extends Type>> oneOf(TT... types) {
    return Set.of(types)::contains;
  }

  private List<S> gen(List<S> result, TT target, boolean includeAny,
      Predicate<TestedT<? extends Type>>... allowedPredicates) {
    for (TT type : generateTypes(2, includeAny)) {
      boolean allowed = stream(allowedPredicates).anyMatch(predicate -> predicate.test(type));
      result.add(testedTFactory.testedAssignmentSpec(target, type, allowed));
    }
    return result;
  }

  private ImmutableList<TT> generateTypes(int depth, boolean includeAny) {
    Builder<TT> builder = ImmutableList.builder();
    builder.add(blob);
    builder.add(nothing);
    builder.add(struct);
    if (includeAny) {
      builder.add(any);
    }
    if (0 < depth) {
      List<TT> types = generateTypes(depth - 1, includeAny);
      for (TT type : types) {
        builder.add(a(type));
        builder.add(f(type, list()));
        for (TT type2 : types) {
          builder.add(f(type, list(type2)));
        }
      }
    }
    return builder.build();
  }

  private TT a(TT type) {
    return testedTFactory.array(type);
  }

  private TT a2(TT type) {
    return testedTFactory.array2(type);
  }

  private TT f(TT resT, ImmutableList<TT> paramTs) {
    return testedTFactory.func(resT, paramTs);
  }

  private TT f(TT resT, TT... paramTs) {
    return testedTFactory.func(resT, list(paramTs));
  }
}
