package pIoT.shared.messages;

import java.util.Date;

public class LightMessage extends DataMessage {
	
	private int intensity;
	
	public LightMessage(){
		
	}
	
	public LightMessage(Date receivedTimestamp, String sourceMessage,
			int sourceAddress, int intensity) {
		super(receivedTimestamp, sourceMessage, sourceAddress);
		this.intensity = intensity;
	}

	public int getIntensity() {
		return intensity;
	}

	public void setIntensity(int intensity) {
		this.intensity = intensity;
	}

}
