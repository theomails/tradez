package net.progressit.tradez.panels.holdings;

import java.util.Optional;

import lombok.Builder;
import lombok.Data;
import net.progressit.tradez.model.Holdings;

@Data
@Builder(toBuilder = true)
public class HoldingsPanelData {
	private final Optional<Holdings> displayedHoldings;
	private final String panelTitle;
}
