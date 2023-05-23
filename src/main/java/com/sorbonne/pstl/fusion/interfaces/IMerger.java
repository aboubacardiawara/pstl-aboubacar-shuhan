package com.sorbonne.pstl.fusion.interfaces;

import java.util.List;

import com.sorbonne.pstl.ruast.interfaces.IRUAST;

public interface IMerger {
	public IRUAST merge(IRUAST a1, IRUAST a2);

	public List<IRUAST> mergeSequences(List<IRUAST> s1, List<IRUAST> s2);

}
