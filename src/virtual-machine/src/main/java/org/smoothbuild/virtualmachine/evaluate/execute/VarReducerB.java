package org.smoothbuild.virtualmachine.evaluate.execute;

import jakarta.inject.Inject;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeF;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.CallB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.CombineB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.OrderB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.PickB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.SelectB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.VarB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.LambdaB;

public class VarReducerB {
  private final BytecodeF bytecodeF;

  @Inject
  public VarReducerB(BytecodeF bytecodeF) {
    this.bytecodeF = bytecodeF;
  }

  public ExprB inline(Job job) throws BytecodeException {
    List<ExprB> inlinedEnvironment = job.environment().map(this::inline);
    return rewriteExpr(job.exprB(), new Resolver(inlinedEnvironment));
  }

  private List<ExprB> rewriteExprs(Resolver resolver, List<ExprB> elements)
      throws BytecodeException {
    return elements.map(e -> rewriteExpr(e, resolver));
  }

  private ExprB rewriteExpr(ExprB exprB, Resolver resolver) throws BytecodeException {
    return switch (exprB) {
      case CallB callB -> rewriteCall(callB, resolver);
      case CombineB combineB -> rewriteCombine(combineB, resolver);
      case OrderB orderB -> rewriteOrder(orderB, resolver);
      case PickB pickB -> rewritePick(pickB, resolver);
      case VarB varB -> rewriteVar(varB, resolver);
      case SelectB selectB -> rewriteSelect(selectB, resolver);
      case LambdaB lambdaB -> rewriteLambda(lambdaB, resolver);
      default -> exprB;
    };
  }

  private ExprB rewriteCall(CallB callB, Resolver resolver) throws BytecodeException {
    var subExprs = callB.subExprs();
    var func = subExprs.func();
    var args = subExprs.args();
    var inlinedFunc = rewriteExpr(func, resolver);
    var inlinedArgs = rewriteCombine(args, resolver);
    if (func.equals(inlinedFunc) && args.equals(inlinedArgs)) {
      return callB;
    } else {
      return bytecodeF.call(inlinedFunc, inlinedArgs);
    }
  }

  private CombineB rewriteCombine(CombineB combineB, Resolver resolver) throws BytecodeException {
    var items = combineB.items();
    var rewrittenItems = rewriteExprs(resolver, items);
    if (items.equals(rewrittenItems)) {
      return combineB;
    } else {
      return bytecodeF.combine(rewrittenItems);
    }
  }

  private ExprB rewriteOrder(OrderB orderB, Resolver resolver) throws BytecodeException {
    var elements = orderB.elements();
    var rewrittenElements = rewriteExprs(resolver, elements);
    if (elements.equals(rewrittenElements)) {
      return orderB;
    } else {
      return bytecodeF.order(orderB.evaluationType(), rewrittenElements);
    }
  }

  private ExprB rewritePick(PickB pickB, Resolver resolver) throws BytecodeException {
    var subExprs = pickB.subExprs();
    var pickable = subExprs.pickable();
    var index = subExprs.index();
    var rewrittenPickable = rewriteExpr(pickable, resolver);
    var rewrittenIndex = rewriteExpr(index, resolver);
    if (pickable.equals(rewrittenPickable) && index.equals(rewrittenIndex)) {
      return pickB;
    } else {
      return bytecodeF.pick(rewrittenPickable, rewrittenIndex);
    }
  }

  private ExprB rewriteVar(VarB var, Resolver resolver) throws BytecodeException {
    return resolver.resolve(var);
  }

  private ExprB rewriteSelect(SelectB selectB, Resolver resolver) throws BytecodeException {
    var subExprsB = selectB.subExprs();
    var selectable = subExprsB.selectable();
    var rewrittenSelectable = rewriteExpr(selectable, resolver);
    if (selectable.equals(rewrittenSelectable)) {
      return selectB;
    } else {
      return bytecodeF.select(rewrittenSelectable, subExprsB.index());
    }
  }

  private LambdaB rewriteLambda(LambdaB lambdaB, Resolver resolver) throws BytecodeException {
    var funcTB = lambdaB.type();
    int paramsSize = funcTB.params().size();
    var body = lambdaB.body();
    var rewrittenBody = rewriteExpr(body, resolver.withIncreasedParamCount(paramsSize));
    if (body.equals(rewrittenBody)) {
      return lambdaB;
    } else {
      return bytecodeF.lambda(funcTB, rewrittenBody);
    }
  }

  private static class Resolver {
    private final int paramCount;
    private final List<ExprB> environment;

    public Resolver(List<ExprB> environment) {
      this(0, environment);
    }

    public Resolver(int paramCount, List<ExprB> environment) {
      this.paramCount = paramCount;
      this.environment = environment;
    }

    public Resolver withIncreasedParamCount(int delta) {
      return new Resolver(paramCount + delta, environment);
    }

    public ExprB resolve(VarB varB) throws BytecodeException {
      int index = varB.index().toJavaBigInteger().intValue();
      if (index < 0) {
        throw new VarOutOfBoundsException(index, paramCount + environment.size());
      }
      if (index < paramCount) {
        return varB;
      }
      int environmentIndex = index - paramCount;
      if (environmentIndex < environment.size()) {
        var referenced = environment.get(environmentIndex);
        var jobEvaluationType = referenced.evaluationType();
        if (jobEvaluationType.equals(varB.evaluationType())) {
          return referenced;
        } else {
          throw new RuntimeException("environment(%d) evaluationType is %s but expected %s."
              .formatted(index, jobEvaluationType.q(), varB.evaluationType().q()));
        }
      }
      throw new VarOutOfBoundsException(index, paramCount + environment.size());
    }
  }
}
