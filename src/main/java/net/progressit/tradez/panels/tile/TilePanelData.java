package net.progressit.tradez.panels.tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.Builder;
import lombok.Data;
import net.progressit.tradez.model.Player;
import net.progressit.tradez.model.Tile;

@Data
@Builder(toBuilder = true)
public class TilePanelData {
	
	@Data
	@Builder(toBuilder = true)
	public static class TileBounds{
		private final int positionX;
		private final int positionY;
		private final int sizeX;
		private final int sizeY;
	}
	
	private final Tile tile;
	private final int tileIndex;
	@Builder.Default
	private final Optional<Player> currentPlayer = Optional.empty();
	@Builder.Default
	private final Optional<Player> owner = Optional.empty();
	@Builder.Default
	private final List<Player> visitingPlayers = new ArrayList<>();
	private final Optional<Integer> numHouses;
	@Builder.Default
	private final Optional<TileBounds> tileBounds = Optional.empty();
	@Builder.Default
	private final boolean selected = false;
}
