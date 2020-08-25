package org.smoothbuild.nativ;

import static org.smoothbuild.lang.parse.ParseError.parseError;
import static org.smoothbuild.nativ.MapTypeToJType.mapTypeToJType;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.smoothbuild.cli.console.Logger;
import org.smoothbuild.db.record.base.Record;
import org.smoothbuild.lang.base.Native;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.parse.ast.Ast;
import org.smoothbuild.lang.parse.ast.AstVisitor;
import org.smoothbuild.lang.parse.ast.EvaluableNode;
import org.smoothbuild.lang.parse.ast.FuncNode;
import org.smoothbuild.lang.parse.ast.ItemNode;
import org.smoothbuild.lang.parse.ast.ValueNode;

public class Natives {
  private final Map<String, Native> map;

  public Natives(Map<String, Native> map) {
    this.map = map;
  }

  public void assignNatives(Ast ast, Logger logger) {
    new AstVisitor() {
      @Override
      public void visitFunc(FuncNode func) {
        super.visitFunc(func);
        assignNative(func, this::nativeParameterTypesMatchesFuncParameters);
      }

      @Override
      public void visitValue(ValueNode value) {
        super.visitValue(value);
        assignNative(value, this::nativeHasOneParameter);
      }

      private void assignNative(EvaluableNode evaluable,
          BiFunction<Native, EvaluableNode, Boolean> parameterChecker) {
        if (map.containsKey(evaluable.name())) {
          Native nativ = map.get(evaluable.name());
          if (evaluable.isNative()) {
            if (nativeTypesMatchesDeclaredTypes(evaluable, nativ, parameterChecker)) {
              evaluable.setNative(nativ);
            }
          } else {
            logger.log(parseError(evaluable, "'" + evaluable.name()
                + "' has both definition and native implementation in " + nativ.jarFile() + "."));
          }
        } else {
          if (evaluable.isNative()) {
            logger.log(parseError(evaluable, "'" + evaluable.name()
                + "' is native but does not have native implementation."));
          }
        }
      }

      private boolean nativeTypesMatchesDeclaredTypes(EvaluableNode evaluable, Native nativ,
          BiFunction<Native, EvaluableNode, Boolean> parameterChecker) {
        Method method = nativ.method();
        Type resultType = evaluable.type().get();
        Class<?> resultJType = method.getReturnType();
        if (!mapTypeToJType(resultType).equals(resultJType)) {
          logger.log(parseError(evaluable, "'" + evaluable.name() + "' declares type "
              + resultType.q() + " so its native implementation result type must be "
              + mapTypeToJType(resultType).getCanonicalName() + " but it is "
              + resultJType.getCanonicalName() + "."));
          return false;
        }
        return parameterChecker.apply(nativ, evaluable);
      }

      private boolean nativeParameterTypesMatchesFuncParameters(
          Native nativ, EvaluableNode evaluable) {
        FuncNode func = (FuncNode) evaluable;
        Parameter[] nativeParams = nativ.method().getParameters();
        List<ItemNode> params = func.params();
        if (params.size() != nativeParams.length - 1) {
          logger.log(parseError(func, "Function '" + func.name() + "' has "
              + params.size() + " parameter(s) but its native implementation has "
              + (nativeParams.length - 1) + " parameter(s)."));
          return false;
        }
        for (int i = 0; i < params.size(); i++) {
          String declaredName = params.get(i).name();
          Parameter nativeParam = nativeParams[i + 1];
          Type paramType = params.get(i).typeNode().type().get();
          Class<?> paramJType = nativeParam.getType();
          Class<? extends Record> expectedParamJType = mapTypeToJType(paramType);
          if (!expectedParamJType.equals(paramJType)) {
            logger.log(parseError(func, "Function '" + func.name()
                + "' parameter '" + declaredName + "' has type "
                + paramType.name() + " so its native implementation type must be "
                + expectedParamJType.getCanonicalName() + " but it is "
                + paramJType.getCanonicalName() + "."));
            return false;
          }
        }
        return true;
      }

      private boolean nativeHasOneParameter(Native nativ, EvaluableNode evaluable) {
        ValueNode value = (ValueNode) evaluable;
        int paramCount = nativ.method().getParameters().length;
        if (paramCount != 1) {
          logger.log(parseError(value, "'" + value.name()
              + "' has native implementation that has too many parameter(s) = " +
              paramCount));
          return false;
        }
        return true;
      }
    }.visitAst(ast);
  }
}
