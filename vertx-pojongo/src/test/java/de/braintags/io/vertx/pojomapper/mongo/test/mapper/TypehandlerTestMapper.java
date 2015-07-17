/*
 * Copyright 2014 Red Hat, Inc.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 * 
 * You may elect to redistribute this code under either of these licenses.
 */

package de.braintags.io.vertx.pojomapper.mongo.test.mapper;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import de.braintags.io.vertx.pojomapper.annotation.field.Id;
import de.braintags.io.vertx.pojomapper.exception.MappingException;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;

/**
 * Mapper with all properties to test {@link ITypeHandler}
 * 
 * @author Michael Remme
 * 
 */

public class TypehandlerTestMapper {
  @Id
  public String id;
  public String stringField = "myString";
  public int myInt = 5;
  public Integer myInteger = new Integer(14);
  public float myFloat = 5.88f;
  public Float myFloatOb = new Float(12.5);
  public boolean myBo = true;
  public Boolean myBoolean = new Boolean(false);
  public Date javaDate = new Date(System.currentTimeMillis());
  public java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());
  public Timestamp timestamp = new Timestamp(System.currentTimeMillis());
  public Calendar calendar = Calendar.getInstance();
  public Time time = new Time(System.currentTimeMillis());
  public short shortvalue = 3;
  public Short shortObject = new Short((short) 5);
  public long longvalue = 6666666666666666l;
  public Long longObject = new Long(88888888888888l);
  public double doubleValue = 333.5555;
  public Double doubleObject = new Double(44.9999);
  public BigDecimal bigDecimal = new BigDecimal(23.44);
  public BigInteger bigInteger = new BigInteger("99999999");
  public StringBuffer buffer = new StringBuffer("test string buffer content");

  public char charValue = 'a';
  public Character character = new Character('c');
  public byte byteValue = 123;
  public Byte byteObject = 88;
  public URI uri;
  public URL url;

  /**
   * 
   */
  public TypehandlerTestMapper() {
    try {
      uri = new URI("https://www.braintags.de/impressum");
      url = new URL("https://www.braintag.de/rebutton");
    } catch (URISyntaxException | MalformedURLException e) {
      throw new MappingException(e);
    }
  }

  @Override
  public boolean equals(Object ob) {
    Field[] fields = getClass().getFields();
    for (Field field : fields) {
      compare(field, ob);
    }

    return true;
  }

  private boolean compare(Field field, Object compare) {
    if (field.getName().equals("id"))
      return true;
    if (field.getName().equals("buffer")) {
      @SuppressWarnings("unused")
      String test = "test";
    }
    try {
      Object value = field.get(this);
      Object compareValue = field.get(compare);
      equalValues(value, compareValue, field.getName());
      return true;
    } catch (Exception e) {
      throw new RuntimeException("Error in field " + field.getName(), e);
    }

  }

  private boolean equalValues(Object value, Object compareValue, String fieldName) {
    if (value == null && compareValue == null)
      return true;
    if (value instanceof CharSequence) {
      value = value.toString();
      compareValue = compareValue.toString();
    }
    if (!value.equals(compareValue))
      throw new MappingException("Contents are not equal: " + fieldName);
    return true;
  }
}
