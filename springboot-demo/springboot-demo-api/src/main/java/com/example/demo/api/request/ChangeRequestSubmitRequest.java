package com.example.demo.api.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 提交修改提议请求
 */
@Data
public class ChangeRequestSubmitRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "playerId 不能为空")
    private Long playerId;

    @NotBlank(message = "submitterId 不能为空")
    private String submitterId;

    private String proposedName;
    private String proposedPosition;
    private String proposedStatus;
    private Integer proposedNumber;
    private String proposedClub;
    private String proposedLeague;
    private String proposedCountry;
    private Integer proposedHeight;
    private String proposedFoot;
}
