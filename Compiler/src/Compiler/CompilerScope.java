package Compiler;

import gen.MoolaListener;
import gen.MoolaParser;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

public class CompilerScope implements MoolaListener {
    private Scope root;

    public Scope getRoot() {
        return root;
    }

    // create all scope needs
    private void extractVarMethod(Scope root, List<ParseTree> children) {
        Feature feature = null;
        for (int j = 1; j < children.size(); j++) {
            if (children.get(j + 1).getText().equalsIgnoreCase(")")) break;
            if (children.get(j).getText().equalsIgnoreCase(",")
                    || children.get(j).getText().equalsIgnoreCase("(")) {
                if (children.get(j) instanceof CommonToken) {
                    feature = new Feature(children.get(j + 1).getText(), ScopeType.VAR, "", ((CommonToken) children.get(j)).getLine());
                } else {
                    feature = new Feature(children.get(j + 1).getText(), ScopeType.VAR, "", -1);// todo
                }
                if (children.get(j + 2) instanceof MoolaParser.MoolaTypeContext) {
                    feature.setFieldType(((MoolaParser.MoolaTypeContext) children.get(5)).st.children.stream().map(ParseTree::getText).reduce((s, s2) -> s + s2));
                    j++;
                }
                root.getInner_level().add(feature);
                j++;
            }
        }
    }

    private void Analyze(Scope root, List<ParseTree> children) {
        for (ParseTree child : children) {
            if (child instanceof MoolaParser.EntryClassDeclarationContext) {
                // todo convert
                MoolaParser.EntryClassDeclarationContext temp =
                        (MoolaParser.EntryClassDeclarationContext) child;
                MoolaParser.ClassDeclarationContext temp2 =
                        (MoolaParser.ClassDeclarationContext) temp.children.get(1);
                Feature feature = new Feature(temp2.className.getText(), ScopeType.CLASS, "", temp2.className.getLine());
                if (temp2.classParent != null) {
                    feature.setClassParent(this.root.find_feature(temp2.classParent.getText(), ScopeType.CLASS));
                }
                root.getNext_level().add(new Scope(feature, ScopeKind.ENTRY_CLASS, root));
            } else if (child instanceof MoolaParser.ClassDeclarationContext) {
                MoolaParser.ClassDeclarationContext temp = (MoolaParser.ClassDeclarationContext) child;
                Feature feature = new Feature(temp.className.getText(), ScopeType.CLASS, "", temp.className.getLine());
                root.getNext_level().add(new Scope(feature, ScopeKind.CLASS, root));
            } else if (child instanceof MoolaParser.MethodDeclarationContext) {
                MoolaParser.MethodDeclarationContext temp = (MoolaParser.MethodDeclarationContext) child;
                Feature feature = new Feature(temp.methodName.getText(), ScopeType.METHOD, "", temp.methodName.getLine());
                root.getNext_level().add(new Scope(feature, ScopeKind.METHOD, root));
            } else if (child instanceof MoolaParser.FieldDeclarationContext) {
                MoolaParser.FieldDeclarationContext temp = (MoolaParser.FieldDeclarationContext) child;
                Feature feature = new Feature(temp.fieldName.getText(), ScopeType.VAR, "", temp.fieldName.getLine());
                feature.setFieldType(temp.fieldType.children.stream().map(ParseTree::getText).reduce((s, s2) -> s + s2));
                root.getInner_level().add(feature);
            } else if (child
                    instanceof MoolaParser.StatementContext) { // just write something that's work
                MoolaParser.StatementContext temp2 = (MoolaParser.StatementContext) child;
                contract_blocks(root, temp2);
            }
        }
    }

    private void contract_blocks(Scope root, MoolaParser.StatementContext statementContext) {
        for (ParseTree innerChild : statementContext.children) {
            if (innerChild instanceof MoolaParser.ClosedStatementContext) {
                MoolaParser.ClosedStatementContext ctx = (MoolaParser.ClosedStatementContext) innerChild;
                if (ctx.s1 != null) { // statementBlock
                    Feature feature =
                            new Feature(
                                    String.valueOf(ctx.s1.hashCode()), ScopeType.BLOCK, "", ctx.s1.start.getLine()); // use hash for find node
                    root.getNext_level().add(new Scope(feature, ScopeKind.BLOCK, root));
                }
                if (ctx.s3 != null) { // statementClosedLoop
                    Feature feature =
                            new Feature(
                                    String.valueOf(ctx.s3.hashCode()), ScopeType.BLOCK, "", ctx.s3.start.getLine()); // use hash for find node
                    root.getNext_level().add(new Scope(feature, ScopeKind.BLOCK, root));
                }// todo s5
                if (ctx.s5 != null) {//assignment
                    MoolaParser.StatementAssignmentContext temp = ctx.s5;
                    Assignment assignment = new Assignment(temp.left.getText(), temp.right.getText(), temp.assignOp.getText(), temp.right.start.getLine());
                    root.getAssignments().add(assignment);


                }
                if (ctx.s7 != null) { // statementVarDef
                    MoolaParser.StatementVarDefContext temp = ctx.s7;
                    for (int j = 0; j < temp.children.size(); j++) {
                        if (temp.children.get(j).getText().equalsIgnoreCase("var")
                                || temp.children.get(j).getText().equalsIgnoreCase(",")) {
                            Feature feature = new Feature(temp.children.get(j + 1).getText(), ScopeType.VAR, "", ctx.s7.start.getLine());
                            root.getInner_level().add(feature);
                            j++;
                        }
                    }
                }
            } else if (innerChild instanceof MoolaParser.OpenStatementContext) {
                MoolaParser.OpenStatementContext ctx = (MoolaParser.OpenStatementContext) innerChild;
                if (ctx.s1 != null) { // statementOpenLoop
                    Feature feature =
                            new Feature(
                                    String.valueOf(ctx.s1.hashCode()), ScopeType.BLOCK, "", ctx.s1.start.getLine()); // use hash for find node
                    root.getNext_level().add(new Scope(feature, ScopeKind.BLOCK, root));
                }
            }
        }
    }

    //
    @Override
    public void enterProgram(MoolaParser.ProgramContext ctx) {
        root = new Scope(null, ScopeKind.GLOBAL); // root node create
        Analyze(root, ctx.children);
    }

    @Override
    public void exitProgram(MoolaParser.ProgramContext ctx) {
        System.out.println(root);
    }

    @Override
    public void enterClassDeclaration(MoolaParser.ClassDeclarationContext ctx) {
        Scope node = root.find_scope(ctx.className.getText(), ScopeType.CLASS);
        Analyze(node, ctx.children);
    }

    @Override
    public void exitClassDeclaration(MoolaParser.ClassDeclarationContext ctx) {
    }

    @Override
    public void enterEntryClassDeclaration(MoolaParser.EntryClassDeclarationContext ctx) {
    }

    @Override
    public void exitEntryClassDeclaration(MoolaParser.EntryClassDeclarationContext ctx) {
    }

    @Override
    public void enterFieldDeclaration(MoolaParser.FieldDeclarationContext ctx) {
    }

    @Override
    public void exitFieldDeclaration(MoolaParser.FieldDeclarationContext ctx) {
    }

    @Override
    public void enterAccess_modifier(MoolaParser.Access_modifierContext ctx) {
    }

    @Override
    public void exitAccess_modifier(MoolaParser.Access_modifierContext ctx) {
    }

    @Override
    public void enterMethodDeclaration(MoolaParser.MethodDeclarationContext ctx) {
        Scope node = root.find_scope(ctx.methodName.getText(), ScopeType.METHOD);
        extractVarMethod(node, ctx.children);
        Analyze(node, ctx.children);
    }

    @Override
    public void exitMethodDeclaration(MoolaParser.MethodDeclarationContext ctx) {
    }

    @Override
    public void enterClosedStatement(MoolaParser.ClosedStatementContext ctx) {
    }

    @Override
    public void exitClosedStatement(MoolaParser.ClosedStatementContext ctx) {
    }

    @Override
    public void enterClosedConditional(MoolaParser.ClosedConditionalContext ctx) {
    }

    @Override
    public void exitClosedConditional(MoolaParser.ClosedConditionalContext ctx) {
    }

    @Override
    public void enterOpenConditional(MoolaParser.OpenConditionalContext ctx) {
    }

    @Override
    public void exitOpenConditional(MoolaParser.OpenConditionalContext ctx) {
    }

    @Override
    public void enterOpenStatement(MoolaParser.OpenStatementContext ctx) {
    }

    @Override
    public void exitOpenStatement(MoolaParser.OpenStatementContext ctx) {
    }

    @Override
    public void enterStatement(MoolaParser.StatementContext ctx) {
    }

    @Override
    public void exitStatement(MoolaParser.StatementContext ctx) {
    }

    @Override
    public void enterStatementVarDef(MoolaParser.StatementVarDefContext ctx) {
    }

    @Override
    public void exitStatementVarDef(MoolaParser.StatementVarDefContext ctx) {
    }

    @Override
    public void enterStatementBlock(MoolaParser.StatementBlockContext ctx) {
        Scope node = root.find_scope(String.valueOf(ctx.hashCode()), ScopeType.BLOCK);
        if (node != null) { // don't call for if and while statement
            Analyze(node, ctx.children);
        }
    }

    @Override
    public void exitStatementBlock(MoolaParser.StatementBlockContext ctx) {
    }

    @Override
    public void enterStatementContinue(MoolaParser.StatementContinueContext ctx) {
    }

    @Override
    public void exitStatementContinue(MoolaParser.StatementContinueContext ctx) {
    }

    @Override
    public void enterStatementBreak(MoolaParser.StatementBreakContext ctx) {
    }

    @Override
    public void exitStatementBreak(MoolaParser.StatementBreakContext ctx) {
    }

    @Override
    public void enterStatementReturn(MoolaParser.StatementReturnContext ctx) {
    }

    @Override
    public void exitStatementReturn(MoolaParser.StatementReturnContext ctx) {
    }

    @Override
    public void enterStatementClosedLoop(MoolaParser.StatementClosedLoopContext ctx) {
    }

    @Override
    public void exitStatementClosedLoop(MoolaParser.StatementClosedLoopContext ctx) {
    }

    @Override
    public void enterStatementOpenLoop(MoolaParser.StatementOpenLoopContext ctx) {
    }

    @Override
    public void exitStatementOpenLoop(MoolaParser.StatementOpenLoopContext ctx) {
    }

    @Override
    public void enterStatementWrite(MoolaParser.StatementWriteContext ctx) {
    }

    @Override
    public void exitStatementWrite(MoolaParser.StatementWriteContext ctx) {
    }

    @Override
    public void enterStatementAssignment(MoolaParser.StatementAssignmentContext ctx) {
    }

    @Override
    public void exitStatementAssignment(MoolaParser.StatementAssignmentContext ctx) {
    }

    @Override
    public void enterStatementInc(MoolaParser.StatementIncContext ctx) {
    }

    @Override
    public void exitStatementInc(MoolaParser.StatementIncContext ctx) {
    }

    @Override
    public void enterStatementDec(MoolaParser.StatementDecContext ctx) {
    }

    @Override
    public void exitStatementDec(MoolaParser.StatementDecContext ctx) {
    }

    @Override
    public void enterExpression(MoolaParser.ExpressionContext ctx) {
    }

    @Override
    public void exitExpression(MoolaParser.ExpressionContext ctx) {
    }

    @Override
    public void enterExpressionOr(MoolaParser.ExpressionOrContext ctx) {
    }

    @Override
    public void exitExpressionOr(MoolaParser.ExpressionOrContext ctx) {
    }

    @Override
    public void enterExpressionOrTemp(MoolaParser.ExpressionOrTempContext ctx) {
    }

    @Override
    public void exitExpressionOrTemp(MoolaParser.ExpressionOrTempContext ctx) {
    }

    @Override
    public void enterExpressionAnd(MoolaParser.ExpressionAndContext ctx) {
    }

    @Override
    public void exitExpressionAnd(MoolaParser.ExpressionAndContext ctx) {
    }

    @Override
    public void enterExpressionAndTemp(MoolaParser.ExpressionAndTempContext ctx) {
    }

    @Override
    public void exitExpressionAndTemp(MoolaParser.ExpressionAndTempContext ctx) {
    }

    @Override
    public void enterExpressionEq(MoolaParser.ExpressionEqContext ctx) {
    }

    @Override
    public void exitExpressionEq(MoolaParser.ExpressionEqContext ctx) {
    }

    @Override
    public void enterExpressionEqTemp(MoolaParser.ExpressionEqTempContext ctx) {
    }

    @Override
    public void exitExpressionEqTemp(MoolaParser.ExpressionEqTempContext ctx) {
    }

    @Override
    public void enterExpressionCmp(MoolaParser.ExpressionCmpContext ctx) {
    }

    @Override
    public void exitExpressionCmp(MoolaParser.ExpressionCmpContext ctx) {
    }

    @Override
    public void enterExpressionCmpTemp(MoolaParser.ExpressionCmpTempContext ctx) {
    }

    @Override
    public void exitExpressionCmpTemp(MoolaParser.ExpressionCmpTempContext ctx) {
    }

    @Override
    public void enterExpressionAdd(MoolaParser.ExpressionAddContext ctx) {
    }

    @Override
    public void exitExpressionAdd(MoolaParser.ExpressionAddContext ctx) {
    }

    @Override
    public void enterExpressionAddTemp(MoolaParser.ExpressionAddTempContext ctx) {
    }

    @Override
    public void exitExpressionAddTemp(MoolaParser.ExpressionAddTempContext ctx) {
    }

    @Override
    public void enterExpressionMultMod(MoolaParser.ExpressionMultModContext ctx) {
    }

    @Override
    public void exitExpressionMultMod(MoolaParser.ExpressionMultModContext ctx) {
    }

    @Override
    public void enterExpressionMultModTemp(MoolaParser.ExpressionMultModTempContext ctx) {
    }

    @Override
    public void exitExpressionMultModTemp(MoolaParser.ExpressionMultModTempContext ctx) {
    }

    @Override
    public void enterExpressionUnary(MoolaParser.ExpressionUnaryContext ctx) {
    }

    @Override
    public void exitExpressionUnary(MoolaParser.ExpressionUnaryContext ctx) {
    }

    @Override
    public void enterExpressionMethods(MoolaParser.ExpressionMethodsContext ctx) {
    }

    @Override
    public void exitExpressionMethods(MoolaParser.ExpressionMethodsContext ctx) {
    }

    @Override
    public void enterExpressionMethodsTemp(MoolaParser.ExpressionMethodsTempContext ctx) {
    }

    @Override
    public void exitExpressionMethodsTemp(MoolaParser.ExpressionMethodsTempContext ctx) {
    }

    @Override
    public void enterExpressionOther(MoolaParser.ExpressionOtherContext ctx) {
    }

    @Override
    public void exitExpressionOther(MoolaParser.ExpressionOtherContext ctx) {
    }

    @Override
    public void enterMoolaType(MoolaParser.MoolaTypeContext ctx) {
    }

    @Override
    public void exitMoolaType(MoolaParser.MoolaTypeContext ctx) {
    }

    @Override
    public void enterSingleType(MoolaParser.SingleTypeContext ctx) {
    }

    @Override
    public void exitSingleType(MoolaParser.SingleTypeContext ctx) {
    }

    @Override
    public void visitTerminal(TerminalNode terminalNode) {
    }

    @Override
    public void visitErrorNode(ErrorNode errorNode) {
    }

    @Override
    public void enterEveryRule(ParserRuleContext parserRuleContext) {
    }

    @Override
    public void exitEveryRule(ParserRuleContext parserRuleContext) {
    }
}
