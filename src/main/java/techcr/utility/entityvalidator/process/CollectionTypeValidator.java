package techcr.utility.entityvalidator.process;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import techcr.utility.entityvalidator.exception.UnsupportedFieldException;
import techcr.utility.entityvalidator.type.CustomCollection;
import techcr.utility.entityvalidator.type.ValidatorUtil;
import techcr.utility.entityvalidator.type.notation.ArrayType;
import techcr.utility.entityvalidator.type.notation.CollectionType;
import techcr.utility.entityvalidator.type.notation.CustomCollectionType;
import techcr.utility.entityvalidator.validator.ValidationError;

public class CollectionTypeValidator implements FieldValidator {

    private int maxLength;
    private int minLength;
    private boolean validateInnerEntity;
    private String errorDescription;
    private String fieldName;
    private Collection entities;


    public CollectionTypeValidator(Field field, Object entity, ArrayType arrayType) throws IllegalAccessException {
        maxLength = arrayType.maxLength();
        minLength = arrayType.minLength();
        validateInnerEntity = arrayType.validateInnerEntity();
        errorDescription = arrayType.errorDecs();
        field.setAccessible(true);
        fieldName = ValidatorUtil.getFieldName(field);
        Object array = field.get(entity);
        if (null != array) {
            int length = Array.getLength(array);
            entities = new ArrayList<>();
            for (int i = 0; i < length; i ++) {
                //Object arrayElement = Array.get(array, i);
                entities.add(Array.get(array, i));
            }
        }
    }

    public CollectionTypeValidator(Field field, Object entity, CollectionType collectionType)
            throws IllegalAccessException {
        maxLength = collectionType.maxLength();
        minLength = collectionType.minLength();
        validateInnerEntity = collectionType.validateInnerEntity();
        errorDescription = collectionType.errorDecs();
        field.setAccessible(true);
        fieldName = ValidatorUtil.getFieldName(field);
        entities = (Collection) field.get(entity);
    }

    public CollectionTypeValidator(Field field, Object entity, CustomCollectionType customCollectionType)
            throws IllegalAccessException {
        maxLength = customCollectionType.maxLength();
        minLength = customCollectionType.minLength();
        validateInnerEntity = customCollectionType.validateInnerEntity();
        errorDescription = customCollectionType.errorDecs();
        field.setAccessible(true);
        fieldName = ValidatorUtil.getFieldName(field);
        CustomCollection customCollection = (CustomCollection) field.get(entity);
        if (null != customCollection) {
            entities = customCollection.get();
        }
    }

    @Override
    public void validate(List<ValidationError> errors) throws UnsupportedFieldException, IllegalAccessException {
        int itemSize = entities == null ? 0 : entities.size();
        boolean lengthError = false;
        if (maxLength > 0 && itemSize > maxLength) {
            String errorDes = errorDescription == null || errorDescription.isEmpty() ? "Max Item exceed" : errorDescription;
            lengthError = true;
            errors.add(
                    new ValidationError(fieldName, errorDes, Integer.toString(itemSize)));
        }
        if (minLength > 0 && itemSize < minLength) {
            String errorDes = errorDescription == null || errorDescription.isEmpty() ? "Minimum Item not covered" : errorDescription;
            lengthError = true;
            errors.add(
                    new ValidationError(fieldName, errorDes, Integer.toString(itemSize)));

        }
        if (validateInnerEntity && itemSize > 0 && !lengthError) {
            for (Object entity : entities) {
                EntityFieldValidator entityFieldValidator = new EntityFieldValidator(entity);
                entityFieldValidator.validate(errors);
            }
        }

    }
}
