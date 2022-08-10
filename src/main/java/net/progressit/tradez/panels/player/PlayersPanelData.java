package net.progressit.tradez.panels.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.Builder;
import lombok.Data;
import net.progressit.tradez.model.Holdings;
import net.progressit.tradez.model.Player;

@Data
@Builder(toBuilder = true)
public class PlayersPanelData {
	@Builder.Default
	private final List<Player> allPlayers = new ArrayList<>();
	@Builder.Default
	private final Map<Player, Holdings> playerHoldings = new HashMap<>();
	@Builder.Default
	private final Optional<Player> currentPlayer = Optional.empty();
	@Builder.Default
	private final Optional<Integer> currentDiceValue = Optional.empty();
}
