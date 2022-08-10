package net.progressit.tradez.panels.holdings;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.google.common.eventbus.EventBus;

import net.miginfocom.swing.MigLayout;
import net.progressit.progressive.PLeafComponent;
import net.progressit.progressive.PLifecycleHandler;
import net.progressit.progressive.PPlacers;
import net.progressit.progressive.helpers.PSimpleLifecycleHandler;
import net.progressit.tradez.model.Holdings;
import net.progressit.tradez.model.Tile;

public class HoldingsPanel extends PLeafComponent<HoldingsPanelData, HoldingsPanelData>{

	private JPanel pnlPlayerHoldings = new JPanel(new MigLayout("insets 10","[grow, fill]","[fill]"));
	private JLabel lblHeldCurrency = new JLabel();
	private JLabel lblHeldTiles = new JLabel();

	public HoldingsPanel(PPlacers placers, EventBus globalBus) {
		super(placers, globalBus);
	}

	@Override
	protected JComponent getUiComponent() {
		return pnlPlayerHoldings;
	}

	@Override
	protected void renderSelf(HoldingsPanelData data) {
		Optional<String> currenciesString = data.getDisplayedHoldings()
				.map( (h)->{ return heldCurrenciesAsString(h);});
			
			Optional<String> tilesString = data.getDisplayedHoldings()
				.map( (h)->{ return heldTilesAsString(h);});
			
			setLabelText(lblHeldCurrency, currenciesString.map( (s)->{ return "Money: " + s; } ).orElse("") );
			setLabelText(lblHeldTiles, tilesString.map( (s)->{ return "".equals(s)?"": "Tiles: " + s; } ).orElse("") );

			pnlPlayerHoldings.setBorder(BorderFactory.createTitledBorder(data.getPanelTitle()));
	}

	@Override
	protected PLifecycleHandler getLifecycleHandler() {
		return new PSimpleLifecycleHandler() {
			@Override
			public void prePlacement() {
				pnlPlayerHoldings.add(lblHeldCurrency, "wrap");
				pnlPlayerHoldings.add(lblHeldTiles, "");
				pnlPlayerHoldings.setOpaque(false);

			}
			@Override
			public void postProps() {
				HoldingsPanel.this.setData(HoldingsPanel.this.getProps());
			}
		};
	}

	@Override
	protected List<Class<?>> declareEmittedEvents() {
		return List.of();
	}

	private static String heldCurrenciesAsString(Holdings h) {
		return h.getCurrencyPossessionMap()
				.entrySet()
				.stream()
				.map( (e)->{ return "$"+e.getKey()+" x "+e.getValue(); })
				.collect(Collectors.joining(", "));
	}
	
	private static String heldTilesAsString(Holdings h) {
		return h.getTilesPossessed()
				.stream()
				.map( Tile::getName )
				.collect(Collectors.joining(", "));
	}

	private void setLabelText(JLabel label, String text) {
		String extText = "<html><div style='text-align:center'>" + text + "</div></html>";
		label.setText(extText);
	}
}
