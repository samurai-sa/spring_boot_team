package com.example.eventapp.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventSpaceForm {

	// 店舗ID
	@NotNull
	private long storeId;

	// 名前
	@NotNull
	@Size(min = 1, max = 50)
	private String name;

	// 収容人数
	@NotNull
	@Positive
	private Integer capacity;
}
