package org.smoothbuild.parse.ast;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.smoothbuild.util.Throwables.unexpectedCaseExc;
import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Lists.sane;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.smoothbuild.antlr.lang.SmoothBaseVisitor;
import org.smoothbuild.antlr.lang.SmoothParser.AnnContext;
import org.smoothbuild.antlr.lang.SmoothParser.ArgContext;
import org.smoothbuild.antlr.lang.SmoothParser.ArgListContext;
import org.smoothbuild.antlr.lang.SmoothParser.ArrayTContext;
import org.smoothbuild.antlr.lang.SmoothParser.ChainCallContext;
import org.smoothbuild.antlr.lang.SmoothParser.ChainContext;
import org.smoothbuild.antlr.lang.SmoothParser.ChainPartContext;
import org.smoothbuild.antlr.lang.SmoothParser.ExprContext;
import org.smoothbuild.antlr.lang.SmoothParser.ExprHeadContext;
import org.smoothbuild.antlr.lang.SmoothParser.FieldContext;
import org.smoothbuild.antlr.lang.SmoothParser.FieldListContext;
import org.smoothbuild.antlr.lang.SmoothParser.FuncTContext;
import org.smoothbuild.antlr.lang.SmoothParser.LiteralContext;
import org.smoothbuild.antlr.lang.SmoothParser.ModContext;
import org.smoothbuild.antlr.lang.SmoothParser.ParamContext;
import org.smoothbuild.antlr.lang.SmoothParser.ParamListContext;
import org.smoothbuild.antlr.lang.SmoothParser.SelectContext;
import org.smoothbuild.antlr.lang.SmoothParser.StructContext;
import org.smoothbuild.antlr.lang.SmoothParser.TopContext;
import org.smoothbuild.antlr.lang.SmoothParser.TypeContext;
import org.smoothbuild.antlr.lang.SmoothParser.TypeListContext;
import org.smoothbuild.antlr.lang.SmoothParser.TypeNameContext;
import org.smoothbuild.fs.space.FilePath;
import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.parse.LocHelpers;

import com.google.common.collect.ImmutableList;

public class AstCreator {
  public static Ast fromParseTree(FilePath filePath, ModContext module) {
    List<StructP> structs = new ArrayList<>();
    List<TopRefableP> refables = new ArrayList<>();
    new SmoothBaseVisitor<Void>() {
      @Override
      public Void visitStruct(StructContext struct) {
        String name = struct.TNAME().getText();
        Loc loc = LocHelpers.locOf(filePath, struct.TNAME().getSymbol());
        List<ItemP> fields = createFields(struct.fieldList());
        structs.add(new StructP(name, fields, loc));
        return null;
      }

      private List<ItemP> createFields(FieldListContext fieldList) {
        if (fieldList != null) {
          return sane(fieldList.field())
              .stream()
              .map(this::createField)
              .collect(toImmutableList());
        }
        return new ArrayList<>();
      }

      private ItemP createField(FieldContext field) {
        TypeP type = createT(field.type());
        TerminalNode nameNode = field.NAME();
        String name = nameNode.getText();
        Loc loc = LocHelpers.locOf(filePath, nameNode);
        return new ItemP(type, name, Optional.empty(), loc);
      }

      @Override
      public Void visitTop(TopContext top) {
        TerminalNode nameNode = top.NAME();
        visitChildren(top);
        Optional<TypeP> type = createTypeSane(top.type());
        String name = nameNode.getText();
        Optional<ObjP> obj = createObjSane(top.expr());
        Optional<AnnP> annotation = createNativeSane(top.ann());
        Loc loc = LocHelpers.locOf(filePath, nameNode);
        if (top.paramList() == null) {
          refables.add(new ValP(type, name, obj, annotation, loc));
        } else {
          List<ItemP> params = createParams(top.paramList());
          refables.add(new FuncP(type, name, params, obj, annotation, loc));
        }
        return null;
      }

      private Optional<AnnP> createNativeSane(AnnContext annotation) {
        if (annotation == null) {
          return Optional.empty();
        } else {
          String name = annotation.TNAME().getText();
          return Optional.of(new AnnP(
              name,
              createStringNode(annotation, annotation.STRING()),
              LocHelpers.locOf(filePath, annotation)));
        }
      }

      private List<ItemP> createParams(ParamListContext paramList) {
        ArrayList<ItemP> result = new ArrayList<>();
        if (paramList != null) {
          return sane(paramList.param())
              .stream().map(this::createParam)
              .collect(toImmutableList());
        }
        return result;
      }

      private ItemP createParam(ParamContext param) {
        var type = createT(param.type());
        var name = param.NAME().getText();
        var defaultArg = Optional.ofNullable(param.expr()).map(this::createObj);
        var loc = LocHelpers.locOf(filePath, param);
        return new ItemP(type, name, defaultArg, loc);
      }

      private Optional<ObjP> createObjSane(ExprContext expr) {
        return expr == null ? Optional.empty() : Optional.of(createObj(expr));
      }

      private ObjP createObj(ExprContext expr) {
        ObjP result = createChainHead(expr.exprHead());
        List<ChainCallContext> chainCallsInPipe = expr.chainCall();
        for (int i = 0; i < chainCallsInPipe.size(); i++) {
          var pipedArg = pipedArg(result, expr.p.get(i));
          ChainCallContext chain = chainCallsInPipe.get(i);
          result = createChainCallObj(pipedArg, chain);
        }
        return result;
      }

      private ObjP createChainHead(ExprHeadContext expr) {
        if (expr.chain() != null) {
          return createChainObj(expr.chain());
        }
        if (expr.literal() != null) {
          return createLiteral(expr.literal());
        }
        throw newRuntimeException(ExprHeadContext.class);
      }

      private ObjP createChainObj(ChainContext chain) {
        ObjP result = newRefNode(chain.NAME());
        return createChainParts(result, chain.chainPart());
      }

      private ArgP pipedArg(ObjP result, Token pipeCharacter) {
        // Loc of nameless piped arg is set to the loc of pipe character '|'.
        Loc loc = LocHelpers.locOf(filePath, pipeCharacter);
        return new ExplicitArgP(Optional.empty(), result, loc);
      }

      private ObjP createLiteral(LiteralContext expr) {
        if (expr.array() != null) {
          List<ObjP> elems = map(expr.array().expr(), this::createObj);
          return new OrderP(elems, LocHelpers.locOf(filePath, expr));
        }
        if (expr.BLOB() != null) {
          return new BlobP(expr.BLOB().getText().substring(2), LocHelpers.locOf(filePath, expr));
        }
        if (expr.INT() != null) {
          return new IntP(expr.INT().getText(), LocHelpers.locOf(filePath, expr));
        }
        if (expr.STRING() != null) {
          return createStringNode(expr, expr.STRING());
        }
        throw newRuntimeException(LiteralContext.class);
      }

      private StringP createStringNode(ParserRuleContext expr, TerminalNode quotedString) {
        String unquoted = unquote(quotedString.getText());
        Loc loc = LocHelpers.locOf(filePath, expr);
        return new StringP(unquoted, loc);
      }

      private ObjP createChainCallObj(ArgP pipedArg, ChainCallContext chainCall) {
        ObjP result = newRefNode(chainCall.NAME());
        for (SelectContext fieldRead : chainCall.select()) {
          result = createSelect(result, fieldRead);
        }

        var args = createArgList(chainCall.argList());
        result = createCall(result, concat(pipedArg, args), chainCall.argList());

        return createChainParts(result, chainCall.chainPart());
      }

      private RefP newRefNode(TerminalNode name) {
        return new RefP(name.getText(), LocHelpers.locOf(filePath, name));
      }

      private SelectP createSelect(ObjP selectable, SelectContext fieldRead) {
        String name = fieldRead.NAME().getText();
        Loc loc = LocHelpers.locOf(filePath, fieldRead);
        return new SelectP(selectable, name, loc);
      }

      private ObjP createChainParts(ObjP obj, List<ChainPartContext> chainParts) {
        ObjP result = obj;
        for (ChainPartContext chainPart : chainParts) {
          if (chainPart.argList() != null) {
            var args = createArgList(chainPart.argList());
            result = createCall(result, args, chainPart.argList());
          } else if (chainPart.select() != null) {
            result = createSelect(result, chainPart.select());
          } else {
            throw newRuntimeException(ChainContext.class);
          }
        }
        return result;
      }

      private List<ArgP> createArgList(ArgListContext argList) {
        List<ArgP> result = new ArrayList<>();
        for (ArgContext arg : argList.arg()) {
          ExprContext expr = arg.expr();
          TerminalNode nameNode = arg.NAME();
          var name = nameNode == null ? Optional.<String>empty() : Optional.of(nameNode.getText());
          ObjP objP = createObj(expr);
          result.add(new ExplicitArgP(name, objP, LocHelpers.locOf(filePath, arg)));
        }
        return result;
      }

      private MonoObjP createCall(
          ObjP callable, List<ArgP> args, ArgListContext argListContext) {
        Loc loc = LocHelpers.locOf(filePath, argListContext);
        return new CallP(callable, args, loc);
      }

      private Optional<TypeP> createTypeSane(TypeContext type) {
        return type == null ? Optional.empty() : Optional.of(createT(type));
      }

      private TypeP createT(TypeContext type) {
        return switch (type) {
          case TypeNameContext name -> createT(name);
          case ArrayTContext arrayT -> createArrayT(arrayT);
          case FuncTContext funcT -> createFuncT(funcT);
          default -> throw unexpectedCaseExc(type);
        };
      }

      private TypeP createT(TypeNameContext type) {
        return new TypeP(type.getText(), LocHelpers.locOf(filePath, type.TNAME()));
      }

      private TypeP createArrayT(ArrayTContext arrayT) {
        TypeP elemType = createT(arrayT.type());
        return new ArrayTP(elemType, LocHelpers.locOf(filePath, arrayT));
      }

      private TypeP createFuncT(FuncTContext funcT) {
        TypeP resultType = createT(funcT.type());
        return new FuncTP(resultType, createTs(funcT.typeList()),
            LocHelpers.locOf(filePath, funcT));
      }

      private ImmutableList<TypeP> createTs(TypeListContext typeList) {
        if (typeList != null) {
          return map(typeList.type(), this::createT);
        } else {
          return list();
        }
      }

      private RuntimeException newRuntimeException(Class<?> clazz) {
        return new RuntimeException("Illegal parse tree: " + clazz.getSimpleName()
            + " without children.");
      }
    }.visit(module);
    return new Ast(structs, refables);
  }

  private static String unquote(String quotedString) {
    return quotedString.substring(1, quotedString.length() - 1);
  }
}
