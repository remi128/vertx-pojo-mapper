/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.util;

import java.util.Arrays;
import java.util.List;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.dataaccess.query.IQueryResult;
import de.braintags.vertx.jomnigate.dataaccess.query.ISearchCondition;
import de.braintags.vertx.jomnigate.exception.NoSuchRecordException;
import de.braintags.vertx.util.IteratorAsync;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * A helper class with several static methods to simplyfy search actions
 *
 * @author Michael Remme
 *
 */
public class QueryHelper {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(QueryHelper.class);

  private QueryHelper() {
  }

  /**
   * Performs a query by id and returns the found instance, or null, if none
   *
   * @param datastore
   *          the datastore to be used
   * @param mapperClass
   *          the mapper class
   * @param id
   *          the id to search for
   * @param handler
   *          the handler to be informed
   */
  public static final <T> void findRecordById(final IDataStore datastore, final Class<T> mapperClass, final String id,
      final Handler<AsyncResult<T>> handler) {
    IQuery<T> query = datastore.createQuery(mapperClass);
    query.setSearchCondition(ISearchCondition.isEqual(query.getMapper().getIdInfo().getIndexedField(), id));
    executeToFirstRecord(query, handler);
  }

  /**
   * Executes the given {@link IQuery} and returns the first record directly to the handler. This method can be used,
   * when only one record is expected to be found, like an ID query, for instance. The same than
   * executeToFirstRecord(query, false, handler)
   *
   * @param query
   *          the query to be executed
   * @param handler
   *          the handler, which will receive the first object
   */
  public static <T> void executeToFirstRecord(final IQuery<T> query, final Handler<AsyncResult<T>> handler) {
    executeToFirstRecord(query, false, handler);
  }

  /**
   * Executes the given {@link IQuery} and returns the first record directly to the handler. This method can be used,
   * when only one record is expected to be found, like an ID query, for instance
   *
   * @param query
   *          the query to be executed
   * @param required
   *          defines wether at least one record must exist
   * @param handler
   *          the handler, which will receive the first object
   */
  public static <T> void executeToFirstRecord(final IQuery<T> query, final boolean required, final Handler<AsyncResult<T>> handler) {
    query.execute(result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        IteratorAsync<T> it = result.result().iterator();
        if (it.hasNext()) {
          it.next(itResult -> {
            if (itResult.failed()) {
              handler.handle(Future.failedFuture(itResult.cause()));
            } else {
              handler.handle(Future.succeededFuture(itResult.result()));
            }
          });
        } else {
          if (required) {
            handler.handle(Future.failedFuture(new NoSuchRecordException(
                "expected record not found for query " + result.result().getOriginalQuery().toString())));
          } else {
            handler.handle(Future.succeededFuture(null));
          }
        }
      }
    });
  }

  /**
   * Executes the given {@link IQuery} and returns all found records as {@link List} directly to
   * the handler
   *
   * @param query
   *          the query to be executed
   * @param handler
   *          the handler, which will receive the list of objects
   */
  public static <T> void executeToList(final IQuery<T> query, final Handler<AsyncResult<List<T>>> handler) {
    query.execute(result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        LOGGER.debug("executed query: " + result.result().toString());
        queryResultToList(result.result(), handler);
      }
    });
  }

  /**
   * Creates a complete {@link List} of objects from the given {@link IQueryResult}
   *
   * @param queryResult
   *          the {@link IQueryResult} to be handled
   * @param handler
   *          the handler to be informed
   */
  @SuppressWarnings("unchecked")
  public static final <T> void queryResultToList(final IQueryResult<T> queryResult, final Handler<AsyncResult<List<T>>> handler) {
    queryResult.toArray(result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        Object[] resultArray = result.result();
        handler.handle(Future.succeededFuture((List<T>) Arrays.asList(resultArray)));
      }
    });
  }

}
