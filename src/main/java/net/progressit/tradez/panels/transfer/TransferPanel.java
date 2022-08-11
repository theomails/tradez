package net.progressit.tradez.panels.transfer;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import lombok.Builder;
import lombok.Data;
import net.miginfocom.swing.MigLayout;
import net.progressit.progressive.PLeafComponent;
import net.progressit.progressive.PLifecycleHandler;
import net.progressit.progressive.PPlacers;
import net.progressit.progressive.helpers.PSimpleLifecycleHandler;
import net.progressit.tradez.TradezKeyEvents.TKETransferRequestCompleted;
import net.progressit.tradez.model.Player;
import net.progressit.tradez.panels.player.PlayersPanel.PlayerSelectedEvent;
import net.progressit.util.CollectionsUtil;

public class TransferPanel extends PLeafComponent<TransferPanelData, TransferPanelData>{
	private static final Logger LOGGER = LoggerFactory.getLogger(TransferPanel.class.getName());
	
	public enum TransferPartyType { PLAYER, BANK, UNCLE }
	
	@Data
	@Builder
	public static class TransferParty{
		private TransferPartyType type;
		@Builder.Default
		private Optional<Player> player = Optional.empty();
		
		public String toString() {
			switch(type) {
			case PLAYER:
				return player.get().getName();
			case BANK:
				return "BANK";
			case UNCLE:
				return "Uncle Penny Bag's Loose Change";
			}
			return "Unknown";
		}
	}
	
	@Data
	@Builder
	public static class DoTransferClick{
		public final TransferParty from;
		public final TransferParty to;
		public final Map<Integer, Integer> send;
		public final Map<Integer, Integer> receive;
		@Override
		public String toString() {
			return "TransferRequestEvent [from=" + from 
						+ ", \nto=" + to 
						+ ", \nsend=" + send 
						+ ", \nreceive=" + receive
					+ "]";
		}
		
	}
	
	private JPanel panel = new JPanel(new MigLayout("insets 0","[grow, fill]40[grow, fill]","[][]"));
	private JPanel pnlFromAndTo = new JPanel(new MigLayout("insets 0","[][][][grow][]","[]"));
	private JPanel pnlSend = new JPanel(new MigLayout("insets 10","[grow][][][][grow][]","[]"));
	private JPanel pnlReceive = new JPanel(new MigLayout("insets 10","[grow][][][][grow][]","[]"));
	private JPanel pnlSubmit = new JPanel(new MigLayout("insets 0","[grow][]","[]"));
	
	private JComboBox<TransferParty> cboFrom = new JComboBox<>();
	private JLabel lblDirection = new JLabel(" to ");
	private JComboBox<TransferParty> cboTo = new JComboBox<>();
	private JButton btnSwitch = new JButton("< Switch >");
	private JButton btnTransfer = new JButton("Transfer");
	
	private ActionListener fromActionListener = new ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if(cboFrom.getSelectedIndex()>=0 && getData()!=null) {
				Optional<TransferParty> fromTp = Optional.of((TransferParty)cboFrom.getSelectedItem());
				TransferPanelData tpData = getData().toBuilder().from(fromTp).build();
				LOGGER.info("Action setting fromTp " + fromTp);
				setData(tpData);
			}
		}
	};
	
	private ActionListener toActionListener = new ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if(cboTo.getSelectedIndex()>=0 && getData()!=null) {
				Optional<TransferParty> toTp = Optional.of((TransferParty)cboTo.getSelectedItem());
				TransferPanelData tpData = getData().toBuilder().to(toTp).build();
				LOGGER.info("Action setting toTp " + toTp);
				setData(tpData);
			}
		}
	};
		
	
	public TransferPanel(PPlacers placers, EventBus globalBus) {
		super(placers, globalBus);
	}

	@Override
	protected JComponent getUiComponent() {
		return panel;
	}

	@Override
	protected void renderSelf(TransferPanelData data) {
		LOGGER.info("Rendering..");
		if(data.getFrom().isEmpty()) {
			LOGGER.info("EMPTY");
		}
		List<TransferParty> froms = new ArrayList<>();
		List<TransferParty> tos = new ArrayList<>();
		data.getAllPlayers().forEach( (p)-> froms.add( TransferParty.builder().type(TransferPartyType.PLAYER).player(Optional.of(p)).build() ) );
		data.getAllPlayers().forEach( (p)-> tos.add( TransferParty.builder().type(TransferPartyType.PLAYER).player(Optional.of(p)).build() ) );
		froms.add(TransferParty.builder().type(TransferPartyType.BANK).build());
		froms.add(TransferParty.builder().type(TransferPartyType.UNCLE).build());
		tos.add(TransferParty.builder().type(TransferPartyType.BANK).build());
		tos.add(TransferParty.builder().type(TransferPartyType.UNCLE).build());
		
		DefaultComboBoxModel<TransferParty> fromModel = new DefaultComboBoxModel<>();
		froms.forEach((tp)->fromModel.addElement(tp));
		LOGGER.info("From model set ");
		cboFrom.setModel(fromModel);
		DefaultComboBoxModel<TransferParty> toModel = new DefaultComboBoxModel<>();
		tos.forEach((tp)->toModel.addElement(tp));
		LOGGER.info("To model set ");
		cboTo.setModel(toModel);
		
		if(data.getFrom().isPresent()) {
			cboFrom.setSelectedItem(data.getFrom().get());
		}
		if(data.getTo().isPresent()) {
			cboTo.setSelectedItem(data.getTo().get());
		}
		
		pnlSend.removeAll();
		pnlReceive.removeAll();
		addSendRows(data);
		addReceiveRows(data);
		
		panel.invalidate();
		panel.repaint();
	}

	@Override
	protected PLifecycleHandler getLifecycleHandler() {
		return new PSimpleLifecycleHandler() {
			@Override
			public void prePlacement() {
				TransferPanel.this.getGlobalBus().register(TransferPanel.this);
				
				panel.add(pnlFromAndTo, "span 2, wrap");
				panel.add(pnlSend);
				panel.add(pnlReceive, "wrap");
				panel.add(pnlSubmit, "span 2");
				panel.setOpaque(false);
				
				pnlFromAndTo.add(cboFrom);
				pnlFromAndTo.add(lblDirection);
				pnlFromAndTo.add(cboTo);
				pnlFromAndTo.add(btnSwitch);
				
				pnlSubmit.add(btnTransfer, "skip 1");
				
				pnlFromAndTo.setOpaque(false);
				pnlSend.setOpaque(false);
				pnlReceive.setOpaque(false);
				pnlSubmit.setOpaque(false);
				
				pnlSend.setBorder(BorderFactory.createTitledBorder("Money to give"));
				pnlReceive.setBorder(BorderFactory.createTitledBorder("Change to get back"));
				
				cboFrom.addActionListener(fromActionListener);
				cboTo.addActionListener(toActionListener);
				
				btnSwitch.addActionListener( (e)->{
					TransferPanelData tpData = getData();
					setData( tpData.toBuilder().from(tpData.getTo()).to(tpData.getFrom()).build() );
				} );
				
				btnTransfer.addActionListener( (e)-> {
					TransferPanelData data = getData();
					LOGGER.info("TP Sending");
					TransferPanel.this.post( new DoTransferClick(data.getFrom().get(), data.getTo().get(), data.getSend(), data.getReceive()) );
				});
				
			}
			@Override
			public void postProps() {
				TransferPanelData existData = getData();
				TransferPanelData props = getProps();
				TransferPanelData finalData = props;
				if(existData!=null) {
					//Merge
					finalData = existData.toBuilder().allPlayers(props.getAllPlayers()).currentPlayer(props.getCurrentPlayer()).currencies(props.getCurrencies()).build();
				}
				if(finalData.getFrom().isEmpty()) {
					Player player = finalData.getCurrentPlayer().orElse(finalData.getAllPlayers().size()>0?finalData.getAllPlayers().get(0):null);
					if(player!=null) {
						LOGGER.info("Fixing from: " + player);
						TransferParty fromTp = TransferParty.builder().type(TransferPartyType.PLAYER).player(Optional.of(player)).build();
						finalData = finalData.toBuilder().from(Optional.of(fromTp)).build();
					}
				}
				if(finalData.getTo().isEmpty()) {
					LOGGER.info("Fixing to. ");
					TransferParty toTp = TransferParty.builder().type(TransferPartyType.BANK).build();
					finalData = finalData.toBuilder().to(Optional.of(toTp)).build();
				}
				setData( getEnsureEntriesData(finalData) );
			}
		};
	}
	
	@Subscribe
	public void handle(TKETransferRequestCompleted tse) {
		LOGGER.info("TransferStatusEvent " + tse);
		if(tse.isSuccessful()) {
			TransferPanelData data = getData();
			LOGGER.info("Clearing Send and Receive bags");
			setData( getEnsureEntriesData(data.toBuilder().send(Map.of()).receive(Map.of()).build()) );
		}
	}
	@Subscribe
	public void handle(PlayerSelectedEvent ps) {
		LOGGER.info("PlayerSelectedEvent " + ps);
		TransferPanelData data = getData();
		
		TransferParty from = TransferParty.builder().type(TransferPartyType.PLAYER).player(Optional.of(ps.getPlayer())).build();
		LOGGER.info("Selecting from: " + from);
		TransferParty to = TransferParty.builder().type(TransferPartyType.BANK).build();
		LOGGER.info("Selecting to:" + to);
		
		setData( data.toBuilder().from(Optional.of(from)).to(Optional.of(to)).build());
	}

	private TransferPanelData getEnsureEntriesData(TransferPanelData data) {
		Set<Integer> currencies = data.getCurrencies();
		Map<Integer, Integer> send = data.getSend();
		Map<Integer, Integer> receive = data.getReceive();
		Map<Integer, Integer> sendNew = CollectionsUtil.cloneToLinkedHashMap(send);
		Map<Integer, Integer> receiveNew = CollectionsUtil.cloneToLinkedHashMap(receive);
		ensureEntries(currencies, sendNew);
		ensureEntries(currencies, receiveNew);
		return data.toBuilder().send(sendNew).receive(receiveNew).build();
	}
	
	@Override
	protected List<Class<?>> declareEmittedEvents() {
		return List.of(DoTransferClick.class);
	}

	private void addSendRows(TransferPanelData data) {
		Map<Integer, Integer> send = data.getSend();
		int totalValue = 0;
		for(Integer currency:send.keySet()) {
			JLabel curr = new JLabel( "$" + currency+" x " );
			JButton plus = new JButton("+");
			JButton minus = new JButton("-");
			int cnt = send.get(currency);
			JLabel count = new JLabel( cnt+"" );
			int val = currency * send.get(currency);
			totalValue += val;
			JLabel value = new JLabel( val+"" );
			pnlSend.add(curr);
			pnlSend.add(minus);
			pnlSend.add(count);
			pnlSend.add(plus);
			pnlSend.add(value, "skip 1, wrap");
			
			minus.setEnabled( cnt>0 );
			
			plus.addActionListener( (e)->{
				Map<Integer, Integer> sendNew = CollectionsUtil.cloneToLinkedHashMap(data.getSend());
				sendNew.put(currency, data.getSend().get(currency)+1);
				setData( data.toBuilder().send( sendNew ).build() );
			});
			minus.addActionListener( (e)->{
				Map<Integer, Integer> sendNew = CollectionsUtil.cloneToLinkedHashMap(data.getSend());
				sendNew.put(currency, data.getSend().get(currency)-1);
				setData( data.toBuilder().send( sendNew ).build() );
			});
		}
		JLabel total = new JLabel( "$" + totalValue );
		pnlSend.add(new JLabel("Total"));
		pnlSend.add(total, "skip 4");
	}
	private void addReceiveRows(TransferPanelData data) {
		Map<Integer, Integer> receive = data.getReceive();
		int totalValue = 0;
		for(Integer currency:receive.keySet()) {
			JLabel curr = new JLabel( "$" + currency+" x " );
			JButton plus = new JButton("+");
			JButton minus = new JButton("-");
			int cnt = receive.get(currency);
			JLabel count = new JLabel( cnt+"" );
			int val = currency * receive.get(currency);
			totalValue += val;
			JLabel value = new JLabel( val+"" );
			pnlReceive.add(curr);
			pnlReceive.add(minus);
			pnlReceive.add(count);
			pnlReceive.add(plus);
			pnlReceive.add(value, "skip 1, wrap");
			
			minus.setEnabled( cnt>0 );
			
			plus.addActionListener( (e)->{
				Map<Integer, Integer> receiveNew = CollectionsUtil.cloneToLinkedHashMap(data.getReceive());
				receiveNew.put(currency, data.getReceive().get(currency)+1);
				setData( data.toBuilder().receive( receiveNew ).build() );
			});
			minus.addActionListener( (e)->{
				Map<Integer, Integer> receiveNew = CollectionsUtil.cloneToLinkedHashMap(data.getReceive());
				receiveNew.put(currency, data.getReceive().get(currency)-1);
				setData( data.toBuilder().receive( receiveNew ).build() );
			});
		}
		JLabel total = new JLabel( "$" + totalValue );
		pnlReceive.add(new JLabel("Total"));
		pnlReceive.add(total, "skip 4");
	}
	private void ensureEntries(Set<Integer> currencies, Map<Integer, Integer> bag) {
		for(Integer currency:currencies) {
			if(bag.get(currency)==null) {
				bag.put(currency, 0);
			}
		}
	}
}
