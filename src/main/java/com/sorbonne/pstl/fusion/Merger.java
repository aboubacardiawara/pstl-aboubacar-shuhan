package com.sorbonne.pstl.fusion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sorbonne.pstl.fusion.interfaces.IMerger;
import com.sorbonne.pstl.ruast.impl.RUASTNode;
import com.sorbonne.pstl.ruast.impl.RUASTTree;
import com.sorbonne.pstl.ruast.interfaces.IRUAST;
import com.sorbonne.pstl.ruast.interfaces.IRUASTNode;
import com.sorbonne.pstl.util.Utile;

public class Merger implements IMerger {

	@Override
	public IRUAST merge(IRUAST a1, IRUAST a2) {
		Utile.assertionCheck(Utile.similarite(a1, a2), "Deux arbres à fusionner sont similaires");
		IRUASTNode root1 = a1.getRoot();
		IRUASTNode root2 = a2.getRoot();

		List<IRUAST> subTrees = new ArrayList<>();
		if (!a1.isLeaf() && !a2.isLeaf()) {
			List<IRUAST> subtrees1 = a1.getChildren();
			List<IRUAST> subtrees2 = a2.getChildren();
			subTrees.addAll(mergeSequences(subtrees1, subtrees2));
		}

		IRUASTNode root = mergeNode(root1, root2);
		IRUAST parent = a1.getParent();
		IRUAST mergedTree = new RUASTTree(root, parent, subTrees);
		return mergedTree;
	}

	/**
	 * Fusionner deux noeuds reviendrait à fusionner leurs informaitons
	 * 
	 * @param root1
	 * @param root2
	 * @return
	 */
	private IRUASTNode mergeNode(IRUASTNode root1, IRUASTNode root2) {
		root1.getVariants().addAll(root2.getVariants());
		IRUASTNode newNode = new RUASTNode(root1.getJdtNode(), 0, root1.getVariants(), root1.getType());
		newNode.setName(root1.getName());
		return newNode;
	}

	@Override
	public List<IRUAST> mergeSequences(List<IRUAST> s1, List<IRUAST> s2) {
		List<IRUAST> subTrees1 = new ArrayList<>(s1);
		List<IRUAST> subTrees2 = new ArrayList<>(s2);

		Map<String, IRUAST> allTree = new HashMap<>();
		Set<String> collectedTrees = new HashSet<String>();
		List<IRUAST> superSequence = new ArrayList<>();

		// ajout des arbres de la première sequence
		for (IRUAST tree : subTrees1) {
			allTree.put(tree.getName(), tree);
		}

		// ajout des arbres de la seconde sequence, si collision, fusionner.
		for (IRUAST tree : subTrees2) {
			if (allTree.containsKey(tree.getName())) {
				IRUAST mergedTree = merge(allTree.get(tree.getName()), tree);
				allTree.put(tree.getName(), mergedTree);
			} else {
				allTree.put(tree.getName(), tree);
			}
		}

		// constitution de la super sequence.
		for (IRUAST tree : subTrees1) {
			superSequence.add(allTree.get(tree.getName()));
			collectedTrees.add(tree.getName());
		}

		// Add nodes from subtrees2 to superSequence if they are not already in the list
		for (IRUAST tree : subTrees2) {
			if (!collectedTrees.contains(tree.getName())) {
				superSequence.add(tree);
			} else {
				collectedTrees.add(tree.getName());
			}
		}
		return superSequence;
	}

}