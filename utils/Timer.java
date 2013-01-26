package team122.utils;

import battlecode.common.Clock;

public class Timer {
	public static final void StartTimer() {
		time = -Clock.getBytecodeNum();
		round = -Clock.getRoundNum();
	}
	
	public static final void EndTimer() {
		round += Clock.getRoundNum();
		
		if (round == 0) {
			System.out.println("ByteCodes: " + (time + Clock.getBytecodeNum() - TIMER_COSTS));
		} else {
			System.out.println("ByteCodes: " + (time + (round * 10000) + Clock.getBytecodeNum() - TIMER_COSTS));
		}
	}
	
	private static int time;
	private static int round;
	private static final int TIMER_COSTS = 20;
}