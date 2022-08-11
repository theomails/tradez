package net.progressit.tradez;

import java.util.Map;

import lombok.Builder;
import lombok.Data;
import net.progressit.tradez.model.Player;
import net.progressit.tradez.model.Tile;
import net.progressit.tradez.panels.transfer.TransferPanel.TransferParty;

public class TradezKeyEvents {
	public interface TradezKeyEvent{}
	
	@Data
	public static class TKEPlayerAdded implements TradezKeyEvent {
		private final Player player;
		@Override
		public String toString() {
			return String.format("%s added to the Game!", player.getName());
		}
		
	}
	
	@Data
	public static class TKEDiceRolled implements TradezKeyEvent {
		private final int diceValue;
		@Override
		public String toString() {
			return String.format("Dice rolled - %d!", diceValue);
		}
	}
	
	@Data
	public static class TKEPlayerMoved implements TradezKeyEvent {
		private final Player player;
		private final Tile tile;
		@Override
		public String toString() {
			return String.format("%s moved to tile %s", player.getName(), tile.getName());
		}
	}
	
	@Data
	public static class TKEChanceCardPicked implements TradezKeyEvent {
		@Override
		public String toString() {
			return "Chance card picked!";
		}
	}
	
	@Data
	public static class TKETileBought implements TradezKeyEvent {
		private final Player player;
		private final Tile tile;
		@Override
		public String toString() {
			return String.format("%s bought %s!", player.getName(), tile.getName());
		}
	}
	
	@Data
	public static class TKETicketBoothAdded implements TradezKeyEvent {
		private final Tile tile;
		@Override
		public String toString() {
			return String.format("Ticket booth added to %s!", tile.getName());
		}
	}
	
	@Data
	@Builder
	public static class TKETransferRequestCompleted implements TradezKeyEvent {
		private final boolean successful;
		private final String failureReason;
		public final TransferParty from;
		public final TransferParty to;
		public final Map<Integer, Integer> send;
		public final Map<Integer, Integer> receive;
		
		public String toString() {
			int send = bagTotalAsString(getSend());
			int receive = bagTotalAsString(getReceive());
			int net  = send-receive;
			if(successful) {
				return String.format("$%d transferred from %s to %s", net, getFrom(), getTo());
			}else {
				return String.format("Unable to transfer $%d from %s to %s", net, getFrom(), getTo());
			}
		}
		
		private static int bagTotalAsString(Map<Integer, Integer> bag) {
			return bag
					.entrySet()
					.stream()
					.reduce(Map.entry(1, 0), (cumlEntry, next)->{ return Map.entry(1, cumlEntry.getValue() + next.getKey()*next.getValue()); })
					.getValue();
		}
	}
	
}
