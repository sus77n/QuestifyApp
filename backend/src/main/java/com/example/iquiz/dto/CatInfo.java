package com.example.iquiz.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CatInfo {
    UUID categoryId;
    double baseWeight;
    Double min;
    Double max;
    double accuracy;
    double weight;
    double rate;
    int target;
    double frac;
}