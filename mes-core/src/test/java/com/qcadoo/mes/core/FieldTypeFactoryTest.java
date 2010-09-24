package com.qcadoo.mes.core;

import static com.google.common.collect.Lists.newArrayList;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;

import com.qcadoo.mes.core.api.Entity;
import com.qcadoo.mes.core.internal.model.FieldDefinitionImpl;
import com.qcadoo.mes.core.internal.types.EagerBelongsToType;
import com.qcadoo.mes.core.internal.types.BooleanType;
import com.qcadoo.mes.core.internal.types.DateTimeType;
import com.qcadoo.mes.core.internal.types.DateType;
import com.qcadoo.mes.core.internal.types.DecimalType;
import com.qcadoo.mes.core.internal.types.DictionaryType;
import com.qcadoo.mes.core.internal.types.EnumType;
import com.qcadoo.mes.core.internal.types.IntegerType;
import com.qcadoo.mes.core.internal.types.PasswordType;
import com.qcadoo.mes.core.internal.types.PriorityType;
import com.qcadoo.mes.core.internal.types.StringType;
import com.qcadoo.mes.core.internal.types.TextType;
import com.qcadoo.mes.core.model.FieldDefinition;
import com.qcadoo.mes.core.types.EnumeratedFieldType;
import com.qcadoo.mes.core.types.FieldType;

public class FieldTypeFactoryTest extends DataAccessTest {

    private Entity entity = null;

    private final FieldDefinition fieldDefinition = new FieldDefinitionImpl("aa");

    @Before
    public void init() {
        entity = new Entity();
    }

    @Test
    public void shouldReturnEnumType() throws Exception {
        // when
        EnumeratedFieldType fieldType = fieldTypeFactory.enumType("val1", "val2", "val3");

        // then
        assertThat(fieldType, is(EnumType.class));
        assertThat(fieldType.values(), JUnitMatchers.hasItems("val1", "val2", "val3"));
        assertTrue(fieldType.isSearchable());
        assertTrue(fieldType.isOrderable());
        assertFalse(fieldType.isAggregable());
        assertEquals(String.class, fieldType.getType());

        assertNotNull(fieldType.toObject(fieldDefinition, "val1", entity));
        assertNull(fieldType.toObject(fieldDefinition, "val4", entity));
        assertEquals("commons.validate.field.error.invalidDictionaryItem", entity.getError("aa").getMessage());
        assertEquals("[val1, val2, val3]", entity.getError("aa").getVars()[0]);
    }

    @Test
    public void shouldReturnDictionaryType() throws Exception {
        // given
        given(dictionaryService.values("dict")).willReturn(newArrayList("val1", "val2", "val3"));

        // when
        EnumeratedFieldType fieldType = fieldTypeFactory.dictionaryType("dict");

        // then
        assertThat(fieldType, is(DictionaryType.class));
        assertThat(fieldType.values(), JUnitMatchers.hasItems("val1", "val2", "val3"));
        assertTrue(fieldType.isSearchable());
        assertTrue(fieldType.isOrderable());
        assertFalse(fieldType.isAggregable());
        assertEquals(String.class, fieldType.getType());
        assertNotNull(fieldType.toObject(fieldDefinition, "val1", entity));
        assertNull(fieldType.toObject(fieldDefinition, "val4", entity));
        assertEquals("commons.validate.field.error.invalidDictionaryItem", entity.getError("aa").getMessage());
        assertEquals("[val1, val2, val3]", entity.getError("aa").getVars()[0]);
    }

    @Test
    public void shouldReturnBooleanType() throws Exception {
        // when
        FieldType fieldType = fieldTypeFactory.booleanType();

        // then
        assertThat(fieldType, is(BooleanType.class));
        assertTrue(fieldType.isSearchable());
        assertTrue(fieldType.isOrderable());
        assertFalse(fieldType.isAggregable());
        assertEquals(Boolean.class, fieldType.getType());
        assertNotNull(fieldType.toObject(fieldDefinition, false, entity));
    }

    @Test
    public void shouldReturnDateType() throws Exception {
        // when
        FieldType fieldType = fieldTypeFactory.dateType();

        // then
        assertThat(fieldType, is(DateType.class));
        assertTrue(fieldType.isSearchable());
        assertTrue(fieldType.isOrderable());
        assertFalse(fieldType.isAggregable());
        assertEquals(Date.class, fieldType.getType());
        assertNotNull(fieldType.toObject(fieldDefinition, new Date(), entity));
    }

    @Test
    public void shouldReturnDateTimeType() throws Exception {
        // when
        FieldType fieldType = fieldTypeFactory.dateTimeType();

        // then
        assertThat(fieldType, is(DateTimeType.class));
        assertTrue(fieldType.isSearchable());
        assertTrue(fieldType.isOrderable());
        assertFalse(fieldType.isAggregable());
        assertEquals(Date.class, fieldType.getType());
        assertNotNull(fieldType.toObject(fieldDefinition, new Date(), entity));
    }

    @Test
    public void shouldReturnDecimalType() throws Exception {
        // when
        FieldType fieldType = fieldTypeFactory.decimalType();

        // then
        assertThat(fieldType, is(DecimalType.class));
        assertTrue(fieldType.isSearchable());
        assertTrue(fieldType.isOrderable());
        assertTrue(fieldType.isAggregable());
        assertEquals(BigDecimal.class, fieldType.getType());
        assertNotNull(fieldType.toObject(fieldDefinition, BigDecimal.valueOf(1.21), entity));
        assertNotNull(fieldType.toObject(fieldDefinition, BigDecimal.valueOf(1), entity));
        assertNotNull(fieldType.toObject(fieldDefinition, BigDecimal.valueOf(1), entity));
        assertNotNull(fieldType.toObject(fieldDefinition, BigDecimal.valueOf(1234567), entity));
        assertNull(fieldType.toObject(fieldDefinition, BigDecimal.valueOf(12345678), entity));
        assertEquals("commons.validate.field.error.invalidNumericFormat", entity.getError("aa").getMessage());
    }

    @Test
    public void shouldReturnIntegerType() throws Exception {
        // when
        FieldType fieldType = fieldTypeFactory.integerType();

        // then
        assertThat(fieldType, is(IntegerType.class));
        assertTrue(fieldType.isSearchable());
        assertTrue(fieldType.isOrderable());
        assertTrue(fieldType.isAggregable());
        assertEquals(Integer.class, fieldType.getType());
        assertNotNull(fieldType.toObject(fieldDefinition, 1, entity));
        assertNotNull(fieldType.toObject(fieldDefinition, 1234567890, entity));
    }

    @Test
    public void shouldReturnStringType() throws Exception {
        // when
        FieldType fieldType = fieldTypeFactory.stringType();

        // then
        assertThat(fieldType, is(StringType.class));
        assertTrue(fieldType.isSearchable());
        assertTrue(fieldType.isOrderable());
        assertFalse(fieldType.isAggregable());
        assertEquals(String.class, fieldType.getType());
        assertNotNull(fieldType.toObject(fieldDefinition, "test", entity));
        assertNotNull(fieldType.toObject(fieldDefinition, StringUtils.repeat("a", 255), entity));
        assertNull(fieldType.toObject(fieldDefinition, StringUtils.repeat("a", 256), entity));
        assertEquals("commons.validate.field.error.stringIsTooLong", entity.getError("aa").getMessage());
        assertEquals("255", entity.getError("aa").getVars()[0]);
    }

    @Test
    public void shouldReturnTextType() throws Exception {
        // when
        FieldType fieldType = fieldTypeFactory.textType();

        // then
        assertThat(fieldType, is(TextType.class));
        assertTrue(fieldType.isSearchable());
        assertTrue(fieldType.isOrderable());
        assertFalse(fieldType.isAggregable());
        assertEquals(String.class, fieldType.getType());
        assertNotNull(fieldType.toObject(fieldDefinition, "test", entity));
        assertNotNull(fieldType.toObject(fieldDefinition, StringUtils.repeat("a", 2048), entity));
        assertNull(fieldType.toObject(fieldDefinition, StringUtils.repeat("a", 2049), entity));
        assertEquals("commons.validate.field.error.stringIsTooLong", entity.getError("aa").getMessage());
        assertEquals("2048", entity.getError("aa").getVars()[0]);
    }

    @Test
    public void shouldReturnBelongToType() throws Exception {
        // when
        FieldType fieldType = fieldTypeFactory.eagerBelongsToType("parent", "entity", "name");

        // then
        assertThat(fieldType, is(EagerBelongsToType.class));
        assertFalse(fieldType.isSearchable());
        assertFalse(fieldType.isOrderable());
        assertFalse(fieldType.isAggregable());
        assertEquals(Object.class, fieldType.getType());
        assertNotNull(fieldType.toObject(fieldDefinition, new Entity(), entity));
    }

    @Test
    public void shouldReturnPasswordType() throws Exception {
        // when
        FieldType fieldType = fieldTypeFactory.passwordType();

        // then
        assertThat(fieldType, is(PasswordType.class));
        assertFalse(fieldType.isSearchable());
        assertFalse(fieldType.isOrderable());
        assertFalse(fieldType.isAggregable());
        assertEquals(String.class, fieldType.getType());
    }

    @Test
    public void shouldReturnPriorityType() throws Exception {
        // given
        FieldDefinition fieldDefinition = new FieldDefinitionImpl("aaa");

        // when
        FieldType fieldType = fieldTypeFactory.priorityType(fieldDefinition);

        // then
        assertThat(fieldType, is(PriorityType.class));
        assertFalse(fieldType.isSearchable());
        assertTrue(fieldType.isOrderable());
        assertFalse(fieldType.isAggregable());
        assertEquals(Integer.class, fieldType.getType());
        assertNotNull(fieldType.toObject(fieldDefinition, 1, entity));
        assertEquals(fieldDefinition, ((PriorityType) fieldType).getScopeFieldDefinition());
    }
}
