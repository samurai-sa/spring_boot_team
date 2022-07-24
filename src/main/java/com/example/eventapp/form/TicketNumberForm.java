package com.example.eventapp.form;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class TicketNumberForm {
    // 認証用入力整理番号
    @NotNull
    private String ticketNum;

}
