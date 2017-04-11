package de.braintags.vertx.jomnigate.dataaccess.query.impl;

import de.braintags.vertx.jomnigate.dataaccess.query.IIndexedField;

/**
 * Default implementation of {@link IIndexedField}
 * 
 * @author sschmitt
 * 
 */
public class IndexedField implements IIndexedField {

  private String fieldName;

  public IndexedField(String name) {
    this.fieldName = name;
  }

  @Override
  public String getFieldName() {
    return fieldName;
  }

  @Override
  public String toString() {
    return fieldName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    IndexedField other = (IndexedField) obj;
    if (fieldName == null) {
      if (other.fieldName != null)
        return false;
    } else if (!fieldName.equals(other.fieldName))
      return false;
    return true;
  }
}
