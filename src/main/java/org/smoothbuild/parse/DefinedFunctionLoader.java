package org.smoothbuild.parse;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.smoothbuild.util.Lists.map;

import java.util.List;
import java.util.Map;

import org.smoothbuild.antlr.SmoothParser.ExprContext;
import org.smoothbuild.lang.expr.ArrayExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.StringLiteralExpression;
import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.Parameter;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.Conversions;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.parse.ast.ArrayNode;
import org.smoothbuild.parse.ast.CallNode;
import org.smoothbuild.parse.ast.ExprNode;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.parse.ast.ParamNode;
import org.smoothbuild.parse.ast.StringNode;

public class DefinedFunctionLoader {
  public static DefinedFunction loadDefinedFunction(Functions loadedFunctions,
      FuncNode funcNode) {
    return new Worker(loadedFunctions).loadFunction(funcNode);
  }

  private static class Worker {
    private final Functions loadedFunctions;

    public Worker(Functions loadedFunctions) {
      this.loadedFunctions = loadedFunctions;
    }

    public DefinedFunction loadFunction(FuncNode func) {
      List<Parameter> parameters = createParameters(func.params());
      Expression expression = createExpression(func.expr());
      return createFunction(func, expression);
    }

    private static List<Parameter> createParameters(List<ParamNode> params) {
      return params
          .stream()
          .map(p -> new Parameter(p.type().get(Type.class), p.name(), null))
          .collect(toList());
    }

    private static DefinedFunction createFunction(FuncNode func, Expression expression) {
      Signature signature = new Signature(expression.type(), func.name(), asList());
      return new DefinedFunction(signature, expression);
    }

    private Expression createExpression(ExprNode expr) {
      if (expr instanceof CallNode) {
        return createCall((CallNode) expr);
      }
      if (expr instanceof StringNode) {
        return createStringLiteral((StringNode) expr);
      }
      if (expr instanceof ArrayNode) {
        return createArray((ArrayNode) expr);
      }
      throw new RuntimeException("Illegal parse tree: " + ExprContext.class.getSimpleName()
          + " without children.");
    }

    private Expression createCall(CallNode call) {
      Function function = loadedFunctions.get(call.name());
      List<Expression> expressions = createSortedArgumentExpressions(call, function);
      return function.createCallExpression(expressions, false, call.location());
    }

    private List<Expression> createSortedArgumentExpressions(CallNode call, Function function) {
      Map<Parameter, Expression> assignedExpressions = call
          .args()
          .stream()
          .filter(a -> a.has(Parameter.class))
          .collect(toMap(a -> a.get(Parameter.class), a -> createExpression(a.expr())));
      return function
          .parameters()
          .stream()
          .map(p -> assignedExpressions.containsKey(p)
              ? implicitConversion(p.type(), assignedExpressions.get(p))
              : p.defaultValueExpression())
          .collect(toImmutableList());
    }

    private Expression createStringLiteral(StringNode string) {
      return new StringLiteralExpression(string.get(String.class), string.location());
    }

    private Expression createArray(ArrayNode array) {
      List<Expression> exprList = map(array.elements(), this::createExpression);
      return createArray(array, exprList);
    }

    private Expression createArray(ArrayNode array, List<Expression> elements) {
      ArrayType type = (ArrayType) array.get(Type.class);
      List<Expression> converted = map(elements, e -> implicitConversion(type.elemType(), e));
      return new ArrayExpression(type, converted, array.location());
    }

    public <T extends Value> Expression implicitConversion(Type destinationType,
        Expression source) {
      Type sourceType = source.type();
      if (sourceType == destinationType) {
        return source;
      }

      Name functionName = Conversions.convertFunctionName(sourceType, destinationType);
      Function function = loadedFunctions.get(functionName);
      return function.createCallExpression(asList(source), true, source.location());
    }
  }
}
