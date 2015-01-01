package org.smoothbuild.parse;

import static org.smoothbuild.lang.base.Types.BLOB;
import static org.smoothbuild.lang.base.Types.FILE;
import static org.smoothbuild.lang.base.Types.NIL;
import static org.smoothbuild.lang.base.Types.NOTHING;
import static org.smoothbuild.lang.base.Types.STRING;
import static org.smoothbuild.lang.base.Types.basicTypes;
import static org.smoothbuild.lang.expr.Expressions.callExpression;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.def.Argument.namedArgument;
import static org.smoothbuild.lang.function.def.Argument.namelessArgument;
import static org.smoothbuild.lang.function.def.Argument.pipedArgument;
import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.smoothbuild.message.base.MessageType.FATAL;
import static org.smoothbuild.parse.LocationHelpers.locationOf;
import static org.smoothbuild.util.StringUnescaper.unescaped;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.smoothbuild.antlr.SmoothParser.ArgContext;
import org.smoothbuild.antlr.SmoothParser.ArgListContext;
import org.smoothbuild.antlr.SmoothParser.ArrayContext;
import org.smoothbuild.antlr.SmoothParser.ArrayElemContext;
import org.smoothbuild.antlr.SmoothParser.CallContext;
import org.smoothbuild.antlr.SmoothParser.ExpressionContext;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.ParamNameContext;
import org.smoothbuild.antlr.SmoothParser.PipeContext;
import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.lang.base.ArrayType;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.Type;
import org.smoothbuild.lang.base.Types;
import org.smoothbuild.lang.base.Value;
import org.smoothbuild.lang.expr.ArrayExpression;
import org.smoothbuild.lang.expr.ConstantExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.ImplicitConverter;
import org.smoothbuild.lang.expr.InvalidExpression;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.def.Argument;
import org.smoothbuild.lang.function.def.ArgumentExpressionCreator;
import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.lang.module.Module;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.CodeMessage;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.LoggedMessages;
import org.smoothbuild.parse.err.ForbiddenArrayElemError;
import org.smoothbuild.parse.err.IncompatibleArrayElemsError;
import org.smoothbuild.util.Empty;
import org.smoothbuild.util.UnescapingFailedException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DefinedFunctionsCreator {
  private final ObjectsDb objectsDb;
  private final ArgumentExpressionCreator argumentExpressionCreator;
  private final ImplicitConverter implicitConverter;

  @Inject
  public DefinedFunctionsCreator(ObjectsDb objectsDb,
      ArgumentExpressionCreator argumentExpressionCreator, ImplicitConverter implicitConverter) {
    this.objectsDb = objectsDb;
    this.argumentExpressionCreator = argumentExpressionCreator;
    this.implicitConverter = implicitConverter;
  }

  public Map<Name, Function> createDefinedFunctions(LoggedMessages messages, Module builtinModule,
      Map<Name, FunctionContext> functionContexts, List<Name> sorted) {
    Worker worker =
        new Worker(messages, builtinModule, functionContexts, sorted, objectsDb,
            argumentExpressionCreator, implicitConverter);
    Map<Name, Function> result = worker.run();
    messages.failIfContainsProblems();
    return result;
  }

  private static class Worker {
    private final LoggedMessages messages;
    private final Module builtinModule;
    private final Map<Name, FunctionContext> functionContexts;
    private final List<Name> sorted;
    private final ObjectsDb objectsDb;
    private final ArgumentExpressionCreator argumentExpressionCreator;
    private final ImplicitConverter implicitConverter;

    private final Map<Name, Function> functions = Maps.newHashMap();

    public Worker(LoggedMessages messages, Module builtinModule,
        Map<Name, FunctionContext> functionContexts, List<Name> sorted, ObjectsDb objectsDb,
        ArgumentExpressionCreator argumentExpressionCreator, ImplicitConverter implicitConverter) {
      this.messages = messages;
      this.builtinModule = builtinModule;
      this.functionContexts = functionContexts;
      this.sorted = sorted;
      this.objectsDb = objectsDb;
      this.argumentExpressionCreator = argumentExpressionCreator;
      this.implicitConverter = implicitConverter;
    }

    public Map<Name, Function> run() {
      for (Name name : sorted) {
        DefinedFunction definedFunction = build(functionContexts.get(name));
        functions.put(name, definedFunction);
      }
      return functions;
    }

    public DefinedFunction build(FunctionContext function) {
      Expression expression = build(function.pipe());
      return buildDefinedFunction(function, expression);
    }

    private DefinedFunction buildDefinedFunction(FunctionContext function, Expression expression) {
      Name name = name(function.functionName().getText());
      Signature signature = new Signature(expression.type(), name, Empty.paramList());
      return new DefinedFunction(signature, expression);
    }

    private Expression build(PipeContext pipe) {
      Expression result = build(pipe.expression());
      List<CallContext> elements = pipe.call();
      for (int i = 0; i < elements.size(); i++) {
        CallContext call = elements.get(i);
        List<Argument> arguments = build(call.argList());
        // nameless piped argument's location is set to the pipe character '|'
        CodeLocation codeLocation = locationOf(pipe.p.get(i));
        arguments.add(pipedArgument(result, codeLocation));
        result = build(call, arguments);
      }
      return result;
    }

    private Expression build(ExpressionContext expression) {
      if (expression.array() != null) {
        return build(expression.array());
      }
      if (expression.call() != null) {
        return build(expression.call());
      }
      if (expression.STRING() != null) {
        return buildStringExpr(expression.STRING());
      }
      throw new Message(FATAL, "Illegal parse tree: " + ExpressionContext.class.getSimpleName()
          + " without children.");
    }

    private Expression build(ArrayContext list) {
      List<ArrayElemContext> elems = list.arrayElem();
      ImmutableList<Expression> elemExpressions = build(elems);

      CodeLocation location = locationOf(list);
      Type elemType = commonSuperType(elems, elemExpressions, location);

      if (elemType != null) {
        return buildArray(elemType, elemExpressions, location);
      } else {
        return new InvalidExpression(NIL, location);
      }
    }

    private <T extends Value> Expression buildArray(Type elemType,
        ImmutableList<Expression> elemExpressions, CodeLocation location) {
      ArrayType arrayType = Types.arrayTypeContaining(elemType);
      ImmutableList<Expression> convertedExpression = convertExpressions(elemType, elemExpressions);
      return new ArrayExpression(arrayType, convertedExpression, location);
    }

    public <T extends Value> ImmutableList<Expression> convertExpressions(Type type,
        Iterable<? extends Expression> expressions) {
      ImmutableList.Builder<Expression> builder = ImmutableList.builder();
      for (Expression expression : expressions) {
        builder.add(implicitConverter.apply(type, expression));
      }
      return builder.build();
    }

    private ImmutableList<Expression> build(List<ArrayElemContext> elems) {
      Builder<Expression> builder = ImmutableList.builder();
      for (ArrayElemContext elem : elems) {
        Expression expression = build(elem);
        if (!basicTypes().contains(expression.type())) {
          CodeLocation location = locationOf(elem);
          messages.log(new ForbiddenArrayElemError(location, expression.type()));
          builder.add(new InvalidExpression(NOTHING, location));
        } else {
          builder.add(expression);
        }
      }
      return builder.build();
    }

    private Expression build(ArrayElemContext elem) {
      if (elem.STRING() != null) {
        return buildStringExpr(elem.STRING());
      }
      if (elem.call() != null) {
        return build(elem.call());
      }

      throw new Message(FATAL, "Illegal parse tree: " + ArrayElemContext.class.getSimpleName()
          + " without children.");
    }

    private Type commonSuperType(List<ArrayElemContext> elems,
        ImmutableList<Expression> elemExpressions, CodeLocation location) {
      if (elems.size() == 0) {
        return NOTHING;
      }
      Type firstType = elemExpressions.get(0).type();
      Type commonSuperType = firstType;

      for (int i = 1; i < elemExpressions.size(); i++) {
        Type currentType = elemExpressions.get(i).type();
        commonSuperType = commonSuperType(commonSuperType, currentType);

        if (commonSuperType == null) {
          messages.log(new IncompatibleArrayElemsError(location, firstType, i, currentType));
          return null;
        }
      }
      return commonSuperType;
    }

    private static Type commonSuperType(Type type1, Type type2) {
      if (type1 == STRING) {
        if (type2 == STRING) {
          return STRING;
        } else {
          return null;
        }
      } else if (type1 == BLOB) {
        if (type2 == BLOB || type2 == FILE) {
          return BLOB;
        } else {
          return null;
        }
      } else if (type1 == FILE) {
        if (type2 == FILE) {
          return FILE;
        } else if (type2 == BLOB) {
          return BLOB;
        } else {
          return null;
        }
      }
      return null;
    }

    private Expression build(CallContext call) {
      List<Argument> arguments = build(call.argList());
      return build(call, arguments);
    }

    private Expression build(CallContext call, List<Argument> arguments) {
      String functionName = call.functionName().getText();

      Function function = getFunction(functionName);

      CodeLocation codeLocation = locationOf(call.functionName());
      Map<String, ? extends Expression> namedArgs =
          argumentExpressionCreator.createArgExprs(codeLocation, messages, function, arguments);

      if (namedArgs == null) {
        return new InvalidExpression(function.type(), locationOf(call.functionName()));
      } else {
        return callExpression(function, false, codeLocation, namedArgs);
      }
    }

    private Function getFunction(String functionName) {
      // UndefinedFunctionDetector has been run already so we can be sure at
      // this point that function with given name exists either among imported
      // functions or among already handled defined functions.

      Name name = Name.name(functionName);
      Function function = builtinModule.getFunction(name);
      if (function == null) {
        return functions.get(name);
      } else {
        return function;
      }
    }

    private List<Argument> build(ArgListContext argList) {
      List<Argument> result = Lists.newArrayList();
      if (argList != null) {
        List<ArgContext> argContextList = argList.arg();
        for (int i = 0; i < argContextList.size(); i++) {
          result.add(build(i, argContextList.get(i)));
        }
      }
      return result;
    }

    private Argument build(int index, ArgContext arg) {
      Expression expression = build(arg.expression());

      CodeLocation location = locationOf(arg);
      ParamNameContext paramName = arg.paramName();
      if (paramName == null) {
        return namelessArgument(index + 1, expression, location);
      } else {
        return namedArgument(index + 1, paramName.getText(), expression, location);
      }
    }

    private Expression buildStringExpr(TerminalNode stringToken) {
      String quotedString = stringToken.getText();
      String string = quotedString.substring(1, quotedString.length() - 1);
      try {
        SString stringValue = objectsDb.string(unescaped(string));
        return new ConstantExpression(STRING, stringValue, locationOf(stringToken.getSymbol()));
      } catch (UnescapingFailedException e) {
        CodeLocation location = locationOf(stringToken.getSymbol());
        messages.log(new CodeMessage(ERROR, location, e.getMessage()));
        return new InvalidExpression(STRING, locationOf(stringToken.getSymbol()));
      }
    }
  }
}
