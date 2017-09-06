package org.smoothbuild.parse;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.smoothbuild.lang.function.base.Scope.scope;
import static org.smoothbuild.util.Lists.map;

import java.util.List;
import java.util.Map;

import org.smoothbuild.antlr.SmoothParser.ExprContext;
import org.smoothbuild.lang.expr.ArrayExpression;
import org.smoothbuild.lang.expr.BoundValueExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.StringLiteralExpression;
import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.Parameter;
import org.smoothbuild.lang.function.base.Scope;
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
    private Scope<Name> scope;

    public Worker(Functions loadedFunctions) {
      this.loadedFunctions = loadedFunctions;
    }

    public DefinedFunction loadFunction(FuncNode func) {
      List<Parameter> parameters = createParameters(func.params());
      scope = scope();
      parameters
          .stream()
          .forEach(p -> scope.add(p.name(), null));
      Expression expression = createExpression(func.expr());
      Signature signature = new Signature(expression.type(), func.name(), parameters);
      return new DefinedFunction(signature, expression);
    }

    private static List<Parameter> createParameters(List<ParamNode> params) {
      return params
          .stream()
          .map(p -> new Parameter(p.type().get(Type.class), p.name(), null))
          .collect(toList());
    }

    private Expression createExpression(ExprNode expr) {
      if (expr instanceof CallNode) {
        CallNode call = (CallNode) expr;
        if (call.has(CallNode.ParamRefFlag.class)) {
          return createReference(call);
        } else {
          return createCall(call);
        }
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

    private Expression createReference(CallNode ref) {
      return new BoundValueExpression(ref.get(Type.class), ref.name(), ref.location());
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
