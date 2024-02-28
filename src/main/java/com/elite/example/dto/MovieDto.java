package com.elite.example.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MovieDto {

	private String id;
	private String name;
	private Integer durationInMinutes;

}
