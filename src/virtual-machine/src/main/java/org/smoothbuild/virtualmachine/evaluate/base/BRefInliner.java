package org.smoothbuild.virtualmachine.evaluate.base;

import static org.smoothbuild.common.collect.List.listOfAll;

import jakarta.inject.Inject;
import java.util.ArrayList;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArrayGet;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCall;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCreateArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCreateTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCreateVariant;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BFold;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BIf;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BLambda;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BMap;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BRef;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSwitch;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTupleGet;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.job.Job;
import org.smoothbuild.virtualmachine.evaluate.job.RefIndexOutOfBoundsException;

public class BRefInliner {
  private final BytecodeFactory bytecodeFactory;

  @Inject
  public BRefInliner(BytecodeFactory bytecodeFactory) {
    this.bytecodeFactory = bytecodeFactory;
  }

  public BExpr inline(Job job) throws BytecodeException, RefIndexOutOfBoundsException {
    List<BExpr> inlinedEnvironment = rewriteJobs(job);
    return rewriteExpr(job.expr(), new Resolver(inlinedEnvironment));
  }

  private List<BExpr> rewriteJobs(Job job) throws BytecodeException, RefIndexOutOfBoundsException {
    ArrayList<BExpr> result = new ArrayList<>();
    for (var element : job.environment()) {
      result.add(inline(element));
    }
    return listOfAll(result);
  }

  private List<BExpr> rewriteExprs(List<BExpr> elements, Resolver resolver)
      throws BytecodeException, RefIndexOutOfBoundsException {
    ArrayList<BExpr> result = new ArrayList<>();
    for (var element : elements) {
      result.add(rewriteExpr(element, resolver));
    }
    return listOfAll(result);
  }

  private BExpr rewriteExpr(BExpr expr, Resolver resolver)
      throws BytecodeException, RefIndexOutOfBoundsException {
    return switch (expr) {
      case BCall call -> rewriteCall(call, resolver);
      case BCreateTuple createTuple -> rewriteCreateTuple(createTuple, resolver);
      case BCreateVariant createVariant -> rewriteCreateVariant(createVariant, resolver);
      case BFold fold -> rewriteFold(fold, resolver);
      case BIf if_ -> rewriteIf(if_, resolver);
      case BInvoke invoke -> rewriteInvoke(invoke, resolver);
      case BLambda lambda -> rewriteLambda(lambda, resolver);
      case BMap map -> rewriteMap(map, resolver);
      case BCreateArray createArray -> rewriteCreateArray(createArray, resolver);
      case BArrayGet arrayGet -> rewriteArrayGet(arrayGet, resolver);
      case BRef ref -> rewriteRef(ref, resolver);
      case BTupleGet tupleGet -> rewriteTupleGet(tupleGet, resolver);
      case BSwitch switch_ -> rewriteSwitch(switch_, resolver);
      case BValue value -> value;
    };
  }

  private BExpr rewriteCall(BCall call, Resolver resolver)
      throws BytecodeException, RefIndexOutOfBoundsException {
    var lambda = call.lambda();
    var arguments = call.arguments();

    var inlinedLambda = rewriteExpr(lambda, resolver);
    var inlinedArgs = rewriteExpr(arguments, resolver);

    if (lambda.equals(inlinedLambda) && arguments.equals(inlinedArgs)) {
      return call;
    } else {
      return bytecodeFactory.call(inlinedLambda, inlinedArgs);
    }
  }

  private BSwitch rewriteSwitch(BSwitch switch_, Resolver resolver)
      throws BytecodeException, RefIndexOutOfBoundsException {
    var variant = switch_.variant();
    var handlers = switch_.handlers();

    var rewrittenVariant = rewriteExpr(variant, resolver);
    var rewrittenHandlers = rewriteCreateTuple(handlers, resolver);

    if (variant.equals(rewrittenVariant) && handlers.equals(rewrittenHandlers)) {
      return switch_;
    } else {
      return bytecodeFactory.switch_(rewrittenVariant, rewrittenHandlers);
    }
  }

  private BCreateTuple rewriteCreateTuple(BCreateTuple createTuple, Resolver resolver)
      throws BytecodeException, RefIndexOutOfBoundsException {
    var items = createTuple.unvalidatedSubExprs();
    var rewrittenItems = rewriteExprs(items, resolver);
    if (items.equals(rewrittenItems)) {
      return createTuple;
    } else {
      return bytecodeFactory.createTuple(rewrittenItems);
    }
  }

  private BCreateVariant rewriteCreateVariant(BCreateVariant createVariant, Resolver resolver)
      throws BytecodeException, RefIndexOutOfBoundsException {
    var index = createVariant.index();
    var choice = createVariant.choice();

    // Only choice has to be rewritten as index is BValue
    var rewrittenChoice = rewriteExpr(choice, resolver);

    if (rewrittenChoice.equals(choice)) {
      return createVariant;
    } else {
      return bytecodeFactory.createVariant(createVariant.evaluationType(), index, rewrittenChoice);
    }
  }

  private BExpr rewriteIf(BIf if_, Resolver resolver)
      throws BytecodeException, RefIndexOutOfBoundsException {
    var condition = if_.condition();
    var then_ = if_.then_();
    var else_ = if_.else_();

    var rewrittenCondition = rewriteExpr(condition, resolver);
    var rewrittenThen = rewriteExpr(then_, resolver);
    var rewrittenElse = rewriteExpr(else_, resolver);

    if (condition.equals(rewrittenCondition)
        && then_.equals(rewrittenThen)
        && else_.equals(rewrittenElse)) {
      return if_;
    } else {
      return bytecodeFactory.if_(rewrittenCondition, rewrittenThen, rewrittenElse);
    }
  }

  private BExpr rewriteInvoke(BInvoke invoke, Resolver resolver)
      throws BytecodeException, RefIndexOutOfBoundsException {
    var method = invoke.method();
    var isPure = invoke.isPure();
    var arguments = invoke.arguments();

    var rewrittenMethod = rewriteExpr(method, resolver);
    var rewrittenIsPure = rewriteExpr(isPure, resolver);
    var rewrittenArguments = rewriteExpr(arguments, resolver);

    if (method.equals(rewrittenMethod)
        && isPure.equals(rewrittenIsPure)
        && arguments.equals(rewrittenArguments)) {
      return invoke;
    } else {
      return bytecodeFactory.invoke(
          invoke.evaluationType(), rewrittenMethod, rewrittenIsPure, rewrittenArguments);
    }
  }

  private BLambda rewriteLambda(BLambda lambda, Resolver resolver)
      throws BytecodeException, RefIndexOutOfBoundsException {
    var lambdaType = lambda.type();
    int paramsSize = lambdaType.params().size();
    var body = lambda.body();
    var rewrittenBody = rewriteExpr(body, resolver.withIncreasedParamCount(paramsSize + 1));
    if (body.equals(rewrittenBody)) {
      return lambda;
    } else {
      return bytecodeFactory.lambda(lambdaType, rewrittenBody);
    }
  }

  private BExpr rewriteMap(BMap map, Resolver resolver)
      throws BytecodeException, RefIndexOutOfBoundsException {
    var array = map.array();
    var mapper = map.mapper();

    var rewrittenArray = rewriteExpr(array, resolver);
    var rewrittenMapper = rewriteExpr(mapper, resolver);

    if (array.equals(rewrittenArray) && mapper.equals(rewrittenMapper)) {
      return map;
    } else {
      return bytecodeFactory.map(rewrittenArray, rewrittenMapper);
    }
  }

  private BExpr rewriteFold(BFold fold, Resolver resolver)
      throws BytecodeException, RefIndexOutOfBoundsException {
    var array = fold.array();
    var initial = fold.initial();
    var folder = fold.folder();

    var rewrittenArray = rewriteExpr(array, resolver);
    var rewrittenInitial = rewriteExpr(initial, resolver);
    var rewrittenFolder = rewriteExpr(folder, resolver);

    if (array.equals(rewrittenArray)
        && initial.equals(rewrittenInitial)
        && folder.equals(rewrittenFolder)) {
      return fold;
    } else {
      return bytecodeFactory.fold(rewrittenArray, rewrittenInitial, rewrittenFolder);
    }
  }

  private BExpr rewriteCreateArray(BCreateArray createArray, Resolver resolver)
      throws BytecodeException, RefIndexOutOfBoundsException {
    var elements = createArray.elements();
    var rewrittenElements = rewriteExprs(elements, resolver);
    if (elements.equals(rewrittenElements)) {
      return createArray;
    } else {
      return bytecodeFactory.createArray(createArray.evaluationType(), rewrittenElements);
    }
  }

  private BExpr rewriteArrayGet(BArrayGet arrayGet, Resolver resolver)
      throws BytecodeException, RefIndexOutOfBoundsException {
    var array = arrayGet.array();
    var index = arrayGet.index();

    var rewrittenArray = rewriteExpr(array, resolver);
    var rewrittenIndex = rewriteExpr(index, resolver);

    if (array.equals(rewrittenArray) && index.equals(rewrittenIndex)) {
      return arrayGet;
    } else {
      return bytecodeFactory.arrayGet(rewrittenArray, rewrittenIndex);
    }
  }

  private BExpr rewriteTupleGet(BTupleGet tupleGet, Resolver resolver)
      throws BytecodeException, RefIndexOutOfBoundsException {
    var tuple = tupleGet.tuple();
    var rewrittenTuple = rewriteExpr(tuple, resolver);
    if (tuple.equals(rewrittenTuple)) {
      return tupleGet;
    } else {
      return bytecodeFactory.tupleGet(rewrittenTuple, tupleGet.index());
    }
  }

  private BExpr rewriteRef(BRef ref, Resolver resolver)
      throws BytecodeException, RefIndexOutOfBoundsException {
    return resolver.resolve(ref);
  }

  private static class Resolver {
    private final int paramCount;
    private final List<BExpr> environment;

    private Resolver(List<BExpr> environment) {
      this(0, environment);
    }

    private Resolver(int paramCount, List<BExpr> environment) {
      this.paramCount = paramCount;
      this.environment = environment;
    }

    private Resolver withIncreasedParamCount(int delta) {
      return new Resolver(paramCount + delta, environment);
    }

    private BExpr resolve(BRef ref) throws BytecodeException, RefIndexOutOfBoundsException {
      int index = ref.index().toJavaBigInteger().intValue();
      if (index < 0) {
        throw new RefIndexOutOfBoundsException(index, paramCount + environment.size());
      }
      if (index < paramCount) {
        return ref;
      }
      int environmentIndex = index - paramCount;
      if (environmentIndex < environment.size()) {
        var referenced = environment.get(environmentIndex);
        var jobEvaluationType = referenced.evaluationType();
        if (jobEvaluationType.equals(ref.evaluationType())) {
          return referenced;
        } else {
          throw new RuntimeException("environment(%d) evaluationType is %s but expected %s."
              .formatted(index, jobEvaluationType.q(), ref.evaluationType().q()));
        }
      }
      throw new RefIndexOutOfBoundsException(index, paramCount + environment.size());
    }
  }
}
