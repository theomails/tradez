package net.progressit.tradez.panels;

import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import lombok.Data;
import net.miginfocom.swing.MigLayout;
import net.progressit.progressive.PChildPlan;
import net.progressit.progressive.PChildrenPlan;
import net.progressit.progressive.PComponent;
import net.progressit.progressive.PEventListener;
import net.progressit.progressive.PLifecycleHandler;
import net.progressit.progressive.PPlacers;
import net.progressit.progressive.helpers.PComponentHelper;
import net.progressit.progressive.helpers.PSimpleContainerPlacers;
import net.progressit.progressive.helpers.PSimpleLifecycleHandler;
import net.progressit.tradez.TradezLogic;
import net.progressit.tradez.TradezMain.PlayerAddedEvent;
import net.progressit.tradez.model.Player;
import net.progressit.tradez.model.Tile;
import net.progressit.tradez.model.TradezData;
import net.progressit.tradez.panels.chance.ChancePanel;
import net.progressit.tradez.panels.chance.ChancePanel.ChanceClearCurrentCardEvent;
import net.progressit.tradez.panels.chance.ChancePanel.ChancePickCardEvent;
import net.progressit.tradez.panels.chance.ChancePanelData;
import net.progressit.tradez.panels.dice.DicePanel.DiceRequestRollEvent;
import net.progressit.tradez.panels.dice.DicePanel.MovePlayerEvent;
import net.progressit.tradez.panels.holdings.HoldingsPanel;
import net.progressit.tradez.panels.holdings.HoldingsPanelData;
import net.progressit.tradez.panels.player.PlayersPanel;
import net.progressit.tradez.panels.player.PlayersPanel.PlayerSelectedEvent;
import net.progressit.tradez.panels.player.PlayersPanelData;
import net.progressit.tradez.panels.tile.TileActionPanel;
import net.progressit.tradez.panels.tile.TileActionPanel.AddTicketBooth;
import net.progressit.tradez.panels.tile.TileActionPanel.BuySelectedTile;
import net.progressit.tradez.panels.tile.TileActionPanel.JumpToSelectedTile;
import net.progressit.tradez.panels.tile.TilePanel;
import net.progressit.tradez.panels.tile.TilePanel.TileClicked;
import net.progressit.tradez.panels.tile.TilePanelData;
import net.progressit.tradez.panels.tile.TilePanelData.TileBounds;
import net.progressit.tradez.panels.transfer.TransferPanel;
import net.progressit.tradez.panels.transfer.TransferPanel.TransferRequestEvent;
import net.progressit.tradez.panels.transfer.TransferPanelData;

public class TradezOuterPanel extends PComponent<TradezData, TradezData>{
	private static final Logger LOGGER = Logger.getLogger(TradezOuterPanel.class.getName());
	
	@Data
	public static class Ref<T>{
		private T value;
	}
	
	public static final int X_TILES = 11;
	public static final int Y_TILES = 7;
	private static final int[] XPOS = new int[] {10,9,8,7,6,5,4,3,2,1,0,
			0,0,0,0,0,
			0,1,2,3,4,5,6,7,8,9,10,
			10,10,10,10,10};
	private static final int[] YPOS = new int[] {6,6,6,6,6,6,6,6,6,6,6,
			5,4,3,2,1,
			0,0,0,0,0,0,0,0,0,0,0,
			1,2,3,4,5};
	
	private PEventListener tilesListener = new PEventListener() {
		@Subscribe
		public void handle(TileClicked tc) {
			logic.handle(tc);
		}
	};
	
	private PEventListener playersListener = new PEventListener() {
		@Subscribe
		public void handle(PlayerSelectedEvent ps) {
			logic.handle(ps);
		}
		@Subscribe
		public void handle(DiceRequestRollEvent drr ) {
			logic.handle(drr);
		}
		@Subscribe
		public void handle(MovePlayerEvent mp ) {
			logic.handle(mp);
		}
	};
	
	private PEventListener chanceListener = new PEventListener() {
		@Subscribe
		public void handle(ChancePickCardEvent cpc) {
			logic.handle(cpc);
		}
		@Subscribe
		public void handle(ChanceClearCurrentCardEvent cclear) {
			logic.handle(cclear);
		}
	};
	
	private PEventListener tileActionListener = new PEventListener() {
		@Subscribe
		public void handle(JumpToSelectedTile j) {
			logic.handle(j);
		}
		@Subscribe
		public void handle(BuySelectedTile b) {
			logic.handle(b);
		}
		@Subscribe
		public void handle(AddTicketBooth a) {
			logic.handle(a);
		}
	};
	
	private PEventListener transferListener = new PEventListener() {
		@Subscribe
		public void handle(TransferRequestEvent tr) {
			LOGGER.info("TOP to Logic");
			logic.handle(tr);
		}
	};
	
	private JPanel panel = new JPanel();
	private JPanel statusPanelsWrapper = new JPanel(new MigLayout("insets 0","[grow, fill][grow, fill]","[grow, fill]"));
	private JPanel statusLeftPanel = new JPanel(new MigLayout("insets 0","[grow, fill]","[]"));
	private JPanel statusRightPanel = new JPanel(new MigLayout("insets 0","[grow, fill]","[]"));
	private final TradezLogic logic;
	
	public TradezOuterPanel(PPlacers placers, EventBus globalBus) {
		super(placers, globalBus);
		logic = new TradezLogic(this, (data)-> this.setData(data), globalBus);
	}

	@Override
	protected Set<Object> partitionDataForSelf(TradezData data) {
		int tw = data.getTileWidth();
		int th = data.getTileHeight();
		int ox = data.getOriginX();
		int oy = data.getOriginY();
		return new HashSet<>( List.of( tw, th, ox, oy ) );
	}

	@Override
	protected Set<Object> partitionDataForChildren(TradezData data) {
		return PComponentHelper.setWithAllData(data);
	}

	@Override
	protected JComponent getUiComponent() {
		return panel;
	}

	@Override
	protected void renderSelf(TradezData data) {
		int tw = data.getTileWidth();
		int th = data.getTileHeight();
		int ox = data.getOriginX();
		int oy = data.getOriginY();
		int pw = panel.getWidth();
		int ph = panel.getHeight();
		statusPanelsWrapper.setBounds(tw+ox+10, th+oy+10, pw-2*tw-2*ox-2*10, ph-2*th-2*oy-2*10);
	}

	@Override
	protected PChildrenPlan renderChildrenPlan(TradezData data) {
		PChildrenPlan childrenPlan = new PChildrenPlan();
		
		PPlacers mainPanelPlacer = new PSimpleContainerPlacers(panel);
		List<Tile> tiles = data.getAllTiles();
		for(int i=0;i<tiles.size();i++) {
			Tile tile = tiles.get(i);
			TilePanelData tpd = makeDataForTilePanel(tile, i, true, data);
			
			PChildPlan childPlan = PChildPlan.builder()
					.component(new TilePanel(mainPanelPlacer, getGlobalBus()))
					.props(tpd)
					.listener(Optional.of(tilesListener))
					.build();
			childrenPlan.addChildPlan(childPlan);
		}
		
		PPlacers leftPanelPlacer = new PPlacers( (c)-> statusLeftPanel.add(c, "wrap") , (c)-> statusLeftPanel.remove(c));
		PlayersPanelData pdata = PlayersPanelData.builder()
				.allPlayers(data.getAllPlayers())
				.currentPlayer(data.getCurrentPlayer())
				.currentDiceValue(data.getCurrentDiceValue())
				.playerHoldings(data.getPlayerHoldings())
				.build();
		PChildPlan playersPanelPlan = PChildPlan.builder()
				.component(new PlayersPanel(leftPanelPlacer, getGlobalBus()))
				.props(pdata)
				.listener(Optional.of(playersListener))
				.build();
		childrenPlan.addChildPlan(playersPanelPlan);
		
		if(data.getCurrentTile().isPresent()) {
			TilePanelData tpData = data.getCurrentTile().map( (t)-> makeDataForTilePanel(t, data.getAllTiles().indexOf(t), false, data)).orElse(null);
			PChildPlan tapPlan = PChildPlan.builder()
					.component(new TileActionPanel(leftPanelPlacer, getGlobalBus()))
					.props(tpData)
					.listener(Optional.of(tileActionListener))
					.build();
			childrenPlan.addChildPlan(tapPlan);
		}
		
		ChancePanelData chanceData = ChancePanelData.builder()
				.availableChanceCards( data.getAvailableChanceCards() )
				.currentChanceCard( data.getCurrentChanceCard() )
				.build();
		PChildPlan chancePanelPlan = PChildPlan.builder()
				.component(new ChancePanel(leftPanelPlacer, getGlobalBus()))
				.props(chanceData)
				.listener(Optional.of(chanceListener))
				.build();
		childrenPlan.addChildPlan(chancePanelPlan);
		
		PPlacers rightPanelPlacer = new PPlacers( (c)-> statusRightPanel.add(c, "wrap") , (c)-> statusRightPanel.remove(c));
		HoldingsPanelData bankdata = HoldingsPanelData.builder()
				.displayedHoldings( Optional.of(data.getBankHoldings()) )
				.panelTitle("Bank Holdings")
				.build();
		PChildPlan bankHoldingsPlan = PChildPlan.builder()
				.component(new HoldingsPanel(rightPanelPlacer, getGlobalBus()))
				.props(bankdata)
				.listener(Optional.of(new PEventListener() {}))
				.build();
		childrenPlan.addChildPlan(bankHoldingsPlan);
		
		HoldingsPanelData uncledata = HoldingsPanelData.builder()
				.displayedHoldings( Optional.of(data.getUncleHoldings()) )
				.panelTitle("Uncle Penny Bag's Loose Change")
				.build();
		PChildPlan uncleHoldingsPlan = PChildPlan.builder()
				.component(new HoldingsPanel(rightPanelPlacer, getGlobalBus()))
				.props(uncledata)
				.listener(Optional.of(new PEventListener() {}))
				.build();
		childrenPlan.addChildPlan(uncleHoldingsPlan);
		
		TransferPanelData trfdata = TransferPanelData.builder()
				.allPlayers(data.getAllPlayers())
				.currentPlayer(data.getCurrentPlayer())
				.currencies(data.getBankHoldings().getCurrencyPossessionMap().keySet())
				.build();
		PChildPlan trfPanelPlan = PChildPlan.builder()
				.component(new TransferPanel(rightPanelPlacer, getGlobalBus()))
				.props(trfdata)
				.listener(Optional.of(transferListener))
				.build();
		childrenPlan.addChildPlan(trfPanelPlan);
		
		return childrenPlan;
	}

	@Override
	protected PLifecycleHandler getLifecycleHandler() {
		return new PSimpleLifecycleHandler() {
			@Override
			public void prePlacement() {
				TradezOuterPanel.this.getGlobalBus().register(TradezOuterPanel.this);
				
				panel.setLayout(null);
				panel.setBackground(new Color(0xCBE3C6));
				panel.setBorder(BorderFactory.createLineBorder(Color.gray) );
				
				panel.add(statusPanelsWrapper);
				
				statusPanelsWrapper.add(statusLeftPanel);
				statusPanelsWrapper.add(statusRightPanel);
				statusPanelsWrapper.setBounds(200,200, 500, 500);
				statusPanelsWrapper.setOpaque(false);
				//statusPanelsWrapper.setBorder(BorderFactory.createLineBorder(Color.gray));
				
				statusLeftPanel.setOpaque(false);
				statusRightPanel.setOpaque(false);
				
				panel.addComponentListener(new ComponentAdapter() {
					public void componentResized(ComponentEvent e) {
						handleResize();
					}
				});
			}			
			@Override
			public void postProps() {
				setData(getProps());
			}
		};
	}
	
	@Subscribe
	public void handle(PlayerAddedEvent pa) {
		logic.handle(pa);
	}

	@Override
	protected List<Class<?>> declareEmittedEvents() {
		return List.of();
	}

	private TilePanelData makeDataForTilePanel(Tile tile, int tileIndex, boolean setBounds, TradezData data) {
		Optional<Player> currentPlayer = data.getCurrentPlayer();
		Ref<Player> tileOwner = new Ref<>();
		data.getPlayerHoldings().forEach( (p, h)->{
			if(h.getTilesPossessed().contains(tile)) {
				tileOwner.setValue(p);
			}
		});
		List<Player> visitingPlayers = new ArrayList<>();
		for(Entry<Player, Integer> playerPosition:data.getPlayerPosition().entrySet()) {
			if(playerPosition.getValue().equals(tileIndex)) {
				visitingPlayers.add(playerPosition.getKey());
			}
		}
		Integer tileHouses = data.getTileHouses().get(tile);
		
		Optional<TileBounds> tileBounds = Optional.empty();
		if(setBounds) {
			int positionX = XPOS[tileIndex] * data.getTileWidth() + data.getOriginX();
			int positionY = YPOS[tileIndex] * data.getTileHeight() + data.getOriginY();
			int sizeX = data.getTileWidth();
			int sizeY = data.getTileHeight();
			tileBounds = Optional.of( TileBounds.builder()
					.positionX(positionX)
					.positionY(positionY)
					.sizeX(sizeX)
					.sizeY(sizeY)
					.build() );
		}
		
		boolean selected = data.getCurrentTile().isPresent()?(data.getCurrentTile().get().equals(tile)):false;
		
		TilePanelData tpd = TilePanelData.builder()
				.tile(tile)
				.tileIndex(tileIndex)
				.currentPlayer(currentPlayer)
				.owner(Optional.ofNullable(tileOwner.value))
				.visitingPlayers(visitingPlayers)
				.numHouses(Optional.ofNullable(tileHouses))
				.tileBounds(tileBounds)
				.selected(selected)
				.build();
		
		return tpd;
	}
	
	private void handleResize() {
		int w = panel.getWidth();
		int h = panel.getHeight();
		TradezData data = TradezOuterPanel.this.getData();
		if(data!=null) {
			int tw = (w-8)/X_TILES;
			int th = (h-8)/Y_TILES;
			int originx = (w - tw*X_TILES)/2;
			int originy = (h - th*Y_TILES)/2;
			TradezData newData = data.toBuilder()
					.tileWidth(tw).tileHeight(th)
					.originX(originx).originY(originy)
					.build();
			TradezOuterPanel.this.setData(newData);
		}
	}
}
