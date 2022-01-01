package com.dubreuia.pih.folding

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.*
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.ProjectManager
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.python.psi.PyTargetExpression
import com.jetbrains.python.psi.impl.stubs.PyAnnotationElementType
import com.jetbrains.python.psi.types.PyCollectionType
import com.jetbrains.python.psi.types.PyTupleType
import com.jetbrains.python.psi.types.PyType
import com.jetbrains.python.psi.types.TypeEvalContext
import java.util.logging.Logger

/**
 * Folder builder for python variable which folds the variable name and its
 * type annotation together. A variable with type unknown, with type annotation,
 * or which is qualified won't fold.
 *
 * See the following fold region between the plus (+) sign:
 *
 * - +my_int: int+ = 123
 * - +my_str: str+ = "string"
 *
 * For the whole syntax tree: `PsiTreeUtil.findChildrenOfType(root, PyExpression::class.java)`
 */
class PythonVariableTypeHintFolderBuilder : FoldingBuilderEx() {

    private var logger = Logger.getLogger("python-inlay-hints");

    /**
     * Returns the expression type with generic types if the expression is a collection
     * or just the type otherwise
     *
     * e.g. "List[str, int]" or "int" or "(int, str)"
     */
    private fun getExpressionTypeWithGenerics(expressionType: PyType?): String? {
        return if (
            expressionType is PyCollectionType
            && expressionType.name != null
            // No generics for tuple since type already has them
            && expressionType !is PyTupleType
            && expressionType.elementTypes.isNotEmpty()
        ) {
            // Uppercase collection type names, e.g. converts list -> List
            val collectionName = expressionType.name?.capitalize()
            expressionType.elementTypes
                .mapNotNull { getExpressionTypeWithGenerics(it) }
                .joinToString(separator = ", ", prefix = "$collectionName[", postfix = "]") { it }
        } else expressionType?.name
    }

    /**
     * Returns variable with type information (including generic expression type if collection)
     *
     * Do not show type information in fold if no analysed type, already has type annotation,
     * or if the expression is qualified (e.g. `contract.name` won't show types)
     *
     * e.g. "my_list: List[str, int]" or "my_int: int" or "contract.name"
     */
    private fun getVariableWithTypeInformation(
        expression: PyTargetExpression,
        typeEvalContext: TypeEvalContext
    ): String? {
        val expressionType = typeEvalContext.getType(expression)
        val hasTypeAnnotation = expression.node?.treeNext?.elementType is PyAnnotationElementType
        return if (expressionType == null || hasTypeAnnotation || expression.isQualified) {
            null
        } else {
            val expressionTypeWithGenerics = getExpressionTypeWithGenerics(expressionType)
            if (expressionTypeWithGenerics == null) expression.name
            else "${expression.name}: $expressionTypeWithGenerics"
        }
    }

    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        return PsiTreeUtil.findChildrenOfType(root, PyTargetExpression::class.java).flatMap { expression ->
            // We only have target expressions, which corresponds to variable assignments
            ProjectManager.getInstance().openProjects.mapNotNull { project ->
                try {
                    val deepCodeInsight = TypeEvalContext.deepCodeInsight(project)
                    val expressionType = getVariableWithTypeInformation(expression, deepCodeInsight)
                    if (expressionType == null) null
                    else FoldingDescriptor(
                        expression.node,
                        expression.textRange,
                        null,
                        expressionType,
                    )
                } catch (exception: Exception) {
                    logger.info("Error during folding for ${expression}: $exception")
                    null
                }
            }
        }.toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode) = "..."

    override fun isCollapsedByDefault(node: ASTNode) = true

}
