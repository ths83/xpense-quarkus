package org.thoms.xpenses.model.request.activities;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CreateActivityRequest {
    private final String name;
    private final String createdBy;
}
