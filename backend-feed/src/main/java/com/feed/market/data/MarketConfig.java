package com.feed.market.data;

import java.util.ArrayList;
import java.util.List;

import com.feed.market.data.model.Markets;


public class MarketConfig {
	
	public static final List<Markets> markets = new ArrayList<>();
	
	static {
		
		markets.add(Markets.builder().code("ECV.L").name("Eco City Vehicles plc").build());
		markets.add(Markets.builder().code("MHN.L").name("Menhaden Capital").build());
		markets.add(Markets.builder().code("SWJ.L").name("John Swan & Sons plc").build());
		markets.add(Markets.builder().code("JDT.L").name("Jupiter Dividend & Growth Trust PLC").build());
		markets.add(Markets.builder().code("UANC.L").name("Urban&Civic; plc").build());
		markets.add(Markets.builder().code("SDP.L").name("Schroder Asia Pacific Ord").build());
		markets.add(Markets.builder().code("HSBA.L").name("HSBC Holdings plc").build());
		markets.add(Markets.builder().code("XPL.L").name("XPLORER").build());
		markets.add(Markets.builder().code("SSE.L").name("SSE plc").build());
		markets.add(Markets.builder().code("JSI.L").name("Jiasen International Holdings Limited").build());
		markets.add(Markets.builder().code("UBMN.L").name("UBM I14 NP").build());
		markets.add(Markets.builder().code("WPC.L").name("Witan Pacific Ord").build());
		markets.add(Markets.builder().code("VTC.L").name("The Vitec Group plc").build());
		
	}

 

}
