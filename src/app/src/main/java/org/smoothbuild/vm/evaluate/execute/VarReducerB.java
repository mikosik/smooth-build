package org.smoothbuild.vm.evaluate.execute;

import static org.smoothbuild.util.collect.Lists.map;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.vm.bytecode.BytecodeF;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.oper.CallB;
import org.smoothbuild.vm.bytecode.expr.oper.ClosurizeB;
import org.smoothbuild.vm.bytecode.expr.oper.CombineB;
import org.smoothbuild.vm.bytecode.expr.oper.OrderB;
import org.smoothbuild.vm.bytecode.expr.oper.PickB;
import org.smoothbuild.vm.bytecode.expr.oper.SelectB;
import org.smoothbuild.vm.bytecode.expr.oper.VarB;
import org.smoothbuild.vm.bytecode.expr.value.ClosureB;
import org.smoothbuild.vm.bytecode.expr.value.ExprFuncB;

import com.google.common.collect.ImmutableList;

public class VarReducerB {
  private final BytecodeF bytecodeF;

  @Inject
  public VarReducerB(BytecodeF bytecodeF) {
    this.bytecodeF = bytecodeF;
  }

  public ImmutableList<ExprB> inline(ImmutableList<Job> environment) {
    return map(environment, this::inline);
  }

  public ExprB inline(Job job) {
    return rewriteExpr(job.exprB(), new Resolver(inline(job.environment())));
  }

  private ImmutableList<ExprB> rewriteExprs(
      Resolver resolver, List<ExprB> elements) {
    return map(elements, e -> rewriteExpr(e, resolver));
  }

  private ExprB rewriteExpr(ExprB exprB, Resolver resolver) {
    return switch (exprB) {
      // @formatter:off
      case CallB      callB      -> rewriteCall(callB, resolver);
      case ClosurizeB closurizeB -> rewriteClosurize(closurizeB, resolver);
      case CombineB   combineB   -> rewriteCombine(combineB, resolver);
      case OrderB     orderB     -> rewriteOrder(orderB, resolver);
      case PickB      pickB      -> rewritePick(pickB, resolver);
      case VarB       varB       -> rewriteVar(varB, resolver);
      case SelectB    selectB    -> rewriteSelect(selectB, resolver);

      case ClosureB   closureB   -> rewriteClosure(closureB, resolver);
      case ExprFuncB  exprFuncB  -> rewriteExprFunc(exprFuncB, resolver);
      default                    -> exprB;
      // @formatter:on
    };
  }

  private ExprB rewriteCall(CallB callB, Resolver resolver) {
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

  private ExprB rewriteClosurize(ClosurizeB closurizeB, Resolver resolver) {
    var exprFuncB = closurizeB.func();
    var funcTB = exprFuncB.type();
    int paramSize = funcTB.params().size();
    var body = exprFuncB.body();
    var rewrittenBody = rewriteExpr(body, resolver.withIncreasedParamCount(paramSize));
    if (body.equals(rewrittenBody)) {
      return closurizeB;
    } else {
      return bytecodeF.closurize(bytecodeF.exprFunc(funcTB, rewrittenBody));
    }
  }

  private CombineB rewriteCombine(CombineB combineB, Resolver resolver) {
    var items = combineB.items();
    var rewrittenItems = rewriteExprs(resolver, items);
    if (items.equals(rewrittenItems)) {
      return combineB;
    } else {
      return bytecodeF.combine(rewrittenItems);
    }
  }

  private ExprB rewriteOrder(OrderB orderB, Resolver resolver) {
    var elements = orderB.elements();
    var rewrittenElements = rewriteExprs(resolver, elements);
    if (elements.equals(rewrittenElements)) {
      return orderB;
    } else {
      return bytecodeF.order(orderB.evaluationT(), rewrittenElements);
    }
  }

  private ExprB rewritePick(PickB pickB, Resolver resolver) {
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

  private ExprB rewriteVar(VarB var, Resolver resolver) {
    return resolver.resolve(var);
  }

  private ExprB rewriteSelect(SelectB selectB, Resolver resolver) {
    var subExprsB = selectB.subExprs();
    var selectable = subExprsB.selectable();
    var rewrittenSelectable = rewriteExpr(selectable, resolver);
    if (selectable.equals(rewrittenSelectable)) {
      return selectB;
    } else {
      return bytecodeF.select(rewrittenSelectable, subExprsB.index());
    }
  }

  private ExprB rewriteClosure(ClosureB closureB, Resolver resolver) {
    var environment = closureB.environment();
    var func = closureB.func();
    var rewrittenFunc = rewriteExprFunc(
        func, resolver.withIncreasedParamCount(environment.items().size()));
    if (func.equals(rewrittenFunc)) {
      return closureB;
    } else {
      return bytecodeF.closure(environment, rewrittenFunc);
    }
  }

  private ExprFuncB rewriteExprFunc(ExprFuncB exprFuncB, Resolver resolver) {
    var funcTB = exprFuncB.type();
    int paramsSize = funcTB.params().size();
    var body = exprFuncB.body();
    var rewrittenBody = rewriteExpr(body, resolver.withIncreasedParamCount(paramsSize));
    if (body.equals(rewrittenBody)) {
      return exprFuncB;
    } else {
      return bytecodeF.exprFunc(funcTB, rewrittenBody);
    }
  }

  private static class Resolver {
    private final int paramCount;
    private final ImmutableList<ExprB> environment;

    public Resolver(ImmutableList<ExprB> environment) {
      this(0, environment);
    }

    public Resolver(int paramCount, ImmutableList<ExprB> environment) {
      this.paramCount = paramCount;
      this.environment = environment;
    }

    public Resolver withIncreasedParamCount(int delta) {
      return new Resolver(paramCount + delta, environment);
    }

    public ExprB resolve(VarB varB) {
      int index = varB.index().toJ().intValue();
      if (index < 0) {
        throw new VarOutOfBoundsExc(index, paramCount + environment.size());
      }
      if (index < paramCount) {
        return varB;
      }
      int environmentIndex = index - paramCount;
      if (environmentIndex < environment.size()) {
        var referenced = environment.get(environmentIndex);
        var jobEvaluationT = referenced.evaluationT();
        if (jobEvaluationT.equals(varB.evaluationT())) {
          return referenced;
        } else {
          throw new RuntimeException("environment(%d) evaluationT is %s but expected %s."
              .formatted(index, jobEvaluationT.q(), varB.evaluationT().q()));
        }
      }
      throw new VarOutOfBoundsExc(index, paramCount + environment.size());
    }
  }
}
