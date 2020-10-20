package com.github.adrian83.trends.domain.common;

@FunctionalInterface
public interface StatusCleaner {

  void removeOlderThanSec(int seconds);
}
