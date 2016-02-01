package com.example.dimitar.currencycalculator;

import java.util.HashMap;
import java.util.Map;

public interface OnTaskCompleted {
    void onTaskCompleted(Map<String,Double> result);
}
