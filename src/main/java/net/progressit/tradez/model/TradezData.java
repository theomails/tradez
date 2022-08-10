package net.progressit.tradez.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class TradezData {
	@Builder.Default
	private final List<Player> allPlayers = new ArrayList<>();
	@Builder.Default
	private final List<Tile> allTiles = new ArrayList<>();
	@Builder.Default
	private final List<String> availableChanceCards = new ArrayList<>();
	@Builder.Default
	private final Optional<String> currentChanceCard = Optional.empty();
	@Builder.Default
	private final Optional<Integer> currentDiceValue = Optional.empty();
	@Builder.Default
	private final Holdings bankHoldings = Holdings.builder().build();
	@Builder.Default
	private final Holdings uncleHoldings = Holdings.builder().build();
	@Builder.Default
	private final Map<Player, Holdings> playerHoldings = new HashMap<>();
	@Builder.Default
	private final Map<Player, Integer> playerPosition = new HashMap<>();
	@Builder.Default
	private final Map<Tile, Integer> tileHouses = new HashMap<>();
	@Builder.Default
	private final Optional<Player> currentPlayer = Optional.empty();
	@Builder.Default
	private final Optional<Tile> currentTile = Optional.empty();
	@Builder.Default
	private final int tileWidth = 120;
	@Builder.Default
	private final int tileHeight = 150;
	@Builder.Default
	private final int originX = 0;
	@Builder.Default
	private final int originY = 0;
}
