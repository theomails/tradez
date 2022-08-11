package net.progressit.tradez.panels.log;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import net.progressit.tradez.TradezLogic.LogEvent;

@Data
@Builder(toBuilder = true)
public class LogPanelData {
	private final List<LogEvent> loggedEvents;
}
