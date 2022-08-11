package net.progressit.tradez.panels.player;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import net.progressit.progressive.helpers.PSimpleLifecycleHandler;
import net.progressit.tradez.model.Player;
import net.progressit.tradez.panels.dice.DicePanel;
import net.progressit.tradez.panels.dice.DicePanel.DiceRequestRollEvent;
import net.progressit.tradez.panels.dice.DicePanel.MovePlayerEvent;
import net.progressit.tradez.panels.dice.DicePanelData;
import net.progressit.tradez.panels.holdings.HoldingsPanel;
import net.progressit.tradez.panels.holdings.HoldingsPanelData;

public class PlayersPanel extends PComponent<PlayersPanelData, PlayersPanelData>{
	private static final Logger LOGGER = LoggerFactory.getLogger(PlayersPanel.class.getName());

	@Data
	public static class PlayerSelectedEvent{
		private final Player player;
	}
	
	
	private JPanel panel = new JPanel(new MigLayout("insets 15","[550::, grow, fill][grow]","[fill]10[fill]"));
	private JPanel pnlTokens = new JPanel(new MigLayout("insets 0","[::120,grow,fill]10","[]"));
	
	private PEventListener diceListener = new PEventListener() {
		@Subscribe
		public void handle(DiceRequestRollEvent drr ) {
			PlayersPanel.this.post(drr);
		}
		@Subscribe
		public void handle(MovePlayerEvent mp ) {
			PlayersPanel.this.post(mp);
		}
	};
	
	public PlayersPanel(PPlacers placers, EventBus globalBus) {
		super(placers, globalBus);
	}

	@Override
	protected JComponent getUiComponent() {
		return panel;
	}

	@Override
	protected void renderSelf(PlayersPanelData data) {
		LOGGER.info("Rendering..");
		pnlTokens.removeAll();
		
		data.getAllPlayers().forEach((p)->{
			boolean selectedPlayer = p.equals(data.getCurrentPlayer().orElse(null));
			LOGGER.info("selectedPlayer: " + selectedPlayer);
			
			JLabel lPlayer = new JLabel(p.getName());
			lPlayer.setBackground(p.getColor());
			lPlayer.setOpaque(true);
			if(selectedPlayer) {
				Border outerBorder = BorderFactory.createLineBorder(Color.orange, 2);
				Border innerBorder = BorderFactory.createEmptyBorder(5, 10, 5, 10);
				lPlayer.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
			}else {
				Border outerBorder = BorderFactory.createLineBorder(Color.gray);
				Border innerBorder = BorderFactory.createEmptyBorder(5, 10, 5, 10);
				lPlayer.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
			}
			pnlTokens.add(lPlayer);
			lPlayer.addMouseListener( new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					PlayersPanel.this.post(new PlayerSelectedEvent( p ));
				}
			} );
			
			//pnlTokens.invalidate();
			pnlTokens.revalidate();
		});
		
		
	}

	@Override
	protected PLifecycleHandler getLifecycleHandler() {
		return new PSimpleLifecycleHandler() {			
			@Override
			public void prePlacement() {
				panel.add(pnlTokens, "wrap");
				panel.setOpaque(false);
				
				panel.setBorder(BorderFactory.createTitledBorder("Players: "));
				
				pnlTokens.setOpaque(false);
				
			}
			@Override
			public void postProps() {
				PlayersPanel.this.setData( PlayersPanel.this.getProps() );
			}
		};
	}

	@Override
	protected List<Class<?>> declareEmittedEvents() {
		return List.of(PlayerSelectedEvent.class, DiceRequestRollEvent.class, MovePlayerEvent.class);
	}

	@Override
	protected Set<Object> partitionDataForSelf(PlayersPanelData data) {
		return Set.of(data);
	}

	@Override
	protected Set<Object> partitionDataForChildren(PlayersPanelData data) {
		return Set.of(data);
	}

	@Override
	protected PChildrenPlan renderChildrenPlan(PlayersPanelData data) {
		PChildrenPlan childrenPlan = new PChildrenPlan();
		
		PPlacers wrapPlacer = new PPlacers( (c)-> panel.add(c, "wrap") , (c)-> panel.remove(c));
		
		DicePanelData dpdata = DicePanelData.builder()
				.currentPlayer(data.getCurrentPlayer())
				.currentDiceValue(data.getCurrentDiceValue())
				.build();
		PChildPlan dicePlan = PChildPlan.builder()
				.component(new DicePanel(wrapPlacer, getGlobalBus()))
				.props(dpdata)
				.listener(Optional.of(diceListener))
				.build();
		childrenPlan.addChildPlan(dicePlan);

		
		HoldingsPanelData hpdata = HoldingsPanelData.builder()
				.displayedHoldings( data.getCurrentPlayer().map( (p)->{ return data.getPlayerHoldings().get(p); } ) )
				.panelTitle("Player Holdings")
				.build();
		
		PChildPlan holdingsPlan = PChildPlan.builder()
				.component(new HoldingsPanel(wrapPlacer, getGlobalBus()))
				.props(hpdata)
				.listener(Optional.empty())
				.build();
		childrenPlan.addChildPlan(holdingsPlan);
		return childrenPlan;
	}
}
