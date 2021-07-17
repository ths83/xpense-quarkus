package org.thoms.xpenses.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.KeysAndAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DynamoDBUtils {

    private static final String ID = "id";

    public static String getStringAttribute(final AttributeValue value) {
        return Optional.ofNullable(value.s())
                .orElse(null);
    }

    public static Float getNumberAttribute(final AttributeValue value) {
        return Optional.of(Float.valueOf(value.n()))
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

    public static AttributeValue buildNumberAttribute(final Float attribute) {
        return AttributeValue.builder().n(attribute.toString()).build();
    }

    public static AttributeValue buildAttributeList(final List<AttributeValue> attributeValues) {
        return AttributeValue.builder().l(attributeValues).build();
    }

    public static AttributeValueUpdate buildStringAttributeUpdate(final String attribute) {
        return AttributeValueUpdate.builder().value(buildStringAttribute(attribute)).build();
    }

    public static AttributeValueUpdate buildNumberAttributeUpdate(final Float attribute) {
        return AttributeValueUpdate.builder().value(buildNumberAttribute(attribute)).build();
    }

    public static KeysAndAttributes buildAttributes(final List<String> ids) {
        return KeysAndAttributes.builder()
                .keys(ids
                        .stream()
                        .map(id -> Map.of(ID, buildStringAttribute(id)))
                        .collect(Collectors.toList()))
                .build();
    }

}
