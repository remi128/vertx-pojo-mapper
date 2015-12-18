/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.mongo.dataaccess;

import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryOperator;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryOperatorTranslator;

/**
 * Translates operator definitions into propriate expressions for the datastore
 * 
 * @author Michael Remme
 * 
 */

public class QueryOperatorTranslator implements IQueryOperatorTranslator {

  /**
   * Translate the given {@link QueryOperator} into an expression fitting for Mongo
   * 
   * @param op
   * @return
   */
  @Override
  public String translate(QueryOperator op) {
    switch (op) {
    case EQUALS:
      return "$eq";
    case CONTAINS:
    case STARTS:
    case ENDS:
      return "$regex";
    case NOT_EQUALS:
      return "$ne";
    case LARGER:
      return "$gt";
    case LARGER_EQUAL:
      return "$gte";
    case SMALLER:
      return "$lt";
    case SMALLER_EQUAL:
      return "$lte";
    case IN:
      return "$in";
    case NOT_IN:
      return "$nin";

    default:
      throw new UnsupportedOperationException("No translator for " + op);
    }
  }
}
