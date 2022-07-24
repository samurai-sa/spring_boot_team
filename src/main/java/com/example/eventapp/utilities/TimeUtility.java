package com.example.eventapp.utilities;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class TimeUtility {

	// 開始時間と終了時間と何分刻みか入力で、それが返る
	public List<LocalTime> timeList(LocalTime start, LocalTime end, Long step) {
		List<LocalTime> timeList = new ArrayList<>();
		for (LocalTime listTime = start; listTime.isBefore(end)
				|| listTime.equals(end); listTime = listTime.plusMinutes(step)) {
			timeList.add(listTime);
		}
		return timeList;
	}

}
