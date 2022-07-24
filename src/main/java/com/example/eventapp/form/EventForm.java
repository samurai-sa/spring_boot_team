package com.example.eventapp.form;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventForm {
	
	// イベントの名前
	@NotNull
	@Size(min=1, max=50)
	private String name;
	
	// イベントの説明
	@NotNull
	@Size(min=1, max=1000)
	private String description;
	
	// カテゴリーid
//	@NotNull
//	private long categoryId;
	
	// 店舗id
//	@NotNull
//	private long storeId;
	
	// スペースid
	@NotNull
	private long eventspaceId;
	
	
	// 開催日時	
	@FutureOrPresent
	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate eventDate;
	
	@NotNull
	private LocalTime eventTime;
	
	@NotNull
	private LocalTime eventEndTime;
	
	// 開始時間が終了時間より後かどうか
		@AssertTrue(message = "開始時間は終了時間より前にしてください")
		public boolean isEventTimeAfterEventEndTime() {
			 if (eventTime == null) {
		            return true;
		        } else if (eventEndTime == null) {
		            return true;
		        }
			return !eventTime.isAfter(eventEndTime);
		}
	
	// 参加上限
	@NotNull
	@Positive
	private Integer maxTicket;
	
	
}
