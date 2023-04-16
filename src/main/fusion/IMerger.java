package main.fusion;

import java.util.List;

import main.adaptation.interfaces.IRUAST;

public interface IMerger {
	public IRUAST merge(IRUAST a1, IRUAST a2);

	public List<IRUAST> mergeSequences(List<IRUAST> s1, List<IRUAST> s2);

}