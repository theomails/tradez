package net.progressit.tradez.panels.tile;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

import net.miginfocom.swing.MigLayout;
import net.progressit.progressive.PChildPlan;
import net.progressit.progressive.PChildrenPlan;
import net.progressit.progressive.PComponent;
import net.progressit.progressive.PLifecycleHandler;
import net.progressit.progressive.PPlacers;
import net.progressit.progressive.helpers.PSimpleLifecycleHandler;

public class TileActionPanel extends PComponent<TilePanelData, TilePanelData>{
	private static final Logger LOGGER = LoggerFactory.getLogger(TileActionPanel.class.getName());
	
	public static class JumpToSelectedTile{}
	public static class BuySelectedTile{}
	public static class AddTicketBooth{}
	
	private JPanel panel = new JPanel(new MigLayout("insets 15","[180::180, fill][grow][]","[]"));
	private JPanel pnlTilePanelWrapper = new JPanel(new BorderLayout());
	private JPanel pnlButtons = new JPanel(new MigLayout("insets 0","[fill]","[]"));
	private JButton btnJumpHere = new JButton("Jump Here");
	private JButton btnBuyTile = new JButton("Buy!");
	private JButton btnAddTicketBooth = new JButton("Add Ticket Booth");
	
	public TileActionPanel(PPlacers placers, EventBus globalBus) {
		super(placers, globalBus);
	}

	@Override
	protected JComponent getUiComponent() {
		return panel;
	}

	@Override
	protected void renderSelf(TilePanelData data) {
		LOGGER.info("Rendering..");
		btnJumpHere.setEnabled( data.getCurrentPlayer().isPresent() );
		btnBuyTile.setEnabled( data.getCurrentPlayer().isPresent() && data.getOwner().isEmpty() && data.getTile().isBuyable() );
		btnAddTicketBooth.setEnabled(data.getOwner().isPresent() && data.getTile().isHouseAllowed() && data.getNumHouses().orElse(0)<2);
	}

	@Override
	protected PLifecycleHandler getLifecycleHandler() {
		return new PSimpleLifecycleHandler() {			
			@Override
			public void prePlacement() {
				panel.setOpaque(false);
				panel.add(pnlTilePanelWrapper);
				panel.add(pnlButtons, "skip 1");
				panel.setBorder(BorderFactory.createTitledBorder("Tile"));
				
				pnlButtons.add(btnJumpHere, "wrap, hidemode 3");
				pnlButtons.add(btnBuyTile, "wrap, hidemode 3");
				pnlButtons.add(btnAddTicketBooth, "wrap, hidemode 3");
				pnlButtons.setOpaque(false);
				
				btnJumpHere.addActionListener( (e) -> TileActionPanel.this.post(new JumpToSelectedTile()) );
				btnBuyTile.addActionListener( (e) -> TileActionPanel.this.post(new BuySelectedTile()) );
				btnAddTicketBooth.addActionListener( (e) -> TileActionPanel.this.post(new AddTicketBooth()) );
				
			}
			@Override
			public void postProps() {
				TileActionPanel.this.setData( TileActionPanel.this.getProps() );
			}
		};
	}

	@Override
	protected List<Class<?>> declareEmittedEvents() {
		return List.of(JumpToSelectedTile.class, BuySelectedTile.class, AddTicketBooth.class);
	}

	@Override
	protected Set<Object> partitionDataForSelf(TilePanelData data) {
		return Set.of(data);
	}

	@Override
	protected Set<Object> partitionDataForChildren(TilePanelData data) {
		return Set.of(data);
	}

	@Override
	protected PChildrenPlan renderChildrenPlan(TilePanelData data) {
		PChildrenPlan childrenPlan = new PChildrenPlan();
		
		PPlacers placer = new PPlacers(
				(c)->{
					pnlTilePanelWrapper.add(c, BorderLayout.CENTER);
				},
				(c)-> pnlTilePanelWrapper.remove(c)
				);
		
		PChildPlan tilePlan = PChildPlan.builder()
				.component(new TilePanel(placer, getGlobalBus()))
				.props(data)
				.listener(Optional.empty())
				.build();
		childrenPlan.addChildPlan(tilePlan);

		return childrenPlan;
	}
}
