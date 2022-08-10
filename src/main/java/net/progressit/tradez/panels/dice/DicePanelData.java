package net.progressit.tradez.panels.dice;

import java.util.Optional;

import lombok.Builder;
import lombok.Data;
import net.progressit.tradez.model.Player;

@Data
@Builder(toBuilder = true)
public class DicePanelData {
	private final Optional<Player> currentPlayer;
	private final Optional<Integer> currentDiceValue;
}
