package net.progressit.tradez.panels.log;

import lombok.Getter;
import lombok.Setter;

public class LogStringer {
	@Getter
	@Setter
	private static LogStringer instance = new LogStringer();
	
	public String toString(Object originalEvent) {
		return originalEvent.toString();
	}
}
