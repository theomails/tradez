package net.progressit.tradez;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

import lombok.Data;
import net.progressit.tradez.TradezMain.PlayerAddedEvent;
import net.progressit.tradez.model.Holdings;
import net.progressit.tradez.model.Player;
import net.progressit.tradez.model.Tile;
import net.progressit.tradez.model.TradezData;
import net.progressit.tradez.panels.TradezOuterPanel;
import net.progressit.tradez.panels.chance.ChancePanel.ChanceClearCurrentCardEvent;
import net.progressit.tradez.panels.chance.ChancePanel.ChancePickCardEvent;
import net.progressit.tradez.panels.dice.DicePanel.DiceRequestRollEvent;
import net.progressit.tradez.panels.dice.DicePanel.MovePlayerEvent;
import net.progressit.tradez.panels.player.PlayersPanel.PlayerSelectedEvent;
import net.progressit.tradez.panels.tile.TileActionPanel.AddTicketBooth;
import net.progressit.tradez.panels.tile.TileActionPanel.BuySelectedTile;
import net.progressit.tradez.panels.tile.TileActionPanel.JumpToSelectedTile;
import net.progressit.tradez.panels.tile.TilePanel.TileClicked;
import net.progressit.tradez.panels.transfer.TransferPanel.TransferRequestEvent;
import net.progressit.util.CollectionsUtil;

public class TradezLogic {
	private static final Logger LOGGER = LoggerFactory.getLogger(TradezLogic.class.getName());
	
	@Data
	public static class TransferStatusEvent{
		private final boolean successful;
	}
	
	private final EventBus globalBus;
	private final TradezOuterPanel tradezPanel;
	private final Consumer<TradezData> tradeDataSetter;
	public TradezLogic(TradezOuterPanel tradezPanel, Consumer<TradezData> tradeDataSetter, EventBus globalBus) {
		super();
		this.tradezPanel = tradezPanel;
		this.tradeDataSetter = tradeDataSetter;
		this.globalBus = globalBus;
	}

	//Convenience functions
	private TradezData getTradeData() {
		return tradezPanel.getData();
	}
	private void setTradeData(TradezData newTradezData) {
		tradeDataSetter.accept(newTradezData);
	}
	
	public void handle(TileClicked tc) {
		LOGGER.info("Tile clicked: " + tc);
		TradezData data = getTradeData();
		//Select that tile
		setTradeData(data.toBuilder()
				.currentTile(Optional.of(tc.getTile()))
				.build());

	}
	
	public void handle(PlayerAddedEvent pa) {
		LOGGER.info("Player added: " + pa);
		TradezData data = getTradeData();
		Player player = pa.getPlayer();
		
		List<Player> players = data.getAllPlayers();
		List<Player> playersNew = CollectionsUtil.cloneToArrayList(players);
		playersNew.add(player);
		
		Map<Player, Holdings> playerHoldings = data.getPlayerHoldings();
		Map<Player, Holdings> playerHoldingsNew = CollectionsUtil.cloneToHashMap(playerHoldings);
		playerHoldingsNew.put(player, TradezConfig.initialHoldingsForPlayer());
		
		Map<Player, Integer> playerPositions = data.getPlayerPosition();
		Map<Player, Integer> playerPositionsNew = CollectionsUtil.cloneToHashMap(playerPositions);
		playerPositionsNew.put(player, 0);
		
		setTradeData(data.toBuilder()
				.allPlayers(playersNew)
				.playerHoldings(playerHoldingsNew)
				.playerPosition(playerPositionsNew)
				.build());
		
		if(playersNew.size()==1) {
			//When first player gets added, select player
			LOGGER.info("Selecting player:: " + pa.getPlayer());
			handle(new PlayerSelectedEvent(pa.getPlayer()));
		}
	}
	public void handle(PlayerSelectedEvent ps) {
		LOGGER.info("Player selected: " + ps);
		TradezData data = getTradeData();
		//Select that Player and his current positioned Tile
		Optional<Integer> playerPosition = data.getCurrentPlayer().map( (p)-> data.getPlayerPosition().get(p) );
		Optional<Tile> playerTile = playerPosition.map( (i) -> data.getAllTiles().get(i) );
		setTradeData(data.toBuilder()
				.currentPlayer(Optional.of(ps.getPlayer()))
				.currentTile(playerTile)
				.build());
		
		globalBus.post(ps);
	}
	
	public void handle(DiceRequestRollEvent drr ) {
		TradezData data = getTradeData();
		//Throw random dice and set that dice value for display
		int diceValue = new Random(System.currentTimeMillis()).nextInt(6) + 1;
		setTradeData(data.toBuilder()
				.currentDiceValue(Optional.of(diceValue))
				.build());
	}
	
	public void handle(MovePlayerEvent mp ) {
		TradezData data = getTradeData();
		//Get current displayed dice value, get current player, get his/her position, move to new position, consider bounds
		int diceValue = data.getCurrentDiceValue().get();
		Player currentPlayer = data.getCurrentPlayer().get();
		Map<Player, Integer> playerPositions = data.getPlayerPosition();
		Integer curPosition = playerPositions.get(currentPlayer);
		int newPosition = (curPosition+diceValue)%data.getAllTiles().size();
		moveCurrentPlayer(data, newPosition);
	}
	
	public void handle(ChancePickCardEvent cpc) {
		LOGGER.info("ChancePickCardEvent: " + cpc);
		TradezData data = getTradeData();
		//Remove one chance card from available, set the current chance card for display
		List<String> copyChanceCards = CollectionsUtil.cloneToArrayList(data.getAvailableChanceCards());
		String newChanceCard = copyChanceCards.remove(0);
		if(copyChanceCards.size()==0) {
			copyChanceCards.addAll(TradezConfig.getAllChanceMessages());
		}
		setTradeData(data.toBuilder()
				.currentChanceCard(Optional.of(newChanceCard))
				.availableChanceCards(copyChanceCards)
				.build());
	}
	public void handle(ChanceClearCurrentCardEvent cclear) {
		LOGGER.info("ChanceClearCurrentCardEvent: " + cclear);
		TradezData data = getTradeData();
		//Clear current chance card
		setTradeData(data.toBuilder()
				.currentChanceCard(Optional.empty())
				.build());
	}
	public void handle(JumpToSelectedTile j) {
		int newTileIndex = getTradeData().getAllTiles().indexOf(getTradeData().getCurrentTile().get());
		moveCurrentPlayer(getTradeData(), newTileIndex);
	}
	public void handle(BuySelectedTile b) {
		TradezData data = getTradeData();
		Player player = data.getCurrentPlayer().get();
		Tile tile = data.getCurrentTile().get();
		Map<Player, Holdings> playerHoldingsCopy = CollectionsUtil.cloneToHashMap(data.getPlayerHoldings());
		Holdings curPlayerHoldings = data.getPlayerHoldings().get(player);
		List<Tile> tilesPossessedNew = CollectionsUtil.cloneToArrayList(curPlayerHoldings.getTilesPossessed());
		tilesPossessedNew.add(tile);
		playerHoldingsCopy.put(player, curPlayerHoldings.toBuilder().tilesPossessed(tilesPossessedNew).build());
		setTradeData(data.toBuilder().playerHoldings(playerHoldingsCopy).build());
	}
	public void handle(AddTicketBooth a) {
		TradezData data = getTradeData();
		Map<Tile, Integer> tileHousesNew = CollectionsUtil.cloneToHashMap(data.getTileHouses());
		Integer curTileHouses = tileHousesNew.get(data.getCurrentTile().get());
		curTileHouses = (curTileHouses==null)?0:curTileHouses;
		curTileHouses++;
		tileHousesNew.put(data.getCurrentTile().get(), curTileHouses);
		setTradeData(data.toBuilder().tileHouses(tileHousesNew).build());
	}
	
	void moveCurrentPlayer(TradezData data, int newTileIndex) {
		Player currentPlayer = data.getCurrentPlayer().get();
		Map<Player, Integer> playerPositions = data.getPlayerPosition();
		Map<Player, Integer> newPlayerPositions = CollectionsUtil.cloneToHashMap(playerPositions);
		newPlayerPositions.put(currentPlayer, newTileIndex);
		Tile newTile = data.getAllTiles().get(newTileIndex);
		setTradeData(data.toBuilder()
				.playerPosition(newPlayerPositions)
				.currentDiceValue(Optional.empty())
				.currentTile(Optional.of(newTile))
				.build());
	}
	
	public void handle(TransferRequestEvent tr) {
		LOGGER.info("Logic to TTL");
		boolean success = new TradezTransactionLogic().doTransfter(getTradeData(), tr, tradeDataSetter);
		if(!success) {
			JOptionPane.showMessageDialog(null, "Not enough of the selected currency to make the transfer!", "Transfer Error", JOptionPane.ERROR_MESSAGE);
		}
		LOGGER.info("Posting TSE");
		globalBus.post( new TransferStatusEvent(success) );
	}
}
