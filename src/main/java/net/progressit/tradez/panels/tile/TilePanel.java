package net.progressit.tradez.panels.tile;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

import lombok.Data;
import net.miginfocom.swing.MigLayout;
import net.progressit.progressive.PLeafComponent;
import net.progressit.progressive.PLifecycleHandler;
import net.progressit.progressive.PPlacers;
import net.progressit.progressive.helpers.PSimpleLifecycleHandler;
import net.progressit.tradez.model.Player;
import net.progressit.tradez.model.Tile;
import net.progressit.tradez.panels.tile.TilePanelData.TileBounds;

public class TilePanel extends PLeafComponent<TilePanelData, TilePanelData>{
	private static final Logger LOGGER = LoggerFactory.getLogger(TilePanel.class.getName());

	@Data
	public static class TileClicked{
		private final Tile tile;
		private final int tileIndex;
		private final MouseEvent event;
	}
	
	private JPanel panel = new JPanel(new MigLayout("insets 0","[ 30::, grow, fill]","[20::20, fill][]"));
	private JLabel labelTitle = new JLabel();
	private JLabel labelName = new JLabel();
	private JLabel labelDescription = new JLabel();
	private JLabel labelOwner = new JLabel();
	private JLabel labelPrice = new JLabel();
	private JLabel labelBaseRent = new JLabel();
	private JPanel pnlVisitors = new JPanel();
	
	public TilePanel(PPlacers placers, EventBus globalBus) {
		super(placers, globalBus);
	}

	@Override
	protected JComponent getUiComponent() {
		return panel;
	}

	@Override
	protected void renderSelf(TilePanelData data) {
		LOGGER.info("Rendering..");
		setLabelText(labelName, Optional.of(data.getTile().getName()));
		
		setLabelText(labelDescription,data.getTile().getDescription());
		setLabelText(labelOwner,data.getOwner().map(Player::getName));
		setLabelText(labelPrice, data.getTile().getPriceOrCharge().map( (p)->{ return "$"+p; } ));
		setLabelText(labelBaseRent, data.getTile().getBaseRent().map( (p)->{ return "Base rent $"+p; } ));
		setLabelText(labelTitle, Optional.of(data.getNumHouses().map( (i)->{return ""+i;}).orElse("") ));
		
		if(data.getTile().getColor().isPresent()) {
			labelTitle.setBackground(data.getTile().getColor().get());
			labelTitle.setOpaque(true);
		}else {
			labelTitle.setOpaque(false);
			labelTitle.setVisible(false);
		}
		
		pnlVisitors.removeAll();
		data.getVisitingPlayers().forEach((p)->{
			JLabel label = new JLabel(p.getName());
			pnlVisitors.add(label);
			label.setBackground(p.getColor());
			label.setOpaque(true);
			
		});
		
		if(data.isSelected() && data.getTileBounds().isPresent()) {
			panel.setBorder(BorderFactory.createLineBorder(Color.orange, 2));
		}else {
			panel.setBorder(BorderFactory.createLineBorder(Color.gray));
		}
		labelBaseRent.setVisible(data.getTileBounds().isEmpty());
		
		if(data.getTileBounds().isPresent()) {
			TileBounds bounds = data.getTileBounds().get();
			panel.setBounds(bounds.getPositionX(), bounds.getPositionY(), bounds.getSizeX(), bounds.getSizeY());
		}
		
		panel.revalidate();
		panel.repaint();
	}

	@Override
	protected PLifecycleHandler getLifecycleHandler() {
		return new PSimpleLifecycleHandler() {
			@Override
			public void prePlacement() {
				//panel.setBackground(new Color(245,245,245)); ;
				panel.setOpaque(false);
				pnlVisitors.setOpaque(false);
				
				panel.add(labelTitle, "wrap, hidemode 3");
				panel.add(labelName, "wrap, hidemode 3");
				panel.add(labelDescription, "wrap, hidemode 3");
				panel.add(labelOwner, "wrap");
				panel.add(labelPrice, "wrap, hidemode 3");
				panel.add(labelBaseRent, "wrap, hidemode 3");
				panel.add(pnlVisitors, "hidemode 0");
				
				stylize(labelTitle, "Title");
				stylize(labelName, "labelName");
				labelName.setFont(labelTitle.getFont().deriveFont(Font.BOLD));
				stylize(labelDescription, "labelDescription");
				stylize(labelOwner, "labelOwner");
				stylize(labelPrice, "labelPrice");
				stylize(labelBaseRent, "labelBaseRent");
				
				MouseListener mlis = new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						TilePanelData data = TilePanel.this.getData();
						TileClicked tc = new TileClicked(data.getTile(), data.getTileIndex(), e);
						TilePanel.this.post(tc);
					}
				};
				
				panel.addMouseListener(mlis);
				
			}
			@Override
			public void postProps() {
				setData(getProps());
			}
		};
	}

	@Override
	protected List<Class<?>> declareEmittedEvents() {
		return List.of(TileClicked.class);
	}
	
	private void setLabelText(JLabel label, Optional<String> text) {
		if(text.isEmpty()) {
			label.setVisible(false);
		}else {
			String extText = "<html><div style='text-align:center'>" + text.get() + "</div></html>";
			label.setText(extText);
			label.setVisible(true);
		}
	}

	private void stylize(JLabel label, String debugName) {
		//label.setBorder(BorderFactory.createTitledBorder(debugName));
		label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		label.setFont(label.getFont().deriveFont(5));
	}
}
