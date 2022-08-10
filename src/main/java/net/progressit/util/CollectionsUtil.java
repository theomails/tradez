package net.progressit.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CollectionsUtil {
	public static <T,U> Map<T,U> cloneToHashMap(Map<T,U> source){
		Map<T,U> copy = new HashMap<>();
		copy.putAll(source);
		return copy;
	}
	public static <T,U> Map<T,U> cloneToLinkedHashMap(Map<T,U> source){
		Map<T,U> copy = new LinkedHashMap<>();
		copy.putAll(source);
		return copy;
	}
	public static <T> List<T> cloneToArrayList(List<T> source){
		List<T> copy = new ArrayList<>();
		copy.addAll(source);
		return copy;
	}
}
