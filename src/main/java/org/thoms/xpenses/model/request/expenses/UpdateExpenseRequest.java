package org.thoms.xpenses.model.request.expenses;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UpdateExpenseRequest {
    private final String expenseName;
    private final Float amount;
    private final String currency;
    private final String startDate;
}
