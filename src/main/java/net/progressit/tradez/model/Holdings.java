package net.progressit.tradez.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Holdings {
	@Builder.Default
	private final Map<Integer, Integer> currencyPossessionMap = new LinkedHashMap<>();
	@Builder.Default
	private final List<Tile> tilesPossessed = new ArrayList<>();
	@Builder.Default
	private final Map<Tile, Integer> tileToHousesMap = new HashMap<>();
	
	public int currencyAvailable(int denomination) {
		Integer available = currencyPossessionMap.get(denomination);
		return available==null?0:available;
	}
	
	public int housesInstalled(Tile tile) {
		Integer housesInstalled = tileToHousesMap.get(tile);
		return housesInstalled==null?0:housesInstalled;
	}
}
