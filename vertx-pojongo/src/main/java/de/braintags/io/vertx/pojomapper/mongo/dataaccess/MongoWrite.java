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

import de.braintags.io.vertx.pojomapper.dataaccess.impl.AbstractWrite;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteResult;
import de.braintags.io.vertx.pojomapper.dataaccess.write.WriteAction;
import de.braintags.io.vertx.pojomapper.dataaccess.write.impl.WriteResult;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;
import de.braintags.io.vertx.util.CounterObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;

/**
 * An implementation of {@link IWrite} for Mongo
 * 
 * @author Michael Remme
 * @param <T>
 *          the type of the underlaying mapper
 */
public class MongoWrite<T> extends AbstractWrite<T> {
  private static Logger LOG = LoggerFactory.getLogger(MongoWrite.class);

  /**
   * Constructor
   * 
   * @param mapperClass
   *          the mapper class
   * @param datastore
   *          the datastore to be used
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

    CounterObject<IWriteResult> counter = new CounterObject<>(getObjectsToSave().size(), resultHandler);
    for (T entity : getObjectsToSave()) {
      save(entity, rr, result -> {
        if (result.failed()) {
          counter.setThrowable(result.cause());
        } else {
          if (counter.reduce())
            resultHandler.handle(Future.succeededFuture(rr));
        }
      });
      if (counter.isError())
        return;
    }
  }

  private void save(T entity, IWriteResult writeResult, Handler<AsyncResult<Void>> resultHandler) {
    getDataStore().getMapperFactory().getStoreObjectFactory().createStoreObject(getMapper(), entity, result -> {
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
    final Object currentId = storeObject.get(mapper.getIdField());
    LOG.info("now saving");
    mongoClient.save(column, storeObject.getContainer(), result -> {
      if (result.failed()) {
        LOG.info("failed", result.cause());
        Future<Void> future = Future.failedFuture(result.cause());
        resultHandler.handle(future);
        return;
      }

      LOG.info("saved");
      String id = result.result();
      if (id == null) {
        finishUpdate(currentId, entity, storeObject, writeResult, resultHandler);
      } else
        finishInsert(id, entity, storeObject, writeResult, resultHandler);
    });

  }

  private void finishInsert(Object id, T entity, MongoStoreObject storeObject, IWriteResult writeResult,
      Handler<AsyncResult<Void>> resultHandler) {
    setIdValue(id, storeObject, result -> {
      if (result.failed()) {
        resultHandler.handle(result);
        return;
      }
      try {
        executePostSave(entity);
        writeResult.addEntry(storeObject, id, WriteAction.INSERT);
        resultHandler.handle(Future.succeededFuture());
      } catch (Exception e) {
        resultHandler.handle(Future.failedFuture(e));
      }
    });
  }

  private void finishUpdate(Object id, T entity, MongoStoreObject storeObject, IWriteResult writeResult,
      Handler<AsyncResult<Void>> resultHandler) {
    try {
      executePostSave(entity);
      writeResult.addEntry(storeObject, id, WriteAction.UPDATE);
      resultHandler.handle(Future.succeededFuture());
    } catch (Exception e) {
      resultHandler.handle(Future.failedFuture(e));
    }
  }

}
