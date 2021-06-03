package org.thoms.xpenses.model.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateRequestBody {
    private String name;
    private String date;
}
