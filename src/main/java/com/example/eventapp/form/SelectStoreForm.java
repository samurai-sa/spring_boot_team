package com.example.eventapp.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectStoreForm {
	private Long storeId;
	private Long eventspaceId;
}