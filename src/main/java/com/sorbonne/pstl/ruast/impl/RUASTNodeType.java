package com.sorbonne.pstl.ruast.impl;

/**
 * Les types des noeuds dans notre representation de note AST.
 * Variant represente l'element le plus eleve dans la hierarchie
 */
public enum RUASTNodeType {
    VARIANT, FILE, TYPE_DEFINITION, FIELD, METHOD, STATEMENT
}