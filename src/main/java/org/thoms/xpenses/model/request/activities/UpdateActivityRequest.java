package org.thoms.xpenses.model.request.activities;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UpdateActivityRequest {
    private final String name;
    private final String date;
}
