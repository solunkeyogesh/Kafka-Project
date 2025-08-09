package com.yogesh.kafka.model;

import java.io.Serializable;

public record Order(String product, int price, int quantity, String customerName) implements Serializable {}