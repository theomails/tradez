package net.progressit.tradez.panels.chance;

import java.util.List;
import java.util.Optional;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ChancePanelData {
	private final List<String> availableChanceCards;
	private final Optional<String> currentChanceCard;
}
