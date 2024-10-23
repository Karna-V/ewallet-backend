package com.rampnow.ewallet.payloads.commons;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAllData {
    private Object data;
    private String nextUrl;
}
