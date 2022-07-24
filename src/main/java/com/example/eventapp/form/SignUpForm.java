package com.example.eventapp.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpForm {
	
	// 店舗名
	@NotNull
	@Size(min=1, max=30)
	private String name;
	
	// パスワード
	@NotNull
	@Size(min=1, max=30)
	private String password;

}
