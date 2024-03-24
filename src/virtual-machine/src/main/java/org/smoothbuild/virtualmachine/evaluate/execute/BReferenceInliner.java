package org.smoothbuild.virtualmachine.evaluate.execute;

import jakarta.inject.Inject;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BCall;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BCombine;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BIf;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BOrder;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BPick;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BReference;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BSelect;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BLambda;

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

  private List<BExpr> rewriteExprs(Resolver resolver, List<BExpr> elements)
      throws BytecodeException {
    return elements.map(e -> rewriteExpr(e, resolver));
  }

  private BExpr rewriteExpr(BExpr expr, Resolver resolver) throws BytecodeException {
    return switch (expr) {
      case BCall call -> rewriteCall(call, resolver);
      case BCombine combine -> rewriteCombine(combine, resolver);
      case BIf if_ -> rewriteIf(if_, resolver);
      case BOrder order -> rewriteOrder(order, resolver);
      case BPick pick -> rewritePick(pick, resolver);
      case BReference reference -> rewriteVar(reference, resolver);
      case BSelect select -> rewriteSelect(select, resolver);
      case BLambda lambda -> rewriteLambda(lambda, resolver);
      default -> expr;
    };
  }

  private BExpr rewriteCall(BCall call, Resolver resolver) throws BytecodeException {
    var subExprs = call.subExprs();
    var func = subExprs.func();
    var args = subExprs.args();
    var inlinedFunc = rewriteExpr(func, resolver);
    var inlinedArgs = rewriteCombine(args, resolver);
    if (func.equals(inlinedFunc) && args.equals(inlinedArgs)) {
      return call;
    } else {
      return bytecodeFactory.call(inlinedFunc, inlinedArgs);
    }
  }

  private BCombine rewriteCombine(BCombine combine, Resolver resolver) throws BytecodeException {
    var items = combine.items();
    var rewrittenItems = rewriteExprs(resolver, items);
    if (items.equals(rewrittenItems)) {
      return combine;
    } else {
      return bytecodeFactory.combine(rewrittenItems);
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

  private BExpr rewriteOrder(BOrder order, Resolver resolver) throws BytecodeException {
    var elements = order.elements();
    var rewrittenElements = rewriteExprs(resolver, elements);
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

  private BExpr rewriteVar(BReference var, Resolver resolver) throws BytecodeException {
    return resolver.resolve(var);
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

  private BLambda rewriteLambda(BLambda lambda, Resolver resolver) throws BytecodeException {
    var funcType = lambda.type();
    int paramsSize = funcType.params().size();
    var body = lambda.body();
    var rewrittenBody = rewriteExpr(body, resolver.withIncreasedParamCount(paramsSize));
    if (body.equals(rewrittenBody)) {
      return lambda;
    } else {
      return bytecodeFactory.lambda(funcType, rewrittenBody);
    }
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
