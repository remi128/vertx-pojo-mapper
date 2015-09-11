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

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.AfterSave;
import de.braintags.io.vertx.pojomapper.dataaccess.impl.AbstractWrite;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteResult;
import de.braintags.io.vertx.pojomapper.dataaccess.write.WriteAction;
import de.braintags.io.vertx.pojomapper.dataaccess.write.impl.WriteResult;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;
import de.braintags.io.vertx.util.CounterObject;
import de.braintags.io.vertx.util.ErrorObject;

/**
 * @author Michael Remme
 * @param <T>
 */

public class MongoWrite<T> extends AbstractWrite<T> {
  private static Logger logger = LoggerFactory.getLogger(MongoWrite.class);

  /**
   * 
   */
  public MongoWrite(final Class<T> mapperClass, MongoDataStore datastore) {
    super(mapperClass, datastore);
  }

  @Override
  public void save(Handler<AsyncResult<IWriteResult>> resultHandler) {
    WriteResult rr = new WriteResult();
    if (getObjectsToSave().isEmpty()) {
      resultHandler.handle(Future.succeededFuture(rr));
      return;
    }
    ErrorObject<IWriteResult> ro = new ErrorObject<IWriteResult>();
    CounterObject counter = new CounterObject(getObjectsToSave().size());
    for (T entity : getObjectsToSave()) {
      save(entity, rr, result -> {
        if (result.failed()) {
          ro.setThrowable(result.cause());
        } else {
          // logger.info("saving " + counter.getCount());
          if (counter.reduce())
            resultHandler.handle(Future.succeededFuture(rr));
        }
      });
      if (ro.handleError(resultHandler))
        return;
    }
  }

  private void save(T entity, IWriteResult writeResult, Handler<AsyncResult<Void>> resultHandler) {
    getDataStore().getStoreObjectFactory().createStoreObject(getMapper(), entity, result -> {
      if (result.failed()) {
        resultHandler.handle(Future.failedFuture(result.cause()));
      } else {
        doSave(entity, (MongoStoreObject) result.result(), writeResult, sResult -> {
          if (sResult.failed()) {
            resultHandler.handle(Future.failedFuture(sResult.cause()));
          } else {
            resultHandler.handle(Future.succeededFuture());
          }
        });
      }
    });
  }

  /**
   * execute the methods marked with {@link AfterSave}
   * 
   * @param entity
   *          the entity to be handled
   */
  private void executePostSave(T entity) {
    getMapper().executeLifecycle(AfterSave.class, entity);
  }

  /**
   * execute the action to store ONE instance in mongo
   * 
   * @param storeObject
   * @param resultHandler
   */
  private void doSave(T entity, MongoStoreObject storeObject, IWriteResult writeResult,
      Handler<AsyncResult<Void>> resultHandler) {
    MongoClient mongoClient = ((MongoDataStore) getDataStore()).getMongoClient();
    IMapper mapper = getMapper();
    String column = mapper.getTableInfo().getName();
    final String currentId = (String) storeObject.get(mapper.getIdField());
    logger.info("now saving");
    mongoClient.save(column, storeObject.getContainer(), result -> {
      if (result.failed()) {
        logger.info("failed", result.cause());
        Future<Void> future = Future.failedFuture(result.cause());
        resultHandler.handle(future);
        return;
      } else {
        logger.info("saved");
        WriteAction action = WriteAction.UNKNOWN;
        String id = result.result();
        if (id == null) {
          id = currentId;
          action = WriteAction.UPDATE;
        } else
          action = WriteAction.INSERT;
        executePostSave(entity);
        writeResult.addEntry(storeObject, id, action);
        resultHandler.handle(Future.succeededFuture());
      }
    });

  }

}
