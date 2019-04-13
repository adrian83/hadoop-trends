package com.github.adrian83.trends.common;

import java.util.List;

import reactor.core.publisher.Flux;

public interface Service <T> {
	
	long CLEANING_FIXED_RATE_MS = 60000; 
	long CLEANING_INITIAL_DELAY_MS = 120000;

	void removeUnused();
	
}
