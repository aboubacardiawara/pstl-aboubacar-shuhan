package main.adaptation;

/**
 * Les types des noeuds dans notre representation de note AST.
 * Variant represente l'element le plus eleve dans la hierarchie
 */
public enum RUASTNodeType {
    VARIANT, PACKAGE, TYPE_DEFINITION, FIELD, METHOD, STATEMENT
}