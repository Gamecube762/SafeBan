package com.github.Gamecube762.SafeBan;

import java.util.Date;
import java.text.SimpleDateFormat;

public class BanRequest {
	
	protected String requesterName;
	protected String defendantName;
	protected String reason;
	protected String TimeStamp;
	
    //public BanRequest(Player requester, Player defendant, String reason){}

	protected BanRequest(String requester, String defendant, String reason, String TimeStamp) {
		requesterName = requester;
		defendantName = defendant;
		this.reason = reason;
		this.TimeStamp = TimeStamp;
	}
	
	public BanRequest(String requester, String defendant, String reason) {
		this(
				requester,
				defendant,
				reason,
				new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format( new Date() )
			);
	}
	
	public String getRequesterName() {
	    return requesterName;
	}
	
	public String getDefendantName() {
	    return defendantName;
	}
	
	public String getReason() {
	    return reason;
	}
	
	public String getTimeStamp() {
	    return TimeStamp;
	}
	
	
	
	
	
}
