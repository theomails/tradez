package net.progressit.tradez.panels.log;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import net.progressit.tradez.TradezMain.AddPlayerClick;
import net.progressit.tradez.model.Player;
import net.progressit.tradez.model.Tile;
import net.progressit.tradez.model.TradezData;
import net.progressit.tradez.panels.chance.ChancePanel.ChancePickCardClick;
import net.progressit.tradez.panels.dice.DicePanel.DiceRollClick;
import net.progressit.tradez.panels.dice.DicePanel.MovePlayerByDiceClick;
import net.progressit.tradez.panels.tile.TileActionPanel.AddTicketBoothClick;
import net.progressit.tradez.panels.tile.TileActionPanel.BuySelectedTileClick;
import net.progressit.tradez.panels.tile.TileActionPanel.JumpToSelectedTileClick;
import net.progressit.tradez.panels.transfer.TransferPanel.DoTransferClick;

public class LogStringer {
	@Getter
	@Setter
	private static LogStringer instance = new LogStringer();
	
	private String toString(AddPlayerClick pa, TradezData contextData) {
		String player = pa.getPlayer().getName();
		return String.format("%s added to the Game!", player);
	}
	private String toString(DiceRollClick drr , TradezData contextData) { 
		int diceValue = contextData.getCurrentDiceValue().get();
		return String.format("Dice rolled - %d!", diceValue);
	}
	
	private String toString(MovePlayerByDiceClick mp , TradezData contextData) {
		Player player = contextData.getCurrentPlayer().get();
		Tile tile = contextData.getCurrentTile().get();
		return String.format("%s moved to tile %s", player.getName(), tile.getName());
	}
	
	private String toString(ChancePickCardClick cpc, TradezData contextData) { 
		return "Chance card picked!";
	}
	private String toString(JumpToSelectedTileClick j, TradezData contextData) { 
		Player player = contextData.getCurrentPlayer().get();
		Tile tile = contextData.getCurrentTile().get();
		return String.format("%s moved to tile %s", player.getName(), tile.getName());
	}
	private String toString(BuySelectedTileClick b, TradezData contextData) {
		Player player = contextData.getCurrentPlayer().get();
		Tile tile = contextData.getCurrentTile().get();
		return String.format("%s bought %s!", player.getName(), tile.getName());
	}
	private String toString(AddTicketBoothClick a, TradezData contextData) {
		Tile tile = contextData.getCurrentTile().get();
		return String.format("Ticket booth added to %s!", tile.getName());
	}
	private String toString(DoTransferClick tr, TradezData contextData) {
		int send = bagTotalAsString(tr.getSend());
		int receive = bagTotalAsString(tr.getReceive());
		int net  = send-receive;
		return String.format("$%d transferred from %s to %s", net, tr.getFrom(), tr.getTo());
	}
	
	//Default to object
	public String toString(Object originalEvent, TradezData contextData) {
		if(originalEvent instanceof AddPlayerClick) {
			return toString((AddPlayerClick) originalEvent , contextData);
		}else if(originalEvent instanceof DiceRollClick) {
			return toString((DiceRollClick) originalEvent , contextData);
		}else if(originalEvent instanceof MovePlayerByDiceClick) {
			return toString((MovePlayerByDiceClick) originalEvent , contextData);
		}else if(originalEvent instanceof ChancePickCardClick) {
			return toString((ChancePickCardClick) originalEvent , contextData);
		}else if(originalEvent instanceof JumpToSelectedTileClick) {
			return toString((JumpToSelectedTileClick) originalEvent , contextData);
		}else if(originalEvent instanceof BuySelectedTileClick) {
			return toString((BuySelectedTileClick) originalEvent , contextData);
		}else if(originalEvent instanceof AddPlayerClick) {
			return toString((AddPlayerClick) originalEvent , contextData);
		}else if(originalEvent instanceof AddTicketBoothClick) {
			return toString((AddTicketBoothClick) originalEvent , contextData);
		}else if(originalEvent instanceof DoTransferClick) {
			return toString((DoTransferClick) originalEvent , contextData);
		}
		return originalEvent.toString();
	}
	
	//PRIVATE
	private static int bagTotalAsString(Map<Integer, Integer> bag) {
		return bag
				.entrySet()
				.stream()
				.reduce(Map.entry(1, 0), (cumlEntry, next)->{ return Map.entry(1, cumlEntry.getValue() + next.getKey()*next.getValue()); })
				.getValue();
	}
}
