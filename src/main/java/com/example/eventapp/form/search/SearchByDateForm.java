package com.example.eventapp.form.search;

import java.time.LocalDate;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class SearchByDateForm {

    // 検索開始日
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate targetDateStart;

    // 検索終了日
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate targetDateEnd;

    @AssertTrue(message = "開始日より前の日付は指定できません")
    public boolean isAfter() {
        if (targetDateStart == null) {
            return true;
        } else if (targetDateEnd == null) {
            targetDateEnd = targetDateStart;
            return true;
        }

        return targetDateEnd.isAfter(targetDateStart) || targetDateEnd.isEqual(targetDateStart);
    }
    
    //語彙検索
    @Size(min=0 ,max=100)
    private String words;
    
    
}
