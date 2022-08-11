package net.progressit.tradez;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import lombok.Data;
import net.progressit.progressive.PComponent;
import net.progressit.progressive.PEventListener;
import net.progressit.progressive.PPlacers;
import net.progressit.progressive.components.PSimpleButton.ButtonEvent;
import net.progressit.tradez.model.Player;
import net.progressit.tradez.model.Tile;
import net.progressit.tradez.model.TradezData;
import net.progressit.tradez.panels.TradezOuterPanel;
import net.progressit.tradez.playermodal.AddPlayerPanel;

public class TradezMain {

	public static void main(String[] args) {
		new TradezMain().start();
	}
	
	public static final EventBus GLOBAL_BUS = new EventBus();
	
	@Data
	public static class AddPlayerClick{
		private final Player player;
	}
	
	
	private JFrame frame = new JFrame();
	private PPlacers panelPlacers = new PPlacers( 
			(c)->frame.getContentPane().add(c, BorderLayout.CENTER), 
			(c)->frame.getContentPane().remove(c)
		);

	private TradezOuterPanel mainPanel = new TradezOuterPanel(panelPlacers, GLOBAL_BUS);
	private PEventListener panelListener = new PEventListener() {
		@Subscribe
		public void handle(ButtonEvent event) {
			PComponent.remove(mainPanel);
			frame.dispose();
		}
	};
	private JDialog addPlayerDialog = null;
	
	private void start() {
		JToolBar toolbar = new JToolBar();
		JButton btnAddPlayer = new JButton("Add Player");
		toolbar.add(btnAddPlayer);
		frame.getContentPane().add(toolbar, BorderLayout.NORTH);
		btnAddPlayer.addActionListener( (e)->{
			addPlayerDialog = new JDialog(frame, "Add Player", true);
			addPlayerDialog.getContentPane().add( new AddPlayerPanel(this) );
			addPlayerDialog.pack();
			addPlayerDialog.setLocationRelativeTo( frame ) ;
			addPlayerDialog.setVisible(true);
		} );
		
		List<Tile> tiles = TradezConfig.getAllTiles();
		TradezData tzData = TradezData.builder()
				.allTiles(tiles)
				.bankHoldings(TradezConfig.initialHoldingsForBank())
				.uncleHoldings(TradezConfig.initialHoldingsForUncle())
				.availableChanceCards(TradezConfig.getAllChanceMessages())
				.build();
		
		SwingUtilities.invokeLater( ()->{ 
			PComponent.place(mainPanel, panelListener, tzData); 

			TradezConfig.getDefaultPlayers().forEach( (p)->{ addPlayer(p); });
			
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			frame.setVisible(true);
			frame.setTitle("Tradez!");
		} );
	}
	
	public void addPlayer(Player player) {
		if(addPlayerDialog!=null) addPlayerDialog.dispose();
		GLOBAL_BUS.post(new AddPlayerClick(player));
	}
}
