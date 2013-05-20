package appender;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.scene.control.TextArea;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

public class GuiAppender extends AppenderSkeleton {
	
	private TextArea out;
	private SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy hh:mm:ss");
	public GuiAppender(TextArea out) {
		// TODO Auto-generated constructor stub
		this.out = out;
		this.setLayout(new PatternLayout("%-5p [%t]: %m%n"));
	}

	public void close() {
		// TODO Auto-generated method stub
		super.closed = true;
	}

	public boolean requiresLayout() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void append(LoggingEvent event) {
		// TODO Auto-generated method stub
		
		
		 out.appendText(format.format(new Date(event.getTimeStamp()))+"\t");
		 out.appendText("["+event.getLevel()+"]\t");
		 out.appendText(event.getMessage().toString()+"\n");
	}


}
