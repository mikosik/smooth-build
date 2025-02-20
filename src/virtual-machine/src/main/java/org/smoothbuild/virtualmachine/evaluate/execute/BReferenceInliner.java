package org.smoothbuild.virtualmachine.evaluate.execute;

import jakarta.inject.Inject;
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
import org.smoothbuild.virtualmachine.bytecode.expr.base.BMap;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOrder;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BPick;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BReference;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSelect;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSwitch;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;

public class BReferenceInliner {
  private final BytecodeFactory bytecodeFactory;

  @Inject
  public BReferenceInliner(BytecodeFactory bytecodeFactory) {
    this.bytecodeFactory = bytecodeFactory;
  }

  public BExpr inline(Job job) throws BytecodeException {
    List<BExpr> inlinedEnvironment = job.environment().map(this::inline);
    return rewriteExpr(job.expr(), new Resolver(inlinedEnvironment));
  }

  private List<BExpr> rewriteExprs(List<BExpr> elements, Resolver resolver)
      throws BytecodeException {
    return elements.map(e -> rewriteExpr(e, resolver));
  }

  private BExpr rewriteExpr(BExpr expr, Resolver resolver) throws BytecodeException {
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
      case BReference reference -> rewriteReference(reference, resolver);
      case BSelect select -> rewriteSelect(select, resolver);
      case BSwitch switch_ -> rewriteSwitch(switch_, resolver);
      case BValue value -> value;
    };
  }

  private BExpr rewriteCall(BCall call, Resolver resolver) throws BytecodeException {
    var subExprs = call.subExprs();
    var lambda = subExprs.lambda();
    var arguments = subExprs.arguments();
    var inlinedLambda = rewriteExpr(lambda, resolver);
    var inlinedArgs = rewriteExpr(arguments, resolver);
    if (lambda.equals(inlinedLambda) && arguments.equals(inlinedArgs)) {
      return call;
    } else {
      return bytecodeFactory.call(inlinedLambda, inlinedArgs);
    }
  }

  private BSwitch rewriteSwitch(BSwitch switch_, Resolver resolver) throws BytecodeException {
    var subExprs = switch_.subExprs();
    var choice = subExprs.choice();
    var handlers = subExprs.handlers();
    var rewrittenChoice = rewriteExpr(choice, resolver);
    var rewrittenHandlers = rewriteCombine(handlers, resolver);
    if (choice.equals(rewrittenChoice) && handlers.equals(rewrittenHandlers)) {
      return switch_;
    } else {
      return bytecodeFactory.switch_(rewrittenChoice, rewrittenHandlers);
    }
  }

  private BCombine rewriteCombine(BCombine combine, Resolver resolver) throws BytecodeException {
    var items = combine.subExprs().items();
    var rewrittenItems = rewriteExprs(items, resolver);
    if (items.equals(rewrittenItems)) {
      return combine;
    } else {
      return bytecodeFactory.combine(rewrittenItems);
    }
  }

  private BChoose rewriteChoose(BChoose choose, Resolver resolver) throws BytecodeException {
    var subExprs = choose.subExprs();
    var index = subExprs.index();
    var chosen = subExprs.chosen();
    // Only chosen has to be rewritten as index is BValue
    var rewrittenChosen = rewriteExpr(chosen, resolver);
    if (rewrittenChosen.equals(chosen)) {
      return choose;
    } else {
      return bytecodeFactory.choose(choose.evaluationType(), index, rewrittenChosen);
    }
  }

  private BExpr rewriteIf(BIf if_, Resolver resolver) throws BytecodeException {
    var subExprs = if_.subExprs();
    var condition = subExprs.condition();
    var then_ = subExprs.then_();
    var else_ = subExprs.else_();
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

  private BExpr rewriteInvoke(BInvoke invoke, Resolver resolver) throws BytecodeException {
    var subExprs = invoke.subExprs();
    var method = subExprs.method();
    var isPure = subExprs.isPure();
    var arguments = subExprs.arguments();

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

  private BLambda rewriteLambda(BLambda lambda, Resolver resolver) throws BytecodeException {
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

  private BExpr rewriteMap(BMap map, Resolver resolver) throws BytecodeException {
    var subExprs = map.subExprs();
    var array = subExprs.array();
    var mapper = subExprs.mapper();

    var rewrittenArray = rewriteExpr(array, resolver);
    var rewrittenMapper = rewriteExpr(mapper, resolver);
    if (array.equals(rewrittenArray) && mapper.equals(rewrittenMapper)) {
      return map;
    } else {
      return bytecodeFactory.map(rewrittenArray, rewrittenMapper);
    }
  }

  private BExpr rewriteFold(BFold fold, Resolver resolver) throws BytecodeException {
    var subExprs = fold.subExprs();
    var array = subExprs.array();
    var initial = subExprs.initial();
    var folder = subExprs.folder();

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

  private BExpr rewriteOrder(BOrder order, Resolver resolver) throws BytecodeException {
    var elements = order.elements();
    var rewrittenElements = rewriteExprs(elements, resolver);
    if (elements.equals(rewrittenElements)) {
      return order;
    } else {
      return bytecodeFactory.order(order.evaluationType(), rewrittenElements);
    }
  }

  private BExpr rewritePick(BPick pick, Resolver resolver) throws BytecodeException {
    var subExprs = pick.subExprs();
    var pickable = subExprs.pickable();
    var index = subExprs.index();
    var rewrittenPickable = rewriteExpr(pickable, resolver);
    var rewrittenIndex = rewriteExpr(index, resolver);
    if (pickable.equals(rewrittenPickable) && index.equals(rewrittenIndex)) {
      return pick;
    } else {
      return bytecodeFactory.pick(rewrittenPickable, rewrittenIndex);
    }
  }

  private BExpr rewriteSelect(BSelect select, Resolver resolver) throws BytecodeException {
    var subExprs = select.subExprs();
    var selectable = subExprs.selectable();
    var rewrittenSelectable = rewriteExpr(selectable, resolver);
    if (selectable.equals(rewrittenSelectable)) {
      return select;
    } else {
      return bytecodeFactory.select(rewrittenSelectable, subExprs.index());
    }
  }

  private BExpr rewriteReference(BReference reference, Resolver resolver) throws BytecodeException {
    return resolver.resolve(reference);
  }

  private static class Resolver {
    private final int paramCount;
    private final List<BExpr> environment;

    public Resolver(List<BExpr> environment) {
      this(0, environment);
    }

    public Resolver(int paramCount, List<BExpr> environment) {
      this.paramCount = paramCount;
      this.environment = environment;
    }

    public Resolver withIncreasedParamCount(int delta) {
      return new Resolver(paramCount + delta, environment);
    }

    public BExpr resolve(BReference reference) throws BytecodeException {
      int index = reference.index().toJavaBigInteger().intValue();
      if (index < 0) {
        throw new ReferenceIndexOutOfBoundsException(index, paramCount + environment.size());
      }
      if (index < paramCount) {
        return reference;
      }
      int environmentIndex = index - paramCount;
      if (environmentIndex < environment.size()) {
        var referenced = environment.get(environmentIndex);
        var jobEvaluationType = referenced.evaluationType();
        if (jobEvaluationType.equals(reference.evaluationType())) {
          return referenced;
        } else {
          throw new RuntimeException("environment(%d) evaluationType is %s but expected %s."
              .formatted(
                  index, jobEvaluationType.q(), reference.evaluationType().q()));
        }
      }
      throw new ReferenceIndexOutOfBoundsException(index, paramCount + environment.size());
    }
  }
}
