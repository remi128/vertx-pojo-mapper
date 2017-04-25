/*
 * #%L
 * vertx-pojo-mapper-common-test
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.testdatastore.mapper.versioning.converter;

import de.braintags.vertx.jomnigate.versioning.IVersionConverter;
import io.vertx.core.Future;

public class V6Converter implements IVersionConverter {

  @Override
  public Future<Void> convert() {
    return Future.failedFuture(new UnsupportedOperationException());
  }

}
