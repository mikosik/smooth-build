package org.smoothbuild.virtualmachine.evaluate.base;

import static org.smoothbuild.common.collect.List.listOfAll;

import jakarta.inject.Inject;
import java.util.ArrayList;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCall;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BChoose;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCombine;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BFold;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BIf;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BLambda;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BLambdaRef;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BMap;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOrder;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BParamRef;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BPick;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSelect;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSwitch;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.job.Job;
import org.smoothbuild.virtualmachine.evaluate.job.ParamRefIndexOutOfBoundsException;

public class BParamRefInliner {
  private final BytecodeFactory bytecodeFactory;

  @Inject
  public BParamRefInliner(BytecodeFactory bytecodeFactory) {
    this.bytecodeFactory = bytecodeFactory;
  }

  public BExpr inline(Job job) throws BytecodeException, ParamRefIndexOutOfBoundsException {
    List<BExpr> inlinedEnvironment = rewriteJobs(job);
    return rewriteExpr(job.expr(), new Resolver(inlinedEnvironment));
  }

  private List<BExpr> rewriteJobs(Job job)
      throws BytecodeException, ParamRefIndexOutOfBoundsException {
    ArrayList<BExpr> result = new ArrayList<>();
    for (var element : job.environment()) {
      result.add(inline(element));
    }
    return listOfAll(result);
  }

  private List<BExpr> rewriteExprs(List<BExpr> elements, Resolver resolver)
      throws BytecodeException, ParamRefIndexOutOfBoundsException {
    ArrayList<BExpr> result = new ArrayList<>();
    for (var element : elements) {
      result.add(rewriteExpr(element, resolver));
    }
    return listOfAll(result);
  }

  private BExpr rewriteExpr(BExpr expr, Resolver resolver)
      throws BytecodeException, ParamRefIndexOutOfBoundsException {
    return switch (expr) {
      case BCall call -> rewriteCall(call, resolver);
      case BCombine combine -> rewriteCombine(combine, resolver);
      case BChoose choose -> rewriteChoose(choose, resolver);
      case BFold fold -> rewriteFold(fold, resolver);
      case BIf if_ -> rewriteIf(if_, resolver);
      case BInvoke invoke -> rewriteInvoke(invoke, resolver);
      case BLambda lambda -> rewriteLambda(lambda, resolver);
      case BMap map -> rewriteMap(map, resolver);
      case BOrder order -> rewriteOrder(order, resolver);
      case BPick pick -> rewritePick(pick, resolver);
      case BParamRef paramRef -> rewriteParamRef(paramRef, resolver);
      case BLambdaRef reference -> rewriteLambdaRef(reference);
      case BSelect select -> rewriteSelect(select, resolver);
      case BSwitch switch_ -> rewriteSwitch(switch_, resolver);
      case BValue value -> value;
    };
  }

  private BExpr rewriteCall(BCall call, Resolver resolver)
      throws BytecodeException, ParamRefIndexOutOfBoundsException {
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
      throws BytecodeException, ParamRefIndexOutOfBoundsException {
    var choice = switch_.choice();
    var handlers = switch_.handlers();

    var rewrittenChoice = rewriteExpr(choice, resolver);
    var rewrittenHandlers = rewriteCombine(handlers, resolver);

    if (choice.equals(rewrittenChoice) && handlers.equals(rewrittenHandlers)) {
      return switch_;
    } else {
      return bytecodeFactory.switch_(rewrittenChoice, rewrittenHandlers);
    }
  }

  private BCombine rewriteCombine(BCombine combine, Resolver resolver)
      throws BytecodeException, ParamRefIndexOutOfBoundsException {
    var items = combine.subExprs();
    var rewrittenItems = rewriteExprs(items, resolver);
    if (items.equals(rewrittenItems)) {
      return combine;
    } else {
      return bytecodeFactory.combine(rewrittenItems);
    }
  }

  private BChoose rewriteChoose(BChoose choose, Resolver resolver)
      throws BytecodeException, ParamRefIndexOutOfBoundsException {
    var index = choose.index();
    var chosen = choose.chosen();

    // Only chosen has to be rewritten as index is BValue
    var rewrittenChosen = rewriteExpr(chosen, resolver);

    if (rewrittenChosen.equals(chosen)) {
      return choose;
    } else {
      return bytecodeFactory.choose(choose.evaluationType(), index, rewrittenChosen);
    }
  }

  private BExpr rewriteIf(BIf if_, Resolver resolver)
      throws BytecodeException, ParamRefIndexOutOfBoundsException {
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
      throws BytecodeException, ParamRefIndexOutOfBoundsException {
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
      throws BytecodeException, ParamRefIndexOutOfBoundsException {
    var lambdaType = lambda.type();
    int paramsSize = lambdaType.params().size();
    var body = lambda.body();
    var rewrittenBody = rewriteExpr(body, resolver.withIncreasedParamCount(paramsSize));
    if (body.equals(rewrittenBody)) {
      return lambda;
    } else {
      return bytecodeFactory.lambda(lambdaType, rewrittenBody);
    }
  }

  private BExpr rewriteMap(BMap map, Resolver resolver)
      throws BytecodeException, ParamRefIndexOutOfBoundsException {
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
      throws BytecodeException, ParamRefIndexOutOfBoundsException {
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

  private BExpr rewriteOrder(BOrder order, Resolver resolver)
      throws BytecodeException, ParamRefIndexOutOfBoundsException {
    var elements = order.elements();
    var rewrittenElements = rewriteExprs(elements, resolver);
    if (elements.equals(rewrittenElements)) {
      return order;
    } else {
      return bytecodeFactory.order(order.evaluationType(), rewrittenElements);
    }
  }

  private BExpr rewritePick(BPick pick, Resolver resolver)
      throws BytecodeException, ParamRefIndexOutOfBoundsException {
    var pickable = pick.pickable();
    var index = pick.index();

    var rewrittenPickable = rewriteExpr(pickable, resolver);
    var rewrittenIndex = rewriteExpr(index, resolver);

    if (pickable.equals(rewrittenPickable) && index.equals(rewrittenIndex)) {
      return pick;
    } else {
      return bytecodeFactory.pick(rewrittenPickable, rewrittenIndex);
    }
  }

  private BExpr rewriteSelect(BSelect select, Resolver resolver)
      throws BytecodeException, ParamRefIndexOutOfBoundsException {
    var selectable = select.selectable();
    var rewrittenSelectable = rewriteExpr(selectable, resolver);
    if (selectable.equals(rewrittenSelectable)) {
      return select;
    } else {
      return bytecodeFactory.select(rewrittenSelectable, select.index());
    }
  }

  private BExpr rewriteParamRef(BParamRef paramRef, Resolver resolver)
      throws BytecodeException, ParamRefIndexOutOfBoundsException {
    return resolver.resolve(paramRef);
  }

  private BExpr rewriteLambdaRef(BLambdaRef lambdaRef) {
    return lambdaRef;
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

    private BExpr resolve(BParamRef paramRef)
        throws BytecodeException, ParamRefIndexOutOfBoundsException {
      int index = paramRef.index().toJavaBigInteger().intValue();
      if (index < 0) {
        throw new ParamRefIndexOutOfBoundsException(index, paramCount + environment.size());
      }
      if (index < paramCount) {
        return paramRef;
      }
      int environmentIndex = index - paramCount;
      if (environmentIndex < environment.size()) {
        var referenced = environment.get(environmentIndex);
        var jobEvaluationType = referenced.evaluationType();
        if (jobEvaluationType.equals(paramRef.evaluationType())) {
          return referenced;
        } else {
          throw new RuntimeException("environment(%d) evaluationType is %s but expected %s."
              .formatted(index, jobEvaluationType.q(), paramRef.evaluationType().q()));
        }
      }
      throw new ParamRefIndexOutOfBoundsException(index, paramCount + environment.size());
    }
  }
}
