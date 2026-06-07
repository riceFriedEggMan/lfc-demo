package com.rice.msg.model;

import com.rice.lfcdemo.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SourceQuotaModel extends BaseModel implements Serializable {
    private Long id;

    private int num;

    private int unit;

    private int channel;

    private String sourceId;
}
