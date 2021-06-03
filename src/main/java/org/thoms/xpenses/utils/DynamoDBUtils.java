package org.thoms.xpenses.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DynamoDBUtils {
    public static String getStringAttribute(final AttributeValue value) {
        return Optional.ofNullable(value.s())
                .orElse(null);
    }

    public static List<String> getListAttribute(final AttributeValue value) {
        return Optional.ofNullable(value.l())
                .orElse(List.of())
                .stream()
                .map(AttributeValue::s)
                .collect(Collectors.toList());
    }

    public static AttributeValue buildStringAttribute(final String attribute) {
        return AttributeValue.builder().s(attribute).build();
    }

    public static AttributeValue buildAttributeList(final List<AttributeValue> attributeValues) {
        return AttributeValue.builder().l(attributeValues).build();
    }

    public static AttributeValueUpdate buildStringAttributeUpdate(final String attribute) {
        return AttributeValueUpdate.builder().value(buildStringAttribute(attribute)).build();
    }
}
