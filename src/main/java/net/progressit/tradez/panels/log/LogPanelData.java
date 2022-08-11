package net.progressit.tradez.panels.log;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import net.progressit.tradez.TradezKeyEvents.TradezKeyEvent;

@Data
@Builder(toBuilder = true)
public class LogPanelData {
	private final List<TradezKeyEvent> loggedEvents;
}
