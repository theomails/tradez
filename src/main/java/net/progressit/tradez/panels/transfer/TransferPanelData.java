package net.progressit.tradez.panels.transfer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import lombok.Builder;
import lombok.Data;
import net.progressit.tradez.model.Player;
import net.progressit.tradez.panels.transfer.TransferPanel.TransferParty;

@Data
@Builder(toBuilder = true)
public class TransferPanelData {
	private List<Player> allPlayers;
	@Builder.Default
	private Optional<Player> currentPlayer = Optional.empty();
	private Set<Integer> currencies;
	@Builder.Default
	private Optional<TransferParty> from = Optional.empty();
	@Builder.Default
	private Optional<TransferParty> to = Optional.empty();
	@Builder.Default
	private Map<Integer, Integer> send = new LinkedHashMap<>();
	@Builder.Default
	private Map<Integer, Integer> receive = new LinkedHashMap<>();
	
	@Override
	public String toString() {
		return "TransferPanelData [\nallPlayers=" + allPlayers + ", \ncurrentPlayer=" + currentPlayer + ", \ncurrencies="
				+ currencies + ", \nfrom=" + from + ", \nto=" + to + ", \nsend=" + send + ", \nreceive=" + receive + "]";
	}

	
}
