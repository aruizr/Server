package domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_DATE_FORMAT = "HH:mm - dd/MM";
	
	private MessageType messageType;
	
	private String sourceName;
	private String sourceAddress;
	
	private String destinationName;
	private String destinationAddress;
	
	private String content;
	private String condition;
	private Serializable additionalData;
	private Date timeStamp;

	/**
	 * <p>Creates a message of the type specified in the parameter.</p>
	 * @param messageType
	 */
	public Message(MessageType messageType) {
		this.messageType = messageType;
		timeStamp = new Date();
	}
	
	/**
	 * <p>Returns the TimeStamp with the specified Date Format. If the parameter is null the format is going mthe default one: {@value Message#DEFAULT_DATE_FORMAT}.</p>
	 * @param 
	 * @return A String containing the TimeStamp of the message with the format specified in the parameter.
	 */
	public String getTimeStamp(String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format == null ? DEFAULT_DATE_FORMAT : format);
		return dateFormat.format(timeStamp);
	}
	
	@Override
	public String toString() {
		String s = "";
		
		s += "TYPE [" + messageType.toString() + "]";
		if (sourceName != null || sourceAddress != null) {
			s += " FROM ";
			if (sourceName != null) s += "[" + sourceName + "]";
			if (sourceAddress != null) s += "[" + sourceAddress + "]";
		}
		if (destinationName != null || destinationAddress != null) {
			s += " TO ";
			if (destinationName != null) s += "[" + destinationName + "]";
			if (destinationAddress != null) s += "[" + destinationAddress + "]";
		}
		if (content != null) s += " CONTENT [" + content + "]";
		if (condition != null) s += " CONDITION [" + condition + "]";
		if (additionalData != null) s += " EXTRA DATA ["+additionalData.toString()+"]";
		s += " TIMESTAMP ["+ getTimeStamp(DEFAULT_DATE_FORMAT) + "]";
		
		return s;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getSourceAddress() {
		return sourceAddress;
	}

	public void setSourceAddress(String sourceAddress) {
		this.sourceAddress = sourceAddress;
	}

	public String getDestinationName() {
		return destinationName;
	}

	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}

	public String getDestinationAddress() {
		return destinationAddress;
	}

	public void setDestinationAddress(String destinationAddress) {
		this.destinationAddress = destinationAddress;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean getCondition() {
		return Boolean.parseBoolean(condition);
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public Serializable getAdditionalData() {
		return additionalData;
	}

	public void setAdditionalData(Serializable additionalData) {
		this.additionalData = additionalData;
	}

	public MessageType getMessageType() {
		return messageType;
	}
}
